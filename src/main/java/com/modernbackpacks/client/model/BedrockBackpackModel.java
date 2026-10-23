package com.modernbackpacks.client.model;

import com.github.mcmodderanchor.simplebedrockmodel.v1.client.renderer.BedrockModelRenderTypes;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BakedModelInstance;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.runtime.BoneState;
import com.github.mcmodderanchor.simplebedrockmodel.v2.resource.BedrockModelResources;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

public final class BedrockBackpackModel implements BackpackModel {
    private static final String REFERENCE_ROOT = "steve";
    /** Bone swung by {@link #setStrapSway}; must be baked as a runtime bone (see ClientSetup). */
    public static final String STRAPS_BONE = "straps-rot";

    private final ResourceLocation modelId;
    private BakedBedrockModel baked;
    private BakedModelInstance instance;
    private float swayPitch;
    private float swayRoll;

    public BedrockBackpackModel(ResourceLocation modelId) {
        this.modelId = modelId;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, VertexConsumer cubeBuffer,
                       ResourceLocation texture, int light, int overlay) {
        BakedBedrockModel current = BedrockModelResources.getInstance().getBakedModel(this.modelId);
        if (current == null) {
            return;
        }
        if (current != this.baked) {
            this.baked = current;
            this.instance = current.createInstance();
            hideReferenceRig(this.instance);
        }

        BoneState straps = this.instance.getBone(STRAPS_BONE);
        if (straps != null) {
            straps.reset();
            straps.rotation.rotateX((float) Math.toRadians(this.swayPitch)).rotateZ((float) Math.toRadians(this.swayRoll));
        }

        poseStack.pushPose();
        // Bedrock (Y-up, feet origin) -> Java entity (Y-down, body pivot).
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        // Cubes: vanilla entity lighting. Meshes: same shader but TRIANGLES + vertex normals
        // from the geo (poly_mesh), otherwise the sides shade as flat/black faces.
        this.instance.renderToBuffer(poseStack, buffers,
                RenderType.entityCutoutNoCull(texture),
                BedrockModelRenderTypes.polyMeshCutout(texture),
                light, overlay);
        poseStack.popPose();
    }

    @Override
    public void setStrapSway(float pitchDeg, float rollDeg) {
        this.swayPitch = pitchDeg;
        this.swayRoll = rollDeg;
    }

    /** Blockbench exports a Steve mannequin; never draw it with the backpack atlas. */
    private static void hideReferenceRig(BakedModelInstance instance) {
        for (BoneState bone : instance.getBoneIndexes()) {
            if (isReferenceBone(instance, bone)) {
                bone.visible = false;
            }
        }
    }

    private static boolean isReferenceBone(BakedModelInstance instance, BoneState bone) {
        BoneState current = bone;
        while (current != null) {
            if (REFERENCE_ROOT.equals(current.name().toLowerCase(Locale.ROOT))) {
                return true;
            }
            int parent = current.parentIndex();
            if (parent < 0) {
                return false;
            }
            current = instance.getBone(parent);
        }
        return false;
    }
}
