"""Convert a Blockbench Bedrock .bbmodel into a SBM-ready .geo.json.

Uses the bbmodel UV grid (here 64x64 on a 128 PNG = x32) and per-face / mesh
UVs directly. The official Bedrock exporter often writes texture_width 16 even
when the project resolution is 64, which stretches every island 4x in-game.
"""
from __future__ import annotations

import json
import math
import sys
from pathlib import Path

SKIP_GROUPS = {"steve"}
FACE_ORDER = ("north", "east", "south", "west", "up", "down")


def roundf(value: float, digits: int = 5) -> float:
    return round(float(value), digits)


def vec3(values) -> list[float]:
    return [roundf(values[0]), roundf(values[1]), roundf(values[2])]


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
    return [roundf(nx / length, 6), roundf(ny / length, 6), roundf(nz / length, 6)]


def face_uv(face: dict, tex_h: float) -> dict | None:
    uv = face.get("uv")
    if not uv or len(uv) < 4:
        return None
    u1, v1, u2, v2 = (float(uv[0]), float(uv[1]), float(uv[2]), float(uv[3]))
    # SBM poly/cube V is 0-at-top like Blockbench; keep as-is for cubes.
    item = {
        "uv": [roundf(u1), roundf(v1)],
        "uv_size": [roundf(u2 - u1), roundf(v2 - v1)],
    }
    rotation = face.get("rotation")
    if rotation:
        item["uv_rotation"] = int(rotation)
    return item


def cube_to_bedrock(cube: dict) -> dict:
    from_ = vec3(cube["from"])
    to = vec3(cube["to"])
    item = {
        "origin": from_,
        "size": [roundf(to[0] - from_[0]), roundf(to[1] - from_[1]), roundf(to[2] - from_[2])],
    }
    inflate = cube.get("inflate")
    if inflate:
        item["inflate"] = float(inflate)
    origin = cube.get("origin")
    rotation = cube.get("rotation")
    if origin and rotation and any(abs(float(c)) > 1e-6 for c in rotation):
        item["pivot"] = vec3(origin)
        item["rotation"] = vec3(rotation)
    if cube.get("mirror_uv"):
        item["mirror"] = True
    tex_h = 64.0
    faces = {}
    for name in FACE_ORDER:
        face = (cube.get("faces") or {}).get(name)
        if not face:
            continue
        converted = face_uv(face, tex_h)
        if converted:
            faces[name] = converted
    if faces:
        item["uv"] = faces
    return item


def mesh_to_poly(mesh: dict, tex_w: float, tex_h: float) -> dict:
    origin = vec3(mesh.get("origin") or [0, 0, 0])
    vertices = mesh["vertices"]
    keys = list(vertices.keys())
    index_of = {key: i for i, key in enumerate(keys)}
    positions = []
    for key in keys:
        lx, ly, lz = vertices[key]
        positions.append([
            roundf(origin[0] + float(lx)),
            roundf(origin[1] + float(ly)),
            roundf(origin[2] + float(lz)),
        ])

    uvs: list[list[float]] = []
    uv_index: dict[tuple[float, float], int] = {}
    normals: list[list[float]] = []
    polys: list[list[list[int]]] = []

    def add_uv(u: float, v: float) -> int:
        # SBM does v = 1 - v after normalizing; pre-flip in UV-space.
        fv = roundf(tex_h - v)
        key = (roundf(u), fv)
        found = uv_index.get(key)
        if found is not None:
            return found
        idx = len(uvs)
        uv_index[key] = idx
        uvs.append([key[0], key[1]])
        return idx

    for face in (mesh.get("faces") or {}).values():
        vert_ids = face.get("vertices") or []
        if len(vert_ids) < 3:
            continue
        face_uvs = face.get("uv") or {}
        poly = []
        pts = []
        for vid in vert_ids:
            if vid not in index_of:
                continue
            pos_i = index_of[vid]
            uv = face_uvs.get(vid) or [0, 0]
            uv_i = add_uv(float(uv[0]), float(uv[1]))
            poly.append([pos_i, len(normals), uv_i])
            pts.append(positions[pos_i])
        if len(poly) < 3:
            continue
        normals.append(newell_normal(pts))
        nrm_i = len(normals) - 1
        for corner in poly:
            corner[1] = nrm_i
        polys.append(poly)

    return {
        "normalized_uvs": False,
        "positions": positions,
        "normals": normals,
        "uvs": uvs,
        "polys": polys,
    }


def collect_outliner(nodes, parent: str | None, mapping: dict[str, str | None],
                     skip: bool, groups: dict) -> None:
    for node in nodes:
        if isinstance(node, str):
            if not skip:
                mapping[node] = parent
            continue
        uuid = node.get("uuid")
        group = groups.get(uuid) or node
        name = (group.get("name") or "").lower()
        child_skip = skip or name in SKIP_GROUPS
        if uuid and not child_skip:
            mapping[uuid] = parent
        collect_outliner(node.get("children") or [], uuid if not child_skip else parent,
                         mapping, child_skip, groups)


def bone_name(group: dict, used: set[str]) -> str:
    base = group.get("name") or "bone"
    name = base
    index = 1
    while name in used:
        index += 1
        name = f"{base}{index}"
    used.add(name)
    return name


def convert(bbmodel: dict, identifier: str) -> dict:
    resolution = bbmodel.get("resolution") or {}
    tex_w = float(resolution.get("width") or 64)
    tex_h = float(resolution.get("height") or 64)
    textures = bbmodel.get("textures") or []
    if textures:
        tex_w = float(textures[0].get("uv_width") or tex_w)
        tex_h = float(textures[0].get("uv_height") or tex_h)

    groups = {g["uuid"]: g for g in bbmodel.get("groups") or []}
    elements = {e["uuid"]: e for e in bbmodel.get("elements") or []}

    parent_of: dict[str, str | None] = {}
    collect_outliner(bbmodel.get("outliner") or [], None, parent_of, False, groups)

    used_names: set[str] = set()
    uuid_to_name: dict[str, str] = {}
    for uuid, group in groups.items():
        if uuid not in parent_of and uuid not in parent_of.values():
            continue
        if uuid not in parent_of:
            continue
        uuid_to_name[uuid] = bone_name(group, used_names)

    bones_by_uuid: dict[str, dict] = {}
    for uuid, name in uuid_to_name.items():
        group = groups[uuid]
        bone = {"name": name, "pivot": vec3(group.get("origin") or [0, 0, 0])}
        parent_uuid = parent_of.get(uuid)
        if parent_uuid and parent_uuid in uuid_to_name:
            bone["parent"] = uuid_to_name[parent_uuid]
        rot = group.get("rotation")
        if rot and any(abs(float(c)) > 1e-6 for c in rot):
            bone["rotation"] = vec3(rot)
        bones_by_uuid[uuid] = bone

    # Elements whose parent group was skipped (Steve) are absent from parent_of.
    for uuid, element in elements.items():
        parent_uuid = parent_of.get(uuid)
        if parent_uuid is None or parent_uuid not in bones_by_uuid:
            continue
        bone = bones_by_uuid[parent_uuid]
        if element.get("type") == "mesh":
            existing = bone.get("poly_mesh")
            converted = mesh_to_poly(element, tex_w, tex_h)
            if existing:
                # Merge extra meshes into the same bone by appending geometry.
                offset_pos = len(existing["positions"])
                offset_nrm = len(existing["normals"])
                offset_uv = len(existing["uvs"])
                existing["positions"].extend(converted["positions"])
                existing["normals"].extend(converted["normals"])
                existing["uvs"].extend(converted["uvs"])
                for poly in converted["polys"]:
                    existing["polys"].append([
                        [c[0] + offset_pos, c[1] + offset_nrm, c[2] + offset_uv]
                        for c in poly
                    ])
            else:
                bone["poly_mesh"] = converted
            continue
        bone.setdefault("cubes", []).append(cube_to_bedrock(element))

    # Preserve a stable order: groups as listed, then any leftover.
    bones = []
    seen = set()
    for group in bbmodel.get("groups") or []:
        uuid = group.get("uuid")
        if uuid in bones_by_uuid and uuid not in seen:
            bones.append(bones_by_uuid[uuid])
            seen.add(uuid)
    for uuid, bone in bones_by_uuid.items():
        if uuid not in seen:
            bones.append(bone)

    return {
        "format_version": "1.12.0",
        "minecraft:geometry": [
            {
                "description": {
                    "identifier": identifier,
                    "texture_width": int(tex_w),
                    "texture_height": int(tex_h),
                    "visible_bounds_width": 2,
                    "visible_bounds_height": 3.5,
                    "visible_bounds_offset": [0, 1.25, 0],
                },
                "bones": bones,
            }
        ],
    }


def main() -> None:
    source = Path(sys.argv[1])
    dest = Path(sys.argv[2])
    identifier = sys.argv[3] if len(sys.argv) > 3 else "geometry.unknown"
    data = json.loads(source.read_text(encoding="utf-8"))
    geo = convert(data, identifier)
    dest.parent.mkdir(parents=True, exist_ok=True)
    dest.write_text(json.dumps(geo, indent=2), encoding="utf-8")
    desc = geo["minecraft:geometry"][0]["description"]
    bones = geo["minecraft:geometry"][0]["bones"]
    cubes = sum(len(b.get("cubes") or []) for b in bones)
    meshes = sum(1 for b in bones if b.get("poly_mesh"))
    print(f"wrote {dest}  tex={desc['texture_width']}x{desc['texture_height']}  "
          f"bones={len(bones)} cubes={cubes} meshes={meshes}")


if __name__ == "__main__":
    main()
