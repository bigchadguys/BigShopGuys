package com.bigchadguys.bigshopguys.client;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.client.screen.ShopScreen;
import com.bigchadguys.bigshopguys.menu.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = BigShopGuys.MOD_ID, dist = Dist.CLIENT)
public final class BigShopGuysClient {

    public BigShopGuysClient(IEventBus modEventBus) {
        modEventBus.addListener(this::registerScreens);
    }

    private void registerScreens(
            RegisterMenuScreensEvent event
    ) {
        event.register(
                ModMenus.SHOP.get(),
                ShopScreen::new
        );
    }
}
