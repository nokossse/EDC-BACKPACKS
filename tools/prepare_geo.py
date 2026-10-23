"""Prepare a Blockbench Bedrock .geo.json for in-game use with Simple Bedrock Model.

- Drops the Steve reference rig (STEVE bone and its children).
- Replaces smoothed per-vertex poly_mesh normals with flat, outward-facing
  per-face normals. SBM shades and visibility-culls mesh faces from these
  normals, so smoothed normals give dark seams and missing faces.
- Leaves poly_mesh V alone: Blockbench already exports v = 1 - v and SBM
  flips it back on load, so the geo UVs are correct as exported.
- Drops the closing vertex on looped triangles (SBM treats every 4-index poly
  as a quad) and insets each face's UVs by half a texel so nearest-neighbour
  sampling does not pick neighbouring islands or leftover UV marker pixels.

Usage: python tools/prepare_geo.py <in.geo.json> <out.geo.json>
"""
from __future__ import annotations

import json
import math
import sys
from pathlib import Path

REFERENCE_ROOTS = {"steve"}


def strip_reference_bones(bones: list[dict]) -> list[dict]:
    by_name = {b["name"]: b for b in bones}

    def is_reference(bone: dict) -> bool:
        while bone is not None:
            if bone["name"].lower() in REFERENCE_ROOTS:
                return True
            bone = by_name.get(bone.get("parent"))
        return False

    return [b for b in bones if not is_reference(b)]


def newell_normal(points: list[list[float]]) -> list[float]:
    nx = ny = nz = 0.0
    count = len(points)
    for i in range(count):
        x0, y0, z0 = points[i]
        x1, y1, z1 = points[(i + 1) % count]
        nx += (y0 - y1) * (z0 + z1)
        ny += (z0 - z1) * (x0 + x1)
        nz += (x0 - x1) * (y0 + y1)
    length = math.sqrt(nx * nx + ny * ny + nz * nz)
    if length < 1e-9:
        return [0.0, 1.0, 0.0]
    return [nx / length, ny / length, nz / length]


def flatten_normals(poly_mesh: dict) -> int:
    positions = poly_mesh["positions"]
    smooth = poly_mesh.get("normals") or []
    flat: list[list[float]] = []
    flipped = 0

    for poly in poly_mesh["polys"]:
        unique: list[int] = []
        for vertex in poly:
            if vertex[0] not in unique:
                unique.append(vertex[0])
        normal = newell_normal([positions[i] for i in unique])

        hint = [0.0, 0.0, 0.0]
        for vertex in poly:
            if 0 <= vertex[1] < len(smooth):
                for axis in range(3):
                    hint[axis] += smooth[vertex[1]][axis]
        if sum(normal[a] * hint[a] for a in range(3)) < 0.0:
            normal = [-c for c in normal]
            flipped += 1

        flat.append([round(c, 6) for c in normal])
        index = len(flat) - 1
        for vertex in poly:
            vertex[1] = index

    poly_mesh["normals"] = flat
    return flipped


def collapse_closed_loops(poly_mesh: dict) -> int:
    """Blockbench stores triangles as [a,b,c,a]. SBM treats size==4 as a quad."""
    collapsed = 0
    for poly in poly_mesh.get("polys") or []:
        if len(poly) >= 4 and tuple(poly[0]) == tuple(poly[-1]):
            poly.pop()
            collapsed += 1
    return collapsed


def inset_mesh_uvs(poly_mesh: dict, texel: float = 0.5 / 128.0) -> int:
    """Pull each face's UVs toward its centroid so edge samples stay in-island."""
    uvs = poly_mesh.get("uvs") or []
    if not uvs:
        return 0
    rebuilt: list[list[float]] = []
    moved = 0
    for poly in poly_mesh.get("polys") or []:
        unique: list[tuple[int, int, int]] = []
        for vertex in poly:
            key = (vertex[0], vertex[1], vertex[2])
            if key not in unique:
                unique.append(key)
        if not unique:
            continue
        center_u = sum(uvs[v[2]][0] for v in unique) / len(unique)
        center_v = sum(uvs[v[2]][1] for v in unique) / len(unique)
        for vertex in poly:
            src = uvs[vertex[2]]
            du = center_u - src[0]
            dv = center_v - src[1]
            length = math.hypot(du, dv)
            if length > 1e-9:
                step = min(texel, length * 0.35)
                dest = [round(src[0] + du / length * step, 6), round(src[1] + dv / length * step, 6)]
                if dest != src:
                    moved += 1
            else:
                dest = [src[0], src[1]]
            rebuilt.append(dest)
            vertex[2] = len(rebuilt) - 1
    poly_mesh["uvs"] = rebuilt
    return moved


def main() -> None:
    source = Path(sys.argv[1])
    dest = Path(sys.argv[2])
    data = json.loads(source.read_text(encoding="utf-8"))

    for geometry in data["minecraft:geometry"]:
        geometry["description"]["identifier"] = "geometry." + dest.name.removesuffix(".geo.json")
        geometry["bones"] = strip_reference_bones(geometry["bones"])
        for bone in geometry["bones"]:
            mesh = bone.get("poly_mesh")
            if mesh:
                bone["pivot"] = [0, 0, 0]
                flipped = flatten_normals(mesh)
                closed = collapse_closed_loops(mesh)
                inset = inset_mesh_uvs(mesh)
                print(
                    f"{bone['name']}: {len(mesh['polys'])} faces, "
                    f"{flipped} winding-opposed normals fixed, "
                    f"{closed} closed loops collapsed, {inset} uvs inset"
                )

    dest.parent.mkdir(parents=True, exist_ok=True)
    dest.write_text(json.dumps(data, indent=2), encoding="utf-8")
    print(f"wrote {dest}")


if __name__ == "__main__":
    main()
