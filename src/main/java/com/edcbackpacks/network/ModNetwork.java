package com.edcbackpacks.network;

import com.edcbackpacks.EdcBackpacks;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EdcBackpacks.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id, OpenBackpackPacket.class,
                OpenBackpackPacket::encode,
                OpenBackpackPacket::decode,
                OpenBackpackPacket::handle);
    }
}
