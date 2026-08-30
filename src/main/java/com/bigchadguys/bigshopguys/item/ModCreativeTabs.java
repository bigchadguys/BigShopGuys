package com.bigchadguys.bigshopguys.item;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.shop.ShopRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Comparator;

public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BigShopGuys.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHOP_TAB =
            CREATIVE_MODE_TABS.register(
                    "shops",
                    () -> CreativeModeTab.builder()

                            .title(
                                    Component.translatable(
                                            "itemGroup.bigshopguys.shops"
                                    )
                            )

                            .icon(
                                    () -> new ItemStack(
                                            ModItems.SHOP.get()
                                    )
                            )

                            .displayItems(
                                    (parameters, output) -> {

                                        var shopLookup =
                                                parameters
                                                        .holders()
                                                        .lookup(
                                                                ShopRegistries
                                                                        .SHOP_DEFINITION_REGISTRY_KEY
                                                        );

                                        if (shopLookup.isEmpty()) {
                                            return;
                                        }

                                        shopLookup.get()
                                                .listElements()
                                                .sorted(
                                                        Comparator.comparing(
                                                                holder ->
                                                                        holder.key()
                                                                                .location()
                                                                                .toString()
                                                        )
                                                )
                                                .forEach(holder -> {

                                                    var shopId =
                                                            holder.key()
                                                                    .location();

                                                    var definition =
                                                            holder.value();

                                                    ItemStack stack =
                                                            ModItems.createShopStack(
                                                                    shopId
                                                            );

                                                    stack.set(
                                                            DataComponents.CUSTOM_NAME,
                                                            definition.title()
                                                    );

                                                    output.accept(
                                                            stack
                                                    );
                                                });
                                    }
                            )

                            .build()
            );

    private ModCreativeTabs() {

    }
}