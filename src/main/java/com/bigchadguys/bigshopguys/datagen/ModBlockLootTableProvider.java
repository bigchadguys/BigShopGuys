package com.bigchadguys.bigshopguys.datagen;

import com.bigchadguys.bigshopguys.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    public ModBlockLootTableProvider(HolderLookup.Provider registries) {}

    @Override
    protected void generate() {
        dropSelf(ModBlocks.SHOP.get());
    }
}
