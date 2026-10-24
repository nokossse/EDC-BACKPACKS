package com.modernbackpacks.item;

import com.modernbackpacks.compat.TaczCompat;
import com.modernbackpacks.inventory.BackpackInventory;
import com.modernbackpacks.menu.BackpackMenu;
import com.modernbackpacks.config.ModConfigs;
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
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

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

    /** Puts the backpack held in {@code hand} on the back, handing the previously worn one back in its place. */
    public static boolean equipFromHand(Player player, InteractionHand hand) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(inventory -> inventory.getStacksHandler(BACK_SLOT))
                .map(handler -> {
                    IDynamicStackHandler stacks = handler.getStacks();
                    if (stacks.getSlots() == 0) {
                        return false;
                    }
                    ItemStack previous = stacks.getStackInSlot(0).copy();
                    stacks.setStackInSlot(0, player.getItemInHand(hand).copy());
                    player.setItemInHand(hand, previous);
                    playBackpackSound(player);
                    return true;
                })
                .orElse(false);
    }

    public static boolean isBackpack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BackpackItem;
    }

    public static boolean hasHydrationSlot(ItemStack backpack) {
        return ModConfigs.hydrationSlot(BackpackKind.from(backpack));
    }

    /** Weapon slots only exist when TACZ is installed; otherwise nothing could go in them. */
    public static int weaponSlots(ItemStack backpack) {
        return TaczCompat.isLoaded() ? BackpackKind.from(backpack).getWeaponSlots() : 0;
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
        int slots = BackpackInventory.slotsOf(backpack);
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                (id, inventory, opener) -> new BackpackMenu(id, inventory, backpack, hand),
                backpack.getHoverName()
        ), buffer -> {
            buffer.writeVarInt(slots);
            buffer.writeBoolean(hasHydrationSlot(backpack));
            buffer.writeVarInt(weaponSlots(backpack));
            buffer.writeBoolean(hand != null);
            if (hand != null) {
                buffer.writeEnum(hand);
            }
        });
    }
}
