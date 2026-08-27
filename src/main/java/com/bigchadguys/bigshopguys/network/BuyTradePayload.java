package com.bigchadguys.bigshopguys.network;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.shop.transaction.ShopTransactionService;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record BuyTradePayload(
        BlockPos shopPos,
        ResourceLocation recipeId
) implements CustomPacketPayload {

    public static final Type<BuyTradePayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            BigShopGuys.MOD_ID,
                            "buy_trade"
                    )
            );

    public static final StreamCodec<ByteBuf, BuyTradePayload> STREAM_CODEC =
            StreamCodec.composite(

                    BlockPos.STREAM_CODEC,
                    BuyTradePayload::shopPos,

                    ResourceLocation.STREAM_CODEC,
                    BuyTradePayload::recipeId,

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

        boolean paymentConsumed =
                ShopTransactionService.consumePayment(
                        player,
                        trade
                );

        if (!paymentConsumed) {

            BigShopGuys.LOGGER.info(
                    "Player {} could not pay for trade {}",
                    player.getName().getString(),
                    payload.recipeId()
            );

            return;
        }

        BigShopGuys.LOGGER.info(
                "Player {} successfully paid for trade {}",
                player.getName().getString(),
                payload.recipeId()
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
