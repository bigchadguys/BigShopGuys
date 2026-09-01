package com.bigchadguys.bigshopguys.client.model;

import com.bigchadguys.bigshopguys.component.ModDataComponents;
import com.bigchadguys.bigshopguys.shop.ShopDefinition;
import com.bigchadguys.bigshopguys.shop.ShopRegistries;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopItemBakedModel extends BakedModelWrapper<BakedModel> {
    private final Map<ResourceLocation, BakedModel> shopModels;
    private final Map<ResourceLocation, BakedModel> modelsById;
    private final Map<ResourceLocation, BakedModel> resolvedModels = new HashMap<>();

    public ShopItemBakedModel(
            BakedModel originalModel,
            Map<ResourceLocation, BakedModel> shopModels,
            Map<ResourceLocation, BakedModel> modelsById
    ) {
        super(originalModel);

        this.shopModels = shopModels;
        this.modelsById = modelsById;
    }

    private BakedModel selectModel(ItemStack stack) {
        ResourceLocation shopId =
                stack.get(
                        ModDataComponents.SHOP_ID.get()
                );

        if (shopId == null) {
            return originalModel;
        }

        BakedModel cached =
                resolvedModels.get(shopId);

        if (cached != null) {
            return cached;
        }

        BakedModel resolved =
                resolveModel(shopId);

        /*
         * Only cache when we have an active client world.
         *
         * This avoids accidentally caching a fallback model
         * if an ItemStack gets rendered before the synced
         * ShopDefinition registry is available.
         */
        if (Minecraft.getInstance().level != null) {
            resolvedModels.put(
                    shopId,
                    resolved
            );
        }

        return resolved;
    }

    private BakedModel resolveModel(
            ResourceLocation shopId
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level != null) {

            var registry =
                    minecraft.level
                            .registryAccess()
                            .registryOrThrow(
                                    ShopRegistries
                                            .SHOP_DEFINITION_REGISTRY_KEY
                            );

            ShopDefinition definition =
                    registry.get(shopId);

            if (definition != null) {

                ResourceLocation explicitModelId =
                        definition.display()
                                .model()
                                .orElse(null);

                if (explicitModelId != null) {

                    BakedModel explicitModel =
                            modelsById.get(
                                    explicitModelId
                            );

                    if (explicitModel != null) {
                        return explicitModel;
                    }
                }
            }
        }

        return shopModels.getOrDefault(
                shopId,
                originalModel
        );
    }

    @Override
    public @NotNull BakedModel applyTransform(
            @NotNull ItemDisplayContext transformType,
            @NotNull PoseStack poseStack,
            boolean applyLeftHandTransform
    ) {
        originalModel.applyTransform(
                transformType,
                poseStack,
                applyLeftHandTransform
        );

        return this;
    }

    @Override
    public @NotNull List<BakedModel> getRenderPasses(@NotNull ItemStack itemStack, boolean fabulous) {
        BakedModel selected = selectModel(itemStack);
        return selected.getRenderPasses(itemStack, fabulous);
    }
}
