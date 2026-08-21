package com.edcbackpacks.client.model;

import com.edcbackpacks.EdcBackpacks;
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

public class RaidBackpackModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(EdcBackpacks.MOD_ID, "raid_backpack"), "main");

    private final ModelPart backpack;

    public RaidBackpackModel(ModelPart root) {
        this.backpack = root.getChild("backpack");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition backpack = partdefinition.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition core = backpack.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 0).addBox(-5.1F, -8.0F, 0.0F, 8.2F, 16.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, -12.2F, 2.0F));

        core.addOrReplaceChild("cover_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-5.4F, -8.5F, -0.1F, 8.8F, 4.0F, 4.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.3F, 0.0F, -0.0175F, 0.0F, 0.0F));

        core.addOrReplaceChild("clips", CubeListBuilder.create().texOffs(16, 28).addBox(2.4F, -7.5F, 3.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 28).addBox(-2.8F, -7.5F, 3.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 28).addBox(2.4F, -18.5F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 28).addBox(-2.8F, -18.5F, 3.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.3F, 10.2F, 0.0F));

        PartDefinition attLeft = backpack.addOrReplaceChild("att_left", CubeListBuilder.create().texOffs(26, 25).addBox(-2.0F, -10.0F, -0.5F, 2.1F, 2.0F, 3.8F, new CubeDeformation(0.0F))
                .texOffs(20, 30).addBox(-0.8F, -8.5F, 0.9F, 1.1F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -9.0F, 2.6F));

        attLeft.addOrReplaceChild("lower_r1", CubeListBuilder.create().texOffs(26, 13).addBox(-2.1F, -11.0F, 0.3F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.9F, 12.0F, -0.1F, 0.0F, 0.0F, 0.0175F));

        attLeft.addOrReplaceChild("core_r1", CubeListBuilder.create().texOffs(24, 1).addBox(-1.9983F, -7.9F, -0.4F, 2.0F, 8.0F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, -0.1F, 0.0F, 0.0F, 0.0F, 0.0175F));

        PartDefinition attRight = backpack.addOrReplaceChild("att_right", CubeListBuilder.create().texOffs(20, 30).addBox(-2.3F, -8.5F, 0.9F, 1.1F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 25).addBox(-2.1F, -10.0F, -0.5F, 2.1F, 2.0F, 3.8F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -9.0F, 2.6F));

        attRight.addOrReplaceChild("lower_r2", CubeListBuilder.create().texOffs(26, 19).addBox(-1.1F, -11.0F, 0.3F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 12.0F, -0.1F, 0.0F, 0.0F, -0.0175F));

        attRight.addOrReplaceChild("core_r2", CubeListBuilder.create().texOffs(24, 1).addBox(-2.0017F, -7.9F, -0.4F, 2.0F, 8.0F, 3.6F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, -0.2F, 0.0F, 0.0F, 0.0F, -0.0175F));

        backpack.addOrReplaceChild("straps", CubeListBuilder.create().texOffs(24, 13).addBox(-1.0F, -4.5F, 1.7F, 1.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 13).addBox(-6.2F, -4.5F, 1.7F, 1.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-9.3F, -2.0F, 1.6F, 12.4F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-9.3F, 1.2F, 1.6F, 12.4F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-9.3F, 4.6F, 1.6F, 12.4F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(24, 12).addBox(-8.4F, 8.6F, 1.6F, 10.6F, 1.5F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(-8.4F, 8.6F, -2.4F, 0.0F, 1.5F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(2.2F, 8.6F, -2.4F, 0.0F, 1.5F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 28).addBox(-9.3F, -2.0F, -2.4F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 28).addBox(-9.3F, 1.2F, -2.4F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 28).addBox(-9.3F, 4.6F, -2.4F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 28).addBox(3.1F, -2.0F, -2.4F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 28).addBox(3.1F, 1.2F, -2.4F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(8, 28).addBox(3.1F, 4.6F, -2.4F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(3.1F, -15.5F, 4.6F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.backpack.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
