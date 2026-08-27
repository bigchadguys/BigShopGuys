package com.bigchadguys.bigshopguys.network;

import com.bigchadguys.bigshopguys.BigShopGuys;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ModNetworking {


    public static void register(
            RegisterPayloadHandlersEvent event
    ) {
        var registrar = event.registrar("1");

        registrar.playToServer(
                BuyTradePayload.TYPE,
                BuyTradePayload.STREAM_CODEC,
                BuyTradePayload::handle
        );
    }

    private ModNetworking() {

    }
}
