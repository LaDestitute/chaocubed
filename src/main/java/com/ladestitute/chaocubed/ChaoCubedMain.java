package com.ladestitute.chaocubed;

import com.ladestitute.chaocubed.entities.chao.NeutralChaoEntity;
import com.ladestitute.chaocubed.registry.ChaoCubedBiomeModifiers;
import com.ladestitute.chaocubed.registry.ChaoCubedEntityTypes;
import com.ladestitute.chaocubed.registry.ChaoCubedItems;
import com.mojang.logging.LogUtils;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ChaoCubedMain.MODID)
public class ChaoCubedMain
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "chaocubed";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    //Required due to using a parent abstract superclass for the models
    public static final ModelLayerLocation NEUTRAL_CHAO_LAYER = new ModelLayerLocation(new ResourceLocation("chaocubed", "neutral_chao"), "main");
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CHAOCUBED_EGGS = CREATIVE_MODE_TABS.register("chaocubed_eggs", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.chaocubed_eggs")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ChaoCubedItems.NEUTRAL_CHAO_SPAWN_EGG.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ChaoCubedItems.NEUTRAL_CHAO_SPAWN_EGG.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ChaoCubedMain(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ChaoCubedConfig.SPEC);

        ChaoCubedItems.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ChaoCubedEntityTypes.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::addAttributes);
        ChaoCubedBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }

    @SubscribeEvent
    private void addAttributes(final EntityAttributeCreationEvent event) {
        event.put(ChaoCubedEntityTypes.NEUTRAL_CHAO.get(), NeutralChaoEntity.createAttributes().build());
    }
}
