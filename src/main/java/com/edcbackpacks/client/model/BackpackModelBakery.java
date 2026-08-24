package com.edcbackpacks.client.model;

import com.edcbackpacks.item.BackpackKind;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.entity.Entity;

public final class BackpackModelBakery {
    private BackpackModelBakery() {
    }

    public static EntityModel<?> bake(EntityModelSet models, BackpackKind kind) {
        return switch (kind) {
            case RAID_BACKPACK -> new RaidBackpackModel<Entity>(models.bakeLayer(RaidBackpackModel.LAYER_LOCATION));
            case LARGE_HIKING_GREEN, LARGE_HIKING_BLUE, LARGE_HIKING_RED ->
                    new LargeHikingGreenModel<Entity>(models.bakeLayer(LargeHikingGreenModel.LAYER_LOCATION));
            case DAYPACK_SAND -> new DaypackSandModel<Entity>(models.bakeLayer(DaypackSandModel.LAYER_LOCATION));
            case DUFFLE_ADI -> new DuffleAdiModel<Entity>(models.bakeLayer(DuffleAdiModel.LAYER_LOCATION));
        };
    }
}
