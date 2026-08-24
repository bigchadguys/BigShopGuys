package com.bigchadguys.bigshopguys.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ShopSettings(
    boolean allowBulkPurchase,
    boolean showPlayerInventory
) {
    public static final ShopSettings DEFAULT =
            new ShopSettings(
                    true,
                    true
            );

    public static final Codec<ShopSettings> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL
                        .optionalFieldOf("allow_bulk_purchase", true)
                        .forGetter(ShopSettings::allowBulkPurchase),
                Codec.BOOL
                        .optionalFieldOf("show_player_inventory", true)
                        .forGetter(ShopSettings::showPlayerInventory)
            ).apply(instance, ShopSettings::new));
}
