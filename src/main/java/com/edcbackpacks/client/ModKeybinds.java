package com.edcbackpacks.client;

import com.edcbackpacks.EdcBackpacks;
import com.edcbackpacks.network.ModNetwork;
import com.edcbackpacks.network.OpenBackpackPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = EdcBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModKeybinds {
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping(
            "key.edc_backpacks.open",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.edc_backpacks");

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        while (OPEN_BACKPACK.consumeClick()) {
            if (minecraft.screen == null && minecraft.player != null) {
                ModNetwork.CHANNEL.sendToServer(new OpenBackpackPacket());
            }
        }
    }
}
