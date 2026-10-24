package com.modernbackpacks.item;

import com.modernbackpacks.ModernBackpacks;
import com.modernbackpacks.compat.ThirstCompat;
import com.modernbackpacks.inventory.BackpackInventory;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;

/** Server-side: drinks from the worn backpack's hydration pack whenever the player is thirsty. */
@Mod.EventBusSubscriber(modid = ModernBackpacks.MOD_ID)
public final class AutoHydrateHandler {
    private static final int CHECK_INTERVAL_TICKS = 20;

    private AutoHydrateHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide
                || player.tickCount % CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        ItemStack backpack = BackpackHelper.getWornBackpack(player);
        if (!BackpackHelper.isBackpack(backpack) || !BackpackHelper.hasHydrationSlot(backpack)
                || !BackpackInventory.isAutoHydrate(backpack) || !ThirstCompat.needsDrink(player)) {
            return;
        }
        ItemStackHandler slot = BackpackInventory.hydration(backpack);
        ItemStack pack = slot.getStackInSlot(0);
        if (!ModItems.isHydrationPack(pack)) {
            return;
        }
        int quality = ThirstCompat.takeDrink(pack);
        if (quality != ThirstCompat.NONE) {
            slot.setStackInSlot(0, pack);
            ThirstCompat.drink(player, quality);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }
}
