package com.java.tradeEngine.validation;

import com.java.tradeEngine.model.Orders;

import java.util.ArrayList;
import java.util.List;

// Runs multiple validators on a single order. in sequence chain of responsibity pattern
// Useful when we want to combine many validation rules.
public class OrderValidationService implements OrderValidator {

    private final List<OrderValidator> validators = new ArrayList<>();

    public OrderValidationService(List<OrderValidator> validators) {
        this.validators.addAll(validators);
    }

    @Override
    public void validate(Orders order) throws Exception {
        for (OrderValidator v : validators) {  // apply each validator one by one to the order
            v.validate(order);
        }
    }
}