package com.modernbackpacks.client;

import com.modernbackpacks.ModernBackpacks;
import com.modernbackpacks.network.ModNetwork;
import com.modernbackpacks.network.OpenBackpackPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ModernBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModKeybinds {
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping(
            "key.modern_backpacks.open",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.modern_backpacks");

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
