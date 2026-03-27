
import com.java.tradeEngine.book.PriceTimeOrderBook;
import com.java.tradeEngine.validation.*;
import com.java.tradeEngine.validation.OrderValidationService;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.List;
import com.java.tradeEngine.book.OrderBook;
import com.java.tradeEngine.book.OrderBookManager;
import com.java.tradeEngine.matchingEngine.MatchingEngine;
import com.java.tradeEngine.matchingEngine.PriceTimePriorityStrategy;
import com.java.tradeEngine.model.OrderStatus;
import com.java.tradeEngine.model.OrderType;
import com.java.tradeEngine.model.Orders;
import com.java.tradeEngine.model.TradeType;
import static org.junit.jupiter.api.Assertions.*;


public class ValidationFailureTest {
//ensure approved countries and ammount ,maxamount
    @Test
    void testInvalidCountryRejected() {
        OrderValidationService validator = new OrderValidationService(
                List.of(new FieldValidator(), new CountryValidator(), new MaxAmountValidator())
        );

        Orders order = Orders.builder()
                .orderId("X1").traderId("T1").tradeType(TradeType.EQUITY)
                .orderType(OrderType.BUY).price(100).quantity(10)
                .countryCode("ZZ") // Invalid country
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        assertThrows(Exception.class, () -> validator.validate(order));
    }

    @Test
    void testAmountLimitExceeded() {
        OrderValidationService validator = new OrderValidationService(
                List.of(new FieldValidator(), new CountryValidator(), new MaxAmountValidator())
        );

        Orders order = Orders.builder()
                .orderId("X2").traderId("T1").tradeType(TradeType.CRYPTO)
                .orderType(OrderType.BUY).price(10_000).quantity(10)
                .countryCode("US")
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        assertThrows(Exception.class, () -> validator.validate(order));
    }
// FIFO order matching when prices are equal
    @Test
        void testFifoForSamePrice() {
        PriceTimeOrderBook book = new PriceTimeOrderBook();
        MatchingEngine engine = new MatchingEngine(new PriceTimePriorityStrategy());

        Orders sell1 = Orders.builder()
                .orderId("S1").traderId("T1").tradeType(TradeType.EQUITY)
                .orderType(OrderType.SELL).price(100).quantity(10)
                .countryCode("US").timestamp(new Timestamp(1000))// different time stamps
                .build();

        Orders sell2 = Orders.builder()
                .orderId("S2").traderId("T2").tradeType(TradeType.EQUITY)
                .orderType(OrderType.SELL).price(100).quantity(10)
                .countryCode("US").timestamp(new Timestamp(2000))
                .build();

        book.addOrder(sell1);
        book.addOrder(sell2);

        Orders buy = Orders.builder()
                .orderId("B1").traderId("TB").tradeType(TradeType.EQUITY)
                .orderType(OrderType.BUY).price(150).quantity(10)
                .countryCode("US").timestamp(new Timestamp(3000))
                .build();
//Create BUY order that matches price
        engine.processOrder(book, buy);

        assertEquals(OrderStatus.FILLED, sell1.getStatus());  // oldest filled first
        assertEquals(OrderStatus.PENDING, sell2.getStatus());
    }
}
