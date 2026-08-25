package com.bigchadguys.bigshopguys.item;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(BigShopGuys.MOD_ID);

    public static final DeferredItem<BlockItem> SHOP =
            ITEMS.registerSimpleBlockItem(ModBlocks.SHOP);

    private ModItems() {

    }
}
