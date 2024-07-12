package com.ladestitute.chaocubed.world.biome;

import com.ladestitute.chaocubed.registry.ChaoCubedBiomeModifiers;
import com.ladestitute.chaocubed.registry.ChaoCubedEntityTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

import java.util.Random;

public class ChaoBiomeModifier implements BiomeModifier {
    public static final ChaoBiomeModifier INSTANCE = new ChaoBiomeModifier();


    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        Random random = new Random();

        if (phase == Phase.ADD) {
            if (biome.is(BiomeTags.IS_OVERWORLD) && !biome.is(Biomes.DEEP_DARK) && !biome.is(Tags.Biomes.IS_VOID)) {
                float biomeTemperature = biome.value().getBaseTemperature();
                if (biomeTemperature < 0.2f || biomeTemperature > 1.0f || biome.is(Biomes.OCEAN) || biome.is(Biomes.DEEP_OCEAN)) {
                    return;
                }

//                Level level = EnvironmentContext.getCurrentLevel();
//                BlockPos origin = EnvironmentContext.getCurrentPosition();
//
//                if (level == null || origin == null || !scanNearbyChunksForSmallWater(level, origin, 2)) {
//                    return;
//                }

                if (biome.is(Biomes.MANGROVE_SWAMP) || biome.is(Biomes.SWAMP) || biome.is(Biomes.SPARSE_JUNGLE) ||
                        biome.is(Biomes.SUNFLOWER_PLAINS) || biome.is(Biomes.PLAINS) || biome.is(Biomes.MEADOW)) {

                    if (random.nextInt(6) == 0) {
                        builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(
                                new MobSpawnSettings.SpawnerData(ChaoCubedEntityTypes.NEUTRAL_CHAO.get(), 15, 4, 8)
                        );
                    } else {
                        builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(
                                new MobSpawnSettings.SpawnerData(ChaoCubedEntityTypes.NEUTRAL_CHAO.get(), 15, 2, 4)
                        );
                    }
                }

                if (biome.is(Biomes.RIVER)) {
                    if (random.nextInt(6) == 0) {
                        builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(
                                new MobSpawnSettings.SpawnerData(ChaoCubedEntityTypes.NEUTRAL_CHAO.get(), 7, 4, 8)
                        );
                    } else {
                        builder.getMobSpawnSettings().getSpawner(MobCategory.CREATURE).add(
                                new MobSpawnSettings.SpawnerData(ChaoCubedEntityTypes.NEUTRAL_CHAO.get(), 7, 2, 4)
                        );
                    }
                }
            }
        }
    }


    public static boolean scanNearbyChunksForSmallWater(Level level, BlockPos origin, int radius) {
        ChunkPos originChunkPos = new ChunkPos(origin);

        // Iterate through chunks
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                ChunkPos chunkPos = new ChunkPos(originChunkPos.x + x, originChunkPos.z + z);
                LevelChunk chunk = level.getChunk(chunkPos.x, chunkPos.z);

                if (chunkContainsBodiesofWater(chunk)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean chunkContainsBodiesofWater(LevelChunk chunk) {
        int waterBlockCount = 0;
        int maxWaterBlocksForSmallBody = 50; // Needs adjustment, will take 25ish samples to build a moderate size

        for (BlockPos pos : chunk.getBlockEntitiesPos()) {
            if (chunk.getBlockState(pos).is(Blocks.WATER)) {
                waterBlockCount++;
                // If too many water blocks, it's not a small water body
                if (waterBlockCount > maxWaterBlocksForSmallBody) {
                    return false;
                }
            }
        }

        // Return true if there are water blocks and they are below the threshold
        return waterBlockCount > 0 && waterBlockCount <= maxWaterBlocksForSmallBody;
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ChaoCubedBiomeModifiers.CHAO_ENTITY_MODIFIER_TYPE.get();
    }

}
