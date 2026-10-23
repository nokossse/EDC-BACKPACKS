"""Convert a Blockbench modded_entity .bbmodel into a 1.17+ Java model class."""
from __future__ import annotations

import json
import math
import sys
from pathlib import Path

DEG = math.pi / 180.0
SKIP_GROUPS = {"steve"}


def fnum(value: float) -> str:
    value = float(value)
    if abs(value) < 1e-6:
        value = 0.0
    text = f"{value:.5f}".rstrip("0").rstrip(".")
    if text == "-0":
        text = "0"
    return text + "F"


def rad(degrees: float) -> float:
    return degrees * DEG


def vec(values) -> tuple[float, float, float]:
    return (float(values[0]), float(values[1]), float(values[2]))


def bone_pose(origin, parent_origin, rotation, is_root: bool):
    ox, oy, oz = origin
    rx, ry, rz = rotation
    if is_root or parent_origin is None:
        px, py, pz = -ox, 24.0 - oy, oz
    else:
        pox, poy, poz = parent_origin
        px = -(ox - pox)
        py = -(oy - poy)
        pz = oz - poz
    return px, py, pz, -rad(rx), -rad(ry), rad(rz)


def cube_box(from_, to, bone_origin):
    fx, fy, fz = from_
    tx, ty, tz = to
    bx, by, bz = bone_origin
    return bx - tx, by - ty, fz - bz, tx - fx, ty - fy, tz - fz


def unique_name(base: str, used: set[str]) -> str:
    name = base
    index = 1
    while name in used:
        index += 1
        name = f"{base}{index}"
    used.add(name)
    return name


def pose_call(px, py, pz, rx, ry, rz) -> str:
    if abs(rx) < 1e-6 and abs(ry) < 1e-6 and abs(rz) < 1e-6:
        return f"PartPose.offset({fnum(px)}, {fnum(py)}, {fnum(pz)})"
    return (
        f"PartPose.offsetAndRotation({fnum(px)}, {fnum(py)}, {fnum(pz)}, "
        f"{fnum(rx)}, {fnum(ry)}, {fnum(rz)})"
    )


def addbox_call(cube, bone_origin) -> str:
    x, y, z, dx, dy, dz = cube_box(vec(cube["from"]), vec(cube["to"]), bone_origin)
    inflate = float(cube.get("inflate") or 0.0)
    uv = cube.get("uv_offset") or [0, 0]
    box = (
        f".texOffs({int(uv[0])}, {int(uv[1])})"
        f".addBox({fnum(x)}, {fnum(y)}, {fnum(z)}, {fnum(dx)}, {fnum(dy)}, {fnum(dz)}, "
        f"new CubeDeformation({fnum(inflate)}))"
    )
    if cube.get("mirror_uv"):
        return f".texOffs({int(uv[0])}, {int(uv[1])}).mirror().addBox({fnum(x)}, {fnum(y)}, {fnum(z)}, {fnum(dx)}, {fnum(dy)}, {fnum(dz)}, new CubeDeformation({fnum(inflate)})).mirror(false)"
    return box


def compile_model(bbmodel_path: Path, class_name: str, layer_id: str) -> str:
    data = json.loads(bbmodel_path.read_text(encoding="utf-8"))
    elements = {el["uuid"]: el for el in data["elements"]}
    groups = {g["uuid"]: g for g in data.get("groups", [])}
    resolution = data.get("resolution") or {}
    tex_w = int(resolution.get("width") or 64)
    tex_h = int(resolution.get("height") or 64)

    used_names: set[str] = {"root", "partdefinition", "meshdefinition"}
    rot_counts: dict[str, int] = {}
    lines: list[str] = []

    def emit_group(node: dict, parent_origin, parent_var: str, is_root: bool) -> None:
        group = groups[node["uuid"]]
        name = group["name"]
        if name.lower() in SKIP_GROUPS:
            return

        origin = vec(group.get("origin") or [0, 0, 0])
        rotation = vec(group.get("rotation") or [0, 0, 0])
        java_name = unique_name(name.replace(" ", "_").lower(), used_names)
        px, py, pz, rx, ry, rz = bone_pose(origin, parent_origin, rotation, is_root)

        children = node.get("children") or []
        unrotated: list[dict] = []
        nested: list = []
        for child in children:
            if isinstance(child, str):
                cube = elements.get(child)
                if cube is None:
                    continue
                if cube.get("rotation"):
                    nested.append(cube)
                else:
                    unrotated.append(cube)
            else:
                nested.append(child)

        builder = "CubeListBuilder.create()"
        for cube in unrotated:
            builder += "\n                " + addbox_call(cube, origin)

        lines.append(
            f"        PartDefinition {java_name} = {parent_var}.addOrReplaceChild(\"{java_name}\", "
            f"{builder}, {pose_call(px, py, pz, rx, ry, rz)});"
        )
        lines.append("")

        batches: dict[tuple, list[dict]] = {}
        batch_order: list[tuple] = []
        leftover_nodes: list = []
        for child in nested:
            if isinstance(child, dict) and child.get("from") is not None:
                key = (
                    tuple(vec(child["origin"])),
                    tuple(vec(child.get("rotation") or [0, 0, 0])),
                    bool(child.get("mirror_uv")),
                )
                if key not in batches:
                    batches[key] = []
                    batch_order.append(key)
                batches[key].append(child)
            else:
                leftover_nodes.append(child)

        for key in batch_order:
            cubes = batches[key]
            cube_origin, cube_rot, _mirror = key
            base = cubes[0].get("name") or "cube"
            if base == "cube":
                rot_counts["cube"] = rot_counts.get("cube", 0) + 1
                part_name = f"cube_r{rot_counts['cube']}"
            else:
                rot_counts[base] = rot_counts.get(base, 0) + 1
                part_name = f"{base}_r{rot_counts[base]}"
            part_name = unique_name(part_name.replace(" ", "_"), used_names)
            cpx, cpy, cpz, crx, cry, crz = bone_pose(cube_origin, origin, cube_rot, False)
            builder = "CubeListBuilder.create()"
            for cube in cubes:
                builder += "\n                " + addbox_call(cube, cube_origin)
            lines.append(
                f"        PartDefinition {part_name} = {java_name}.addOrReplaceChild(\"{part_name}\", "
                f"{builder}, {pose_call(cpx, cpy, cpz, crx, cry, crz)});"
            )
            lines.append("")

        for child in leftover_nodes:
            if isinstance(child, dict) and child.get("uuid") in groups:
                emit_group(child, origin, java_name, False)

    header = f"""package com.modernbackpacks.client.model;

import com.modernbackpacks.ModernBackpacks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class {class_name}<T extends Entity> extends EntityModel<T> {{
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(ModernBackpacks.MOD_ID, "{layer_id}"), "main");

    private final ModelPart backpack;

    public {class_name}(ModelPart root) {{
        this.backpack = root.getChild("backpack");
    }}

    public static LayerDefinition createBodyLayer() {{
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
"""
    footer = f"""
        return LayerDefinition.create(meshdefinition, {tex_w}, {tex_h});
    }}

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {{
    }}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {{
        this.backpack.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }}
}}
"""
    for node in data.get("outliner") or []:
        if isinstance(node, dict) and node.get("uuid") in groups:
            emit_group(node, None, "partdefinition", True)

    return header + "\n".join(lines) + footer


def extract_texture(bbmodel_path: Path, dest: Path) -> None:
    import base64

    data = json.loads(bbmodel_path.read_text(encoding="utf-8"))
    source = (data.get("textures") or [{}])[0].get("source") or ""
    marker = "base64,"
    if marker not in source:
        return
    dest.write_bytes(base64.b64decode(source.split(marker, 1)[1]))


if __name__ == "__main__":
    bbmodel = Path(sys.argv[1])
    dest = Path(sys.argv[2])
    class_name = sys.argv[3]
    layer_id = sys.argv[4]
    dest.write_text(compile_model(bbmodel, class_name, layer_id), encoding="utf-8")
    print(f"wrote {dest}")
