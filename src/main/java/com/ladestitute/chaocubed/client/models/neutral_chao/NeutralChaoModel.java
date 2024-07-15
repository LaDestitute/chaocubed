package com.ladestitute.chaocubed.client.models.neutral_chao;

import com.ladestitute.chaocubed.client.models.helper.ChaoModel;
import com.ladestitute.chaocubed.entities.base.ChaoEntity;
import com.ladestitute.chaocubed.entities.chao.NeutralChaoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class NeutralChaoModel extends ChaoModel<NeutralChaoEntity> {

    public NeutralChaoModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return ChaoModel.createBodyLayer();
    }

   @Override
    public void setupAnim(NeutralChaoEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetAnimations();

        if (entity.isInWater()) {
            if (entity.getDeltaMovement().lengthSqr() == 0) {
                setTreadWaterAnimation(limbSwing, limbSwingAmount);
            } else if (entity.getEntityData().get(ChaoEntity.SWIM_POINTS) >= 50)
            {
                setSwimAnimation1(limbSwing, limbSwingAmount);

            }
            else {
                setStruggleSwimAnimation(limbSwing, limbSwingAmount);

            }
        }
       if (!entity.isInWater()) {
           if (entity.getEntityData().get(ChaoEntity.RUN_POINTS) >= 50) {
               setWalkAnimation(limbSwing, limbSwingAmount);
           } else {
               setCrawlAnimation(limbSwing, limbSwingAmount);
           }
       }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        int tickCount = (int) Minecraft.getInstance().level.getGameTime();

        // Save the current state of the pose stack
        poseStack.pushPose();

        // Apply wobble effect to the sphere part only
        applyEmoteballWobble(poseStack, tickCount);
        sphere.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        heart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        question.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        spiral.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        exclamation.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        // Restore the previous state of the pose stack
        poseStack.popPose();

        // Render the rest of the parts without wobble
        chest.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_foot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        tail_tip.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        // Apply wing animation for left wing
        poseStack.pushPose();
        applyWingAnimation(poseStack, tickCount, true);
        left_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();

        // Apply wing animation for right wing
        poseStack.pushPose();
        applyWingAnimation(poseStack, tickCount, false);
        right_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.popPose();

        tail_base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_foot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void applyEmoteballWobble(PoseStack poseStack, int tickCount) {
        float frameTime = Minecraft.getInstance().getFrameTime();
        float time = (float) (tickCount + frameTime) / 10.0F;

        // Use sin and cos to create a circular motion
        float wobbleX = Mth.sin(time) * 0.05F;
        float wobbleY = Mth.sin(time * 1.5F) * 0.075F; // Different frequency for variation
        float wobbleZ = Mth.cos(time) * 0.05F;

        poseStack.translate(wobbleX, wobbleY, wobbleZ);
    }

    private void applyWingAnimation(PoseStack poseStack, int tickCount, boolean isLeftWing) {
        float frameTime = Minecraft.getInstance().getFrameTime();
        float time = (float) (tickCount + frameTime) / 5.0F;

        // Wing flapping motion using sin wave
        float wingAngle = Mth.sin(time) * 5.0F; // Adjust the multiplier for the desired flapping intensity
        if (!isLeftWing) {
            wingAngle = -wingAngle; // Inverse the angle for the right wing
        }

        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.toRadians(wingAngle));
        poseStack.mulPose(rotation);
    }
}
