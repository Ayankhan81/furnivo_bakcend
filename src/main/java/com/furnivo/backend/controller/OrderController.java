package com.furnivo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CartController cartController;

    public OrderController(CartController cartController) {
        this.cartController = cartController;
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(Authentication auth, @RequestBody Map<String, Object> payload) {
        // Calculate total amount from cart
        ResponseEntity<?> cartResponse = cartController.getCart(auth);
        double total = 0;
        if (cartResponse.getBody() instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cart = (List<Map<String, Object>>) cartResponse.getBody();
            for (Map<String, Object> item : cart) {
                double price = Double.parseDouble(item.get("price").toString());
                int qty = Integer.parseInt(item.get("quantity").toString());
                total += price * qty;
            }
        }

        String paymentMethod = (String) payload.get("paymentMethod");
        Map<String, Object> response = new HashMap<>();
        
        if ("credit-card".equals(paymentMethod)) {
            response.put("razorpayOrderId", "order_" + UUID.randomUUID().toString().substring(0, 10));
            response.put("totalAmount", total);
            return ResponseEntity.ok(response);
        } else {
            // Cash on delivery
            cartController.clearCart(auth);
            response.put("message", "Order placed successfully via COD");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(Authentication auth, @RequestBody Map<String, Object> payload) {
        // In a real app we'd verify the signature with Razorpay API here.
        // For demo, we just assume success, clear the cart, and return.
        cartController.clearCart(auth);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payment verified and order confirmed");
        
        return ResponseEntity.ok(response);
    }
}
