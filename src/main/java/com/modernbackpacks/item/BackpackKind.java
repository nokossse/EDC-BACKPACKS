package com.modernbackpacks.item;

import com.modernbackpacks.ModernBackpacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public enum BackpackKind {
    TASMANIAN_TIGER_MK3_GREEN("tasmanian_tiger_mk3_green", 54),
    OLIVE_DAYPACK("olive_daypack", 36);

    private final String id;
    private final int slots;

    BackpackKind(String id, int slots) {
        this.id = id;
        this.slots = slots;
    }

    /** Loaded from {@code assets/modern_backpacks/models/bedrock/backpack/<id>.geo.json}. */
    public ResourceLocation getBedrockModel() {
        return new ResourceLocation(ModernBackpacks.MOD_ID, "backpack/" + this.id + ".geo");
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
        return new ResourceLocation(ModernBackpacks.MOD_ID, "textures/asset/" + this.id + ".png");
    }

    public static BackpackKind from(ItemStack stack) {
        if (stack.getItem() instanceof BackpackItem backpackItem) {
            return backpackItem.getKind();
        }
        return TASMANIAN_TIGER_MK3_GREEN;
    }
}
