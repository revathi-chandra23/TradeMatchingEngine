package com.java.tradeEngine.trade;

import com.java.tradeEngine.model.Trade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradeStore {
// add synchronized to the list
    private static final List<Trade> trades =
            Collections.synchronizedList(new ArrayList<>());

    public static void addTrade(Trade trade) {
        trades.add(trade);
    }

    public static List<Trade> getAllTrades() {
        return trades;
    }
}

