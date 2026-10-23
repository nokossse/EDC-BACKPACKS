package com.modernbackpacks;

import com.modernbackpacks.item.ModCreativeTabs;
import com.modernbackpacks.item.ModItems;
import com.modernbackpacks.menu.ModMenus;
import com.modernbackpacks.network.ModNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModernBackpacks.MOD_ID)
public class ModernBackpacks {
    public static final String MOD_ID = "modern_backpacks";

    public ModernBackpacks() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModCreativeTabs.CREATIVE_TABS.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModNetwork.register();
    }
}
