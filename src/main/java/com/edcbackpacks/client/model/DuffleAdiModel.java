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

public class DuffleAdiModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(EdcBackpacks.MOD_ID, "duffle_adi"), "main");

    private final ModelPart backpack;

    public DuffleAdiModel(ModelPart root) {
        this.backpack = root.getChild("backpack");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition backpack = partdefinition.addOrReplaceChild("backpack", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.125F, 9.975F, 2.025F, 0.13094F, -0.02596F, 0.12748F));

        PartDefinition pouches = backpack.addOrReplaceChild("pouches", CubeListBuilder.create()
                .texOffs(0, 40).addBox(10.9F, -3.2F, -3.7F, 1F, 3F, 3F, new CubeDeformation(0.0F)), PartPose.offset(-3.375F, -0.075F, 1.975F));

        pouches.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                .texOffs(32, 33).addBox(-1F, -3F, -4F, 1F, 3F, 5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, -0.04363F));

        pouches.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                .texOffs(16, 40).addBox(0F, -3F, -1F, 2F, 3F, 1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(9.9F, -0.4F, 0.7F, 0F, 0F, 0.08727F));

        PartDefinition straps = backpack.addOrReplaceChild("straps", CubeListBuilder.create()
                .texOffs(8, 40).addBox(-3F, -4F, 4F, 1F, 4F, 1F, new CubeDeformation(0.0F))
                .texOffs(26, 40).addBox(3F, -4F, 4F, 1F, 4F, 1F, new CubeDeformation(0.0F))
                .texOffs(40, 31).addBox(3F, -5.5F, 2.6F, 1F, 1F, 1F, new CubeDeformation(0.0F))
                .texOffs(34, 41).addBox(3F, -5.5F, 0.2F, 1F, 1F, 2F, new CubeDeformation(0.0F))
                .texOffs(28, 21).addBox(-3F, -5.5F, 0.2F, 1F, 1F, 2F, new CubeDeformation(0.0F))
                .texOffs(36, 31).addBox(-3F, -5.5F, 2.6F, 1F, 1F, 1F, new CubeDeformation(0.0F))
                .texOffs(18, 33).addBox(3F, -0.9F, -1.1F, 1F, 1F, 6F, new CubeDeformation(0.0F))
                .texOffs(28, 14).addBox(-3F, -0.9F, -1.1F, 1F, 1F, 6F, new CubeDeformation(0.0F))
                .texOffs(22, 40).addBox(-3F, -4F, -1.2F, 1F, 4F, 1F, new CubeDeformation(0.0F))
                .texOffs(30, 41).addBox(3F, -4F, -1.2F, 1F, 4F, 1F, new CubeDeformation(0.0F)), PartPose.offset(1.525F, 0.225F, -1.425F));

        straps.addOrReplaceChild("cube_r12", CubeListBuilder.create()
                .texOffs(42, 6).addBox(-1F, -2F, -1F, 1F, 2F, 1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2F, -4F, 5F, 0.7854F, 0F, 0F));

        straps.addOrReplaceChild("cube_r22", CubeListBuilder.create()
                .texOffs(12, 42).addBox(-1F, -2F, -1F, 1F, 2F, 1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4F, -4F, 5F, 0.7854F, 0F, 0F));

        straps.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                .texOffs(42, 14).addBox(-0.5F, -2F, 0F, 1F, 2F, 1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -4F, -1.2F, -0.7854F, 0F, 0F));

        straps.addOrReplaceChild("cube_r4", CubeListBuilder.create()
                .texOffs(42, 3).addBox(-0.5F, -2F, 0F, 1F, 2F, 1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -4F, -1.2F, -0.7854F, 0F, 0F));

        PartDefinition details = backpack.addOrReplaceChild("details", CubeListBuilder.create()
                .texOffs(40, 41).addBox(-4F, -4F, -1F, 1F, 2F, 1F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(7.4F, -4F, -1F, 1F, 2F, 1F, new CubeDeformation(0.0F)), PartPose.offset(-0.175F, -0.375F, 0.975F));

        details.addOrReplaceChild("cube_r13", CubeListBuilder.create()
                .texOffs(28, 12).addBox(-5F, -0.5F, -0.5F, 10F, 1F, 1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.2F, -4.4F, 0F, -0.7854F, 0F, 0F));

        PartDefinition core = backpack.addOrReplaceChild("core", CubeListBuilder.create()
                .texOffs(0, 24).addBox(-6.1F, -4F, -1F, 3F, 4F, 6F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3F, -4F, -1F, 6F, 4F, 6F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(3.1F, -4F, -1F, 3F, 4F, 6F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(-6F, -5.4F, 1.6F, 12F, 2F, 2F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-6F, -5.4F, 0.4F, 12F, 2F, 2F, new CubeDeformation(0.0F)), PartPose.offset(2.025F, 0.225F, -1.525F));

        core.addOrReplaceChild("cube_r14", CubeListBuilder.create()
                .texOffs(36, 21).addBox(0F, -1F, -2F, 1F, 1F, 4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.1F, -4F, 2F, 0F, 0F, 0.10472F));

        core.addOrReplaceChild("cube_r23", CubeListBuilder.create()
                .texOffs(36, 26).addBox(-1F, -1F, -2F, 1F, 1F, 4F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.1F, -4F, 2F, 0F, 0F, -0.10472F));

        core.addOrReplaceChild("cube_r32", CubeListBuilder.create()
                .texOffs(0, 21).addBox(-8F, 0F, -2F, 12F, 1F, 2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2F, -4F, 5F, -0.7854F, 0F, 0F));

        core.addOrReplaceChild("cube_r42", CubeListBuilder.create()
                .texOffs(0, 18).addBox(-6F, 0F, 0F, 12F, 1F, 2F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0F, -4F, -1F, 0.7854F, 0F, 0F));

        PartDefinition shoulder_straps = backpack.addOrReplaceChild("shoulder_straps", CubeListBuilder.create(), PartPose.offset(2.725F, 14.025F, -2.025F));

        shoulder_straps.addOrReplaceChild("cube_r15", CubeListBuilder.create()
                .texOffs(24, 56).addBox(-1F, -7F, 0F, 1F, 7F, 1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4F, -18F, 1.5F, 0.4303F, -0.06851F, 0.21508F));

        shoulder_straps.addOrReplaceChild("cube_r24", CubeListBuilder.create()
                .texOffs(12, 56).addBox(0F, -2F, -4F, 1F, 1F, 5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.9F, -22.8F, -1.4F, 0F, -0.1309F, 0F));

        shoulder_straps.addOrReplaceChild("cube_r33", CubeListBuilder.create()
                .texOffs(40, 61).addBox(-5.5F, -0.5F, -0.5F, 11F, 1F, 1F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.36391F, -21.50385F, -5.1F, 0F, 0.03491F, 0.56723F));

        shoulder_straps.addOrReplaceChild("cube_r43", CubeListBuilder.create()
                .texOffs(31, 49).addBox(0F, -1F, -8F, 1F, 1F, 8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.2F, -16.3F, 2F, -0.23708F, 0.11947F, -0.02567F));

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
