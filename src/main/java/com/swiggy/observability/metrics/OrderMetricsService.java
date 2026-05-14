package com.swiggy.observability.metrics;

import com.swiggy.observability.exception.OrderFailureException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMetricsService {

    private final MeterRegistry meterRegistry;
    private final Queue<String> pendingOrdersQueue = new ConcurrentLinkedQueue<>();
    private Counter ordersPlacedCounter;

    @PostConstruct
    public void initMetrics() {

        // COUNTER — only goes up
        ordersPlacedCounter = Counter.builder("orders.placed")
                .tag("app", "swiggy-demo")
                .register(meterRegistry);

        // GAUGE — goes up and down with queue size
        Gauge.builder("orders.pending.queue.size", pendingOrdersQueue, Queue::size)
                .tag("app", "swiggy-demo")
                .register(meterRegistry);
    }

    public void recordOrderPlaced(String orderId) {
        ordersPlacedCounter.increment();
        pendingOrdersQueue.add(orderId);
    }

    public void recordOrderDelivered(String orderId) {
        pendingOrdersQueue.remove(orderId);
    }

    public void recordOrderFailure(OrderFailureException.FailureReason reason) {
        Counter.builder("orders.failed")
                .tag("app", "swiggy-demo")
                .tag("reason", reason.name().toLowerCase())
                .register(meterRegistry)
                .increment();
    }
}