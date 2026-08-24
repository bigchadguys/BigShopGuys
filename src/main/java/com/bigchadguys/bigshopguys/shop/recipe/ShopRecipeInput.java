package com.bigchadguys.bigshopguys.shop.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ShopRecipeInput (
    ResourceLocation shopId
) implements RecipeInput {

    @Override
    public ItemStack getItem(int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }
}
