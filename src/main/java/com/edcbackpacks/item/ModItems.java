package com.edcbackpacks.item;

import com.edcbackpacks.EdcBackpacks;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EdcBackpacks.MOD_ID);

    public static final RegistryObject<Item> RAID_BACKPACK = ITEMS.register("raid_backpack",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.RAID_BACKPACK));

    public static final RegistryObject<Item> LARGE_HIKING_GREEN = ITEMS.register("large_hiking_green",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.LARGE_HIKING_GREEN));

    public static final RegistryObject<Item> LARGE_HIKING_BLUE = ITEMS.register("large_hiking_blue",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.LARGE_HIKING_BLUE));

    public static final RegistryObject<Item> LARGE_HIKING_RED = ITEMS.register("large_hiking_red",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.LARGE_HIKING_RED));

    public static final RegistryObject<Item> DAYPACK_SAND = ITEMS.register("daypack_sand",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.DAYPACK_SAND));

    public static final RegistryObject<Item> DUFFLE_ADI = ITEMS.register("duffle_adi",
            () -> new BackpackItem(new Item.Properties().stacksTo(1), BackpackKind.DUFFLE_ADI));
}
