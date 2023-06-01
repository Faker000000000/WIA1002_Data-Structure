package com.example.tradingSystem;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class OrderCancellationSystem {

    private PriorityQueue<PendingOrder> pendingOrders;

    public OrderCancellationSystem() {
        // Initialize the priority queue with a custom comparator
        pendingOrders = new PriorityQueue<>(Comparator.comparing(PendingOrder::getWaitTimeInMinutes)
                .thenComparing(PendingOrder::getAmount).reversed());
    }

    // Method to add a pending order to the cancellation system
    public void addPendingOrder(PendingOrder order) {
        pendingOrders.offer(order);
    }

    // Method to cancel the pending order with the highest priority
    public PendingOrder cancelHighestPriorityOrder() {
        return pendingOrders.poll();
    }

    public PendingOrder cancelOrderWithLongestTime() {
        PendingOrder longestTimeOrder = pendingOrders.poll();
        pendingOrders.remove(longestTimeOrder);
        return longestTimeOrder;
    }

    public PendingOrder cancelOrderWithHighestAmount() {
        PriorityQueue<PendingOrder> tempQueue = new PriorityQueue<>(pendingOrders);
        PendingOrder highestAmountOrder = null;
        while (!tempQueue.isEmpty()) {
            PendingOrder order = tempQueue.poll();
            if (highestAmountOrder == null || order.getAmount() > highestAmountOrder.getAmount()) {
                highestAmountOrder = order;
            }
        }
        if (highestAmountOrder != null) {
            pendingOrders.remove(highestAmountOrder);
        }
        return highestAmountOrder;
    }

    // Method to check if a pending order falls within the acceptable price range
    public boolean isWithinAcceptableRange(PendingOrder order, double marketPrice, double acceptableRangePercentage) {
        double lowerBound = marketPrice - (marketPrice * acceptableRangePercentage / 100);
        double upperBound = marketPrice + (marketPrice * acceptableRangePercentage / 100);
        return order.getExpectedPrice() >= lowerBound && order.getExpectedPrice() <= upperBound;
    }

    // Inner class representing a pending order
    public static class PendingOrder {

        private LocalDateTime placementTime;
        private double amount;
        private String stockSymbol;
        private int quantity;
        private double expectedPrice;

        public PendingOrder(LocalDateTime placementTime, double amount, String stockSymbol, int quantity, double expectedPrice) {
            this.placementTime = placementTime;
            this.amount = amount;
            this.stockSymbol = stockSymbol;
            this.quantity = quantity;
            this.expectedPrice = expectedPrice;
        }

        public long getWaitTimeInMinutes() {
            return ChronoUnit.MINUTES.between(placementTime, LocalDateTime.now());
        }

        public double getAmount() {
            return amount;
        }

        public String getStockSymbol() {
            return stockSymbol;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getExpectedPrice() {
            return expectedPrice;
        }
    }
}
