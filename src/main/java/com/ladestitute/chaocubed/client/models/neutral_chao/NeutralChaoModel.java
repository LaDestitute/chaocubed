package com.ladestitute.chaocubed.client.models.neutral_chao;

import com.ladestitute.chaocubed.client.models.helper.ChaoModel;
import com.ladestitute.chaocubed.entities.base.ChaoEntity;
import com.ladestitute.chaocubed.entities.chao.NeutralChaoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class NeutralChaoModel extends ChaoModel<NeutralChaoEntity> {

    private long lastSwitchTime = 0;
    private boolean useSwimAnimation1 = true;

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
            } else if (entity.getEntityData().get(ChaoEntity.SWIM_POINTS) >= 50) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSwitchTime >= 7500) {
                    useSwimAnimation1 = !useSwimAnimation1;
                    lastSwitchTime = currentTime;
                }
                if (useSwimAnimation1) {
                    setSwimAnimation1(limbSwing, limbSwingAmount);
                } else {
                    setSwimAnimation2(limbSwing, limbSwingAmount);
                }
            } else {
                setStruggleSwimAnimation(limbSwing, limbSwingAmount);
            }
        } else if (entity.onGround()) {
            if (entity.getEntityData().get(ChaoEntity.RUN_POINTS) >= 50) {
                setWalkAnimation(limbSwing, limbSwingAmount);
            } else {
                setCrawlAnimation(limbSwing, limbSwingAmount);
            }
        } else if (entity.getFlyPoints() >= 200 && !isTGrass(entity.level(), entity.blockPosition()) && entity.getDeltaMovement().y < 0.5) {
            setFlyAnimation(limbSwing, limbSwingAmount);
        }
    }

    private static boolean isTGrass(Level world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return isGrass(blockState);
    }

    private static boolean isGrass(BlockState blockState) {
        return blockState.is(Blocks.TALL_GRASS) ||
                blockState.is(Blocks.SHORT_GRASS);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        int tickCount = (int) Minecraft.getInstance().level.getGameTime();
        poseStack.pushPose();

        applyEmoteballWobble(poseStack, tickCount);
        sphere.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        heart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        question.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        spiral.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        exclamation.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.popPose();

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
        float time = (tickCount + frameTime) / 10.0F;

        float wobbleX = Mth.sin(time) * 0.05F;
        float wobbleY = Mth.sin(time * 1.5F) * 0.075F;
        float wobbleZ = Mth.cos(time) * 0.05F;

        poseStack.translate(wobbleX, wobbleY, wobbleZ);
    }

    private void applyWingAnimation(PoseStack poseStack, int tickCount, boolean isLeftWing) {
        float frameTime = Minecraft.getInstance().getFrameTime();
        float time = (tickCount + frameTime) / 5.0F;

        float wingAngle = Mth.sin(time) * 5.0F;
        if (!isLeftWing) {
            wingAngle = -wingAngle;
        }

        Quaternionf rotation = new Quaternionf().rotateZ((float) Math.toRadians(wingAngle));
        poseStack.mulPose(rotation);
    }
}