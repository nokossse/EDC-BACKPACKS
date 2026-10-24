package com.modernbackpacks.config;

import com.modernbackpacks.item.BackpackKind;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.EnumMap;
import java.util.Map;

public final class ModConfigs {
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.IntValue HYDRATION_CAPACITY;
    private static final ForgeConfigSpec.IntValue HYDRATION_DRINKS;
    private static final Map<BackpackKind, ForgeConfigSpec.BooleanValue> HYDRATION_SLOT = new EnumMap<>(BackpackKind.class);

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        HYDRATION_CAPACITY = builder
                .comment("Capacity of the hydration pack, in millibuckets (1000 = 1 bucket = 3 drinks), without Thirst Was Taken 2.")
                .defineInRange("hydration_pack_capacity_mb", 3333, 1, 1_000_000);
        HYDRATION_DRINKS = builder
                .comment("Capacity of the hydration pack, in drinks, when Thirst Was Taken 2 is installed (the pack is then one of its flasks).")
                .defineInRange("hydration_pack_capacity_drinks", 10, 1, 64);
        builder.comment("Per backpack: does it have a hydration pack slot (with the auto-hydrate toggle)?").push("backpacks");
        for (BackpackKind kind : BackpackKind.values()) {
            builder.push(kind.getId());
            HYDRATION_SLOT.put(kind, builder.define("hydration_slot", kind != BackpackKind.EASTPAK_BACKPACK));
            builder.pop();
        }
        builder.pop();
        SPEC = builder.build();
    }

    private ModConfigs() {
    }

    public static int hydrationCapacityMb() {
        return HYDRATION_CAPACITY.get();
    }

    public static int hydrationDrinks() {
        return HYDRATION_DRINKS.get();
    }

    public static boolean hydrationSlot(BackpackKind kind) {
        return HYDRATION_SLOT.get(kind).get();
    }
}
