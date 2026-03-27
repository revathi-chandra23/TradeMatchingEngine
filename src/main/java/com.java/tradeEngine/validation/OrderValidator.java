package com.java.tradeEngine.validation;

import com.java.tradeEngine.model.Orders;

//  main interface for all the validators
public interface OrderValidator {
    void validate(Orders order) throws Exception;
}