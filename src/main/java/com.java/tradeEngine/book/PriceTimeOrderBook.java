package com.java.tradeEngine.book;
import com.java.tradeEngine.model.OrderType;
import com.java.tradeEngine.model.Orders;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * at stores BUY and SELL orders and decides their priority using:
 * Price priority
 * Time (FIFO) priority
 * It implements the OrderBook interface, meaning it follows a standard contract for storing orders.
 */
public class PriceTimeOrderBook implements OrderBook {

    // Priority queue for BUY orders: highest price first, then earliest timestamp
    //Buyers willing to pay more get priority
    private final PriorityQueue<Orders> buyOrders = new PriorityQueue<>(
            Comparator.comparingDouble(Orders::getPrice).reversed()
                    .thenComparing(Orders::getTimestamp)
    );

    // Priority queue for SELL orders: lowest price first
    private final PriorityQueue<Orders> sellOrders = new PriorityQueue<>(
            Comparator.comparingDouble(Orders::getPrice)
                    .thenComparing(Orders::getTimestamp)
    );

    @Override
    public synchronized void addOrder(Orders order) {
        //add order to buy or sell queue based on type
        if (order.getOrderType() == OrderType.BUY)
        {
            buyOrders.offer(order);//Adds BUY order to BUY queue
        }
        else
        {
            sellOrders.offer(order);
        }
    }

    @Override
    public synchronized Queue<Orders> getBuyOrders() {
        return buyOrders;
    }

    @Override
    public synchronized Queue<Orders> getSellOrders() {
        return sellOrders;
    }
//Returns top priority order without removing.
    public synchronized Orders peekBestBuy() {
        return buyOrders.peek();
    }
    //Returns lowest-price SELL order
    public synchronized Orders peekBestSell() {
        return sellOrders.peek();  // look at highest priority  sell
    }
    // remove buy order after full filled
    public synchronized Orders pollBestBuy() {
        return buyOrders.poll();  // Remove and return highest priority BUY order
    }
    public synchronized Orders pollBestSell() {
        return sellOrders.poll(); // same as like before remove and return high priority sell order
    }
}