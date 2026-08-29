package com.bigchadguys.bigshopguys.network;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.shop.transaction.ShopPaymentPlan;
import com.bigchadguys.bigshopguys.shop.transaction.ShopTransactionService;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BuyTradePayload(BlockPos shopPos, ResourceLocation recipeId, boolean bulk
) implements CustomPacketPayload {

    public static final Type<BuyTradePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BigShopGuys.MOD_ID, "buy_trade"));

    public static final StreamCodec<ByteBuf, BuyTradePayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    BuyTradePayload::shopPos,
                    ResourceLocation.STREAM_CODEC,
                    BuyTradePayload::recipeId,
                    ByteBufCodecs.BOOL,
                    BuyTradePayload::bulk,
                    BuyTradePayload::new
            );

    public static void handle(
            BuyTradePayload payload,
            IPayloadContext context
    ) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        var validatedTrade =
                ShopTransactionService.validateTradeRequest(
                        player,
                        payload.shopPos(),
                        payload.recipeId()
                );

        if (validatedTrade.isEmpty()) {

            BigShopGuys.LOGGER.warn(
                    "Rejected shop trade request from {}: {} at {}",
                    player.getName().getString(),
                    payload.recipeId(),
                    payload.shopPos()
            );

            return;
        }

        BigShopGuys.LOGGER.info(
                "Validated trade {} for player {} at shop {}",
                payload.recipeId(),
                player.getName().getString(),
                payload.shopPos()
        );

        var trade = validatedTrade.get();

        boolean bulkRequested = payload.bulk();
        boolean bulkAllowed = ShopTransactionService.allowsBulkPurchase(player);
        boolean effectiveBulk = bulkRequested && bulkAllowed;

        int purchaseCount = 1;

        if (effectiveBulk) {
            purchaseCount = ShopPaymentPlan.maxAffordablePurchases(player.getInventory(), trade);
        }

        BigShopGuys.LOGGER.info(
                "Trade {} requested by {} - requestedBulk={}, allowedBulk={}, effectiveBulk={}, maxAffordable={}",
                payload.recipeId(),
                player.getName().getString(),
                bulkRequested,
                bulkAllowed,
                effectiveBulk,
                purchaseCount
        );

        if (purchaseCount < 1) {

            BigShopGuys.LOGGER.info(
                    "Player {} could not pay for trade {}",
                    player.getName().getString(),
                    payload.recipeId()
            );

            player.displayClientMessage(
                    Component.literal(
                            "You cannot afford this trade."
                    ),
                    true
            );

            return;
        }

        boolean paymentConsumed =
                ShopTransactionService.consumePayment(
                        player,
                        trade,
                        purchaseCount
                );

        if (!paymentConsumed) {

            BigShopGuys.LOGGER.info(
                    "Payment Plan failed for {} purchasing {} x{}",
                    player.getName().getString(),
                    payload.recipeId(),
                    purchaseCount
            );

            player.displayClientMessage(
                    Component.literal("You cannot afford this trade."),
                    true
            );

            return;
        }

        ShopTransactionService.giveResult(
                player,
                trade,
                purchaseCount
        );

        player.containerMenu.broadcastChanges();

        BigShopGuys.LOGGER.info(
                "Player {} completed trade {} x{}",
                player.getName().getString(),
                payload.recipeId(),
                purchaseCount
        );

        Component purchaseMessage;
        if (purchaseCount > 1) {
            purchaseMessage = Component.literal("Purchase Complete x" + purchaseCount);
        } else {
            purchaseMessage = Component.literal("Purchase Complete");
        }

        player.displayClientMessage(
                purchaseMessage,
                true
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
