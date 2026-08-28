package com.bigchadguys.bigshopguys.shop.transaction;

import com.bigchadguys.bigshopguys.shop.recipe.ShopTradeRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public record ShopPaymentPlan(
        List<PaymentEntry> entries
) {

    public static Optional<ShopPaymentPlan> create(
            Inventory inventory,
            ShopTradeRecipe trade
    ) {
        return create(
                inventory,
                trade,
                1
        );
    }

    public static Optional<ShopPaymentPlan> create(
            Inventory inventory,
            ShopTradeRecipe trade,
            int purchaseCount
    ) {
        if (purchaseCount < 1) {
            return Optional.empty();
        }

        var items = inventory.items;

        int[] remainingPerSlot =
                new int[items.size()];

        for (int slot = 0;
             slot < items.size();
             slot++) {

            remainingPerSlot[slot] =
                    items.get(slot).getCount();
        }

        List<PaymentEntry> entries =
                new ArrayList<>();

        /*
         * Process ShopCost one at a time.
         */
        for (var cost : trade.costs()) {

            long required = (long) cost.count()
                    * purchaseCount;

            if(required > Integer.MAX_VALUE) {
                return Optional.empty();
            }

            int amountNeeded =
                    cost.count();

            for (int slot = 0;
                 slot < items.size()
                         && amountNeeded > 0;
                 slot++) {

                ItemStack stack =
                        items.get(slot);

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

    public static int maxAffordablePurchases(
            Inventory inventory,
            ShopTradeRecipe trade
    ) {
        if (trade.costs().isEmpty()) {
            return 1;
        }

        int totalItems = 0;

        for (ItemStack stack : inventory.items) {
            totalItems += stack.getCount();
        }

        int smallestCost =
                trade.costs().stream().mapToInt(cost -> cost.count()).min().orElse(1);

        int high = totalItems / smallestCost;
        int low = 0;

        while(low < high) {
            int middle = low + (high - low + 1) / 2;

            boolean affordable =
                    create(inventory, trade, middle).isPresent();

            if (affordable) {
                low = middle;
            } else {
                high = middle - 1;
            }
        }

        return low;
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