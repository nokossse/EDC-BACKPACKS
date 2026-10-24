package com.modernbackpacks.item;

import com.modernbackpacks.ModernBackpacks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public enum BackpackKind {
    TASMANIAN_TIGER_MK3_GREEN("tasmanian_tiger_mk3_green", 7, 2),
    OLIVE_DAYPACK("olive_daypack", 4),
    OSPREY_KESTREL("osprey_kestrel", 6),
    LARGE_MILITARY_DAYPACK("large_military_daypack", 5, 2),
    EASTPAK_BACKPACK("eastpak_backpack", 2);

    private static final String[] ROMAN = {"I", "II", "III", "IV", "V", "VI", "VII"};
    // Tier I..VII
    private static final ChatFormatting[] TIER_COLORS = {
            ChatFormatting.DARK_GRAY, ChatFormatting.GRAY, ChatFormatting.WHITE, ChatFormatting.GREEN,
            ChatFormatting.BLUE, ChatFormatting.DARK_PURPLE, ChatFormatting.GOLD};

    private final String id;
    private final int slots;
    private final int tier;
    private final int weaponSlots;

    BackpackKind(String id, int tier) {
        this(id, tier, 0);
    }

    /** {@code weaponSlots} = number of {@code weaponDummyN} bones in the geo. */
    BackpackKind(String id, int tier, int weaponSlots) {
        this.id = id;
        this.weaponSlots = weaponSlots;
        this.slots = tier * 9;
        this.tier = tier;
    }

    public Component getTierComponent() {
        return Component.translatable("tooltip." + ModernBackpacks.MOD_ID + ".tier", ROMAN[this.tier - 1])
                .withStyle(TIER_COLORS[this.tier - 1]);
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

    public int getWeaponSlots() {
        return this.weaponSlots;
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
