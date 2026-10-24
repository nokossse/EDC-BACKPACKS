package com.modernbackpacks.item;

import com.modernbackpacks.ModernBackpacks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModernBackpacks.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MODERN_BACKPACKS = CREATIVE_TABS.register("modern_backpacks",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.modern_backpacks"))
                    .icon(() -> new ItemStack(ModItems.TASMANIAN_TIGER_MK3_GREEN.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TASMANIAN_TIGER_MK3_GREEN.get());
                        output.accept(ModItems.OLIVE_DAYPACK.get());
                        output.accept(ModItems.OSPREY_KESTREL.get());
                        output.accept(ModItems.LARGE_MILITARY_DAYPACK.get());
                        output.accept(ModItems.EASTPAK_BACKPACK.get());
                        output.accept(ModItems.HYDRATION_PACK.get());
                    })
                    .build());
}
