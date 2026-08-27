package com.bigchadguys.bigshopguys;

import com.bigchadguys.bigshopguys.block.ModBlockEntities;
import com.bigchadguys.bigshopguys.block.ModBlocks;
import com.bigchadguys.bigshopguys.component.ModDataComponents;
import com.bigchadguys.bigshopguys.datagen.DataGenerators;
import com.bigchadguys.bigshopguys.item.ModItems;
import com.bigchadguys.bigshopguys.menu.ModMenus;
import com.bigchadguys.bigshopguys.network.ModNetworking;
import com.bigchadguys.bigshopguys.shop.ShopDefinition;
import com.bigchadguys.bigshopguys.shop.ShopRegistries;
import com.bigchadguys.bigshopguys.shop.recipe.ModShopRecipes;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

@Mod(BigShopGuys.MOD_ID)
public class BigShopGuys {

    public static final String MOD_ID = "bigshopguys";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BigShopGuys(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(ShopRegistries::register);
        modEventBus.addListener(DataGenerators::gatherData);
        ModShopRecipes.RECIPE_TYPES.register(modEventBus);
        ModShopRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ModNetworking::register);
        NeoForge.EVENT_BUS.register(this);

        // modEventBus.addListener(this::addCreative);
        // modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event)
    {
        Registry<ShopDefinition> shops =
                event.getServer()
                        .registryAccess()
                        .registryOrThrow(
                                ShopRegistries.SHOP_DEFINITION_REGISTRY_KEY
                        );

        LOGGER.info(
                "Loaded {} Big Shop Guys Definitions",
                shops.size()
        );

        shops.entrySet().forEach(entry -> {
            LOGGER.info(
                    "Loaded shop {} -> {}",
                    entry.getKey().location(),
                    entry.getValue().title().getString()
            );
        });

        var trades = event.getServer()
                .getRecipeManager()
                .getAllRecipesFor(
                        ModShopRecipes.SHOP_TRADE_RECIPE_TYPE.get()
                );

        LOGGER.info(
                "Loaded {} Big Shop Guys Trades",
                trades.size()
        );

        trades.forEach(holder -> {
            LOGGER.info(
                    "Loaded trade {} for shop {}",
                    holder.id(),
                    holder.value().shop()
            );
        });
    }
}
