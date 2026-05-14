package com.swiggy.observability.service;

import com.swiggy.observability.exception.OrderFailureException;
import com.swiggy.observability.metrics.OrderMetricsService;
import com.swiggy.observability.model.Order;
import com.swiggy.observability.model.OrderRequest;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMetricsService metricsService;

    // @Timed — measures every call automatically
    // tracks count, sum, max, p99 percentiles
    @Timed(value = "order.processing.time")
    public Order placeOrder(OrderRequest request) {

        String orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // simulate failure
        if (request.getSimulateFailure() != null) {
            OrderFailureException.FailureReason reason = parseReason(request.getSimulateFailure());
            metricsService.recordOrderFailure(reason);
            throw new OrderFailureException(reason);
        }

        // happy path
        simulateProcessing();

        Order order = Order.builder()
                .orderId(orderId)
                .customerId(request.getCustomerId())
                .restaurantId(request.getRestaurantId())
                .itemName(request.getItemName())
                .amount(request.getAmount())
                .status(Order.OrderStatus.PLACED)
                .build();

        metricsService.recordOrderPlaced(orderId);
        return order;
    }

    public void deliverOrder(String orderId) {
        metricsService.recordOrderDelivered(orderId);
    }

    private OrderFailureException.FailureReason parseReason(String failure) {
        try {
            return OrderFailureException.FailureReason.valueOf(failure.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OrderFailureException.FailureReason.PAYMENT_DECLINED;
        }
    }

    private void simulateProcessing() {
        try {
            Thread.sleep(50 + (long) (Math.random() * 150));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}