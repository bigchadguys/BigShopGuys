package com.bigchadguys.bigshopguys.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

public final class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(
                event.includeServer(),
                new LootTableProvider(
                        output,
                        Set.of(),
                        List.of(
                                new LootTableProvider.SubProviderEntry(
                                        ModBlockLootTableProvider::new,
                                        LootContextParamSets.BLOCK
                                )
                        ),
                        event.getLookupProvider()
                )
        );
    }

    private DataGenerators() {

    }
}