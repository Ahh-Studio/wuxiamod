package com.aiden.wuxia.block.entity;

import com.aiden.wuxia.block.JianghuPortalBlock;
import com.aiden.wuxia.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class JianghuPortalBlockEntity extends BlockEntity {
    public JianghuPortalBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    public JianghuPortalBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlocks.JIANGHU_PORTAL_BLOCK_ENTITY_TYPE, pos, state);
    }

    public boolean shouldRenderFace(final Direction direction) {
        return direction.getAxis() == Direction.Axis.Y;
    }
}
