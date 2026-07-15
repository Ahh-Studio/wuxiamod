package com.aiden.wuxia.block;

import com.aiden.wuxia.block.entity.JianghuPortalBlockEntity;
import com.aiden.wuxia.dimension.ModDimensions;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.EndPlatformFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class JianghuPortalBlock extends BaseEntityBlock implements Portal {
    public static final MapCodec<JianghuPortalBlock> CODEC = simpleCodec(JianghuPortalBlock::new);
    private static final VoxelShape SHAPE = Block.column(16.0, 6.0, 12.0);

    @Override
    public MapCodec<JianghuPortalBlock> codec() {
        return CODEC;
    }

    public JianghuPortalBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
        return new JianghuPortalBlockEntity(worldPosition, blockState);
    }

    @Override
    protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getEntityInsideCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final Entity entity) {
        return state.getShape(level, pos);
    }

    @Override
    protected void entityInside(
            final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise
    ) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }

        // 控制玩家的觉醒
        if (entity instanceof ServerPlayer serverPlayer) {
            PlayerMixinExtension playerExt = (PlayerMixinExtension) serverPlayer;
            if (!playerExt.wuxia$isAwakened()) playerExt.wuxia$setAwakened(true);
        }
    }

    @Nullable
    @Override
    public TeleportTransition getPortalDestination(final ServerLevel currentLevel, final Entity entity, final BlockPos portalEntryPos) {
        ResourceKey<Level> newDimension = ModDimensions.JIANGHU;
        BlockPos spawnBlockPos = new BlockPos(0, 320, 0);
        ServerLevel newLevel = currentLevel.getServer().getLevel(newDimension);
        if (newLevel == null) {
            return null;
        } else {
            Vec3 spawnPos = spawnBlockPos.getBottomCenter();
            float yRot, xRot;
            Set<Relative> relatives;

            EndPlatformFeature.createEndPlatform(newLevel, BlockPos.containing(spawnPos).below(), true);
            yRot = Direction.WEST.toYRot();
            xRot = 0.0F;
            relatives = Relative.union(Relative.DELTA, Set.of(Relative.X_ROT));
            if (entity instanceof ServerPlayer) {
                spawnPos = spawnPos.subtract(0.0, 1.0, 0.0);
            }

            return new TeleportTransition(
                    newLevel, spawnPos, Vec3.ZERO, yRot, xRot, relatives, TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET)
            );
        }
    }

    @Override
    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + 0.8;
        double z = pos.getZ() + random.nextDouble();
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
    }

    @Override
    protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean canBeReplaced(final BlockState state, final Fluid fluid) {
        return false;
    }

    @Override
    protected RenderShape getRenderShape(final BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
