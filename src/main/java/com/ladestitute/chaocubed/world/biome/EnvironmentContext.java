package com.ladestitute.chaocubed.world.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class EnvironmentContext {
    private static Level currentLevel;
    private static BlockPos currentPosition;

    public static void setContext(Level level, BlockPos position) {
        currentLevel = level;
        currentPosition = position;
    }

    public static Level getCurrentLevel() {
        return currentLevel;
    }

    public static BlockPos getCurrentPosition() {
        return currentPosition;
    }
}
