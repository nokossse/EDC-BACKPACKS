package com.modernbackpacks.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

/**
 * Backpack geometry rendered in Java entity-model space (Y down, origin at the body pivot),
 * whether it comes from a Modded Entity Java class or a Bedrock .geo.json.
 */
public interface BackpackModel {
    void render(PoseStack poseStack, MultiBufferSource buffers, VertexConsumer cubeBuffer,
                ResourceLocation texture, int light, int overlay);

    /**
     * Runs {@code draw} with the pose stack at the {@code weaponDummy<slot+1>} bone, in a world-aligned frame
     * (Y up). Does nothing for a model without that bone, or before its first render.
     */
    default void atWeaponSlot(PoseStack poseStack, int slot, Runnable draw) {
    }

    /** Hanging straps swing, in degrees: pitch = back/forward, roll = sideways. No-op when the model has none. */
    default void setStrapSway(float pitchDeg, float rollDeg) {
    }
}
