package com.bigchadguys.bigshopguys.shop.transaction;

import com.bigchadguys.bigshopguys.menu.ShopMenu;
import com.bigchadguys.bigshopguys.shop.ShopRegistries;
import com.bigchadguys.bigshopguys.shop.block.ShopBlockEntity;
import com.bigchadguys.bigshopguys.shop.recipe.ShopTradeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public final class ShopTransactionService {

    public static Optional<ShopTradeRecipe> validateTradeRequest(
            ServerPlayer player,
            BlockPos shopPos,
            ResourceLocation recipeId
    ) {
        ServerLevel level = player.serverLevel();

        // 1. Player must actually have a ShopMenu open.
        if (!(player.containerMenu instanceof ShopMenu shopMenu)) {return Optional.empty();}

        // 2. The menu must belong to the block position supplied by the request.
        if (!shopMenu.getShopPos().equals(shopPos)) {return Optional.empty();}

        // 3. Make sure the player is still allowed to use this menu.
        if (!shopMenu.stillValid(player)) {return Optional.empty();}

        // 4. Don't poke an unloaded chunk because a packet may arrive after the world changes.
        if (!level.hasChunkAt(shopPos)) {return Optional.empty();}

        // 5. There must really be our ShopBlockEntity there.
        BlockEntity blockEntity = level.getBlockEntity(shopPos);
        if (!(blockEntity instanceof ShopBlockEntity shopBlockEntity)) {return Optional.empty();}

        // 6. The physical shop must have a configured shop ID.
        var shopIdOptional = shopBlockEntity.getShopId();

        if (shopIdOptional.isEmpty()) {return Optional.empty();}

        ResourceLocation actualShopId = shopIdOptional.get();

        // 7. The server-created menu should still agree with the actual BlockEntity.
        if (!shopMenu.getShopId().equals(actualShopId)) {return Optional.empty();}

        // 8. Resolve the recipe by ID FROM THE SERVER'S RecipeManager.
        var recipeHolderOptional = level.getRecipeManager().byKey(recipeId);

        if (recipeHolderOptional.isEmpty()) {return Optional.empty();}

        var recipe = recipeHolderOptional.get().value();

        // 9. It must actually be one of OUR shop recipes.
        if (!(recipe instanceof ShopTradeRecipe trade)) {return Optional.empty();}

        // 10. Does this trade belong to THIS shop?
        if (!trade.shop().equals(actualShopId)) {return Optional.empty();}

        return Optional.of(trade);
    }

    public static boolean canAfford(ServerPlayer player, ShopTradeRecipe trade) {
        return ShopPaymentPlan
                .create(player.getInventory(), trade)
                .isPresent();
    }

    public static boolean consumePayment(ServerPlayer player, ShopTradeRecipe trade) {
        return consumePayment(player, trade, 1);
    }

    public static boolean consumePayment(ServerPlayer player, ShopTradeRecipe trade, int purchaseCount) {
        var paymentPlan =
                ShopPaymentPlan.create(
                        player.getInventory(),
                        trade,
                        purchaseCount
                );

        if (paymentPlan.isEmpty()) {
            return false;
        }

        paymentPlan.get().consume(player);

        return true;
    }

    public static void giveResult(ServerPlayer player, ShopTradeRecipe trade) {
        ItemStack resultStack =
                trade.result().copy();

        if (resultStack.isEmpty()) {
            return;
        }

        player.getInventory().add(resultStack);

        if (!resultStack.isEmpty()) {
            player.drop(
                    resultStack,
                    false
            );
        }

        player.getInventory().setChanged();
    }

    public static void giveResult(ServerPlayer player, ShopTradeRecipe trade, int purchaseCount) {
        for (int i = 0; i < purchaseCount; i++) {
            giveResult(
                    player,
                    trade
            );
        }
    }

    public static boolean allowsBulkPurchase(ServerPlayer player) {
        if(!(player.containerMenu instanceof ShopMenu shopMenu)){
            return false;
        }

        var shopRegistry =
                player.serverLevel().registryAccess().registryOrThrow(ShopRegistries.SHOP_DEFINITION_REGISTRY_KEY);

        var definition =
                shopRegistry.get(
                        shopMenu.getShopId()
                );

        if (definition == null) {
            return false;
        }

        return definition
                .settings()
                .allowBulkPurchase();
    }

    private ShopTransactionService() {

    }
}