package com.example.flashsale.service;

import com.example.flashsale.domain.Order;
import com.example.flashsale.domain.OrderEvent;
import com.example.flashsale.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "flash-sale-orders", groupId = "flash-sale-group")
    public void consumeOrder(OrderEvent event) {
        log.info("📦 Consuming Kafka Event: Saving Order to DB for User: {}", event.userId());
        
        // Convert the Event into a Database Entity
        Order order = new Order();
        order.setUserId(event.userId());
        order.setProductId(event.productId());
        order.setStatus("CONFIRMED");
        
        // Save to PostgreSQL
        orderRepository.save(order);
    }
}