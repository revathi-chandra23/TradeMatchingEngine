package com.java.tradeEngine.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.sql.Timestamp;

@Getter
@ToString

public class Orders {

    private final String orderId;
    private final String traderId;
    private final TradeType tradeType;
    private final OrderType orderType;
    private final double price;
    private final long quantity;
    private final String countryCode;
    private final Timestamp timestamp;

    @Setter
    private long remainingQuantity;

    @Setter
    private OrderStatus status;

    @Builder
    public Orders(String orderId, String traderId, TradeType tradeType, OrderType orderType,
                  double price, long quantity, String countryCode, Timestamp timestamp) {
        this.orderId = orderId;
        this.traderId = traderId;
        this.tradeType = tradeType;
        this.orderType = orderType;
        this.price = price;
        this.quantity = quantity;
        this.remainingQuantity = quantity;
        this.countryCode = countryCode;
        this.timestamp = timestamp;
        this.status = OrderStatus.PENDING;
    }

    public boolean isFilled() {    // check it fully matched or not
        return remainingQuantity == 0;
    }

    public void reduceQuantity(long qty) {
        //Matching engine will reduce quantity and update the status called during the matching
        // if (qty <= 0) return;
        if (qty > remainingQuantity) qty = remainingQuantity;
        remainingQuantity -= qty;
        if (remainingQuantity == 0)
        {
            status = OrderStatus.FILLED;
        }
        else
        {
            status = OrderStatus.PARTIALLY_FILLED;
        }
    }
//mark invalid orders
    public void reject() {
        status = OrderStatus.REJECTED;
    }

}
