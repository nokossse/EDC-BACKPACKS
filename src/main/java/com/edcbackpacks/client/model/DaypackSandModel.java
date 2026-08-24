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

public class DaypackSandModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(EdcBackpacks.MOD_ID, "daypack_sand"), "main");

    private final ModelPart backpack;

    public DaypackSandModel(ModelPart root) {
        this.backpack = root.getChild("backpack");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition backpack = partdefinition.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offset(0.0F, 23.5F, 0.0F));

        backpack.addOrReplaceChild("back_padding", CubeListBuilder.create().texOffs(21, 22).addBox(-3.0F, -5.0F, -1.0F, 3.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(3, 29).addBox(1.0F, -5.0F, -1.0F, 3.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -17.5F, -0.2F));

        PartDefinition core = backpack.addOrReplaceChild("core", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -1.0F, 9.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 18).addBox(0.3F, 0.7F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 23).addBox(-3.3F, 0.7F, -1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(-2.5F, -8.7F, -0.9F, 6.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, -16.0F, 0.0F));

        core.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(20, 10).addBox(-3.0F, -4.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -5.0F, 1.0F, 0.0F, 0.0F, -0.3752F));

        core.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(26, 0).addBox(0.0F, -4.0F, -2.0F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -5.0F, 1.0F, 0.0F, 0.0F, 0.3752F));

        core.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 0).addBox(0.0F, -1.0F, -1.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        core.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(34, 36).addBox(-1.0F, -1.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 1.0F, 1.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition sideAtt = backpack.addOrReplaceChild("side_att", CubeListBuilder.create().texOffs(12, 40).addBox(-10.0F, -1.9F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(4.5F, -17.4F, 1.0F));

        sideAtt.addOrReplaceChild("left_r1", CubeListBuilder.create().texOffs(34, 41).addBox(-0.5F, -2.0F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0698F));

        PartDefinition frontAtt = backpack.addOrReplaceChild("front_att", CubeListBuilder.create(), PartPose.offset(-1.0F, -15.9F, 3.6F));

        frontAtt.addOrReplaceChild("patch_r1", CubeListBuilder.create().texOffs(0, 47).addBox(-1.5F, -0.5F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -7.1F, -0.1F, 0.0698F, 0.0F, 0.0F));

        frontAtt.addOrReplaceChild("top_r1", CubeListBuilder.create().texOffs(12, 36).addBox(-2.5F, -1.5F, -0.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -6.9F, -0.6F, 0.0698F, 0.0F, 0.0F));

        frontAtt.addOrReplaceChild("btn2_r1", CubeListBuilder.create().texOffs(24, 36).addBox(-1.0F, -4.0F, -1.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.1F, -0.0524F, 0.0F, 0.0F));

        frontAtt.addOrReplaceChild("btm_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-3.0F, -5.0F, -1.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.0349F, 0.0F, 0.0F));

        PartDefinition shoulderStraps = backpack.addOrReplaceChild("shoulder_straps", CubeListBuilder.create().texOffs(44, 30).addBox(-7.5F, -5.5F, -2.4F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(3.9F, -16.8F, -3.5F));

        PartDefinition leftStraps = shoulderStraps.addOrReplaceChild("left_straps", CubeListBuilder.create().texOffs(30, 56).addBox(-0.5F, -6.5F, -2.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 24).addBox(-0.5F, -7.5F, -2.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        leftStraps.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(10, 58).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.5F, 2.5F, 0.3316F, 0.0F, 0.0F));

        leftStraps.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(24, 45).addBox(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition rightStraps = shoulderStraps.addOrReplaceChild("right_straps", CubeListBuilder.create().texOffs(50, 57).addBox(-0.5F, -6.5F, -2.5F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 52).addBox(-0.5F, -7.5F, -2.5F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.8F, 0.0F, 0.0F));

        rightStraps.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(14, 58).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.5F, 2.5F, 0.3316F, 0.0F, 0.0F));

        rightStraps.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(38, 45).addBox(-0.5F, -0.5F, -2.5F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3927F, 0.0F, 0.0F));

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
