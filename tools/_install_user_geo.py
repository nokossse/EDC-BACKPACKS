import json
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from prepare_geo import (
    collapse_closed_loops,
    flatten_normals,
    inset_mesh_uvs,
    strip_reference_bones,
)

src = Path(r"C:\Users\Administrator\Downloads\military_daypack.geo.json")
dst = Path(r"src\main\resources\assets\modern_backpacks\models\bedrock\backpack\olive_daypack.geo.json")
data = json.loads(src.read_text(encoding="utf-8"))

for geo in data["minecraft:geometry"]:
    geo["description"]["identifier"] = "geometry.olive_daypack"
    geo["bones"] = strip_reference_bones(geo["bones"])
    for bone in geo["bones"]:
        if bone.get("poly_mesh"):
            bone["pivot"] = [0, 0, 0]
            mesh = bone["poly_mesh"]
            flipped = flatten_normals(mesh)
            closed = collapse_closed_loops(mesh)
            inset = inset_mesh_uvs(mesh)
            print(
                f"{bone['name']}: {len(mesh['polys'])} faces, {flipped} normals, "
                f"{closed} loops, {inset} inset, pivot={bone['pivot']}"
            )
        print("bone", bone["name"], "cubes", len(bone.get("cubes") or []))

dst.parent.mkdir(parents=True, exist_ok=True)
dst.write_text(json.dumps(data, indent=2), encoding="utf-8")
print("wrote", dst)
