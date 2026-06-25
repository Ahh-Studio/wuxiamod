package com.aiden.wuxia.block;

import com.aiden.wuxia.block.entity.EndReactorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class EndReactor extends BaseEntityBlock {
    public static final MapCodec<EndReactor> CODEC = simpleCodec(EndReactor::new);
    public static final VoxelShape SHAPE = Block.column(16.0, 0.0, 12.0);

    public EndReactor(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof EndReactorBlockEntity endReactorBlockEntity) {
            player.openMenu(endReactorBlockEntity);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return (level1, pos, state, entity) -> EndReactorBlockEntity.tick(level1, pos, state, (EndReactorBlockEntity) entity);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<EndReactor> codec() {
        return CODEC;
    }

    @Override
    public @Nullable EndReactorBlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new EndReactorBlockEntity(worldPosition, blockState);
    }
}
