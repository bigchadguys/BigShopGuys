package com.bigchadguys.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record ShopDisplay(
    Optional<ResourceLocation> model,
    Optional<ResourceLocation> icon,
    Optional<ResourceLocation> theme
) {
    public static final ShopDisplay DEFAULT =
            new ShopDisplay(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
            );

    public static final Codec<ShopDisplay> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC
                        .optionalFieldOf("model")
                        .forGetter(ShopDisplay::model),
                ResourceLocation.CODEC
                        .optionalFieldOf("icon")
                        .forGetter(ShopDisplay::icon),
                ResourceLocation.CODEC
                        .optionalFieldOf("theme")
                        .forGetter(ShopDisplay::theme)
    ).apply(instance, ShopDisplay::new));
}
