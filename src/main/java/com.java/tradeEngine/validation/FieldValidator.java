package com.java.tradeEngine.validation;

import com.java.tradeEngine.exceptions.InsufficientQuantityException;
import com.java.tradeEngine.exceptions.InvalidCountryException;
import com.java.tradeEngine.exceptions.InvalidOrderException;
import com.java.tradeEngine.exceptions.ValidationException;
import com.java.tradeEngine.model.Orders;

public class FieldValidator implements OrderValidator {

    @Override
    public void validate(Orders order) throws Exception {
        //order must not be null
        if (order == null) throw new ValidationException("Order is null");
        //orderId and TradeId must not be empty
        if (order.getOrderId() == null || order.getOrderId().trim().isEmpty())
            throw new ValidationException("OrderId is invalid");
        if (order.getTraderId() == null || order.getTraderId().trim().isEmpty())
            throw new ValidationException("TraderId is invalid");
        if (order.getTradeType() == null)
            throw new ValidationException("TradeType is null");
        if (order.getOrderType() == null)
            throw new InvalidOrderException("OrderType is null");
        // price and Quantity must be positive
        if (order.getPrice() <= 0)
            throw new ValidationException("Price must be positive");
        if (order.getQuantity() <= 0)
            throw new InsufficientQuantityException("Quantity must be positive");
        // country must not be empty or null
        if (order.getCountryCode() == null || order.getCountryCode().trim().isEmpty())
            throw new InvalidCountryException("Country code is null or empty");

    }
}
