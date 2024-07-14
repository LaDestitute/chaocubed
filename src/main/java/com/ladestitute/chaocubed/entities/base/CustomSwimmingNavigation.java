package com.ladestitute.chaocubed.entities.base;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;

public class CustomSwimmingNavigation extends WaterBoundPathNavigation {

    public CustomSwimmingNavigation(Mob mob, Level world) {
        super(mob, world);
    }

    @Override
    protected boolean canUpdatePath() {
        return true;
    }
}
