package com.ladestitute.chaocubed.client;

import com.ladestitute.chaocubed.ChaoCubedMain;
import com.ladestitute.chaocubed.client.models.neutral_chao.NeutralChaoModel;
import com.ladestitute.chaocubed.client.renderer.NeutralChaoRenderer;
import com.ladestitute.chaocubed.registry.ChaoCubedEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChaoCubedClientEBS {

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ChaoCubedEntityTypes.NEUTRAL_CHAO.get(), NeutralChaoRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ChaoCubedMain.NEUTRAL_CHAO_LAYER, NeutralChaoModel::createBodyLayer);
        }

}
