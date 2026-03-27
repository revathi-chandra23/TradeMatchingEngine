package com.java.tradeEngine.matchingEngine;
import com.java.tradeEngine.book.OrderBook;
import com.java.tradeEngine.book.PriceTimeOrderBook;
import com.java.tradeEngine.matchingEngine.MatchingStrategy;
import com.java.tradeEngine.model.OrderType;
import com.java.tradeEngine.model.Orders;
import com.java.tradeEngine.trade.TradeExecutor;

public class PriceTimePriorityStrategy implements MatchingStrategy {
//routes order matchBuy or matchSell
    @Override
    public void match(OrderBook orderBook, Orders incoming) {
        PriceTimeOrderBook book = (PriceTimeOrderBook) orderBook;
        if (incoming.getOrderType() == OrderType.BUY) {
            matchBuy(book, incoming);
        } else {
            matchSell(book, incoming);
        }
    }

    private void matchBuy(PriceTimeOrderBook book, Orders buy) {

        while (buy.getRemainingQuantity() > 0) {
            Orders bestSell;
            synchronized (book) { // peek best sell
                bestSell = book.peekBestSell();
            }
//check the price condition
            if (bestSell == null || buy.getPrice() < bestSell.getPrice()) {
                break;
            }
// match minimum quantity
            long qty = Math.min(buy.getRemainingQuantity(), bestSell.getRemainingQuantity());

         //reduce both orders
            buy.reduceQuantity(qty);
            bestSell.reduceQuantity(qty);

            //  EXECUTE TRADE (FILE ONLY)
            TradeExecutor.executeTrade(
                    buy.getOrderId(),
                    bestSell.getOrderId(),
                    buy.getTradeType(),
                    bestSell.getPrice(),
                    qty
            );
// remove the filled sell
            if (bestSell.isFilled()) {
                synchronized (book) {
                    book.pollBestSell();
                }
            }
        }
// add buy if remaining
        if (!buy.isFilled()) {
            synchronized (book) {
                book.addOrder(buy);
            }
        }
    }

    private void matchSell(PriceTimeOrderBook book, Orders sell) {

        while (sell.getRemainingQuantity() > 0) {
            Orders bestBuy;
            synchronized (book) {
                bestBuy = book.peekBestBuy();// peek the best sell
            }

            // check price condition
            if (bestBuy == null || bestBuy.getPrice() < sell.getPrice()) {
                break;
            }
//calaculate the match the min quantity
            long qty = Math.min(bestBuy.getRemainingQuantity(), sell.getRemainingQuantity());

        // reuce the orders
            sell.reduceQuantity(qty);
            bestBuy.reduceQuantity(qty);

            // EXECUTE TRADE (FILE ONLY)
            TradeExecutor.executeTrade(
                    bestBuy.getOrderId(),
                    sell.getOrderId(),
                    sell.getTradeType(),
                    sell.getPrice(),
                    qty
            );
//remove the filed sell here
            if (bestBuy.isFilled()) {
                synchronized (book) {
                    book.pollBestBuy();
                }
            }
        }
//add sell if remaining
        if (!sell.isFilled()) {
            synchronized (book) {
                book.addOrder(sell);
            }
        }
    }
}
