package com.java.tradeEngine.model;

public enum OrderStatus {
    PENDING,  //no trades yet
    PARTIALLY_FILLED,
    FILLED,
    REJECTED // invalid
}