package com.edcbackpacks.item;

import com.edcbackpacks.EdcBackpacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public enum BackpackKind {
    RAID_BACKPACK("raid_backpack", 63),
    LARGE_HIKING_GREEN("large_hiking_green", 45),
    LARGE_HIKING_BLUE("large_hiking_blue", 45),
    LARGE_HIKING_RED("large_hiking_red", 45),
    DAYPACK_SAND("daypack_sand", 36),
    DUFFLE_ADI("duffle_adi", 18);

    private final String id;
    private final int slots;

    BackpackKind(String id, int slots) {
        this.id = id;
        this.slots = slots;
    }

    public String getId() {
        return this.id;
    }

    public int getSlots() {
        return this.slots;
    }

    public int getRows() {
        return this.slots / 9;
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(EdcBackpacks.MOD_ID, "textures/asset/" + this.id + ".png");
    }

    public static BackpackKind from(ItemStack stack) {
        if (stack.getItem() instanceof BackpackItem backpackItem) {
            return backpackItem.getKind();
        }
        return RAID_BACKPACK;
    }
}
