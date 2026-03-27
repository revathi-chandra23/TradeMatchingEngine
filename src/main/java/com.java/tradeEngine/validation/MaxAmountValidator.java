package com.java.tradeEngine.validation;
import com.java.tradeEngine.exceptions.AmountLimitExceededException;
import com.java.tradeEngine.exceptions.ValidationException;
import com.java.tradeEngine.model.Orders;
public class MaxAmountValidator implements OrderValidator {
    @Override
    public void validate(Orders order) throws Exception {
        double value = order.getPrice() * order.getQuantity();// Ensures that the order value (price × quantity)
        switch (order.getTradeType()) { // Not to Exceed the Maximum allowed limit . if limit exceeds  it rejected
            case EQUITY -> {
                if (value > 100_000) throw new AmountLimitExceededException("EQUITY order exceeds 100000");
            }
            case FOREX -> {
                if (value > 500_000) throw new AmountLimitExceededException("FOREX order exceeds 500000");
            }
            case CRYPTO -> {
                if (value > 50_000) throw new AmountLimitExceededException("CRYPTO order exceeds 50000");
            }

            default -> throw new ValidationException("Unknown trade type: " + order.getTradeType());
        }
    }


}
