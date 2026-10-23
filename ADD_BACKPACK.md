# Adding a backpack to Modern Backpacks

Mod id `modern_backpacks`, package `com.modernbackpacks`. Every backpack is a Bedrock `.geo.json`
(from Blockbench) rendered by Simple Bedrock Model (SBM, embedded jar-in-jar). Use a lowercase
`snake_case` id everywhere below (`<id>`, e.g. `osprey_kestrel`); `UPPER_CASE` for the Java enum/field.

Inputs from the user: the `.geo.json`, the texture PNG, the display name, the slot count, the tier (I-VII).

## 1. Model
```
python tools/prepare_geo.py <path/to/model.geo.json> src/main/resources/assets/modern_backpacks/models/bedrock/backpack/<id>.geo.json
```
The output file name must be `<id>.geo.json` (the script sets `geometry.<id>` from it). It drops the Steve
reference rig, flattens mesh normals and collapses closed triangle loops. It does NOT flip mesh V:
Blockbench already exports V flipped and SBM flips it back, so flipping again makes mesh faces read
transparent texture (this was a real bug).

## 2. Texture
Save the PNG as `asset/<id>.png` (the build copies `asset/*.png` to `textures/asset/`). Any resolution works
(the geo UVs are proportional to the geo's `texture_width`/`texture_height`); keep it as the user gave it,
do not resize. If it arrives as a pasted image the file is only a copy; downscaling to the geo grid blurs it.

## 3. Item model
Copy `src/main/resources/assets/modern_backpacks/models/item/olive_daypack.json` to `<id>.json` and replace
`olive_daypack` with `<id>` (the `particle` texture).

## 4. Code (all under `src/main/java/com/modernbackpacks/`)
- `item/BackpackKind.java`: add `<ID>("<id>", slots, tier)`.
- `item/ModItems.java`: add a `RegistryObject<Item> <ID>` registering `new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.<ID>)`.
- `item/ModCreativeTabs.java`: `output.accept(ModItems.<ID>.get());`.
- `client/ClientSetup.java`: add a `CuriosRendererRegistry.register(ModItems.<ID>.get(), () -> new BackpackCurioRenderer(BackpackKind.<ID>))`
  next to the others. SBM baking is automatic (loops over `BackpackKind.values()`).
- `client/render/BackpackPlacement.java`: add the kind to `curio(...)` (`Pose.of(0, 0F, 0F, 0, 0, 0, 1.0F)` is right for models built on the
  Blockbench Steve) and to `itemDefault(...)` (`itemShared(context)` unless it needs its own hand pose).
  Both are exhaustive `switch`es, so the build fails until you do.

## 5. Data and lang
- `src/main/resources/data/curios/tags/items/back.json`: add `"modern_backpacks:<id>"`.
- `src/main/resources/assets/modern_backpacks/lang/en_us.json` and `fr_fr.json`: add
  `item.modern_backpacks.<id>` and `container.modern_backpacks.<id>` (same display name).

## 6. Optional: swinging straps
Bones named in `BedrockBackpackModel.STRAPS_BONES` (`straps-rot`, `rota_staps`) swing with the wearer's velocity.
For a new strap bone, add its exact name (as spelled in the geo) to that set. It is baked as an animated bone
via `ClientSetup`, so the name must match the geo. Other bones stay static.

## 7. Check
- `./gradlew compileJava`, then `./gradlew runClient`.
- Tune the hand/inventory pose live, no rebuild: hold the bag and use `/mbplace first|third|gui|ground x|y|z|rx|ry|rz|s <value>`,
  then `/mbplace print` and paste the `Pose.of(...)` values into `BackpackPlacement`. Resource changes (geo, texture, lang) reload with F3+T; Java changes need a restart.
- Chestplate offset (`armorOffsetPx`) and the hand poses in `itemShared` are shared by all bags.

## Don't
- Don't rename the mod id or package (it breaks existing items and every resource path).
- Don't add dependencies other than Curios; SBM is embedded (see `build.gradle`, `jarjar/metadata.json`).
- Don't commit `libs/*.jar` (git-ignored) or the test mods in `run/`.
