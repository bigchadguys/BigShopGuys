package com.bigchadguys.bigshopguys.client.model;

import com.bigchadguys.bigshopguys.shop.block.ShopBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ShopBakeModel extends BakedModelWrapper<BakedModel> {
    private final Map<ResourceLocation, BakedModel> shopModels;

    public ShopBakeModel(BakedModel originalModel, Map<ResourceLocation, BakedModel> shopModels) {
        super(originalModel);

        this.shopModels = shopModels;
    }

    private BakedModel selectModel(ModelData data) {
        ResourceLocation shopId = data.get(ShopBlockEntity.SHOP_ID_PROPERTY);

        if (shopId == null) {
            return originalModel;
        }

        return shopModels.getOrDefault(shopId, originalModel);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            @NotNull RandomSource rand,
            @NotNull ModelData modelData,
            RenderType renderType
    ) {
        BakedModel selected = selectModel(modelData);
        return selected.getQuads(state, side, rand, modelData, renderType);
    }
}
