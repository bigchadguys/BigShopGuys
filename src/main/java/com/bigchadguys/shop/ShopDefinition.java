package com.bigchadguys.shop;

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
                        .fieldOf("display")
                        .forGetter(ShopDefinition::display),
                ShopSettings.CODEC
                        .fieldOf("settings")
                        .forGetter(ShopDefinition::settings)
            ).apply(instance, ShopDefinition::new));
}
