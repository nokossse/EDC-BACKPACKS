package com.edcbackpacks.client.render;

import com.edcbackpacks.EdcBackpacks;
import com.edcbackpacks.client.model.RaidBackpackModel;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class BackpackCurioRenderer implements ICurioRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(EdcBackpacks.MOD_ID, "textures/entity/raid_backpack.png");

    private final RaidBackpackModel<LivingEntity> model;

    public BackpackCurioRenderer() {
        this.model = new RaidBackpackModel<>(
                Minecraft.getInstance().getEntityModels().bakeLayer(RaidBackpackModel.LAYER_LOCATION));
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

        // The Blockbench entity model is built in player space (pivot y=16). After
        // parenting to the body, shift it onto the upper back.
        poseStack.translate(0.0F, -0.15F, 0.12F);
        poseStack.scale(1.0F, 1.0F, 1.0F);

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
                renderTypeBuffer,
                RenderType.entityCutoutNoCull(TEXTURE),
                false,
                stack.hasFoil());
        this.model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
