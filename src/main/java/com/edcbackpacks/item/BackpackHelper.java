package com.edcbackpacks.item;

import com.edcbackpacks.menu.BackpackMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public final class BackpackHelper {
    public static final String BACK_SLOT = "back";

    private BackpackHelper() {
    }

    public static ItemStack getWornBackpack(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .map(inventory -> inventory.findCurio(BACK_SLOT, 0)
                        .map(SlotResult::stack)
                        .orElse(ItemStack.EMPTY))
                .orElse(ItemStack.EMPTY);
    }

    public static boolean isBackpack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BackpackItem;
    }

    public static void playBackpackSound(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    public static void openWornBackpack(ServerPlayer player) {
        ItemStack backpack = getWornBackpack(player);
        if (!isBackpack(backpack)) {
            return;
        }
        openBackpack(player, backpack, null);
    }

    public static void openBackpack(ServerPlayer player, ItemStack backpack, @Nullable InteractionHand hand) {
        playBackpackSound(player);
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                (id, inventory, opener) -> new BackpackMenu(id, inventory, backpack, hand),
                backpack.getHoverName()
        ), buffer -> {
            buffer.writeBoolean(hand != null);
            if (hand != null) {
                buffer.writeEnum(hand);
            }
        });
    }
}
