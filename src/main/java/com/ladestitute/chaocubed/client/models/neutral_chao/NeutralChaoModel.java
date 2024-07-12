package com.ladestitute.chaocubed.client.models.neutral_chao;

import com.ladestitute.chaocubed.client.models.helper.ChaoModel;
import com.ladestitute.chaocubed.entities.chao.NeutralChaoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;

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
            if (entity.swim_points >= 100) {

                    setSwimAnimation1(limbSwing, limbSwingAmount);

            } else {
                setStruggleSwimAnimation(limbSwing, limbSwingAmount);
            }
        } else {
            if(entity.run_points >= 50) {
                setWalkAnimation(limbSwing, limbSwingAmount);
            }
            else setCrawlAnimation(limbSwing, limbSwingAmount);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
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
}
