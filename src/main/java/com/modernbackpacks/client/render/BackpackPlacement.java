package com.modernbackpacks.client.render;

import com.modernbackpacks.item.BackpackKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tous les placements 3D se règlent ICI.
 *
 * Unité : {@link #px(float)} — 1.0 pixel Minecraft = 1/16 de bloc.
 * Exemple : {@code px(3)} décale de 3 pixels. {@code px(-1)} d'un pixel dans l'autre sens.
 *
 * Méthode de travail :
 * 1. Change UNE valeur (X ou Y ou Z), pas les trois d'un coup.
 * 2. Rebuild le JAR ({@code gradlew build}) et relance le client.
 * 3. Note dans quel sens ça a bougé, puis affine au pixel.
 *
 * Rotations en degrés. Scale 1.0 = taille normale, 0.9 = 10% plus petit.
 */
public final class BackpackPlacement {
    private BackpackPlacement() {
    }

    /**
     * RÉGLAGES RAPIDES (tous les sacs) — en pixels.
     * Mets 6, rebuild, regarde. Si ça monte au lieu de descendre, mets -6.
     *
     * INVENTORY = cases d'inventaire + hotbar
     */
    public static final float INVENTORY_Y_PX = 12.0F;

    /** Recul du sac (px, vers l'arrière) quand le joueur porte un plastron, quel qu'il soit. Réglé via /mbplace armor. */
    public static float armorOffsetPx = 1.2F;

    public static final int MAX_WEAPON_SLOTS = 2;

    // --- Arme TACZ accrochée à un weaponDummy, par sac et par slot : [x, y, z (px), rotX, rotY, rotZ, scale]. ---
    private static final Map<BackpackKind, float[][]> WEAPONS = new EnumMap<>(BackpackKind.class);

    /** Valeurs de départ ; colle ici le résultat de /mbplace print pour les rendre permanentes. */
    private static float[] weaponDefault(BackpackKind kind, int slot) {
        return switch (kind) {
            case TASMANIAN_TIGER_MK3_GREEN -> slot == 0
                    ? new float[]{-6.2F, 22.0F, 5.0F, -90.0F, 90.0F, 0.0F, 0.5F}
                    : new float[]{7.0F, 22.0F, 5.0F, -90.0F, 90.0F, 0.0F, 0.5F};
            case LARGE_MILITARY_DAYPACK -> slot == 0
                    ? new float[]{-4.3F, 20.0F, 4.0F, -90.0F, 90.0F, 0.0F, 0.5F}
                    : new float[]{5.5F, 20.0F, 4.0F, -90.0F, 90.0F, 0.0F, 0.5F};
            default -> new float[]{0, 0, 0, 0, 0, 0, 1.0F};
        };
    }

    /** Valeurs modifiables (/mbplace weapon) pour ce sac et ce slot (0 = weaponDummy1). */
    public static float[] weaponTunable(BackpackKind kind, int slot) {
        float[][] perSlot = WEAPONS.computeIfAbsent(kind, k -> new float[MAX_WEAPON_SLOTS][]);
        if (perSlot[slot] == null) {
            perSlot[slot] = weaponDefault(kind, slot);
        }
        return perSlot[slot];
    }

    public static Map<BackpackKind, float[][]> weapons() {
        return WEAPONS;
    }

    public static Pose weapon(BackpackKind kind, int slot) {
        float[] v = weaponTunable(kind, slot);
        return Pose.of(v[0], v[1], v[2], v[3], v[4], v[5], v[6]);
    }

    /** Convertit des pixels Minecraft en blocs (1 bloc = 16 px). */
    public static float px(float pixels) {
        return pixels / 16.0F;
    }

    public record Pose(float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
        public static Pose offset(float xPx, float yPx, float zPx) {
            return new Pose(px(xPx), px(yPx), px(zPx), 0.0F, 0.0F, 0.0F, 1.0F);
        }

        public static Pose of(float xPx, float yPx, float zPx, float rotX, float rotY, float rotZ, float scale) {
            return new Pose(px(xPx), px(yPx), px(zPx), rotX, rotY, rotZ, scale);
        }

        public void apply(PoseStack poseStack) {
            poseStack.translate(this.x, this.y, this.z);
            if (this.rotX != 0.0F) {
                poseStack.mulPose(Axis.XP.rotationDegrees(this.rotX));
            }
            if (this.rotY != 0.0F) {
                poseStack.mulPose(Axis.YP.rotationDegrees(this.rotY));
            }
            if (this.rotZ != 0.0F) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(this.rotZ));
            }
            if (this.scale != 1.0F) {
                poseStack.scale(this.scale, this.scale, this.scale);
            }
        }
    }

    /**
     * Sur le dos (slot Curios BACK), après attache au torso.
     *
     * X+ = gauche du joueur / X- = droite
     * Y+ = vers les pieds / Y- = vers la tête
     * Z+ = sort du dos (vers l'arrière) / Z- = rentre dans le torse
     */
    public static Pose curio(BackpackKind kind) {
        return switch (kind) {
            // Modélisé directement sur le Steve de Blockbench : 0 = position exacte de Blockbench.
            case TASMANIAN_TIGER_MK3_GREEN -> Pose.of(0, 0F, 0F, 0, 0, 0, 1.0F);
            // Modélisé directement sur le Steve de Blockbench : 0 = position exacte de Blockbench.
            case OLIVE_DAYPACK, OSPREY_KESTREL, LARGE_MILITARY_DAYPACK, EASTPAK_BACKPACK -> Pose.of(0, 0F, 0F, 0, 0, 0, 1.0F);
        };
    }

    /**
     * Ajustement par sac, APRÈS le cadrage commun main/GUI.
     * Sers-toi de ça pour coller le modèle au pixel près sans casser les autres sacs.
     *
     * Sens (après le flip 180 déjà appliqué) : teste 1 pixel à la fois.
     * GUI = inventaire / hotbar. FIRST = 1ère personne. THIRD = 3e personne. GROUND = au sol.
     */
    public static Pose item(BackpackKind kind, ItemDisplayContext context) {
        String group = group(context);
        float[] o = group == null ? null : OVERRIDES.getOrDefault(kind, Map.of()).get(group);
        return o == null ? itemDefault(kind, context) : Pose.of(o[0], o[1], o[2], o[3], o[4], o[5], o[6]);
    }

    // --- Réglage en jeu (/mbplace) : valeurs [x, y, z (px), rotX, rotY, rotZ, scale] par sac et par contexte. ---
    private static final Map<BackpackKind, Map<String, float[]>> OVERRIDES = new EnumMap<>(BackpackKind.class);

    public static final List<String> GROUPS = List.of("first", "third", "gui", "ground");

    private static String group(ItemDisplayContext context) {
        return switch (context) {
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> "first";
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> "third";
            case GUI -> "gui";
            case GROUND -> "ground";
            default -> null;
        };
    }

    /** Valeurs modifiables pour ce sac/contexte, initialisées depuis la pose actuelle. */
    public static float[] tunable(BackpackKind kind, String group) {
        return OVERRIDES.computeIfAbsent(kind, k -> new HashMap<>()).computeIfAbsent(group, g -> {
            Pose p = itemDefault(kind, switch (g) {
                case "first" -> ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
                case "third" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                case "gui" -> ItemDisplayContext.GUI;
                default -> ItemDisplayContext.GROUND;
            });
            return new float[]{p.x() * 16, p.y() * 16, p.z() * 16, p.rotX(), p.rotY(), p.rotZ(), p.scale()};
        });
    }

    public static Map<BackpackKind, Map<String, float[]>> overrides() {
        return OVERRIDES;
    }

    private static Pose itemDefault(BackpackKind kind, ItemDisplayContext context) {
        return switch (kind) {
            case TASMANIAN_TIGER_MK3_GREEN -> itemMk3Green(context);
            case OLIVE_DAYPACK -> itemOliveDaypack(context);
            case OSPREY_KESTREL, LARGE_MILITARY_DAYPACK, EASTPAK_BACKPACK -> itemShared(context);
        };
    }

    private static Pose itemMk3Green(ItemDisplayContext context) {
        return itemShared(context);
    }

    private static Pose itemOliveDaypack(ItemDisplayContext context) {
        return itemShared(context);
    }

    private static Pose itemShared(ItemDisplayContext context) {
        return switch (context) {
            case GUI -> Pose.offset(0, INVENTORY_Y_PX, 0);
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> Pose.of(-15, 5, -6, 0, 90, 0, 1.0F);
            case GROUND -> Pose.offset(0, 0, 0);
            default -> Pose.of(0, 19, 0, 90, 0, 0, 1.0F);
        };
    }
}
