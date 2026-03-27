

import com.java.tradeEngine.book.OrderBook;
import com.java.tradeEngine.book.OrderBookManager;
import com.java.tradeEngine.book.PriceTimeOrderBook;
import com.java.tradeEngine.matchingEngine.MatchingEngine;
import com.java.tradeEngine.matchingEngine.PriceTimePriorityStrategy;
import com.java.tradeEngine.model.OrderStatus;
import com.java.tradeEngine.model.OrderType;
import com.java.tradeEngine.model.Orders;
import com.java.tradeEngine.model.TradeType;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingEngineFullMatchTest {

    @Test
    void testBuyFullyMatchesSell() {
        //create order book
        OrderBook book = new PriceTimeOrderBook();
        MatchingEngine engine = new MatchingEngine(new PriceTimePriorityStrategy());
// complete matching scenarios
        Orders sell = Orders.builder()
                .orderId("S1")
                .traderId("TS")
                .tradeType(TradeType.EQUITY)
                .orderType(OrderType.SELL)
                .price(100)//create a sell order
                .quantity(50)
                .countryCode("US")
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        Orders buy = Orders.builder()
                .orderId("B1")
                .traderId("TB")
                .tradeType(TradeType.EQUITY)
                .orderType(OrderType.BUY)//create buy order
                .price(120) // Higher price → match
                .quantity(50)
                .countryCode("US")
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        book.addOrder(sell); // add sell first
        engine.processOrder(book, buy);
//if both are filled
        assertEquals(OrderStatus.FILLED, sell.getStatus());
        assertEquals(OrderStatus.FILLED, buy.getStatus());
        //order book is empty
        assertTrue(book.getBuyOrders().isEmpty());
        assertTrue(book.getSellOrders().isEmpty());
    }
}
