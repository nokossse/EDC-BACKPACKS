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

public class LargeHikingGreenModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(EdcBackpacks.MOD_ID, "large_hiking_green"), "main");

    private final ModelPart backpack;

    public LargeHikingGreenModel(ModelPart root) {
        this.backpack = root.getChild("backpack");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition backpack = partdefinition.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition core = backpack.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -1.0F, 8.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -18.0F, 0.0F));

        core.addOrReplaceChild("bottom_cover_r1", CubeListBuilder.create().texOffs(26, 0).addBox(-4.0F, -0.5F, -2.5F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.0F, 1.5F, 0.0175F, 0.0F, 0.0F));

        core.addOrReplaceChild("pouch_front_r1", CubeListBuilder.create().texOffs(26, 6).addBox(-4.0F, -5.0F, -1.0F, 8.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.2F, -0.0175F, 0.0F, 0.0F));

        core.addOrReplaceChild("pouch_right_r1", CubeListBuilder.create().texOffs(24, 34).addBox(-0.5F, -2.5F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5F, 3.0F, 1.5F, 0.0F, 0.0F, -0.0349F));

        core.addOrReplaceChild("pouch_left_r1", CubeListBuilder.create().texOffs(34, 34).addBox(-0.5F, -2.5F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, 3.0F, 1.5F, 0.0F, 0.0F, 0.0349F));

        core.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(12, 27).addBox(-0.5F, -7.5F, -2.5F, 1.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9F, -0.5F, 1.5F, 0.0F, -0.0175F, 0.0F));

        core.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 27).addBox(-0.5F, -7.5F, -2.5F, 1.0F, 14.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.9F, -0.5F, 1.5F, 0.0F, 0.0175F, 0.0F));

        PartDefinition straps = core.addOrReplaceChild("straps", CubeListBuilder.create().texOffs(42, 18).addBox(-4.0F, -8.0F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(38, 25).addBox(-4.0F, -11.9F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(44, 12).addBox(4.0F, -11.9F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(44, 6).addBox(4.0F, -8.0F, -1.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 7.0F, 0.2F));

        straps.addOrReplaceChild("straps_left_r1", CubeListBuilder.create().texOffs(4, 46).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 46).addBox(-7.3F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9F, -13.1F, 3.4F, 0.0175F, 0.0F, 0.0F));

        PartDefinition topCover = backpack.addOrReplaceChild("top_cover", CubeListBuilder.create(), PartPose.offset(0.5F, -25.9F, 0.6F));

        topCover.addOrReplaceChild("cover_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-4.5F, -1.5F, -2.5F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -1.2F, 0.8F, 0.1571F, 0.0F, 0.0F));

        topCover.addOrReplaceChild("brand_r1", CubeListBuilder.create().texOffs(28, 25).addBox(-1.5F, -1.5F, 2.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1571F, 0.0F, 0.0F));

        PartDefinition shoulderStraps = backpack.addOrReplaceChild("shoulder_straps", CubeListBuilder.create().texOffs(38, 31).addBox(-7.5F, -5.5F, -2.4F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.9F, -17.3F, -3.5F));

        PartDefinition leftStraps = shoulderStraps.addOrReplaceChild("left_straps", CubeListBuilder.create().texOffs(44, 33).addBox(-0.5F, -6.5F, -2.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 43).addBox(-0.5F, -7.5F, -2.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftStraps.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(28, 18).addBox(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition rightStraps = shoulderStraps.addOrReplaceChild("right_straps", CubeListBuilder.create().texOffs(0, 46).addBox(-0.5F, -6.5F, -2.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 43).addBox(-0.5F, -7.5F, -2.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.8F, 0.0F, 0.0F));

        rightStraps.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(24, 27).addBox(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

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
