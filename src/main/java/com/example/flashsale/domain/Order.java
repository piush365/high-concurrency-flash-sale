package com.example.flashsale.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
@Table(name = "orders") // "order" is a reserved keyword in Postgres, so we use "orders"
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String userId;
    private String productId;
    private String status;
    private Instant createdAt = Instant.now();
}