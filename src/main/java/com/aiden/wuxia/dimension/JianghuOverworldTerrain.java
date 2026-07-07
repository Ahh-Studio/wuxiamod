package com.aiden.wuxia.dimension;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;

/**
 * Holds the overworld NoiseBasedChunkGenerator and RandomState used for
 * generating vanilla overworld terrain near the jianghu dimension spawn point.
 */
public final class JianghuOverworldTerrain {

    public static final long RADIUS = 2000L;
    public static final long RADIUS_SQUARED = RADIUS * RADIUS;

    private static volatile NoiseBasedChunkGenerator overworldGenerator;
    private static volatile RandomState overworldRandomState;

    private JianghuOverworldTerrain() {
    }

    public static void initialize(BiomeSource biomeSource, long seed, HolderGetter.Provider registryAccess) {
        Holder<NoiseGeneratorSettings> overworldSettings = registryAccess
                .lookupOrThrow(Registries.NOISE_SETTINGS)
                .getOrThrow(NoiseGeneratorSettings.OVERWORLD);
        RandomState randomState = RandomState.create(registryAccess, NoiseGeneratorSettings.OVERWORLD, seed);
        NoiseBasedChunkGenerator generator = new NoiseBasedChunkGenerator(biomeSource, overworldSettings);
        overworldRandomState = randomState;
        overworldGenerator = generator;
    }

    public static NoiseBasedChunkGenerator getOverworldGenerator() {
        return overworldGenerator;
    }

    public static RandomState getOverworldRandomState() {
        return overworldRandomState;
    }

    public static boolean isInitialized() {
        return overworldGenerator != null;
    }

    public static boolean shouldUseOverworld(ChunkPos pos) {
        int centerX = pos.getMiddleBlockX();
        int centerZ = pos.getMiddleBlockZ();
        return (long) centerX * centerX + (long) centerZ * centerZ <= RADIUS_SQUARED;
    }

    public static boolean shouldUseOverworld(int blockX, int blockZ) {
        return shouldUseOverworld(new ChunkPos(blockX >> 4, blockZ >> 4));
    }
}
