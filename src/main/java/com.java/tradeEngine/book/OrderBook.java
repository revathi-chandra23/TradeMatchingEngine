package com.java.tradeEngine.book;


import com.java.tradeEngine.model.Orders;

import java.util.Queue;
//OrderBook is an in-memory data structure that stores active (unfilled) orders.
public interface OrderBook {
// Returns all buy orders stored in the order book for loose coupling
    Queue<Orders> getBuyOrders();
    // Returns all sell orders stored in the order book
    Queue<Orders> getSellOrders();
    void addOrder(Orders order);// add new order buy or sell
}