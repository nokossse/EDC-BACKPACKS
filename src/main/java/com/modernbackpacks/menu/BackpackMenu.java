package com.modernbackpacks.menu;

import com.modernbackpacks.compat.TaczCompat;
import com.modernbackpacks.inventory.BackpackInventory;
import com.modernbackpacks.item.BackpackHelper;
import com.modernbackpacks.item.BackpackItem;
import com.modernbackpacks.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
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
    public static final int HYDRATION_SLOT_X = -25;
    public static final int HYDRATION_SLOT_Y = 18;
    public static final int WEAPON_SLOT_X = HYDRATION_SLOT_X;
    /** Weapon panel sits under the hydration panel (10 + 50 px), or at its place when there is none. */
    public static final int WEAPON_PANEL_TOP = 60;
    public static final int WEAPON_PANEL_TOP_ALONE = 10;

    private final Inventory playerInventory;
    private final int backpackSlots;
    private final int backpackRows;
    private final DataSlot autoHydrate;
    private int hydrationIndex = -1;
    private int weaponStart = -1;
    private int weaponCount;
    private final int playerEnd;
    @Nullable
    private final InteractionHand hand;

    public BackpackMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, dummyHandler(buffer.readVarInt()),
                buffer.readBoolean() ? new ItemStackHandler(1) : null, weaponsFrom(buffer), DataSlot.standalone(),
                buffer.readBoolean() ? buffer.readEnum(InteractionHand.class) : null);
    }

    public BackpackMenu(int containerId, Inventory playerInventory, ItemStack backpack,
                        @Nullable InteractionHand hand) {
        this(containerId, playerInventory, BackpackInventory.from(backpack),
                BackpackHelper.hasHydrationSlot(backpack) ? BackpackInventory.hydration(backpack) : null,
                BackpackHelper.weaponSlots(backpack) > 0
                        ? BackpackInventory.weapons(backpack, BackpackHelper.weaponSlots(backpack)) : null,
                new DataSlot() {
                    @Override
                    public int get() {
                        return BackpackInventory.isAutoHydrate(backpack) ? 1 : 0;
                    }

                    @Override
                    public void set(int value) {
                        BackpackInventory.setAutoHydrate(backpack, value != 0);
                    }
                }, hand);
    }

    private BackpackMenu(int containerId, Inventory playerInventory, ItemStackHandler handler,
                         @Nullable ItemStackHandler hydration, @Nullable ItemStackHandler weapons,
                         DataSlot autoHydrate,
                         @Nullable InteractionHand hand) {
        super(ModMenus.BACKPACK.get(), containerId);
        this.playerInventory = playerInventory;
        this.backpackSlots = handler.getSlots();
        this.backpackRows = this.backpackSlots / BACKPACK_COLUMNS;
        this.hand = hand;
        this.autoHydrate = autoHydrate;
        this.addDataSlot(autoHydrate);

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

        this.playerEnd = this.slots.size();
        if (hydration != null) {
            this.hydrationIndex = this.slots.size();
            this.addSlot(new SlotItemHandler(hydration, 0, HYDRATION_SLOT_X, HYDRATION_SLOT_Y));
        }

        if (weapons != null) {
            this.weaponStart = this.slots.size();
            this.weaponCount = weapons.getSlots();
            for (int i = 0; i < this.weaponCount; i++) {
                this.addSlot(new SlotItemHandler(weapons, i, WEAPON_SLOT_X, weaponPanelTop(hydration != null) + 8 + i * SLOT_SIZE));
            }
        }
    }

    public static int weaponPanelTop(boolean hasHydration) {
        return hasHydration ? WEAPON_PANEL_TOP : WEAPON_PANEL_TOP_ALONE;
    }

    /** Client side: only the slot count travels, the server holds the content. */
    @Nullable
    private static ItemStackHandler weaponsFrom(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        return count > 0 ? new ItemStackHandler(count) : null;
    }

    public int getWeaponSlotCount() {
        return this.weaponCount;
    }

    public boolean hasHydrationSlot() {
        return this.hydrationIndex >= 0;
    }

    public boolean isAutoHydrate() {
        return this.autoHydrate.get() != 0;
    }

    /** Auto-hydrate only works on the backpack worn on the back, not one opened from the hand. */
    public boolean isWorn() {
        return this.hand == null;
    }

    public void toggleAutoHydrate() {
        if (!isWorn()) {
            return;
        }
        this.autoHydrate.set(isAutoHydrate() ? 0 : 1);
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

            boolean moved;
            if (index >= this.playerEnd) {
                moved = this.moveItemStackTo(moving, this.backpackSlots, this.playerEnd, true);
            } else if (index < this.backpackSlots) {
                moved = this.moveItemStackTo(moving, this.backpackSlots, this.slots.size(), true);
            } else {
                moved = ModItems.isHydrationPack(moving) && this.hydrationIndex >= 0
                        && this.moveItemStackTo(moving, this.hydrationIndex, this.hydrationIndex + 1, false);
                moved = moved || this.weaponStart >= 0 && TaczCompat.isGun(moving)
                        && this.moveItemStackTo(moving, this.weaponStart, this.weaponStart + this.weaponCount, false);
                moved = moved || this.moveItemStackTo(moving, 0, this.backpackSlots, false);
            }
            if (!moved) {
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
