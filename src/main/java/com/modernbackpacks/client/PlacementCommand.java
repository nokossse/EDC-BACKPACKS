package com.modernbackpacks.client;

import com.modernbackpacks.ModernBackpacks;
import com.modernbackpacks.client.render.BackpackPlacement;
import com.modernbackpacks.item.BackpackHelper;
import com.modernbackpacks.item.BackpackItem;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Dev tool: tune how the backpack in your main hand is placed, live, without rebuilding.
 * /mbplace first|third|gui|ground x|y|z|rx|ry|rz|s <value>   (x,y,z in pixels, r in degrees)
 * /mbplace weapon 1|2 x|y|z|rx|ry|rz|s <value>   -> pose of the TACZ gun on weaponDummy1|2 of the held (else worn) backpack
 * /mbplace armor <px>   -> backpack shift backwards when a chestplate is worn
 * /mbplace print   -> final values to paste into BackpackPlacement
 */
@Mod.EventBusSubscriber(modid = ModernBackpacks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class PlacementCommand {
    private static final List<String> AXES = List.of("x", "y", "z", "rx", "ry", "rz", "s");

    private PlacementCommand() {
    }

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("mbplace");
        for (String group : BackpackPlacement.GROUPS) {
            LiteralArgumentBuilder<CommandSourceStack> g = Commands.literal(group);
            for (int i = 0; i < AXES.size(); i++) {
                int axis = i;
                g.then(Commands.literal(AXES.get(i)).then(Commands.argument("value", FloatArgumentType.floatArg())
                        .executes(c -> set(c.getSource(), group, axis, FloatArgumentType.getFloat(c, "value")))));
            }
            root.then(g);
        }
        LiteralArgumentBuilder<CommandSourceStack> weapon = Commands.literal("weapon");
        for (int slot = 0; slot < BackpackPlacement.MAX_WEAPON_SLOTS; slot++) {
            int slotIndex = slot;
            LiteralArgumentBuilder<CommandSourceStack> n = Commands.literal(String.valueOf(slot + 1));
            for (int i = 0; i < AXES.size(); i++) {
                int axis = i;
                n.then(Commands.literal(AXES.get(i)).then(Commands.argument("value", FloatArgumentType.floatArg())
                        .executes(c -> setWeapon(c.getSource(), slotIndex, axis, FloatArgumentType.getFloat(c, "value")))));
            }
            weapon.then(n);
        }
        root.then(weapon);
        root.then(Commands.literal("armor").then(Commands.argument("value", FloatArgumentType.floatArg()).executes(c -> {
            BackpackPlacement.armorOffsetPx = FloatArgumentType.getFloat(c, "value");
            c.getSource().sendSuccess(() -> Component.literal("armor offset: " + BackpackPlacement.armorOffsetPx + " px"), false);
            return 1;
        })));
        root.then(Commands.literal("print").executes(c -> print(c.getSource())));
        event.getDispatcher().register(root);
    }

    private static int set(CommandSourceStack source, String group, int axis, float value) {
        var player = Minecraft.getInstance().player;
        if (player == null || !(player.getMainHandItem().getItem() instanceof BackpackItem item)) {
            source.sendFailure(Component.literal("Tiens un sac dans la main principale."));
            return 0;
        }
        float[] v = BackpackPlacement.tunable(item.getKind(), group);
        v[axis] = value;
        source.sendSuccess(() -> Component.literal(item.getKind() + " " + group + ": " + format(v)), false);
        return 1;
    }

    /** The backpack in the main hand, else the one worn on the back. */
    private static BackpackItem tunedBackpack() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return null;
        }
        if (player.getMainHandItem().getItem() instanceof BackpackItem held) {
            return held;
        }
        return BackpackHelper.getWornBackpack(player).getItem() instanceof BackpackItem worn ? worn : null;
    }

    private static int setWeapon(CommandSourceStack source, int slot, int axis, float value) {
        BackpackItem item = tunedBackpack();
        if (item == null) {
            source.sendFailure(Component.literal("Tiens ou porte un sac."));
            return 0;
        }
        float[] v = BackpackPlacement.weaponTunable(item.getKind(), slot);
        v[axis] = value;
        source.sendSuccess(() -> Component.literal(item.getKind() + " weapon" + (slot + 1) + ": " + format(v)), false);
        return 1;
    }

    private static int print(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("armorOffsetPx = " + BackpackPlacement.armorOffsetPx + "F"), false);
        BackpackPlacement.weapons().forEach((kind, slots) -> {
            for (int i = 0; i < slots.length; i++) {
                if (slots[i] != null) {
                    String line = kind + " weapon" + (i + 1) + " -> {" + format(slots[i]) + "}";
                    source.sendSuccess(() -> Component.literal(line), false);
                }
            }
        });
        BackpackPlacement.overrides().forEach((kind, groups) -> groups.forEach((group, v) -> {
            String line = kind + " " + group + " -> Pose.of(" + format(v) + ")";
            source.sendSuccess(() -> Component.literal(line), false);
        }));
        return 1;
    }

    private static String format(float[] v) {
        return String.format("%sF, %sF, %sF, %sF, %sF, %sF, %sF", v[0], v[1], v[2], v[3], v[4], v[5], v[6]);
    }
}
