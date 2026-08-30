package com.bigchadguys.bigshopguys.datagen;

import com.bigchadguys.bigshopguys.block.ModBlocks;
import com.bigchadguys.bigshopguys.component.ModDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected void generate() {
        add(
                ModBlocks.SHOP.get(),

                createSingleItemTable(ModBlocks.SHOP.get())
                        .apply(
                                CopyComponentsFunction
                                        .copyComponents(
                                                CopyComponentsFunction.Source.BLOCK_ENTITY
                                        )
                                        .include(
                                                ModDataComponents.SHOP_ID.get()
                                        )
                                        .include(
                                                DataComponents.CUSTOM_NAME
                                        )
                        )
        );
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries()
                .stream()
                .map(entry -> (Block) entry.value())
                .toList();
    }
}
