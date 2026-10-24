package com.modernbackpacks.item;

import com.modernbackpacks.compat.ThirstCompat;
import com.modernbackpacks.config.ModConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Fallback hydration pack, used when Thirst Was Taken 2 is absent (with it the pack is a real TWT2 flask, see
 * {@link ThirstCompat#createHydrationPack()}). Water is counted in drinks (three to a bucket), drunk by hand with
 * right-click and filled from a water source. Stored as a water-only fluid so tanks and buckets can fill it too.
 */
public class HydrationPackItem extends Item {
    private static final int BAR_COLOR = 0x3F76E4;
    private static final int SOURCE_FILL_MB = 1000;
    private static final int DRINKS_PER_BUCKET = 3;
    private static final int DRINK_MB = 334;
    private static final int DRINK_TICKS = 32;

    public HydrationPackItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemStack(stack, ModConfigs.hydrationCapacityMb()) {
            @Override
            public boolean isFluidValid(int tank, FluidStack fluid) {
                return fluid.getFluid() == Fluids.WATER;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return fluid.getFluid() == Fluids.WATER;
            }
        };
    }

    public static int getWater(ItemStack stack) {
        return FluidUtil.getFluidContained(stack).map(FluidStack::getAmount).orElse(0);
    }

    public static int drinks(ItemStack stack) {
        return toDrinks(getWater(stack));
    }

    private static int toDrinks(int mb) {
        return (mb * DRINKS_PER_BUCKET + 999) / 1000;
    }

    /** Removes one drink; returns {@link ThirstCompat#UNKNOWN} (this water has no quality), or {@link ThirstCompat#NONE}. */
    public static int takeDrink(ItemStack stack) {
        boolean taken = FluidUtil.getFluidHandler(stack)
                .map(handler -> handler.drain(DRINK_MB, IFluidHandler.FluidAction.EXECUTE).getAmount() > 0)
                .orElse(false);
        return taken ? ThirstCompat.UNKNOWN : ThirstCompat.NONE;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getWater(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.min(13, Math.round(13F * getWater(stack) / ModConfigs.hydrationCapacityMb()));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int water = getWater(stack);
        if (water <= 0) {
            tooltip.add(Component.translatable("tooltip.modern_backpacks.hydration_pack.empty").withStyle(ChatFormatting.GRAY));
            return;
        }
        tooltip.add(Component.translatable("tooltip.modern_backpacks.hydration_pack.servings",
                toDrinks(water), toDrinks(ModConfigs.hydrationCapacityMb())).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.modern_backpacks.hydration_pack.content",
                water, ModConfigs.hydrationCapacityMb()).withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos pos = hit.getBlockPos();
        if (hit.getType() == BlockHitResult.Type.BLOCK && level.getFluidState(pos).isSource()
                && level.getFluidState(pos).getType() == Fluids.WATER && level.mayInteract(player, pos)) {
            return fill(level, player, stack, pos);
        }
        if (ThirstCompat.isActive() && getWater(stack) > 0) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    private InteractionResultHolder<ItemStack> fill(Level level, Player player, ItemStack stack, BlockPos pos) {
        boolean filled = FluidUtil.getFluidHandler(stack)
                .map(handler -> handler.fill(new FluidStack(Fluids.WATER, SOURCE_FILL_MB),
                        level.isClientSide ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE) > 0)
                .orElse(false);
        if (!filled) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
            // Thirsty Bottles makes filling a bottle consume the source block; do the same.
            if (ModList.get().isLoaded("thirstybottles") && level.getBlockState(pos).getBlock() instanceof LiquidBlock) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return DRINK_TICKS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            int quality = takeDrink(stack);
            if (quality != ThirstCompat.NONE) {
                ThirstCompat.drink(player, quality);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }
        return stack;
    }
}
