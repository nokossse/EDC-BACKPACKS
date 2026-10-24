package com.modernbackpacks.network;

import com.modernbackpacks.menu.BackpackMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleAutoHydratePacket {
    public static void encode(ToggleAutoHydratePacket packet, FriendlyByteBuf buffer) {
    }

    public static ToggleAutoHydratePacket decode(FriendlyByteBuf buffer) {
        return new ToggleAutoHydratePacket();
    }

    public static void handle(ToggleAutoHydratePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof BackpackMenu menu && menu.hasHydrationSlot()) {
                menu.toggleAutoHydrate();
            }
        });
        context.setPacketHandled(true);
    }
}
