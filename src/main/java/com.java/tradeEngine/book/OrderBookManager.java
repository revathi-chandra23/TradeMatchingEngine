package com.java.tradeEngine.book;

import com.java.tradeEngine.model.TradeType;

import java.util.EnumMap;
import java.util.Map;

public class OrderBookManager {
//singleton pattern
    // stores it separate order book for trade type : FOREX,EQUITY ,CRYPTO
    private static final Map<TradeType, OrderBook> books = new EnumMap<>(TradeType.class);

    // create one pricetimeorderBook per trade type
    static {
        for (TradeType t : TradeType.values()) {
            books.put(t, new PriceTimeOrderBook());
        }
    }
// Returns the correct OrderBook for the given TradeType factory method
    public static OrderBook getOrderBook(TradeType tradeType) {
        return books.get(tradeType);
    }
}
