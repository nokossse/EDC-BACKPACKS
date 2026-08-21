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
            () -> new BackpackItem(new Item.Properties().stacksTo(1)));
}
