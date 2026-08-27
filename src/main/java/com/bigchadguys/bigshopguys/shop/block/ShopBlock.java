package com.bigchadguys.bigshopguys.shop.block;

import com.bigchadguys.bigshopguys.component.ModDataComponents;
import com.bigchadguys.bigshopguys.shop.ShopDefinition;
import com.bigchadguys.bigshopguys.shop.ShopRegistries;
import com.bigchadguys.bigshopguys.shop.recipe.ModShopRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShopBlock extends Block implements EntityBlock {

    public ShopBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShopBlockEntity(pos, state);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(
            @NotNull BlockState state,
            Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull BlockHitResult hitResult
    ) {
        if(level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if(!(blockEntity instanceof ShopBlockEntity shopBlockEntity)) {
            return InteractionResult.PASS;
        }

        var shopIdOptional = shopBlockEntity.getShopId();

        if (shopIdOptional.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("shop.bigshopguys.missing_shop_id"),
                    false
            );

            return InteractionResult.SUCCESS;
        }

        ResourceLocation shopId = shopIdOptional.get();

        Registry<ShopDefinition> shops =
                level.registryAccess()
                        .registryOrThrow(
                                ShopRegistries.SHOP_DEFINITION_REGISTRY_KEY
                        );

        ShopDefinition definition = shops.get(shopId);

        if(definition == null) {
            player.displayClientMessage(
                    Component.translatable("shop.bigshopguys.missing_shop_definition"),
                    false
            );

            return InteractionResult.SUCCESS;
        }

        long tradeCount = level.getRecipeManager()
                .getAllRecipesFor(
                        ModShopRecipes.SHOP_TRADE_RECIPE_TYPE.get()
                )
                .stream()
                .filter(holder ->
                        holder.value().shop().equals(shopId)
                )
                .count();

        if (!level.isClientSide
                && player instanceof ServerPlayer serverPlayer) {

            serverPlayer.openMenu(
                    shopBlockEntity,
                    buffer -> {
                        buffer.writeBlockPos(pos);
                        buffer.writeResourceLocation(shopId);
                    }
            );

            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @Nullable LivingEntity placer,
            @NotNull ItemStack stack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack);

        ResourceLocation shopId =
                stack.get(ModDataComponents.SHOP_ID.get());

        if (shopId == null) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof ShopBlockEntity shopBlockEntity) {
            shopBlockEntity.setShopId(shopId);
        }
    }
}
