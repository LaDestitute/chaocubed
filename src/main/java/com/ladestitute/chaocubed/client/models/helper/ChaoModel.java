package com.ladestitute.chaocubed.client.models.helper;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public abstract class ChaoModel<T extends Entity> extends EntityModel<T> {
    // Boilerplate chao model class that all chao inherit
    // Rendering to buffer, animation setup using setup-anim and body layer creation is done in each custom chao model class

    protected final ModelPart sphere;
    protected final ModelPart heart;
    protected final ModelPart question;
    protected final ModelPart spiral;
    protected final ModelPart exclamation;
    protected final ModelPart chest;
    protected final ModelPart right_arm;
    protected final ModelPart left_arm;
    protected final ModelPart left_foot;
    protected final ModelPart tail_tip;
    protected final ModelPart right_wing;
    protected final ModelPart tail_base;
    protected final ModelPart left_wing;
    protected final ModelPart right_foot;
    protected final ModelPart head;

    public ChaoModel(ModelPart root) {
        this.sphere = root.getChild("sphere");
        this.heart = root.getChild("heart");
        this.question = root.getChild("question");
        this.spiral = root.getChild("spiral");
        this.exclamation = root.getChild("exclamation");
        this.chest = root.getChild("chest");
        this.right_arm = root.getChild("right_arm");
        this.left_arm = root.getChild("left_arm");
        this.left_foot = root.getChild("left_foot");
        this.tail_tip = root.getChild("tail_tip");
        this.right_wing = root.getChild("right_wing");
        this.tail_base = root.getChild("tail_base");
        this.left_wing = root.getChild("left_wing");
        this.right_foot = root.getChild("right_foot");
        this.head = root.getChild("head");

        //Todo: implement petting and thus heart emote balls
        //Todo: implement Chao noticing points of interest (fruit blocks via a radius?) for exclamation emote balls
        //Todo: implement loud player whistling for exclamation emote ball and if the tamed chao isn't sitting, have them walk to the player
        //Todo: for question emote balls, what stimuli/interaction triggers?
        this.sphere.visible = true;
        this.heart.visible = false;
        this.exclamation.visible = false;
        this.question.visible = false;
        this.spiral.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition sphere = partdefinition.addOrReplaceChild("sphere", CubeListBuilder.create().texOffs(27, 6).addBox(-1.0F, -27.0F, 0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition heart = partdefinition.addOrReplaceChild("heart", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition emotiball_r1 = heart.addOrReplaceChild("emotiball_r1", CubeListBuilder.create().texOffs(27, 4).addBox(10.3F, -4.15F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -36.0F, 1.75F, 0.0F, 0.0F, 0.829F));

        PartDefinition emotiball_r2 = heart.addOrReplaceChild("emotiball_r2", CubeListBuilder.create().texOffs(27, 6).addBox(8.2F, -1.7F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -36.0F, 1.75F, 0.0F, 0.0F, 0.829F));

        PartDefinition emotiball_r3 = heart.addOrReplaceChild("emotiball_r3", CubeListBuilder.create().texOffs(27, 4).addBox(8.0F, -4.0F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, -33.0F, 1.75F, 0.0F, 0.0F, 0.829F));

        PartDefinition question = partdefinition.addOrReplaceChild("question", CubeListBuilder.create().texOffs(27, 6).addBox(-1.0F, -27.0F, 0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 4).addBox(-1.0F, -33.0F, 0.75F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 6).addBox(-3.0F, -37.0F, 0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 6).addBox(-3.0F, -39.0F, 0.75F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 0).addBox(1.0F, -39.0F, 0.75F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition spiral = partdefinition.addOrReplaceChild("spiral", CubeListBuilder.create().texOffs(27, 6).addBox(-1.0F, -27.0F, 0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 6).addBox(-1.0F, -27.0F, 0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 4).addBox(1.0F, -29.0F, 2.75F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(27, 6).addBox(-3.0F, -31.0F, 4.75F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 4).addBox(-3.0F, -33.0F, 0.75F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(27, 6).addBox(-1.0F, -35.0F, 0.75F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 6).addBox(1.0F, -37.0F, 2.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 6).addBox(-1.0F, -37.0F, 4.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition exclamation = partdefinition.addOrReplaceChild("exclamation", CubeListBuilder.create().texOffs(27, 6).addBox(-1.0F, -27.0F, 0.75F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(27, 2).addBox(-1.0F, -35.0F, 0.75F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition chest = partdefinition.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(8, 24).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 0.75F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 15).addBox(-5.0F, -3.3F, 0.25F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-8.0F, -3.3F, -0.75F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 15).addBox(3.0F, -3.3F, 0.25F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(13, 18).addBox(5.0F, -3.3F, -0.75F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition left_foot = partdefinition.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(13, 5).addBox(1.0F, -3.0F, -3.25F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition tail_tip = partdefinition.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(0, 33).addBox(-0.5F, -6.0F, 5.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition right_wing = partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition right_r1 = right_wing.addOrReplaceChild("right_r1", CubeListBuilder.create().texOffs(44, 9).addBox(1.0F, -2.0F, -0.25F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -6.0F, 3.75F, 0.0F, -0.3491F, 0.0F));

        PartDefinition tail_base = partdefinition.addOrReplaceChild("tail_base", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = tail_base.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(27, 5).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 2.75F, -0.48F, 0.0F, 0.0F));

        PartDefinition left_wing = partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition left_r1 = left_wing.addOrReplaceChild("left_r1", CubeListBuilder.create().texOffs(44, 9).addBox(1.0F, -2.0F, -0.75F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -6.0F, 4.75F, 0.0F, 0.48F, 0.0F));

        PartDefinition right_foot = partdefinition.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(0, 8).addBox(-4.0F, -3.0F, -3.25F, 3.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 19).addBox(-5.0F, -11.5F, -2.25F, 10.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.5F, 0.0F));

        PartDefinition headpoint_r1 = head.addOrReplaceChild("headpoint_r1", CubeListBuilder.create().texOffs(48, 12).addBox(-3.0F, -3.0F, -1.1F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -10.5F, 3.75F, 0.9163F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 35);
    }

    //Reset bone positions to ensure the chao animates properly for each animation switch
    public void resetAnimations() {
        this.head.xRot = 0.0F;
        this.chest.xRot = 0.0F;
        this.left_arm.xRot = 0.0F;
        this.right_arm.xRot = 0.0F;
        this.left_foot.xRot = 0.0F;
        this.right_foot.xRot = 0.0F;
    }

    //Helper method to set visible of emoteballs
    public void setEmoteballVisibility(String type) {
        this.sphere.visible = type.equals("sphere");
        this.heart.visible = type.equals("heart");
        this.exclamation.visible = type.equals("exclamation");
        this.question.visible = type.equals("question");
        this.spiral.visible = type.equals("spiral");
    }

    // Animation methods
    public void setCrawlAnimation(float limbSwing, float limbSwingAmount) {
        this.head.xRot = 0.7854F; // 78 degrees to simulate head tilt
        this.chest.xRot = 1.5708F; // 90 degrees to simulate horizontal position
        this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        this.left_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * -1.8584f * limbSwingAmount;
        this.right_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * -1.8584f * limbSwingAmount;
    }

    public void setWalkAnimation(float limbSwing, float limbSwingAmount) {
        this.head.xRot = 0.0F;
        this.chest.xRot = 0.0F;
        this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
        this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.left_foot.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.right_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
    }

    public void setSwimAnimation1(float limbSwing, float limbSwingAmount) {
        this.head.xRot = 0.7854F; // 78 degrees to simulate head tilt
        this.chest.xRot = 1.5708F; // 90 degrees to simulate horizontal position
        this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        this.left_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * -1.8584f * limbSwingAmount;
        this.right_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * -1.8584f * limbSwingAmount;
    }

    // Unused second swim animation, yet to be tweaked
    public void setSwimAnimation2(float limbSwing, float limbSwingAmount) {
        this.head.xRot = 0.7854F; // 78 degrees to simulate head tilt
        this.chest.xRot = 1.5708F; // 90 degrees to simulate horizontal position
        this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 0.6F * limbSwingAmount;
        this.left_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * -1.8584f * limbSwingAmount;
        this.right_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * -1.8584f * limbSwingAmount;
    }

    // Struggling swim animation
    public void setStruggleSwimAnimation(float limbSwing, float limbSwingAmount) {
        this.head.xRot = 0F;
        this.chest.xRot = 0F;
        this.left_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.8F * limbSwingAmount;
        this.right_arm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.8F * limbSwingAmount;
        this.left_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * -1.8584f * limbSwingAmount;
        this.right_foot.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * -1.8584f * limbSwingAmount;
    }

    // Unused water tread animation, yet to be implemented
    public void setTreadWaterAnimation() {
        this.head.xRot = 0F; // 90 degrees to simulate horizontal position
        this.chest.xRot = 0F; // 90 degrees to simulate horizontal position
        this.left_arm.xRot = Mth.cos(2.0F) * 0.2F * 0.5F;
        this.right_arm.xRot = Mth.cos(2.0F) * 0.2F * 0.5F;
        this.left_foot.xRot = Mth.cos(2.0F) * 0.2F * 0.5F;
        this.right_foot.xRot = Mth.cos(2.0F) * 0.2F * 0.5F;
    }

}
