package com.edcbackpacks.inventory;

import com.edcbackpacks.item.BackpackItem;
import com.edcbackpacks.item.BackpackKind;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackInventory extends ItemStackHandler {
    public static final String INVENTORY_TAG = "Inventory";

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

    public static BackpackInventory from(ItemStack backpack) {
        return new BackpackInventory(backpack);
    }

    public static int slotsOf(ItemStack backpack) {
        if (backpack.getItem() instanceof BackpackItem item) {
            return item.getSlots();
        }
        return BackpackKind.RAID_BACKPACK.getSlots();
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
