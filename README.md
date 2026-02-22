# 🚀 High-Concurrency Flash Sale & Inventory System

An enterprise-grade, event-driven backend system designed to handle massive traffic spikes (like Black Friday sales or sneaker drops) without database deadlocks, downtime, or overselling.

## 🧠 The Problem & The Solution
**The Problem:** During a flash sale, thousands of users hit the "Buy" button at the exact same millisecond. Traditional relational databases (PostgreSQL/MySQL) lock up under this concurrent read/write heavy load, leading to database crashes and "overselling" (selling items you don't actually have).

**The Solution:** I designed an **Event-Driven Architecture** acting as a "shock-absorber":
1. **Pre-heat Cache:** Inventory is loaded into **Redis** before the sale begins.
2. **Anti-Bot Shield:** A **Redisson Rate Limiter** blocks malicious IP/Users from spamming the endpoint (Max 3 req/min).
3. **Atomic Operations & Distributed Locks:** **Redisson RLock** prevents duplicate user requests, and Redis atomic `decrement` guarantees we never oversell.
4. **Event-Driven Persistence:** Successful cache orders are pushed to **Apache Kafka**. A background consumer safely pulls these events and writes them to **PostgreSQL** at a controlled, stable speed.

## 🛠️ Tech Stack
* **Core:** Java 21, Spring Boot 3.4
* **Database:** PostgreSQL (Source of Truth)
* **Cache & Locks:** Redis, Redisson (Distributed Locking & Rate Limiting)
* **Message Broker:** Apache Kafka (Event Queue / Shock Absorber)
* **Observability:** Prometheus, Grafana, Spring Boot Actuator
* **Infrastructure:** Docker, Docker Compose
* **Documentation:** OpenAPI / Swagger UI

## 📸 System Proof & Observability

*(Upload your Grafana screenshot here!)*
> **Grafana Dashboard:** Simulating 200 concurrent requests. The blue line represents `202 Accepted` orders successfully sent to Kafka. The green spike represents `410 Gone` instantly blocking requests the exact millisecond inventory hit 0. 

*(Upload your Swagger UI screenshot here!)*
> **Swagger UI:** Testing the endpoint locally.

## 🚀 How to Run This Project Locally

**1. Start the Infrastructure (Docker)**
Make sure you have Docker Desktop installed. Run this command to spin up Postgres, Redis, Kafka, Prometheus, and Grafana:
```bash
docker compose up -d