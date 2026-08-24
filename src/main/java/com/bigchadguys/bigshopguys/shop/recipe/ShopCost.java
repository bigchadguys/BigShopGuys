package com.bigchadguys.bigshopguys.shop.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record ShopCost (
        Ingredient ingredient,
        int count
) {
    public static final Codec<ShopCost> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY
                            .fieldOf("ingredient")
                            .forGetter(ShopCost::ingredient),
                    Codec.intRange(1,64)
                            .fieldOf("count")
                            .forGetter(ShopCost::count)
            ).apply(instance, ShopCost::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopCost> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    ShopCost::ingredient,
                    ByteBufCodecs.VAR_INT,
                    ShopCost::count,
                    ShopCost::new
            );
}