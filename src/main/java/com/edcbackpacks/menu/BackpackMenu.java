package com.edcbackpacks.menu;

import com.edcbackpacks.inventory.BackpackInventory;
import com.edcbackpacks.item.BackpackHelper;
import com.edcbackpacks.item.BackpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackMenu extends AbstractContainerMenu {
    public static final int BACKPACK_ROWS = 6;
    public static final int BACKPACK_COLUMNS = 9;
    public static final int BACKPACK_SLOTS = BackpackInventory.SLOTS;

    private static final int SLOT_SIZE = 18;
    private static final int BACKPACK_START_X = 8;
    private static final int BACKPACK_START_Y = 18;
    private static final int PLAYER_INV_Y = 140;
    private static final int HOTBAR_Y = 198;

    public BackpackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, new ItemStackHandler(BACKPACK_SLOTS) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return !(stack.getItem() instanceof BackpackItem);
            }
        });
    }

    public BackpackMenu(int containerId, Inventory playerInventory, ItemStack backpack) {
        this(containerId, playerInventory, BackpackInventory.from(backpack));
    }

    public BackpackMenu(int containerId, Inventory playerInventory, ItemStackHandler handler) {
        super(ModMenus.BACKPACK.get(), containerId);

        for (int row = 0; row < BACKPACK_ROWS; row++) {
            for (int column = 0; column < BACKPACK_COLUMNS; column++) {
                int index = column + row * BACKPACK_COLUMNS;
                this.addSlot(new SlotItemHandler(handler, index,
                        BACKPACK_START_X + column * SLOT_SIZE,
                        BACKPACK_START_Y + row * SLOT_SIZE));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9,
                        BACKPACK_START_X + column * SLOT_SIZE,
                        PLAYER_INV_Y + row * SLOT_SIZE));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column,
                    BACKPACK_START_X + column * SLOT_SIZE,
                    HOTBAR_Y));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack moving = slot.getItem();
            original = moving.copy();

            if (moving.getItem() instanceof BackpackItem) {
                return ItemStack.EMPTY;
            }

            if (index < BACKPACK_SLOTS) {
                if (!this.moveItemStackTo(moving, BACKPACK_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(moving, 0, BACKPACK_SLOTS, false)) {
                return ItemStack.EMPTY;
            }

            if (moving.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return BackpackHelper.isWornBackpack(BackpackHelper.getWornBackpack(player));
    }
}
