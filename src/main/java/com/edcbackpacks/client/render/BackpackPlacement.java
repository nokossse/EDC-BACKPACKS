package com.edcbackpacks.client.render;

import com.edcbackpacks.item.BackpackKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.item.ItemDisplayContext;

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
     * HAND = sac tenu en 1ère et 3e personne
     */
    public static final float INVENTORY_Y_PX = 12.0F;
    public static final float HAND_Y_PX = 12.0F;
    /** Rotation du sac en main, en degrés. Un axe à la fois : 90, -90, 180. */
    public static final float HAND_ROT_X = 180.0F;
    public static final float HAND_ROT_Y = 0.0F;
    public static final float HAND_ROT_Z = 0.0F;

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
            case RAID_BACKPACK -> Pose.of(0, 0F, 3.2F, 0, 0, 0, 1.0F);
            case LARGE_HIKING_GREEN, LARGE_HIKING_BLUE, LARGE_HIKING_RED -> Pose.of(0, 0F, 3.2F, 0, 0, 0, 1.0F);
            case DAYPACK_SAND -> Pose.of(0, 0F, 3.2F, 0, 0, 0, 1.0F);
            case DUFFLE_ADI -> Pose.of(1, 0F, 3.2F, 0, 0, 0, 1.0F);
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
        return switch (kind) {
            case RAID_BACKPACK -> itemRaid(context);
            case LARGE_HIKING_GREEN, LARGE_HIKING_BLUE, LARGE_HIKING_RED -> itemHikingGreen(context);
            case DAYPACK_SAND -> itemDaypackSand(context);
            case DUFFLE_ADI -> itemDuffleAdi(context);
        };
    }

    private static Pose itemRaid(ItemDisplayContext context) {
        return itemShared(context);
    }

    private static Pose itemHikingGreen(ItemDisplayContext context) {
        return itemShared(context);
    }

    private static Pose itemDaypackSand(ItemDisplayContext context) {
        return itemShared(context);
    }

    private static Pose itemDuffleAdi(ItemDisplayContext context) {
        return itemShared(context);
    }

    private static Pose itemShared(ItemDisplayContext context) {
        return switch (context) {
            case GUI -> Pose.offset(0, INVENTORY_Y_PX, 0);
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND ->
                    Pose.of(0, HAND_Y_PX, 0, HAND_ROT_X, HAND_ROT_Y, HAND_ROT_Z, 1.0F);
            case GROUND -> Pose.offset(0, 0, 0);
            default -> Pose.of(0, HAND_Y_PX, 0, HAND_ROT_X, HAND_ROT_Y, HAND_ROT_Z, 1.0F);
        };
    }
}
