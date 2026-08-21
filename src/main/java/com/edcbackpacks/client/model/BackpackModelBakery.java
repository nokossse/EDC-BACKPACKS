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
            case LARGE_HIKING_GREEN -> new LargeHikingGreenModel<Entity>(models.bakeLayer(LargeHikingGreenModel.LAYER_LOCATION));
        };
    }
}
