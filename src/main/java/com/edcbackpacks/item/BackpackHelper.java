package com.edcbackpacks.item;

import com.edcbackpacks.menu.BackpackMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    public static boolean isWornBackpack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BackpackItem;
    }

    public static void openWornBackpack(ServerPlayer player) {
        ItemStack backpack = getWornBackpack(player);
        if (!isWornBackpack(backpack)) {
            return;
        }

        player.openMenu(new SimpleMenuProvider(
                (id, inventory, opener) -> new BackpackMenu(id, inventory, backpack),
                backpack.getHoverName()));
    }
}
