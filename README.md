# 🍔 Swiggy Observability Demo
## Spring Boot + Micrometer + Prometheus + Grafana

Full source code for the **"Observe Your Spring Boot App"** series
by [@theconsistentcoder](https://www.youtube.com/@theconsistentcoder)

---

## 📦 Tech Stack

| Tool | Purpose | Port |
|---|---|---|
| Spring Boot 3.2 | Your app | 8080 |
| Actuator + Micrometer | Expose metrics | /actuator/prometheus |
| Prometheus | Collect & store metrics | 9090 |
| Grafana | Dashboards + Alerting | 3000 |

---

## 🚀 Quick Start

### Step 1 — Run Spring Boot
```bash
mvn spring-boot:run
```

Verify it's up:
```
http://localhost:8080/api/orders/health
http://localhost:8080/actuator/prometheus
```

### Step 2 — Start Prometheus + Grafana
```bash
docker-compose up -d
```

### Step 3 — Open Grafana
```
http://localhost:3000
username: admin
password: admin
```

Go to **Dashboards → Import → ID: 19004** → Select Prometheus → Import

---

## 🧪 Demo Commands (curl)

### Place a successful order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C001",
    "restaurantId": "R001",
    "itemName": "Butter Chicken",
    "amount": 349.0
  }'
```

### Simulate payment declined
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C002",
    "restaurantId": "R001",
    "itemName": "Pizza",
    "amount": 499.0,
    "simulateFailure": "PAYMENT_DECLINED"
  }'
```

### Simulate rider not found
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C003",
    "restaurantId": "R002",
    "itemName": "Biryani",
    "amount": 299.0,
    "simulateFailure": "RIDER_NOT_FOUND"
  }'
```

### Simulate item unavailable
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "C004",
    "restaurantId": "R003",
    "itemName": "Dosa",
    "amount": 149.0,
    "simulateFailure": "ITEM_UNAVAILABLE"
  }'
```

### Mark order as delivered (Gauge goes down)
```bash
curl -X PUT http://localhost:8080/api/orders/ORDER_ID_HERE/deliver
```

---

## 📊 Prometheus Queries to Try

```promql
# Total orders placed
orders_placed_total

# Current pending orders in queue
orders_pending_queue_size

# All failure types
orders_failed_total

# Only payment failures
orders_failed_total{reason="payment_declined"}

# Failure rate per minute (for alert rules)
rate(orders_failed_total[2m]) * 60

# HTTP request rate
rate(http_server_requests_seconds_count[1m])

# p99 response time
histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m]))

# JVM heap usage %
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```

---

## 📁 Project Structure

```
swiggy-observability/
├── src/main/java/com/swiggy/observability/
│   ├── ObservabilityDemoApplication.java   # Main class
│   ├── controller/
│   │   └── OrderController.java            # REST endpoints
│   ├── service/
│   │   └── OrderService.java               # Business logic + @Timed
│   ├── metrics/
│   │   └── OrderMetricsService.java        # Counter + Gauge custom metrics
│   ├── model/
│   │   ├── Order.java
│   │   └── OrderRequest.java
│   └── exception/
│       └── OrderFailureException.java      # Failure reasons as enum
├── src/main/resources/
│   └── application.properties             # Actuator + Micrometer config
├── prometheus/
│   ├── prometheus.yml                     # Scrape config
│   └── alert-rules.yml                    # Alerting rules (Part 7)
├── grafana/
│   └── provisioning/
│       └── datasources/
│           └── prometheus.yml             # Auto-connect Grafana to Prometheus
├── docker-compose.yml                     # One command to start everything
└── pom.xml
```

---

## 🔗 Series Links

| Part | Topic |
|---|---|
| Part 1 | What is Observability — M·L·T Pillars |
| Part 2 | Actuator + Micrometer Setup |
| Part 3 | Prometheus Setup + Scraping |
| Part 4 | Grafana Dashboards |
| Part 5 | Custom Metrics — Counter, Gauge, @Timed |
| Part 6 | Business Error Tracking — order.failed, payment.declined |
| Part 7 | Alerting Rules — Know before your users do |

---


