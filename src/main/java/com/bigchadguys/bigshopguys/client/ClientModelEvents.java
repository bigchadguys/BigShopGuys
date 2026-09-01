package com.bigchadguys.bigshopguys.client;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.block.ModBlocks;
import com.bigchadguys.bigshopguys.client.model.ShopBakeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = BigShopGuys.MOD_ID, value = Dist.CLIENT)
public final class ClientModelEvents {

private static final String SHOP_MODEL_RESOURCE_PREFIX =
        "models/block/shop/";
private static final String SHOP_MODEL_PATH_PREFIX =
        "block/shop/";

private static final Map<ResourceLocation, ModelResourceLocation> SHOP_MODEL_LOCATIONS = new HashMap<>();
private static final Map<ResourceLocation, ModelResourceLocation> MODEL_LOCATIONS_BY_ID = new HashMap<>();

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        SHOP_MODEL_LOCATIONS.clear();
        MODEL_LOCATIONS_BY_ID.clear();

        Minecraft.getInstance()
                .getResourceManager()
                .listResources("models/block/shop", location ->
                                location.getPath()
                                        .startsWith(
                                                SHOP_MODEL_RESOURCE_PREFIX
                                        )
                                        &&
                                        location.getPath()
                                                .endsWith(".json")
                )
                .keySet()
                .forEach(resourceLocation -> {

                    String resourcePath =
                            resourceLocation.getPath();

                    String shopPath =
                            resourcePath.substring(
                                    SHOP_MODEL_RESOURCE_PREFIX.length(),
                                    resourcePath.length()
                                            - ".json".length()
                            );

                    ResourceLocation shopId =
                            ResourceLocation.fromNamespaceAndPath(
                                    resourceLocation.getNamespace(),
                                    shopPath
                            );

                    ResourceLocation modelId =
                            ResourceLocation.fromNamespaceAndPath(
                                    resourceLocation.getNamespace(),
                                    SHOP_MODEL_PATH_PREFIX
                                            + shopPath
                            );

                    ModelResourceLocation modelLocation =
                            ModelResourceLocation.standalone(
                                    modelId
                            );

                    SHOP_MODEL_LOCATIONS.put(shopId, modelLocation);
                    MODEL_LOCATIONS_BY_ID.put(shopId, modelLocation);

                    event.register(modelLocation);

                    BigShopGuys.LOGGER.debug(
                            "Registered shop model {} for {}",
                            modelId,
                            shopId
                    );
                });
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<ResourceLocation, BakedModel> bakedShopModels = new HashMap<>();
        Map<ResourceLocation, BakedModel> bakedModelsById = new HashMap<>();

        SHOP_MODEL_LOCATIONS.forEach(
                (shopId, modelLocation) -> {

                    BakedModel model =
                            event.getModels()
                                    .get(modelLocation);

                    if (model == null) {
                        BigShopGuys.LOGGER.warn(
                                "Could not load model {} for shop {}",
                                modelLocation,
                                shopId
                        );

                        return;
                    }

                    bakedShopModels.put(
                            shopId,
                            model
                    );
                }
        );

        MODEL_LOCATIONS_BY_ID.forEach(
                (modelId, modelLocation) -> {

                    BakedModel model =
                            event.getModels()
                                    .get(modelLocation);

                    if (model == null) {
                        BigShopGuys.LOGGER.warn(
                                "Could not load shop model {}",
                                modelId
                        );

                        return;
                    }

                    bakedModelsById.put(
                            modelId,
                            model
                    );
                }
        );

        var shopModelLocation =
                BlockModelShaper.stateToModelLocation(
                        ModBlocks.SHOP.get()
                                .defaultBlockState()
                );

        event.getModels()
                .computeIfPresent(
                        shopModelLocation,
                        (location, originalModel) ->
                                new ShopBakeModel(
                                        originalModel,
                                        Map.copyOf(bakedShopModels),
                                        Map.copyOf(bakedModelsById)
                                )
                );

        BigShopGuys.LOGGER.info(
                "Loaded {} shop block models",
                bakedShopModels.size()
        );
    }
    private ClientModelEvents() {

    }
}
