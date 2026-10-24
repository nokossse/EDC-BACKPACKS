package com.modernbackpacks.compat;

import com.modernbackpacks.ModernBackpacks;
import com.modernbackpacks.config.ModConfigs;
import com.modernbackpacks.item.HydrationPackItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.IntSupplier;

/**
 * Thirst mods are soft dependencies reached through reflection, so none is needed to compile or run.
 * Supported: Thirst Was Taken 2, Thirst Was Taken / Reclaimed, Legendary Survival Overhaul.
 * With Thirst Was Taken 2 the hydration pack is a real TWT2 flask, with Legendary Survival Overhaul a real Large Canteen
 * (see {@link #createHydrationPack()}).
 */
public final class ThirstCompat {
    private static final Logger LOGGER = LogManager.getLogger(ModernBackpacks.MOD_ID);

    /** Quality codes: {@link #SALT}, a purity grade (0 and up), or {@link #UNKNOWN} when the water carries none. */
    public static final int SALT = -1;
    public static final int UNKNOWN = -2;
    /** Returned by {@link #takeDrink(ItemStack)} when the pack has nothing to drink. */
    public static final int NONE = -3;

    private static final String TWT2 = "thirstwastaken2";
    private static final String LSO = "legendarysurvivaloverhaul";
    private static final String LSO_PACKAGE = "sfiomn.legendarysurvivaloverhaul.";
    private static final String TWT2_PACKAGE = "com.thirstwastaken2.";

    private interface Adapter {
        boolean needsDrink(Player player) throws Exception;

        void drink(Player player, int quality) throws Exception;

        /** Removes one drink from a pack that is a flask of the thirst mod itself. */
        default int takeFlaskDrink(ItemStack pack) throws Exception {
            return NONE;
        }
    }

    private static Adapter adapter;
    private static boolean resolved;

    private ThirstCompat() {
    }

    /**
     * With TWT2 / LSO the pack is a genuine flask / Large Canteen of that mod; with Thirst Was Taken / Reclaimed a plain
     * water container; with no thirst mod at all it is an inert item.
     */
    public static Item createHydrationPack() {
        try {
            if (ModList.get().isLoaded(TWT2)) {
                return twt2Flask();
            }
            if (ModList.get().isLoaded(LSO)) {
                return lsoLargeCanteen();
            }
        } catch (Exception | LinkageError e) {
            LOGGER.error("Could not build the hydration pack from the thirst mod's own container, using the plain one", e);
        }
        Item.Properties properties = new Item.Properties().stacksTo(1);
        return ModList.get().isLoaded("thirst") ? new HydrationPackItem(properties) : new Item(properties);
    }

    private static Item lsoLargeCanteen() throws Exception {
        return (Item) Class.forName(LSO_PACKAGE + "common.items.drink.LargeCanteenItem")
                .getConstructor(Item.Properties.class)
                .newInstance(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    private static Item twt2Flask() throws Exception {
        Class<?> waterData = Class.forName(TWT2_PACKAGE + "platform.ItemWaterData");
        Item.Properties properties = (Item.Properties) waterData.getMethod("emptyByDefault", Item.Properties.class)
                .invoke(null, new Item.Properties().stacksTo(1).fireResistant());
        Class<?> config = Class.forName(TWT2_PACKAGE + "config.ThirstConfig");
        Method configGet = config.getMethod("get");
        Field boilSeconds = config.getField("netheriteFlaskBoilSeconds");
        IntSupplier boilTicks = () -> {
            try {
                return boilSeconds.getInt(configGet.invoke(null)) * 20;
            } catch (ReflectiveOperationException e) {
                return 40;
            }
        };
        Item flask = (Item) Class.forName(TWT2_PACKAGE + "item.WaterskinItem")
                .getConstructor(Item.Properties.class, IntSupplier.class, IntSupplier.class, boolean.class)
                .newInstance(properties, (IntSupplier) ModConfigs::hydrationDrinks, boilTicks, false);
        // Registers the "empty by default" stack data, as TWT2 does for its own flasks.
        Method itemBuilt = Class.forName(TWT2_PACKAGE + "platform.DefaultData")
                .getDeclaredMethod("itemBuilt", Item.Properties.class, Item.class);
        itemBuilt.setAccessible(true);
        itemBuilt.invoke(null, properties, flask);
        return flask;
    }

    public static boolean needsDrink(Player player) {
        Adapter current = adapter();
        if (current == null) {
            return false;
        }
        try {
            return current.needsDrink(player);
        } catch (Exception | LinkageError e) {
            return disable(e);
        }
    }

    /** True when a supported thirst mod is loaded. */
    public static boolean isActive() {
        return adapter() != null;
    }

    public static void drink(Player player, int quality) {
        Adapter current = adapter();
        if (current == null) {
            return;
        }
        try {
            current.drink(player, quality);
        } catch (Exception | LinkageError e) {
            disable(e);
        }
    }

    /** Takes one drink out of the pack; returns its quality code, or {@link #NONE}. */
    public static int takeDrink(ItemStack pack) {
        if (pack.getItem() instanceof HydrationPackItem) {
            return HydrationPackItem.takeDrink(pack);
        }
        Adapter current = adapter();
        if (current == null) {
            return NONE;
        }
        try {
            return current.takeFlaskDrink(pack);
        } catch (Exception | LinkageError e) {
            disable(e);
            return NONE;
        }
    }

    private static boolean disable(Throwable e) {
        LOGGER.error("Thirst mod integration failed, auto-hydrate disabled", e);
        adapter = null;
        return false;
    }

    private static Adapter adapter() {
        if (!resolved) {
            resolved = true;
            try {
                adapter = detect();
            } catch (Exception | LinkageError e) {
                LOGGER.error("Could not hook into the thirst mod", e);
            }
        }
        return adapter;
    }

    private static Adapter detect() throws Exception {
        ModList mods = ModList.get();
        if (mods.isLoaded(TWT2)) {
            return thirstWasTaken2();
        }
        if (mods.isLoaded("legendarysurvivaloverhaul")) {
            return legendarySurvivalOverhaul();
        }
        if (mods.isLoaded("thirst")) {
            // Thirst Was Taken and Thirst Was Reclaimed share the mod id but not the package.
            for (String root : new String[]{"dev.ghen.thirst", "cn.mlus.thirst"}) {
                try {
                    Class.forName(root + ".api.ThirstHelper");
                    return thirstWasTaken(root);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
        return null;
    }

    private static ItemStack waterBottle() {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
    }

    private static Adapter thirstWasTaken2() throws Exception {
        Class<?> api = Class.forName(TWT2_PACKAGE + "api.ThirstApi");
        Method isEnabled = api.getMethod("isEnabled", Player.class);
        Method thirst = api.getMethod("thirst", Player.class);
        Method maxThirst = api.getMethod("maxThirst");
        Method maxPurity = api.getMethod("maxPurity");
        Method waterBottle = api.getMethod("waterBottle", int.class);
        Method thirstValues = api.getMethod("thirstValues", ItemStack.class);
        Method drink = api.getMethod("drink", Player.class, int.class, int.class);
        Class<?> purity = Class.forName(TWT2_PACKAGE + "purity.WaterPurity");
        Class<?> quality = Class.forName(TWT2_PACKAGE + "purity.WaterQuality");
        Method setQuality = purity.getMethod("setQuality", ItemStack.class, quality);
        Method applyEffects = purity.getMethod("applyEffects", Player.class, ItemStack.class);
        Object salt = quality.getField("SALT").get(null);
        Class<?> skin = Class.forName(TWT2_PACKAGE + "item.WaterskinItem");
        Method servings = skin.getMethod("servings", ItemStack.class);
        Method removeWater = skin.getMethod("removeWater", ItemStack.class, int.class);
        Class<?> waterData = Class.forName(TWT2_PACKAGE + "platform.ItemWaterData");
        Method grade = waterData.getMethod("grade", ItemStack.class);
        Method salty = waterData.getMethod("salty", ItemStack.class);
        return new Adapter() {
            @Override
            public boolean needsDrink(Player player) throws Exception {
                return (boolean) isEnabled.invoke(null, player)
                        && (int) thirst.invoke(null, player) < (int) maxThirst.invoke(null);
            }

            @Override
            public void drink(Player player, int code) throws Exception {
                ItemStack bottle = (ItemStack) waterBottle.invoke(null, code >= 0 ? code : maxPurity.invoke(null));
                if (code == SALT) {
                    bottle = (ItemStack) setQuality.invoke(null, bottle, salt);
                }
                int[] values = (int[]) thirstValues.invoke(null, bottle);
                if (values != null) {
                    drink.invoke(null, player, values[0], values[1]);
                    applyEffects.invoke(null, player, bottle);
                }
            }

            @Override
            public int takeFlaskDrink(ItemStack pack) throws Exception {
                if ((int) servings.invoke(null, pack) <= 0) {
                    return NONE;
                }
                Integer purityGrade = (Integer) grade.invoke(null, pack);
                int code = (boolean) salty.invoke(null, pack) ? SALT : purityGrade != null ? purityGrade : UNKNOWN;
                return (boolean) removeWater.invoke(null, pack, 1) ? code : NONE;
            }
        };
    }

    private static Adapter thirstWasTaken(String root) throws Exception {
        Class<?> iThirst = Class.forName(root + ".foundation.common.capability.IThirst");
        Field capField = Class.forName(root + ".foundation.common.capability.ModCapabilities").getField("PLAYER_THIRST");
        @SuppressWarnings("unchecked")
        Capability<Object> capability = (Capability<Object>) capField.get(null);
        Method getThirst = iThirst.getMethod("getThirst");
        Class<?> purity = Class.forName(root + ".content.purity.WaterPurity");
        Method addPurity = purity.getMethod("addPurity", ItemStack.class, int.class);
        int maxPurity = purity.getField("MAX_PURITY").getInt(null);
        Method drink = Class.forName(root + ".content.thirst.PlayerThirst").getMethod("drink", ItemStack.class, Player.class);
        return new Adapter() {
            @Override
            public boolean needsDrink(Player player) {
                return player.getCapability(capability).map(data -> {
                    try {
                        return (int) getThirst.invoke(data) < 20;
                    } catch (ReflectiveOperationException e) {
                        throw new IllegalStateException(e);
                    }
                }).orElse(false);
            }

            @Override
            public void drink(Player player, int quality) throws Exception {
                drink.invoke(null, addPurity.invoke(null, waterBottle(), maxPurity), player);
            }
        };
    }

    private static Adapter legendarySurvivalOverhaul() throws Exception {
        Method getCapability = Class.forName("sfiomn.legendarysurvivaloverhaul.util.CapabilityUtil")
                .getMethod("getThirstCapability", Player.class);
        Method atMax = Class.forName("sfiomn.legendarysurvivaloverhaul.api.thirst.IThirstCapability")
                .getMethod("isHydrationLevelAtMax");
        Class<?> util = Class.forName(LSO_PACKAGE + "api.thirst.ThirstUtil");
        Class<?> hydrationEnum = Class.forName(LSO_PACKAGE + "api.thirst.HydrationEnum");
        Object purified = hydrationEnum.getField("PURIFIED").get(null);
        Object normal = hydrationEnum.getField("NORMAL").get(null);
        Method setEnum = util.getMethod("setHydrationEnumTag", ItemStack.class, hydrationEnum);
        Method getEnum = util.getMethod("getHydrationEnumTag", ItemStack.class);
        Method getCapacity = util.getMethod("getCapacityTag", ItemStack.class);
        Method takeDrink = util.getMethod("takeDrink", Player.class, ItemStack.class);
        Method shrink = Class.forName(LSO_PACKAGE + "common.items.drink.CanteenItem")
                .getMethod("shrinkCapacity", ItemStack.class);
        Item largeCanteen = ForgeRegistries.ITEMS.getValue(new ResourceLocation(LSO, "large_canteen"));
        return new Adapter() {
            @Override
            public boolean needsDrink(Player player) throws Exception {
                Object data = getCapability.invoke(null, player);
                return data != null && !(boolean) atMax.invoke(data);
            }

            /** Same data the Large Canteen drinks with: its own consumable entry, normal or purified. */
            @Override
            public void drink(Player player, int quality) throws Exception {
                ItemStack canteen = new ItemStack(largeCanteen);
                setEnum.invoke(null, canteen, quality == 0 ? normal : purified);
                takeDrink.invoke(null, player, canteen);
            }

            @Override
            public int takeFlaskDrink(ItemStack pack) throws Exception {
                if ((int) getCapacity.invoke(null, pack) <= 0) {
                    return NONE;
                }
                int code = getEnum.invoke(null, pack) == purified ? 1 : 0;
                shrink.invoke(null, pack);
                return code;
            }
        };
    }
}
