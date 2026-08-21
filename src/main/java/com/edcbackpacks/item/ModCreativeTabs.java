package com.edcbackpacks.item;

import com.edcbackpacks.EdcBackpacks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EdcBackpacks.MOD_ID);

    public static final RegistryObject<CreativeModeTab> EDC_BACKPACKS = CREATIVE_TABS.register("edc_backpacks",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.edc_backpacks"))
                    .icon(() -> new ItemStack(ModItems.RAID_BACKPACK.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RAID_BACKPACK.get());
                        output.accept(ModItems.LARGE_HIKING_GREEN.get());
                    })
                    .build());
}
