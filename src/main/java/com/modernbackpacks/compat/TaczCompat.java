package com.modernbackpacks.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

/** TACZ is a soft dependency: guns are recognised by registry id, so nothing links against it. */
public final class TaczCompat {
    private static final String MOD_ID = "tacz";
    private static final ResourceLocation GUN = new ResourceLocation(MOD_ID, "modern_kinetic_gun");

    private TaczCompat() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MOD_ID);
    }

    public static boolean isGun(ItemStack stack) {
        return GUN.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()));
    }
}
