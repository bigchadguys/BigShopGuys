package com.bigchadguys.bigshopguys.shop.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public final class ShopTradeRecipeSerializer
        implements RecipeSerializer<ShopTradeRecipe> {

    public static final MapCodec<ShopTradeRecipe> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceLocation.CODEC
                            .fieldOf("shop")
                            .forGetter(ShopTradeRecipe::shop),
                    ShopCost.CODEC
                            .listOf()
                            .fieldOf("costs")
                            .forGetter(ShopTradeRecipe::costs),
                    ItemStack.CODEC
                            .fieldOf("result")
                            .forGetter(ShopTradeRecipe::result)
            ).apply(instance, ShopTradeRecipe::new));

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            ShopTradeRecipe
            > STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(
                    CODEC.codec()
            );

    @Override
    public @NotNull MapCodec<ShopTradeRecipe> codec() {
        return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, ShopTradeRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}

