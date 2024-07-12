package com.ladestitute.chaocubed;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = ChaoCubedMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChaoCubedConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue CAN_ALREADY_SWIM = BUILDER
            .comment("Whether wild chao will spawn with 100 swim points and thus, be able to swim and not struggle-swim")
            .define("can_already_swim", false);

    private static final ModConfigSpec.IntValue MAX_WILD_CHAO_GROUP_SIZE = BUILDER
            .comment("The maximum size for groups for wild Chao to spawn in")
            .defineInRange("max_wild_chao_group_size", 8, 4, 16);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean can_already_swim;
    public static int maxChaoGroupSize;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        can_already_swim = CAN_ALREADY_SWIM.get();
        maxChaoGroupSize = MAX_WILD_CHAO_GROUP_SIZE.get();
    }
}
