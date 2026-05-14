package com.swiggy.observability.controller;

import com.swiggy.observability.exception.OrderFailureException;
import com.swiggy.observability.model.Order;
import com.swiggy.observability.model.OrderRequest;
import com.swiggy.observability.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ── POST /api/orders ──────────────────────────────────────────────
    // Place an order — optionally simulate failures
    //
    // Happy path:
    // { "customerId": "C001", "restaurantId": "R001", "itemName": "Burger", "amount": 299.0 }
    //
    // Simulate payment failure:
    // { ..., "simulateFailure": "PAYMENT_DECLINED" }
    //
    // Simulate rider not found:
    // { ..., "simulateFailure": "RIDER_NOT_FOUND" }
    //
    // Simulate item unavailable:
    // { ..., "simulateFailure": "ITEM_UNAVAILABLE" }
    //
    // Simulate restaurant closed:
    // { ..., "simulateFailure": "RESTAURANT_CLOSED" }
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        try {
            Order order = orderService.placeOrder(request);
            return ResponseEntity.ok(order);
        } catch (OrderFailureException e) {
            log.error("❌ Order failed. Reason: {}", e.getReason());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Order Failed",
                            "reason", e.getReason().name(),
                            "message", e.getMessage()
                    ));
        }
    }

    // ── PUT /api/orders/{orderId}/deliver ─────────────────────────────
    // Mark order as delivered — removes from pending queue (Gauge goes down)
    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<?> deliverOrder(@PathVariable String orderId) {
        orderService.deliverOrder(orderId);
        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "status", "DELIVERED"
        ));
    }

    // ── GET /api/orders/health ────────────────────────────────────────
    // Simple endpoint to generate HTTP traffic for Prometheus graphs
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "Swiggy demo is running 🚀"));
    }
}
