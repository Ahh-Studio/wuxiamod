package com.aiden.wuxia.mixin;

import com.aiden.wuxia.dimension.JianghuOverworldTerrain;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

/**
 * 拦截 NoiseBasedChunkGenerator 的关键生成方法，当区块距离出生点2000格以内时
 * 委托给使用原版 overworld NoiseGeneratorSettings 的生成器实例，
 * 从而在出生点附近生成原版主世界风格的地形。
 *
 * <p>注意：仅对江湖维度的生成器生效；overworld 生成器本身被调用时会跳过，避免无限递归。
 */
@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {
    @Inject(method = "fillFromNoise", at = @At("HEAD"), cancellable = true)
    private void wuxia$fillFromNoise(Blender blender, RandomState randomState, StructureManager structureManager,
                                     ChunkAccess centerChunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        if (wuxia$shouldRedirect(centerChunk.getPos())) {
            cir.setReturnValue(JianghuOverworldTerrain.getOverworldGenerator()
                    .fillFromNoise(blender, JianghuOverworldTerrain.getOverworldRandomState(), structureManager, centerChunk));
        }
    }

    @Inject(method = "createBiomes", at = @At("HEAD"), cancellable = true)
    private void wuxia$createBiomes(RandomState randomState, Blender blender, StructureManager structureManager,
                                    ChunkAccess protoChunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> cir) {
        if (wuxia$shouldRedirect(protoChunk.getPos())) {
            cir.setReturnValue(JianghuOverworldTerrain.getOverworldGenerator()
                    .createBiomes(JianghuOverworldTerrain.getOverworldRandomState(), blender, structureManager, protoChunk));
        }
    }

    @Inject(method = "buildSurface*", at = @At("HEAD"), cancellable = true)
    private void wuxia$buildSurface(WorldGenRegion region, StructureManager structureManager, RandomState randomState,
                                    ChunkAccess protoChunk, CallbackInfo ci) {
        if (wuxia$shouldRedirect(protoChunk.getPos())) {
            JianghuOverworldTerrain.getOverworldGenerator()
                    .buildSurface(region, structureManager, JianghuOverworldTerrain.getOverworldRandomState(), protoChunk);
            ci.cancel();
        }
    }

    @Inject(method = "applyCarvers", at = @At("HEAD"), cancellable = true)
    private void wuxia$applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager,
                                    StructureManager structureManager, ChunkAccess chunk, CallbackInfo ci) {
        if (wuxia$shouldRedirect(chunk.getPos())) {
            JianghuOverworldTerrain.getOverworldGenerator()
                    .applyCarvers(region, seed, JianghuOverworldTerrain.getOverworldRandomState(), biomeManager, structureManager, chunk);
            ci.cancel();
        }
    }

    @Inject(method = "getBaseHeight", at = @At("HEAD"), cancellable = true)
    private void wuxia$getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor heightAccessor,
                                     RandomState randomState, CallbackInfoReturnable<Integer> cir) {
        if (wuxia$shouldRedirect(x, z)) {
            cir.setReturnValue(JianghuOverworldTerrain.getOverworldGenerator()
                    .getBaseHeight(x, z, type, heightAccessor, JianghuOverworldTerrain.getOverworldRandomState()));
        }
    }

    @Inject(method = "getBaseColumn", at = @At("HEAD"), cancellable = true)
    private void wuxia$getBaseColumn(int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState,
                                     CallbackInfoReturnable<NoiseColumn> cir) {
        if (wuxia$shouldRedirect(x, z)) {
            cir.setReturnValue(JianghuOverworldTerrain.getOverworldGenerator()
                    .getBaseColumn(x, z, heightAccessor, JianghuOverworldTerrain.getOverworldRandomState()));
        }
    }

    @Unique
    private boolean wuxia$shouldRedirect(ChunkPos pos) {
        NoiseBasedChunkGenerator self = (NoiseBasedChunkGenerator) (Object) this;
        return JianghuOverworldTerrain.isInitialized()
                && self != JianghuOverworldTerrain.getOverworldGenerator()
                && JianghuOverworldTerrain.shouldUseOverworld(pos);
    }

    @Unique
    private boolean wuxia$shouldRedirect(int blockX, int blockZ) {
        NoiseBasedChunkGenerator self = (NoiseBasedChunkGenerator) (Object) this;
        return JianghuOverworldTerrain.isInitialized()
                && self != JianghuOverworldTerrain.getOverworldGenerator()
                && JianghuOverworldTerrain.shouldUseOverworld(blockX, blockZ);
    }
}
