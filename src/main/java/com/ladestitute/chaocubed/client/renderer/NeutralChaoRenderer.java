package com.ladestitute.chaocubed.client.renderer;

import com.ladestitute.chaocubed.ChaoCubedMain;
import com.ladestitute.chaocubed.client.models.helper.ChaoModel;
import com.ladestitute.chaocubed.client.models.neutral_chao.NeutralChaoModel;
import com.ladestitute.chaocubed.entities.chao.NeutralChaoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NeutralChaoRenderer extends MobRenderer<NeutralChaoEntity, ChaoModel<NeutralChaoEntity>> {
    private static final ResourceLocation NORMAL_TEXTURE = new ResourceLocation("chaocubed", "textures/entity/chao/neutral_chao.png");
    private static final ResourceLocation SHINY_TEXTURE = new ResourceLocation("chaocubed", "textures/entity/chao/shiny_neutral_chao.png");

    public NeutralChaoRenderer(EntityRendererProvider.Context context) {
        super(context, new NeutralChaoModel(context.bakeLayer(ChaoCubedMain.NEUTRAL_CHAO_LAYER)), 0.25f);
    }

    //Variant tests
    @Override
    public ResourceLocation getTextureLocation(NeutralChaoEntity entity) {
        return entity.getEntityData().get(NeutralChaoEntity.SHINY) ? SHINY_TEXTURE : NORMAL_TEXTURE;
    }

    //Scale to fit proper Chao size in the source material
    @Override
    protected void scale(NeutralChaoEntity p_115314_, PoseStack p_115315_, float p_115316_) {
        p_115315_.scale(0.48f, 0.48f, 0.48f);
        super.scale(p_115314_, p_115315_, p_115316_);
    }
}
