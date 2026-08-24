package com.bigchadguys.bigshopguys.block;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.shop.block.ShopBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(BigShopGuys.MOD_ID);

    public static final DeferredBlock<ShopBlock> SHOP =
            BLOCKS.register("shop", () -> new ShopBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(2.0F)
                                    .sound(SoundType.WOOD)
                                    .ignitedByLava()
                            )
            );

    private ModBlocks() {

    }
}
