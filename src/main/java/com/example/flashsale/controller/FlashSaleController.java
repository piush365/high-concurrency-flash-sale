package com.example.flashsale.controller;

import com.example.flashsale.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flash-sale")
@RequiredArgsConstructor
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    @PostMapping("/buy")
    public ResponseEntity<String> buyProduct(@RequestParam String userId, @RequestParam String productId) {
        
        String result = flashSaleService.placeOrder(userId, productId);

        return switch (result) {
            case "ACCEPTED" -> ResponseEntity.status(HttpStatus.ACCEPTED).body("🎉 Order placed successfully! You are in the queue.");
            case "SOLD_OUT" -> ResponseEntity.status(HttpStatus.GONE).body("😭 Sorry, this item is completely sold out!");
            case "RATE_LIMITED" -> ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("⏳ Too many requests. Please slow down.");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Something went wrong.");
        };
    }
}