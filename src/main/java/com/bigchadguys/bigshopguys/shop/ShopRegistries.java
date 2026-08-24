package com.bigchadguys.bigshopguys.shop;

import com.bigchadguys.bigshopguys.BigShopGuys;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class ShopRegistries {

    public static final ResourceKey<Registry<ShopDefinition>> SHOP_DEFINITION_REGISTRY_KEY =
            ResourceKey.createRegistryKey(
                    ResourceLocation.fromNamespaceAndPath(
                            BigShopGuys.MOD_ID,
                            "shop")
            );

    public static void register(
            DataPackRegistryEvent.NewRegistry event
    ) {
        event.dataPackRegistry(
                SHOP_DEFINITION_REGISTRY_KEY,
                ShopDefinition.CODEC,
                ShopDefinition.CODEC
        );
    }

    private ShopRegistries() {

    }
}
