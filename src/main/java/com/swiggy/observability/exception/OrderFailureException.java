package com.swiggy.observability.exception;

import lombok.Getter;

@Getter
public class OrderFailureException extends RuntimeException {

    private final FailureReason reason;

    public OrderFailureException(FailureReason reason) {
        super("Order failed: " + reason.name());
        this.reason = reason;
    }

    public enum FailureReason {
        PAYMENT_DECLINED,   // Payment gateway rejected
        RIDER_NOT_FOUND,    // No rider available in area
        ITEM_UNAVAILABLE,   // Item out of stock
        RESTAURANT_CLOSED   // Restaurant not accepting orders
    }
}
