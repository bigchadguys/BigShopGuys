package com.bigchadguys.bigshopguys.item;

import com.bigchadguys.bigshopguys.component.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShopBlockItem extends BlockItem {
    public ShopBlockItem(Block block, Item.Properties properties){
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (!flag.isAdvanced()) {
            return;
        }

        var shopId = stack.get(ModDataComponents.SHOP_ID.get());

        if (shopId == null) {
            return;
        }

        tooltip.add(Component.translatable("shop.bigshopguys.tooltip.shopidprefix" + shopId).withStyle(ChatFormatting.GRAY));
    };


}
