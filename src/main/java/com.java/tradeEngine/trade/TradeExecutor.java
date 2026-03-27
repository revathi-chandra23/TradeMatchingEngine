package com.java.tradeEngine.trade;

import com.java.tradeEngine.model.Trade;
import com.java.tradeEngine.model.TradeType;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

/**
 * Responsible for executing trades and persisting them to a file.
 */
public class TradeExecutor {

    private static final String TRADE_FILE = "executed_trades.log";
    private static final Object FILE_LOCK = new Object(); // thread-safety
// create the trade object These come directly from matched orders:
    public static void executeTrade(
            String buyOrderId,
            String sellOrderId,
            TradeType tradeType,

            double price,
            long quantity
    ) { //A new immutable Trade object is created Captures:

     //   Who traded, what type,At what price,How much,When
        Trade trade = new Trade(
                buyOrderId,
                sellOrderId,
                tradeType,
                price,
                quantity,
                Instant.now().toEpochMilli()
        );

        TradeStore.addTrade(trade);//Stores trades in memory
        writeToFile(trade);
    }
// write the file
    private static void writeToFile(Trade trade) {
        synchronized (FILE_LOCK) {
            try (FileWriter writer = new FileWriter(TRADE_FILE, true)) {
                writer.write(trade.toString());//Writes readable trade format
                writer.write(System.lineSeparator());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write trade to file", e);
            }
        }
    }
}
