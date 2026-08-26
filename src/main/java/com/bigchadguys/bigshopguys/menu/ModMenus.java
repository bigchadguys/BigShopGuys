package com.bigchadguys.bigshopguys.menu;

import com.bigchadguys.bigshopguys.BigShopGuys;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    Registries.MENU,
                    BigShopGuys.MOD_ID
            );

    public static final Supplier<MenuType<ShopMenu>> SHOP =
            MENUS.register(
                    "shop",
                    () -> IMenuTypeExtension.create(ShopMenu::new)
            );

    private ModMenus() {

    }
}
