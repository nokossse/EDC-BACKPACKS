package com.modernbackpacks.client.model;

import com.modernbackpacks.item.BackpackKind;

public final class BackpackModelBakery {
    private BackpackModelBakery() {
    }

    public static BackpackModel bake(BackpackKind kind) {
        return new BedrockBackpackModel(kind.getBedrockModel());
    }
}
