package com.ladestitute.chaocubed.registry;

import com.ladestitute.chaocubed.ChaoCubedMain;
import com.ladestitute.chaocubed.world.biome.ChaoBiomeModifier;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ChaoCubedBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ChaoCubedMain.MODID);

    public static final Supplier<Codec<ChaoBiomeModifier>> CHAO_ENTITY_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("chao_entity_modifier", () -> Codec.unit(ChaoBiomeModifier.INSTANCE));
}