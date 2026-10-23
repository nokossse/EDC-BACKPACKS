package com.modernbackpacks.client;

import com.modernbackpacks.ModernBackpacks;
import com.modernbackpacks.client.model.BedrockBackpackModel;
import com.modernbackpacks.client.render.BackpackCurioRenderer;
import com.modernbackpacks.item.BackpackKind;
import com.modernbackpacks.item.ModItems;
import com.modernbackpacks.menu.ModMenus;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakerOptions;
import com.github.mcmodderanchor.simplebedrockmodel.v2.event.RegisterV2BedrockResourcesEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.Set;

@Mod.EventBusSubscriber(modid = ModernBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.BACKPACK.get(), BackpackScreen::new);
            CuriosRendererRegistry.register(ModItems.TASMANIAN_TIGER_MK3_GREEN.get(),
                    () -> new BackpackCurioRenderer(BackpackKind.TASMANIAN_TIGER_MK3_GREEN));
            CuriosRendererRegistry.register(ModItems.OLIVE_DAYPACK.get(),
                    () -> new BackpackCurioRenderer(BackpackKind.OLIVE_DAYPACK));
        });
    }

    @SubscribeEvent
    public static void registerBedrockModels(RegisterV2BedrockResourcesEvent event) {
        for (BackpackKind kind : BackpackKind.values()) {
            event.bakedModel(kind.getBedrockModel())
                    .options(BakerOptions.ofAnimatedBones(Set.of(BedrockBackpackModel.STRAPS_BONE)))
                    .register();
        }
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ModKeybinds.OPEN_BACKPACK);
    }
}
