package com.bigchadguys.bigshopguys.item;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.block.ModBlocks;
import com.bigchadguys.bigshopguys.component.ModDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(BigShopGuys.MOD_ID);

    public static final DeferredItem<ShopBlockItem> SHOP =
            ITEMS.register("shop", () -> new ShopBlockItem(
                        ModBlocks.SHOP.get(),
                        new Item.Properties()
                    )
            );

    public static ItemStack createShopStack(ResourceLocation shopId) {
        ItemStack stack = new ItemStack(SHOP.get());

        stack.set(ModDataComponents.SHOP_ID.get(), shopId);

        return stack;
    }


    private ModItems() {

    }
}
