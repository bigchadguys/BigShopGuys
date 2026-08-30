package com.bigchadguys.bigshopguys.client;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.block.ModBlocks;
import com.bigchadguys.bigshopguys.client.model.ShopBakeModel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = BigShopGuys.MOD_ID, value = Dist.CLIENT)
public final class ClientModelEvents {

    private static final ModelResourceLocation TEST_SHOP_MODEL =
            ModelResourceLocation.standalone(
                    ResourceLocation.fromNamespaceAndPath(
                            BigShopGuys.MOD_ID,
                            "block/shop/test_shop"
                    )
            );
    private static final ModelResourceLocation TARO_SHOP_MODEL =
            ModelResourceLocation.standalone(
                    ResourceLocation.fromNamespaceAndPath(
                            BigShopGuys.MOD_ID,
                            "block/shop/taro_shop"
                    )
            );

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event
    ) {
        event.register(TEST_SHOP_MODEL);
        event.register(TARO_SHOP_MODEL);
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        BakedModel testShopModel =
                event.getModels().get(
                        TEST_SHOP_MODEL
                );
        BakedModel taroShopModel =
                event.getModels().get(
                        TARO_SHOP_MODEL
                );

        if (testShopModel == null || taroShopModel == null) {
            BigShopGuys.LOGGER.warn("Models are not loaded!");
            return;
        }

        var shopModelLocation = BlockModelShaper.stateToModelLocation(
                ModBlocks.SHOP.get().defaultBlockState()
        );

        event.getModels().computeIfPresent(
                shopModelLocation,
                (location, originalModel) ->
                        new ShopBakeModel(
                                originalModel,
                                testShopModel,
                                taroShopModel
                        )
        );
    }
    private ClientModelEvents() {

    }
}
