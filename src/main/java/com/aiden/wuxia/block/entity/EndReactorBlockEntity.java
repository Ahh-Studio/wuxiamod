package com.aiden.wuxia.block.entity;

import com.aiden.wuxia.block.ImplementedContainer;
import com.aiden.wuxia.block.ModBlocks;
import com.aiden.wuxia.block.container_menu.EndReactorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class EndReactorBlockEntity extends BaseContainerBlockEntity implements ImplementedContainer {
    public static final int CONTAINER_SIZE = 9;
    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);

    public EndReactorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlocks.END_REACTOR_BLOCK_ENTITY_TYPE, worldPosition, blockState);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.wuxia.end_reactor");
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new EndReactorMenu(containerId, inventory, this);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, EndReactorBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            boolean bl = blockEntity.getItem(0).getItem() == Items.COAL_BLOCK && blockEntity.getItem(0).getCount() == 64;
            boolean bl2 = blockEntity.getItem(1).getItem() == Items.COPPER_BLOCK && blockEntity.getItem(1).getCount() == 60;
            boolean bl3 = blockEntity.getItem(2).getItem() == Items.IRON_BLOCK && blockEntity.getItem(2).getCount() == 48;
            boolean bl4 = blockEntity.getItem(3).getItem() == Items.GOLD_BLOCK && blockEntity.getItem(3).getCount() == 45;
            boolean bl5 = blockEntity.getItem(4).getItem() == Items.REDSTONE_BLOCK && blockEntity.getItem(4).getCount() == 40;
            boolean bl6 = blockEntity.getItem(5).getItem() == Items.EMERALD_BLOCK && blockEntity.getItem(5).getCount() == 36;
            boolean bl7 = blockEntity.getItem(6).getItem() == Items.LAPIS_BLOCK && blockEntity.getItem(6).getCount() == 32;
            boolean bl8 = blockEntity.getItem(7).getItem() == Items.DIAMOND_BLOCK && blockEntity.getItem(7).getCount() == 24;
            boolean bl9 = blockEntity.getItem(8).getItem() == Items.NETHERITE_BLOCK && blockEntity.getItem(8).getCount() == 8;

            if (bl && bl2 && bl3 && bl4 && bl5 && bl6 && bl7 && bl8 && bl9) {
                serverLevel.removeBlock(blockPos, false);
                level.explode(
                        null, level.damageSources().explosion(null, null), new ExplosionDamageCalculator(),
                        blockPos.getCenter(), 8.0F, true, Level.ExplosionInteraction.BLOCK
                );
                serverLevel.setBlock(blockPos, ModBlocks.JIANGHU_PORTAL.defaultBlockState(), 43);
            }
        }
    }
}
