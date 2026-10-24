package com.modernbackpacks.inventory;

import com.modernbackpacks.compat.TaczCompat;
import com.modernbackpacks.item.BackpackItem;
import com.modernbackpacks.item.BackpackKind;
import com.modernbackpacks.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackInventory extends ItemStackHandler {
    public static final String INVENTORY_TAG = "Inventory";
    public static final String HYDRATION_TAG = "Hydration";
    public static final String WEAPONS_TAG = "Weapons";
    public static final String AUTO_HYDRATE_TAG = "AutoHydrate";

    private final ItemStack backpack;

    public BackpackInventory(ItemStack backpack) {
        super(slotsOf(backpack));
        this.backpack = backpack;
        CompoundTag tag = backpack.getTag();
        if (tag != null && tag.contains(INVENTORY_TAG)) {
            CompoundTag inventory = tag.getCompound(INVENTORY_TAG).copy();
            inventory.putInt("Size", getSlots());
            deserializeNBT(inventory);
        }
    }

    /** Single-slot handler for the hydration pack, stored in the backpack's NBT next to the main inventory. */
    public static ItemStackHandler hydration(ItemStack backpack) {
        ItemStackHandler handler = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                backpack.getOrCreateTag().put(HYDRATION_TAG, serializeNBT());
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return ModItems.isHydrationPack(stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        CompoundTag tag = backpack.getTag();
        if (tag != null && tag.contains(HYDRATION_TAG)) {
            handler.deserializeNBT(tag.getCompound(HYDRATION_TAG));
        }
        return handler;
    }

    /** Weapon slots (TACZ guns only), stored in the backpack's NBT like the hydration pack. */
    public static ItemStackHandler weapons(ItemStack backpack, int slots) {
        ItemStackHandler handler = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                backpack.getOrCreateTag().put(WEAPONS_TAG, serializeNBT());
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return TaczCompat.isGun(stack);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
        CompoundTag tag = backpack.getTag();
        if (tag != null && tag.contains(WEAPONS_TAG)) {
            CompoundTag saved = tag.getCompound(WEAPONS_TAG).copy();
            saved.putInt("Size", slots);
            handler.deserializeNBT(saved);
        }
        return handler;
    }

    public static boolean isAutoHydrate(ItemStack backpack) {
        return backpack.hasTag() && backpack.getTag().getBoolean(AUTO_HYDRATE_TAG);
    }

    public static void setAutoHydrate(ItemStack backpack, boolean value) {
        backpack.getOrCreateTag().putBoolean(AUTO_HYDRATE_TAG, value);
    }

    public static BackpackInventory from(ItemStack backpack) {
        return new BackpackInventory(backpack);
    }

    public static int slotsOf(ItemStack backpack) {
        if (backpack.getItem() instanceof BackpackItem item) {
            return item.getSlots();
        }
        return BackpackKind.TASMANIAN_TIGER_MK3_GREEN.getSlots();
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.backpack.getOrCreateTag().put(INVENTORY_TAG, serializeNBT());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !(stack.getItem() instanceof BackpackItem);
    }
}
