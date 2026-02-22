package com.example.flashsale.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class InventoryConfig {

    @Bean
    CommandLineRunner preloadInventory(StringRedisTemplate redisTemplate) {
        return args -> {
            // This runs once when the application starts up
            redisTemplate.opsForValue().set("stock:product:IPHONE15", "100");
            
            System.out.println("=====================================================");
            System.out.println("✅ INVENTORY PRE-LOADED: 100 IPHONE15s added to Redis!");
            System.out.println("=====================================================");
        };
    }
}