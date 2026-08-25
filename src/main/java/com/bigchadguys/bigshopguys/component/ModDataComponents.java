package com.bigchadguys.bigshopguys.component;

import com.bigchadguys.bigshopguys.BigShopGuys;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(
                    Registries.DATA_COMPONENT_TYPE,
                    BigShopGuys.MOD_ID
            );

    public static final DeferredHolder<
            DataComponentType<?>,
            DataComponentType<ResourceLocation>
            > SHOP_ID =
            DATA_COMPONENTS.registerComponentType(
                    "shop_id",
                    builder -> builder
                            .persistent(ResourceLocation.CODEC)
            );

    private ModDataComponents() {

    }
}
