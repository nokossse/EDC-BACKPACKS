package com.edcbackpacks.client;

import com.edcbackpacks.EdcBackpacks;
import com.edcbackpacks.client.model.DaypackSandModel;
import com.edcbackpacks.client.model.DuffleAdiModel;
import com.edcbackpacks.client.model.LargeHikingGreenModel;
import com.edcbackpacks.client.model.RaidBackpackModel;
import com.edcbackpacks.client.render.BackpackCurioRenderer;
import com.edcbackpacks.item.BackpackKind;
import com.edcbackpacks.item.ModItems;
import com.edcbackpacks.menu.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(modid = EdcBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.BACKPACK.get(), BackpackScreen::new);
            CuriosRendererRegistry.register(ModItems.RAID_BACKPACK.get(),
                    () -> new BackpackCurioRenderer(BackpackKind.RAID_BACKPACK));
            CuriosRendererRegistry.register(ModItems.LARGE_HIKING_GREEN.get(),
                    () -> new BackpackCurioRenderer(BackpackKind.LARGE_HIKING_GREEN));
            CuriosRendererRegistry.register(ModItems.LARGE_HIKING_BLUE.get(),
                    () -> new BackpackCurioRenderer(BackpackKind.LARGE_HIKING_BLUE));
            CuriosRendererRegistry.register(ModItems.LARGE_HIKING_RED.get(),
                    () -> new BackpackCurioRenderer(BackpackKind.LARGE_HIKING_RED));
            CuriosRendererRegistry.register(ModItems.DAYPACK_SAND.get(),
                    () -> new BackpackCurioRenderer(BackpackKind.DAYPACK_SAND));
            CuriosRendererRegistry.register(ModItems.DUFFLE_ADI.get(),
                    () -> new BackpackCurioRenderer(BackpackKind.DUFFLE_ADI));
        });
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(RaidBackpackModel.LAYER_LOCATION, RaidBackpackModel::createBodyLayer);
        event.registerLayerDefinition(LargeHikingGreenModel.LAYER_LOCATION, LargeHikingGreenModel::createBodyLayer);
        event.registerLayerDefinition(DaypackSandModel.LAYER_LOCATION, DaypackSandModel::createBodyLayer);
        event.registerLayerDefinition(DuffleAdiModel.LAYER_LOCATION, DuffleAdiModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.OPEN_BACKPACK);
    }
}
