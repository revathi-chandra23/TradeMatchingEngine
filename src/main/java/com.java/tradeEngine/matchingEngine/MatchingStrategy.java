package com.java.tradeEngine.matchingEngine;
import com.java.tradeEngine.book.OrderBook;
import com.java.tradeEngine.model.Orders;

//strategy pattern
public interface MatchingStrategy {
    void match(OrderBook book, Orders incoming);// defines matching orders in the book

}
