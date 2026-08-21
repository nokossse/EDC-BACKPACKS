package com.edcbackpacks.item;

import com.edcbackpacks.EdcBackpacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public enum BackpackKind {
    RAID_BACKPACK("raid_backpack", 54, 0.0F, -0.15F, 0.12F),
    LARGE_HIKING_GREEN("large_hiking_green", 45, 0.0F, -0.55F, 0.20F);

    private final String id;
    private final int slots;
    private final float curioOffsetX;
    private final float curioOffsetY;
    private final float curioOffsetZ;

    BackpackKind(String id, int slots, float curioOffsetX, float curioOffsetY, float curioOffsetZ) {
        this.id = id;
        this.slots = slots;
        this.curioOffsetX = curioOffsetX;
        this.curioOffsetY = curioOffsetY;
        this.curioOffsetZ = curioOffsetZ;
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

    public float getCurioOffsetX() {
        return this.curioOffsetX;
    }

    public float getCurioOffsetY() {
        return this.curioOffsetY;
    }

    public float getCurioOffsetZ() {
        return this.curioOffsetZ;
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
