package com.bigchadguys.bigshopguys.client.screen;

import com.bigchadguys.bigshopguys.menu.ShopMenu;
import com.bigchadguys.bigshopguys.shop.recipe.ModShopRecipes;
import com.bigchadguys.bigshopguys.shop.recipe.ShopTradeRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> {

    private static final int VISIBLE_TRADE_ROWS = 4;
    private static final int TRADE_ROW_HEIGHT = 24;
    private static final int TRADE_START_Y = 62;

    private int scrollOffset = 0;

    public ShopScreen(
            ShopMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(menu, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 180;
    }

    @Override
    protected void renderBg(
            @NotNull GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.fill(
                x,
                y,
                x + this.imageWidth,
                y + this.imageHeight,
                0xCC202020
        );
    }

    @Override
    protected void renderLabels(
            @NotNull GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        // Shop title
        graphics.drawString(
                this.font,
                this.title,
                8,
                8,
                0xFFFFFF,
                false
        );

        // Shop ID
        graphics.drawString(
                this.font,
                "Shop ID: " + this.menu.getShopId(),
                8,
                28,
                0xFFFFFF,
                false
        );

        // Get all trades belonging to this shop
        var trades = minecraft.level
                .getRecipeManager()
                .getAllRecipesFor(
                        ModShopRecipes.SHOP_TRADE_RECIPE_TYPE.get()
                )
                .stream()
                .filter(holder ->
                        holder.value()
                                .shop()
                                .equals(menu.getShopId())
                )
                .toList();

        // Trade count
        graphics.drawString(
                this.font,
                "Trades available: " + trades.size(),
                8,
                46,
                0xFFFFFF,
                false
        );

        // Draw every trade
        int tradeStartY = 62;
        int tradeSpacing = 24;

        int endIndex = Math.min(
                scrollOffset + VISIBLE_TRADE_ROWS,
                trades.size()
        );

        for (int i = scrollOffset; i < endIndex; i++) {

            int visibleRow = i - scrollOffset;

            renderTradeRow(
                    graphics,
                    trades.get(i).value(),
                    12,
                    TRADE_START_Y
                            + (visibleRow * TRADE_ROW_HEIGHT)
            );
        }
    }

    private void renderTradeRow(
            GuiGraphics graphics,
            ShopTradeRecipe trade,
            int x,
            int y
    ) {
        int currentX = x;

        for (int i = 0; i < trade.costs().size(); i++) {
            var cost = trade.costs().get(i);

            ItemStack[] possibleStacks =
                    cost.ingredient().getItems();

            if (possibleStacks.length == 0) {
                continue;
            }

            ItemStack costStack =
                    possibleStacks[0].copy();

            costStack.setCount(cost.count());

            graphics.renderItem(
                    costStack,
                    currentX,
                    y
            );

            graphics.renderItemDecorations(
                    this.font,
                    costStack,
                    currentX,
                    y
            );

            currentX += 24;

            if (i < trade.costs().size() - 1) {
                graphics.drawString(
                        this.font,
                        "+",
                        currentX,
                        y + 5,
                        0xFFFFFF,
                        false
                );

                currentX += 14;
            }
        }

        graphics.drawString(
                this.font,
                "→",
                currentX,
                y + 5,
                0xFFFFFF,
                false
        );

        currentX += 22;

        ItemStack resultStack =
                trade.result().copy();

        graphics.renderItem(
                resultStack,
                currentX,
                y
        );

        graphics.renderItemDecorations(
                this.font,
                resultStack,
                currentX,
                y
        );
    }
}