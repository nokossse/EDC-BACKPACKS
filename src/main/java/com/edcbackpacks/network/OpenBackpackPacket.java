package com.edcbackpacks.network;

import com.edcbackpacks.item.BackpackHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenBackpackPacket {
    public static void encode(OpenBackpackPacket packet, FriendlyByteBuf buffer) {
    }

    public static OpenBackpackPacket decode(FriendlyByteBuf buffer) {
        return new OpenBackpackPacket();
    }

    public static void handle(OpenBackpackPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                BackpackHelper.openWornBackpack(player);
            }
        });
        context.setPacketHandled(true);
    }
}
