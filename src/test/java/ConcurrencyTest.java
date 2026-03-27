

import com.java.tradeEngine.book.OrderBook;
import com.java.tradeEngine.book.OrderBookManager;
import com.java.tradeEngine.matchingEngine.MatchingEngine;
import com.java.tradeEngine.matchingEngine.PriceTimePriorityStrategy;
import com.java.tradeEngine.model.OrderStatus;
import com.java.tradeEngine.model.OrderType;
import com.java.tradeEngine.model.Orders;
import com.java.tradeEngine.model.TradeType;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A very simple concurrency test to check that
 * 100 threads can submit orders at the same time
 * without breaking the matching engine.
 */
public class ConcurrencyTest {

    @Test
    public void test_100_threads_submitting_orders() throws Exception {

        //  Creating The matching engine under test
        MatchingEngine engine = new MatchingEngine(new PriceTimePriorityStrategy());

        // Shared order book for EQUITY (more contention = better test) and sychronization logic
        OrderBook orderBook = OrderBookManager.getOrderBook(TradeType.EQUITY);

        // Store all orders to verify later
        List<Orders> allOrders = Collections.synchronizedList(new ArrayList<>());

        //  create 100 threads running in parallel
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Used to wait for all threads to complete
        CountDownLatch latch = new CountDownLatch(threadCount);

        // Submit 100 tasks (each task is one thread placing one order)
        for (int i = 0; i < threadCount; i++) {
            int id = i;
            executor.submit(() -> {

                try {
                    // Create one simple order per thread
                    Orders order = Orders.builder()
                            .orderId("ORDER-" + id) // unique order id
                            .traderId("T-" + id)
                            .tradeType(TradeType.EQUITY)
                            .orderType(id % 2 == 0 ? OrderType.BUY : OrderType.SELL) // alternating by and sell
                            .price(100 + (id % 5))
                            .quantity(100)
                            .countryCode("US")
                            .timestamp(new Timestamp(System.currentTimeMillis()))
                            .build();

                    allOrders.add(order);

                    //  call each thread of multiple threads calling processOrder()
                    engine.processOrder(orderBook, order);

                } finally {
                    latch.countDown();  // mark thread as finished
                }
            });
        }

        // Wait for all threads to finish
        latch.await();
        executor.shutdown();

        // ----- Simple validations -----

        // 1. No negative quantities each order reachhed final state
        for (Orders o : allOrders) {
            assertTrue(o.getRemainingQuantity() >= 0,
                    "Negative remaining qty for " + o.getOrderId());
        }

        // 2. Status should always be valid
        for (Orders o : allOrders) {
            assertNotNull(o.getStatus(), "Order status missing: " + o.getOrderId());
        }

        // 3. Total = sum of all statuses
        long total = allOrders.size();
        long count = allOrders.stream().filter(o ->
                o.getStatus() == OrderStatus.FILLED ||
                        o.getStatus() == OrderStatus.PARTIALLY_FILLED ||
                        o.getStatus() == OrderStatus.PENDING ||
                        o.getStatus() == OrderStatus.REJECTED
        ).count();

        assertEquals(total, count, "Some orders are missing a status");

    }
}
