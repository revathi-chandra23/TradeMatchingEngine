package com.java.tradeEngine.matchingEngine;

import com.java.tradeEngine.book.OrderBook;
import com.java.tradeEngine.model.OrderStatus;
import com.java.tradeEngine.model.Orders;

//It controls how each order is processed and sets final order status.
public class MatchingEngine {

    private final MatchingStrategy strategy;

    public MatchingEngine(MatchingStrategy strategy) {
        this.strategy = strategy;
    }

    public void processOrder(OrderBook orderBook, Orders order) {
        //saves the original quantity order engine

        long originalQty = order.getRemainingQuantity();
        strategy.match(orderBook, order);
        long matchedQty = originalQty - order.getRemainingQuantity(); // how much got matched

        if (matchedQty == originalQty && originalQty > 0) {
            order.setStatus(OrderStatus.FILLED);
        }
        else if (matchedQty > 0){
            order.setStatus(OrderStatus.PARTIALLY_FILLED);
        }
        else if (originalQty > 0) {
            order.setStatus(OrderStatus.PENDING);
        }
        else
        {
            order.setStatus(OrderStatus.REJECTED);
        }
    }
}

