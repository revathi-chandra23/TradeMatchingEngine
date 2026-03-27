package com.java.tradeEngine;

import com.java.tradeEngine.book.OrderBookManager;
import com.java.tradeEngine.fileHandling.CsvParser;
import com.java.tradeEngine.matchingEngine.MatchingEngine;
import com.java.tradeEngine.matchingEngine.PriceTimePriorityStrategy;
import com.java.tradeEngine.model.OrderType;
import com.java.tradeEngine.model.Orders;
import com.java.tradeEngine.model.TradeType;
import com.java.tradeEngine.trade.TradeStore;
import com.java.tradeEngine.validation.*;
import com.java.tradeEngine.validation.OrderValidationService;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        // file paths for buy orders and sell orders
        String buyFile = "src/main/resources/tradefiles/buy_orders.csv";
        String sellFile = "src/main/resources/tradefiles/sell_orders.csv";

        // Read both CSVs
        List<Orders> buyOrders = CsvParser.readAllOrders(buyFile);
        List<Orders> sellOrders = CsvParser.readAllOrders(sellFile);

        // combine both into single list
        List<Orders> allOrders = new ArrayList<>();
        allOrders.addAll(buyOrders);
        allOrders.addAll(sellOrders);
        
//order counters
        int totalBuy = 0;
        int totalSell = 0;
        int rejectedBuy = 0;
        int rejectedSell = 0;
     int totalBuyProcessed =0;
     int totalSellProcessed=0;

        for (Orders order : allOrders) {
            if (order.getOrderType() == OrderType.BUY) {
                totalBuy++;
            } else {
                totalSell++;
            }
        }
        int totalOrdersProcessed = totalBuy + totalSell;


        // Create validator chain
        OrderValidationService validator = new OrderValidationService(
                List.of(
                        new FieldValidator(),
                        new CountryValidator(),
                        new MaxAmountValidator()
                )
        );

        //list  stores only  Validate orders
        List<Orders> validOrders = new ArrayList<>();

        for (Orders order : allOrders) {
            try {
                validator.validate(order); //validate each order
                validOrders.add(order);
                if (order.getOrderType() == OrderType.BUY) {
                    totalBuyProcessed++;
                } else {
                    totalSellProcessed++;
                }
            } catch (Exception e) {
                order.reject(); //invalid orders
                if (order.getOrderType() == OrderType.BUY) {
                    rejectedBuy++;
                } else {
                    rejectedSell++;
                }
            }
        }

        //  create Matching engine using priceTimePriority strategy
        MatchingEngine engine = new MatchingEngine(new PriceTimePriorityStrategy());
        logger.info(" STARTING ORDER MATCHING ");

        for (Orders order : validOrders) { // process all valid orders using matching engine
            engine.processOrder(
                    OrderBookManager.getOrderBook(order.getTradeType()), order
            );
        }

        logger.info("MATCHING COMPLETED ");


        // -------------------- TRADE STATS --------------------
        int totalTrades = TradeStore.getAllTrades().size();

        // -------------------- PRINT ORDER STATS --------------------
        System.out.println("\n ORDER PROCESSING STATS -->");
        System.out.println("----------------------------");
        System.out.println("Total orders processed     : " + totalOrdersProcessed);

        System.out.println("\nBuy ORDERS:");
        System.out.println("Total buy Submitted : " + totalBuy);
        System.out.println("Total Buy  Processed : " + totalBuyProcessed);
        System.out.println("Total Buy Rejected  : " + rejectedBuy);

        System.out.println("\nSELL ORDERS:");
        System.out.println("Total Sell Submitted : " + totalSell);
        System.out.println("Total sell Processed : " + totalSellProcessed);
        System.out.println("Total sell Rejected  : " + rejectedSell);
        System.out.println("\n Total orders ");
        System.out.println("Total valid orders submitted: " + validOrders.size());
        System.out.println("Total invalid orders        :" + (rejectedBuy+rejectedSell));
        System.out.println("Total trades executed       : " + totalTrades);


        // Print summary(filled ,pending..etc)
        printFinalSummary(allOrders);
    }

//summary
    private static void printFinalSummary(List<Orders> allOrders) {

        Map<TradeType, Integer> filled = new EnumMap<>(TradeType.class);
        Map<TradeType, Integer> partial = new EnumMap<>(TradeType.class);
        Map<TradeType, Integer> pending = new EnumMap<>(TradeType.class);

        for (TradeType t : TradeType.values()) { // Initialize counters for every trade category
            filled.put(t, 0);
            partial.put(t, 0);
            pending.put(t, 0);
        }

        int totalFilled = 0, totalPartial = 0, totalPending = 0, totalRejected = 0;

        // update counters based on the status
        for (Orders order : allOrders) {
            switch (order.getStatus()) {
                case FILLED -> {
                    totalFilled++;
                    filled.put(order.getTradeType(), filled.get(order.getTradeType()) + 1);
                }
                case PARTIALLY_FILLED -> {
                    totalPartial++;
                    partial.put(order.getTradeType(), partial.get(order.getTradeType()) + 1);
                }
                case PENDING -> {
                    totalPending++;
                    pending.put(order.getTradeType(), pending.get(order.getTradeType()) + 1);
                }
                case REJECTED -> totalRejected++;
            }
        }

        System.out.println("\n FINAL SUMMARY -->");
        System.out.println("--------------------");
        System.out.println("FILLED ORDERS: " + totalFilled);
        System.out.println("PARTIALLY FILLED: " + totalPartial);
        System.out.println("PENDING: " + totalPending);
        System.out.println("REJECTED: " + totalRejected);

        System.out.println("\n CATEGORY-WISE SUMMARY -->");
        System.out.println("---------------------------");
        for (TradeType type : TradeType.values()) {
            System.out.println(type + " => FILLED: " + filled.get(type)
                    + ", PARTIAL: " + partial.get(type)
                    + ", PENDING: " + pending.get(type));
        }

        System.out.println(System.getProperty("user.dir"));
    }
}
