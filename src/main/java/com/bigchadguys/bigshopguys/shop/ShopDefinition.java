package com.bigchadguys.bigshopguys.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record ShopDefinition (
    Component Title,
    ShopDisplay display,
    ShopSettings settings
) {
    public static final Codec<ShopDefinition> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                ComponentSerialization.CODEC
                        .fieldOf("title")
                        .forGetter(ShopDefinition::Title),
                ShopDisplay.CODEC
                        .optionalFieldOf("display", ShopDisplay.DEFAULT)
                        .forGetter(ShopDefinition::display),
                ShopSettings.CODEC
                        .optionalFieldOf("settings", ShopSettings.DEFAULT)
                        .forGetter(ShopDefinition::settings)
            ).apply(instance, ShopDefinition::new));
}
