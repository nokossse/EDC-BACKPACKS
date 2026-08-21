package com.edcbackpacks;

import com.edcbackpacks.item.ModCreativeTabs;
import com.edcbackpacks.item.ModItems;
import com.edcbackpacks.menu.ModMenus;
import com.edcbackpacks.network.ModNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EdcBackpacks.MOD_ID)
public class EdcBackpacks {
    public static final String MOD_ID = "edc_backpacks";

    public EdcBackpacks() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.CREATIVE_TABS.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModNetwork.register();
    }
}
