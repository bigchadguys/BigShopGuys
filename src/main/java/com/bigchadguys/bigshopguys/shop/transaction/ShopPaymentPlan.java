package com.bigchadguys.bigshopguys.shop.transaction;

import com.bigchadguys.bigshopguys.shop.recipe.ShopTradeRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ShopPaymentPlan(
        List<PaymentEntry> entries
) {

    /**
     * Attempts to build a complete payment plan for the trade.
     *
     * If every cost can be satisfied, returns the plan.
     * If even one cost cannot be satisfied, returns Optional.empty().
     *
     * This method does NOT modify the player's real inventory.
     */
    public static Optional<ShopPaymentPlan> create(
            ServerPlayer player,
            ShopTradeRecipe trade
    ) {
        var inventory = player.getInventory().items;

        /*
         * This represents how many items in each real
         * inventory slot are still available to allocate.
         *
         * We modify this temporary array, NOT the player's
         * actual ItemStacks.
         */
        int[] remainingPerSlot =
                new int[inventory.size()];

        for (int slot = 0;
             slot < inventory.size();
             slot++) {

            remainingPerSlot[slot] =
                    inventory.get(slot).getCount();
        }

        List<PaymentEntry> entries =
                new ArrayList<>();

        /*
         * Process ShopCost one at a time.
         */
        for (var cost : trade.costs()) {

            int amountNeeded =
                    cost.count();

            for (int slot = 0;
                 slot < inventory.size()
                         && amountNeeded > 0;
                 slot++) {

                ItemStack stack =
                        inventory.get(slot);

                if (stack.isEmpty()) {
                    continue;
                }

                /*
                 * Does this ItemStack correct?
                 */
                if (!cost.ingredient().test(stack)) {
                    continue;
                }

                int available =
                        remainingPerSlot[slot];

                if (available <= 0) {
                    continue;
                }

                int amountToUse =
                        Math.min(
                                available,
                                amountNeeded
                        );

                entries.add(
                        new PaymentEntry(
                                slot,
                                amountToUse
                        )
                );

                /*
                 * Reserve these items inside our imaginary
                 * inventory.
                 */
                remainingPerSlot[slot] -=
                        amountToUse;

                amountNeeded -=
                        amountToUse;
            }

            /*
             * We searched the whole inventory but couldn't
             * completely satisfy this cost.
             *
             * Throw the entire proposed plan away.
             */
            if (amountNeeded > 0) {
                return Optional.empty();
            }
        }

        return Optional.of(
                new ShopPaymentPlan(
                        List.copyOf(entries)
                )
        );
    }


    /**
     * Actually consumes the items represented by this plan.
     *
     * Only call this after the plan has been successfully
     * created and player is ready to purchase
     */
    public void consume(ServerPlayer player) {

        var inventory =
                player.getInventory().items;

        for (PaymentEntry entry : entries) {

            ItemStack stack =
                    inventory.get(entry.slot());

            stack.shrink(
                    entry.count()
            );
        }
    }


    /**
     * One piece of the payment plan:
     *
     * "Remove this many items from this inventory slot."
     */
    public record PaymentEntry(
            int slot,
            int count
    ) {
    }
}