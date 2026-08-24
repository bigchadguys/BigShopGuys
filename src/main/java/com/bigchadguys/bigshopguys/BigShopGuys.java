package com.bigchadguys.bigshopguys;

import com.bigchadguys.shop.ShopDefinition;
import com.bigchadguys.shop.ShopRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

@Mod(BigShopGuys.MOD_ID)
public class BigShopGuys {

    public static final String MOD_ID = "bigshopguys";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BigShopGuys(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(ShopRegistries::register);

        NeoForge.EVENT_BUS.register(this);

        // modEventBus.addListener(this::addCreative);
        // modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        Registry<ShopDefinition> shops =
                event.getServer()
                        .registryAccess()
                        .registryOrThrow(
                                ShopRegistries.SHOP_DEFINITION_REGISTRY_KEY
                        );

        LOGGER.info(
                "Loaded {} Big Shop Guys Definitions",
                shops.size()
        );

        shops.entrySet().forEach(entry -> {
            LOGGER.info(
                    "Loaded shop {} -> {}",
                    entry.getKey().location(),
                    entry.getValue().Title().getString()
            );
        });
    }
}
