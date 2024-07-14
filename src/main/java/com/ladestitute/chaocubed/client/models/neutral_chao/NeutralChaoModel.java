package com.ladestitute.chaocubed.client.models.neutral_chao;

import com.ladestitute.chaocubed.client.models.helper.ChaoModel;
import com.ladestitute.chaocubed.entities.base.TestAmphiChaoEntity;
import com.ladestitute.chaocubed.entities.chao.NeutralChaoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;

public class NeutralChaoModel extends ChaoModel<NeutralChaoEntity> {

    public NeutralChaoModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        return ChaoModel.createBodyLayer();
    }

    @Override
    public void setupAnim(NeutralChaoEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        resetAnimations(); // Reset before applying new animations

        if (entity.isInWater())
        {
            //||entity.getDeltaMovement().lengthSqr() == 0
            if(entity.getDeltaMovement().lengthSqr() < 0.0001)
            {
                setTreadWaterAnimation(limbSwing, limbSwingAmount);
            }
            else if (entity.swim_points >= 100) {

                    setSwimAnimation1(limbSwing, limbSwingAmount);

            } else
            {
                setStruggleSwimAnimation(limbSwing, limbSwingAmount);
            }
        } else {
            if(entity.getEntityData().get(TestAmphiChaoEntity.RUN_POINTS) >= 50) {
                setWalkAnimation(limbSwing, limbSwingAmount);
            }
            else setCrawlAnimation(limbSwing, limbSwingAmount);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      //  int tickCount = (int) Minecraft.getInstance().level.getGameTime();

         //   applyEmoteballWobble(poseStack, tickCount);

        sphere.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

        heart.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        question.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        spiral.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        exclamation.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        chest.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_arm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_foot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        tail_tip.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        tail_base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        left_wing.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        right_foot.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void applyEmoteballWobble(PoseStack poseStack, int tickCount) {
        // Add wobble effect logic
        PoseStack spherewobble = poseStack;
        float wobbleAmount = Mth.sin((float) (tickCount + Minecraft.getInstance().getFrameTime()) / 10.0F) * 0.05F;
        spherewobble.translate(0.0D, wobbleAmount, 0.0D);
    }
}
