package com.ladestitute.chaocubed.entities.base;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import com.google.common.collect.ImmutableMap;

public class ChaoSitWhenOrderedBehavior extends Behavior<Mob> {
    private boolean isSitting;

    public ChaoSitWhenOrderedBehavior() {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    public void setSitting(boolean sitting) {
        this.isSitting = sitting;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel world, Mob entity) {
        return isSitting;
    }

    @Override
    protected void start(ServerLevel world, Mob entity, long gameTime) {
        entity.getNavigation().stop();
    }

    @Override
    protected boolean canStillUse(ServerLevel world, Mob entity, long gameTime) {
        return isSitting;
    }

    @Override
    protected void stop(ServerLevel world, Mob entity, long gameTime) {
        this.isSitting = false;
    }
}
