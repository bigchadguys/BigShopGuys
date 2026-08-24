package com.bigchadguys.bigshopguys.shop.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public record ShopTradeRecipe (
    ResourceLocation shop,
    List<ShopCost> costs,
    ItemStack result
) implements Recipe<ShopRecipeInput> {
    @Override
    public boolean matches(ShopRecipeInput input, Level level) {
        return shop.equals(input.shopId());
    }

    @Override
    public ItemStack assemble(
            ShopRecipeInput input,
            HolderLookup.Provider registries
    ) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(
            HolderLookup.Provider registries
    ) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (ShopCost cost : costs) {
            ingredients.add(cost.ingredient());
        }

        return ingredients;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModShopRecipes.SHOP_TRADE_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModShopRecipes.SHOP_TRADE_RECIPE_TYPE.get();
    }
}
