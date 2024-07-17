package com.ladestitute.chaocubed.registry;

import com.ladestitute.chaocubed.ChaoCubedMain;
import com.ladestitute.chaocubed.entities.chao.NeutralChaoEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ChaoCubedEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, ChaoCubedMain.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<NeutralChaoEntity>> NEUTRAL_CHAO = ENTITY_TYPES.register("neutral_chao", () ->
            EntityType.Builder.of(NeutralChaoEntity::new, MobCategory.CREATURE)
                    .sized(1F, 1F).setShouldReceiveVelocityUpdates(true)
                    .build(ChaoCubedMain.MODID + "neutral_chao"));
}
