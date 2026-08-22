package com.edcbackpacks.client.render;

import com.edcbackpacks.client.model.BackpackModelBakery;
import com.edcbackpacks.item.BackpackKind;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BackpackItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final BackpackKind kind;
    private final EntityModel<?> model;

    public BackpackItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models, BackpackKind kind) {
        super(dispatcher, models);
        this.kind = kind;
        this.model = BackpackModelBakery.bake(models, kind);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        applyDisplayTransforms(context, poseStack);
        BackpackPlacement.item(this.kind, context).apply(poseStack);

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
                buffer, RenderType.entityCutoutNoCull(this.kind.getTexture()), false, stack.hasFoil());
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private static void applyDisplayTransforms(ItemDisplayContext context, PoseStack poseStack) {
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        switch (context) {
            case GUI -> {
                poseStack.scale(0.7F, 0.7F, 0.7F);
                poseStack.mulPose(Axis.YP.rotationDegrees(330.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(12.0F));
                poseStack.translate(0.0F, -0.85F, 0.0F);
            }
            case GROUND -> {
                poseStack.scale(0.45F, 0.45F, 0.45F);
                poseStack.translate(0.0F, -1.15F, 0.0F);
            }
            case FIXED -> {
                poseStack.scale(0.6F, 0.6F, 0.6F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.translate(0.0F, -0.95F, 0.0F);
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.scale(0.7F, 0.7F, 0.7F);
                poseStack.mulPose(Axis.YP.rotationDegrees(context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? 30.0F : -30.0F));
                poseStack.translate(0.0F, -0.7F, 0.15F);
            }
            default -> {
                poseStack.scale(0.55F, 0.55F, 0.55F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.translate(0.0F, -0.85F, 0.05F);
            }
        }
    }
}
