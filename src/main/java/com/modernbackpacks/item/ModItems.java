package com.modernbackpacks.item;

import com.modernbackpacks.ModernBackpacks;
import com.modernbackpacks.compat.ThirstCompat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ModernBackpacks.MOD_ID);

    public static final RegistryObject<Item> TASMANIAN_TIGER_MK3_GREEN = ITEMS.register("tasmanian_tiger_mk3_green",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.TASMANIAN_TIGER_MK3_GREEN));

    public static final RegistryObject<Item> OSPREY_KESTREL = ITEMS.register("osprey_kestrel",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.OSPREY_KESTREL));

    public static final RegistryObject<Item> LARGE_MILITARY_DAYPACK = ITEMS.register("large_military_daypack",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.LARGE_MILITARY_DAYPACK));

    public static final RegistryObject<Item> EASTPAK_BACKPACK = ITEMS.register("eastpak_backpack",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.EASTPAK_BACKPACK));

    public static final RegistryObject<Item> HYDRATION_PACK = ITEMS.register("hydration_pack",
            ThirstCompat::createHydrationPack);

    public static boolean isHydrationPack(ItemStack stack) {
        return stack.is(HYDRATION_PACK.get());
    }

    public static final RegistryObject<Item> OLIVE_DAYPACK = ITEMS.register("olive_daypack",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.OLIVE_DAYPACK));
}
