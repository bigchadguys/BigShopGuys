package com.bigchadguys.bigshopguys.client.screen;

import com.bigchadguys.bigshopguys.menu.ShopMenu;
import com.bigchadguys.bigshopguys.network.BuyTradePayload;
import com.bigchadguys.bigshopguys.shop.recipe.ModShopRecipes;
import com.bigchadguys.bigshopguys.shop.recipe.ShopTradeRecipe;
import com.bigchadguys.bigshopguys.shop.transaction.ShopPaymentPlan;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> {

    private static final int VISIBLE_TRADE_ROWS = 4;
    private static final int TRADE_ROW_HEIGHT = 24;
    private static final int TRADE_START_Y = 62;
    private static final int BUY_BUTTON_X = 132;
    private static final int BUY_BUTTON_WIDTH = 32;
    private static final int BUY_BUTTON_HEIGHT = 16;

    private int scrollOffset = 0;

    public ShopScreen(ShopMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 258;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        graphics.fill(
                x,
                y,
                x + this.imageWidth,
                y + this.imageHeight,
                0xFF202020
        );
    }

    private List<RecipeHolder<ShopTradeRecipe>> getTrades() {
        assert minecraft != null;
        if (minecraft.level == null) {
            return List.of();
        }

        return minecraft.level
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
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        int localMouseX =
                mouseX - this.leftPos;
        int localMouseY =
                mouseY - this.topPos;

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
        var trades = getTrades();

        // Trade count
        graphics.drawString(
                this.font,
                "Trades available: " + trades.size(),
                8,
                46,
                0xFFFFFF,
                false
        );

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

            var trade = trades.get(i).value();
            boolean canAfford = canClientAfford(trade);

            renderBuyButton(
                    graphics,
                    BUY_BUTTON_X,
                    TRADE_START_Y + (visibleRow * TRADE_ROW_HEIGHT),
                    localMouseX,
                    localMouseY,
                    canAfford
            );
        }

        graphics.drawString(
                this.font,
                this.playerInventoryTitle,
                8,
                164,
                0xFFFFFF,
                false
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            var trades = getTrades();

            int localMouseX =
                    (int) mouseX - this.leftPos;

            int localMouseY =
                    (int) mouseY - this.topPos;

            int endIndex = Math.min(
                    scrollOffset + VISIBLE_TRADE_ROWS,
                    trades.size()
            );

            for (int i = scrollOffset; i < endIndex; i++) {

                int visibleRow = i - scrollOffset;

                int buttonY =
                        TRADE_START_Y
                                + (visibleRow * TRADE_ROW_HEIGHT);

                boolean clicked =
                        localMouseX >= BUY_BUTTON_X
                                && localMouseX
                                < BUY_BUTTON_X + BUY_BUTTON_WIDTH
                                && localMouseY >= buttonY
                                && localMouseY
                                < buttonY + BUY_BUTTON_HEIGHT;

                if (clicked) {
                    var holder = trades.get(i);
                    var trade = holder.value();

                    if(!canClientAfford(trade)) {
                        return true;
                    }

                    PacketDistributor.sendToServer(
                            new BuyTradePayload(
                                    menu.getShopPos(),
                                    holder.id()
                            )
                    );

                    return true;
                }
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    private void renderBuyButton(GuiGraphics graphics, int x, int y, int mouseX, int mouseY, boolean canAfford) {
        boolean hovered =
                mouseX >= x
                        && mouseX < x + BUY_BUTTON_WIDTH
                        && mouseY >= y
                        && mouseY < y + BUY_BUTTON_HEIGHT;

        int background;

        if (!canAfford) {
            background = 0xFF222034;
        }
        else if (hovered) {
            background = 0xFFd77bba;
        }
        else {
            background = 0xFF306082;
        }

        graphics.fill(
                x,
                y,
                x + BUY_BUTTON_WIDTH,
                y + BUY_BUTTON_HEIGHT,
                background
        );

        int textColor =
                canAfford ? 0xFFFFFF : 0x777777;

        String text = "BUY";

        int textX =
                x + (BUY_BUTTON_WIDTH
                        - this.font.width(text)) / 2;

        graphics.drawString(
                this.font,
                text,
                textX,
                y + 4,
                textColor,
                false
        );
    }

    private void renderTradeRow(GuiGraphics graphics, ShopTradeRecipe trade, int x, int y) {
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        var trades = getTrades();

        int maxScrollOffset = Math.max(
                0,
                trades.size() - VISIBLE_TRADE_ROWS
        );

        // Mouse wheel down
        if (scrollY < 0) {
            scrollOffset = Math.min(
                    scrollOffset + 1,
                    maxScrollOffset
            );
        }

        // Mouse wheel up
        else if (scrollY > 0) {
            scrollOffset = Math.max(
                    scrollOffset - 1,
                    0
            );
        }

        return true;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        renderTradeTooltips(
                graphics,
                mouseX,
                mouseY
        );

        renderBuyButtonTooltips(
                graphics,
                mouseX,
                mouseY
        );
    }

    private void renderBuyButtonTooltips(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        var trades = getTrades();

        int endIndex = Math.min(
                scrollOffset + VISIBLE_TRADE_ROWS,
                trades.size()
        );

        for (int i = scrollOffset; i < endIndex; i++) {
            int visibleRow = i - scrollOffset;
            var trade = trades.get(i).value();

            if(canClientAfford(trade)) {
                continue;
            }

            int buttonX = this.leftPos + BUY_BUTTON_X;

            int buttonY =
                    this.topPos
                    + TRADE_START_Y
                    + (visibleRow * TRADE_ROW_HEIGHT);

            boolean hovered =
                    mouseX >= buttonX
                    && mouseX < buttonX + BUY_BUTTON_WIDTH
                    && mouseY >= buttonY
                    && mouseY < buttonY + BUY_BUTTON_HEIGHT;

            if (hovered) {
                graphics.renderTooltip(
                        this.font,
                        Component.literal("You cannot afford this trade"),
                        mouseX,
                        mouseY
                );

                return;
            }
        }
    }

    private void renderTradeTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        var trades = getTrades();

        int endIndex = Math.min(
                scrollOffset + VISIBLE_TRADE_ROWS,
                trades.size()
        );

        for (int i = scrollOffset; i < endIndex; i++) {

            int visibleRow = i - scrollOffset;
            int rowX = this.leftPos + 12;
            int rowY = this.topPos + TRADE_START_Y + (visibleRow * TRADE_ROW_HEIGHT);

            ItemStack hoveredStack =
                    getHoveredTradeStack(
                            trades.get(i).value(),
                            rowX,
                            rowY,
                            mouseX,
                            mouseY
                    );

            if (!hoveredStack.isEmpty()) {

                graphics.renderTooltip(
                        this.font,
                        hoveredStack,
                        mouseX,
                        mouseY
                );

                return;
            }
        }
    }

    private ItemStack getHoveredTradeStack(ShopTradeRecipe trade, int x, int y, double mouseX, double mouseY) {
        int currentX = x;

        // Check every cost
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

            if (isMouseOverItem(
                    mouseX,
                    mouseY,
                    currentX,
                    y
            )) {
                return costStack;
            }

            // Same movement used by renderTradeRow()
            currentX += 24;

            if (i < trade.costs().size() - 1) {
                currentX += 14;
            }
        }

        // Skip past the arrow
        currentX += 22;

        // Check result
        ItemStack resultStack =
                trade.result().copy();

        if (isMouseOverItem(
                mouseX,
                mouseY,
                currentX,
                y
        )) {
            return resultStack;
        }

        return ItemStack.EMPTY;
    }

    private boolean isMouseOverItem(double mouseX, double mouseY, int itemX, int itemY) {
        return mouseX >= itemX
                && mouseX < itemX + 16
                && mouseY >= itemY
                && mouseY < itemY + 16;
    }

    private boolean canClientAfford(ShopTradeRecipe trade) {
        assert minecraft != null;
        if (minecraft.player == null) {
            return false;
        }

        return ShopPaymentPlan
                .create(
                        minecraft.player.getInventory(),
                        trade
                )
                .isPresent();
    }
}