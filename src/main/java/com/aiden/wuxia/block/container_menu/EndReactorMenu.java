package com.aiden.wuxia.block.container_menu;

import com.aiden.wuxia.block.ModBlocks;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EndReactorMenu extends AbstractContainerMenu {
    private static final int SLOTS_ROWS = 1;
    private static final int SLOTS_COLUMNS = 9;
    private static final int SLOTS_COUNT = SLOTS_ROWS * SLOTS_COLUMNS;
    private static final int CONTAINER_START = 0;
    private static final int CONTAINER_END = SLOTS_COUNT;
    private static final int INVENTORY_START = CONTAINER_END;
    private static final int INVENTORY_END = INVENTORY_START + Inventory.INVENTORY_SIZE;
    private static final int CONTAINER_START_X = 8, CONTAINER_START_Y = 36;
    private static final int INVENTORY_START_X = 8, INVENTORY_START_Y = 84;
    private final Container container;

    public EndReactorMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOTS_COUNT));
    }

    public EndReactorMenu(final int containerId, final Inventory inventory, final Container container) {
        super(ModBlocks.END_REACTOR_MENU_TYPE, containerId);
        checkContainerSize(container, SLOTS_COUNT);
        this.container = container;
        container.startOpen(inventory.player);
        this.add1x9GridSlots();
        this.addStandardInventorySlots(inventory, INVENTORY_START_X, INVENTORY_START_Y);
    }

    private void add1x9GridSlots() {
        for (int y = 0; y < SLOTS_ROWS; y++) {
            for (int x = 0; x < SLOTS_COLUMNS; x++) {
                final int slotId = x + y * SLOTS_COLUMNS;
                final int posX = CONTAINER_START_X + x * SLOT_SIZE;
                final int posY = CONTAINER_START_Y + y * SLOT_SIZE;
                Slot slot = switch (slotId) {
                    case 0 -> new CoalBlockSlot(this.container, slotId, posX, posY);
                    case 1 -> new CopperBlockSlot(this.container, slotId, posX, posY);
                    case 2 -> new IronBlockSlot(this.container, slotId, posX, posY);
                    case 3 -> new GoldBlockSlot(this.container, slotId, posX, posY);
                    case 4 -> new RedstoneBlockSlot(this.container, slotId, posX, posY);
                    case 5 -> new EmeraldBlockSlot(this.container, slotId, posX, posY);
                    case 6 -> new LapisLazuliBlockSlot(this.container, slotId, posX, posY);
                    case 7 -> new DiamondBlockSlot(this.container, slotId, posX, posY);
                    case 8 -> new NetheriteBlockSlot(this.container, slotId, posX, posY);
                    default -> new Slot(this.container, slotId, posX, posY);
                };

                this.addSlot(slot);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack clicked = stack.copy();
        if (slotIndex < CONTAINER_END) {
            if (!this.moveItemStackTo(stack, INVENTORY_START, INVENTORY_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(stack, CONTAINER_START, CONTAINER_END, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    private static class CoalBlockSlot extends Slot {
        public CoalBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.COAL_BLOCK);
        }
    }

    private static class CopperBlockSlot extends Slot {
        public CopperBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 60;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.COPPER_BLOCK);
        }
    }

    private static class IronBlockSlot extends Slot {
        public IronBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 48;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.IRON_BLOCK);
        }
    }

    private static class GoldBlockSlot extends Slot {
        public GoldBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 45;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.GOLD_BLOCK);
        }
    }

    private static class RedstoneBlockSlot extends Slot {
        public RedstoneBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 40;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.REDSTONE_BLOCK);
        }
    }

    private static class EmeraldBlockSlot extends Slot {
        public EmeraldBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 36;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.EMERALD_BLOCK);
        }
    }

    private static class LapisLazuliBlockSlot extends Slot {
        public LapisLazuliBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 32;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.LAPIS_BLOCK);
        }
    }

    private static class DiamondBlockSlot extends Slot {
        public DiamondBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 24;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.DIAMOND_BLOCK);
        }
    }

    private static class NetheriteBlockSlot extends Slot {
        public NetheriteBlockSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return 8;
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.NETHERITE_BLOCK);
        }
    }
}
