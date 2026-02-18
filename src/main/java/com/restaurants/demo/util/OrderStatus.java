package com.restaurants.demo.util;

public enum OrderStatus {
    PLACED,
    PREPARING,
    READY,
    SERVED,
    CANCELLED,
    BILLED;

    public boolean canTransitionTo (OrderStatus nextStatus) {
        return switch (this) {
            case PLACED -> nextStatus == PREPARING || nextStatus == CANCELLED;
            case PREPARING -> nextStatus == READY;
            case READY -> nextStatus == SERVED;
            case SERVED -> nextStatus == BILLED;
            case CANCELLED, BILLED -> false;
        };
    }
}