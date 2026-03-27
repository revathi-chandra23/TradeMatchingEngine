package com.java.tradeEngine.exceptions;

public class AmountLimitExceededException extends ValidationException{
    public AmountLimitExceededException(String message) {
        super(message);
    }
}

