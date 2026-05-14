package com.swiggy.observability.model;

import lombok.Data;

@Data
public class OrderRequest {

    private String customerId;
    private String restaurantId;
    private String itemName;
    private double amount;

    // Simulate failure scenarios for demo purposes
    // Pass: "PAYMENT_DECLINED", "RIDER_NOT_FOUND", "ITEM_UNAVAILABLE", "RESTAURANT_CLOSED"
    private String simulateFailure;
}
