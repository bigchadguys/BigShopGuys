package com.bigchadguys.bigshopguys.block;

import com.bigchadguys.bigshopguys.BigShopGuys;
import com.bigchadguys.bigshopguys.shop.block.ShopBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>>
            BLOCK_ENTITY_TYPES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    BigShopGuys.MOD_ID
            );

    public static final Supplier<BlockEntityType<ShopBlockEntity>> SHOP =
            BLOCK_ENTITY_TYPES.register(
                    "shop",
                    () -> BlockEntityType.Builder.of(
                            ShopBlockEntity::new,
                            ModBlocks.SHOP.get()
                    ).build(null)
            );

    private ModBlockEntities() {

    }
}
