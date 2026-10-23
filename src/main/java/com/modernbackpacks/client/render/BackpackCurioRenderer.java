package com.modernbackpacks.client.render;

import com.modernbackpacks.client.model.BackpackModel;
import com.modernbackpacks.client.model.BackpackModelBakery;
import com.modernbackpacks.item.BackpackKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.Map;
import java.util.WeakHashMap;

public class BackpackCurioRenderer implements ICurioRenderer {
    private final BackpackKind kind;
    private final BackpackModel model;

    // Degrees of strap swing per block/tick of speed (sprinting is ~0.28), and the spring that makes it trail like a cape.
    private static final float SWAY_PER_SPEED = 100.0F;
    private static final float MAX_SWAY = 60.0F;
    private static final float SPRING = 0.25F;
    private static final float DAMPING = 0.35F;

    /** pitch, roll, pitch velocity, roll velocity, last time (ticks); one per wearer since the renderer is shared. */
    private final Map<LivingEntity, float[]> sway = new WeakHashMap<>();

    public BackpackCurioRenderer(BackpackKind kind) {
        this.kind = kind;
        this.model = BackpackModelBakery.bake(kind);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        poseStack.pushPose();

        if (renderLayerParent.getModel() instanceof HumanoidModel<?> humanoid) {
            humanoid.body.translateAndRotate(poseStack);
        }

        BackpackPlacement.curio(this.kind).apply(poseStack);

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
                renderTypeBuffer,
                RenderType.entityCutoutNoCull(this.kind.getTexture()),
                false,
                stack.hasFoil());
        updateSway(slotContext.entity(), partialTicks);
        this.model.render(poseStack, renderTypeBuffer, vertexConsumer, this.kind.getTexture(), light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    /**
     * Straps trail behind the wearer's horizontal motion. Signs are Bedrock-space guesses:
     * flip SWAY_PER_SPEED's sign on the pitch or roll line below if they swing the wrong way.
     */
    private void updateSway(LivingEntity entity, float partialTicks) {
        double dx = entity.getX() - entity.xo;
        double dz = entity.getZ() - entity.zo;
        float yaw = (float) Math.toRadians(Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot));
        float forward = (float) (dx * -Mth.sin(yaw) + dz * Mth.cos(yaw));
        float left = (float) (dx * Mth.cos(yaw) + dz * Mth.sin(yaw));
        float targetPitch = Mth.clamp(-forward * SWAY_PER_SPEED, -MAX_SWAY, MAX_SWAY);
        float targetRoll = Mth.clamp(left * SWAY_PER_SPEED, -MAX_SWAY, MAX_SWAY);

        float now = entity.tickCount + partialTicks;
        float[] s = this.sway.computeIfAbsent(entity, e -> new float[]{0, 0, 0, 0, now});
        float dt = Mth.clamp(now - s[4], 0.0F, 2.0F);
        s[4] = now;
        s[2] += (SPRING * (targetPitch - s[0]) - DAMPING * s[2]) * dt;
        s[3] += (SPRING * (targetRoll - s[1]) - DAMPING * s[3]) * dt;
        s[0] = Mth.clamp(s[0] + s[2] * dt, -MAX_SWAY, MAX_SWAY);
        s[1] = Mth.clamp(s[1] + s[3] * dt, -MAX_SWAY, MAX_SWAY);
        this.model.setStrapSway(s[0], s[1]);
    }
}
