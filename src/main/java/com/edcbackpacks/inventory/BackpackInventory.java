package com.edcbackpacks.inventory;

import com.edcbackpacks.item.BackpackItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackInventory extends ItemStackHandler {
    public static final int SLOTS = 54;
    public static final String INVENTORY_TAG = "Inventory";

    private final ItemStack backpack;

    public BackpackInventory(ItemStack backpack) {
        super(SLOTS);
        this.backpack = backpack;
        CompoundTag tag = backpack.getTag();
        if (tag != null && tag.contains(INVENTORY_TAG)) {
            deserializeNBT(tag.getCompound(INVENTORY_TAG));
        }
    }

    public static BackpackInventory from(ItemStack backpack) {
        return new BackpackInventory(backpack);
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
