package com.example.flashsale.service;

import com.example.flashsale.domain.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleService {

    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public String placeOrder(String userId, String productId) {
        
        // 🚀 NEW: ANTI-BOT RATE LIMITER
        // Create a rate limiter specific to this user
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("rate_limiter:" + userId);
        // Allow max 3 requests per 1 minute per user
        rateLimiter.setRate(RateType.OVERALL, 3, Duration.ofMinutes(1));

        // If they exceed 3 requests, block them instantly!
        if (!rateLimiter.tryAcquire(1)) {
            log.warn("🚨 BOT DETECTED! Rate limit exceeded for User: {}", userId);
            return "TOO_MANY_REQUESTS";
        }

        // 1. Lock specific to user + product
        RLock lock = redissonClient.getLock("lock:order:" + userId + ":" + productId);

        try {
            if (lock.tryLock(1, 3, TimeUnit.SECONDS)) {
                String stockKey = "stock:product:" + productId;

                // 2. Atomic Decrement in Redis
                Long stockLeft = redisTemplate.opsForValue().decrement(stockKey);
                
                if (stockLeft != null && stockLeft < 0) {
                    redisTemplate.opsForValue().increment(stockKey);
                    return "SOLD_OUT";
                }

                // 3. Send successful order to Kafka
                OrderEvent event = new OrderEvent(userId, productId, Instant.now());
                kafkaTemplate.send("flash-sale-orders", event);

                log.info("✅ Cache accepted order for User: {} | Stock left: {}", userId, stockLeft);
                return "ACCEPTED";
            } else {
                return "CONCURRENT_REQUEST_BLOCKED"; 
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "ERROR";
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}