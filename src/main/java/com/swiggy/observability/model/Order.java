package com.swiggy.observability.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String orderId;
    private String customerId;
    private String restaurantId;
    private String itemName;
    private double amount;
    private OrderStatus status;

    public enum OrderStatus {
        PLACED, CONFIRMED, OUT_FOR_DELIVERY, DELIVERED, FAILED
    }
}
