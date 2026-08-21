package com.edcbackpacks.menu;

import com.edcbackpacks.inventory.BackpackInventory;
import com.edcbackpacks.item.BackpackHelper;
import com.edcbackpacks.item.BackpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class BackpackMenu extends AbstractContainerMenu {
    public static final int BACKPACK_COLUMNS = 9;

    private static final int SLOT_SIZE = 18;
    private static final int BACKPACK_START_X = 8;
    private static final int BACKPACK_START_Y = 18;

    private final Inventory playerInventory;
    private final int backpackSlots;
    private final int backpackRows;
    @Nullable
    private final InteractionHand hand;

    public BackpackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, dummyHandler(buffer.readVarInt()),
                buffer.readBoolean() ? buffer.readEnum(InteractionHand.class) : null);
    }

    public BackpackMenu(int containerId, Inventory playerInventory, ItemStack backpack,
                        @Nullable InteractionHand hand) {
        this(containerId, playerInventory, BackpackInventory.from(backpack), hand);
    }

    public BackpackMenu(int containerId, Inventory playerInventory, ItemStackHandler handler,
                        @Nullable InteractionHand hand) {
        super(ModMenus.BACKPACK.get(), containerId);
        this.playerInventory = playerInventory;
        this.backpackSlots = handler.getSlots();
        this.backpackRows = this.backpackSlots / BACKPACK_COLUMNS;
        this.hand = hand;

        int extra = (this.backpackRows - 4) * SLOT_SIZE;
        int playerInvY = 103 + extra;
        int hotbarY = 161 + extra;

        for (int row = 0; row < this.backpackRows; row++) {
            for (int column = 0; column < BACKPACK_COLUMNS; column++) {
                int index = column + row * BACKPACK_COLUMNS;
                this.addSlot(new SlotItemHandler(handler, index,
                        BACKPACK_START_X + column * SLOT_SIZE,
                        BACKPACK_START_Y + row * SLOT_SIZE));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(createPlayerSlot(playerInventory, column + row * 9 + 9,
                        BACKPACK_START_X + column * SLOT_SIZE,
                        playerInvY + row * SLOT_SIZE));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(createPlayerSlot(playerInventory, column,
                    BACKPACK_START_X + column * SLOT_SIZE,
                    hotbarY));
        }
    }

    public int getBackpackSlots() {
        return this.backpackSlots;
    }

    public int getBackpackRows() {
        return this.backpackRows;
    }

    private static ItemStackHandler dummyHandler(int slots) {
        return new ItemStackHandler(slots) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return !(stack.getItem() instanceof BackpackItem);
            }
        };
    }

    private Slot createPlayerSlot(Inventory playerInventory, int index, int x, int y) {
        return new Slot(playerInventory, index, x, y) {
            @Override
            public boolean mayPickup(Player player) {
                return !isLockedBackpackSlot(index) && super.mayPickup(player);
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return !isLockedBackpackSlot(index) && super.mayPlace(stack);
            }
        };
    }

    private boolean isLockedBackpackSlot(int playerSlotIndex) {
        return this.hand == InteractionHand.MAIN_HAND
                && playerSlotIndex == this.playerInventory.selected;
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

            if (index < this.backpackSlots) {
                if (!this.moveItemStackTo(moving, this.backpackSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(moving, 0, this.backpackSlots, false)) {
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
        if (this.hand != null) {
            return BackpackHelper.isBackpack(player.getItemInHand(this.hand));
        }
        return BackpackHelper.isBackpack(BackpackHelper.getWornBackpack(player));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            BackpackHelper.playBackpackSound(player);
        }
    }
}
