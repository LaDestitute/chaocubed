package com.ladestitute.chaocubed.registry;

import com.ladestitute.chaocubed.ChaoCubedMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ChaoCubedItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ChaoCubedMain.MODID);

    public static final DeferredHolder<Item, DeferredSpawnEggItem> NEUTRAL_CHAO_SPAWN_EGG = ITEMS.register("neutral_chao_spawn_egg",
            () -> new DeferredSpawnEggItem(ChaoCubedEntityTypes.NEUTRAL_CHAO, 16645629, 16645629, new Item.Properties()));
}
