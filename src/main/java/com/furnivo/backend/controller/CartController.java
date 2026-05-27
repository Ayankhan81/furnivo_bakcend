package com.furnivo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final ProductController productController;

    // Use an in-memory map to store carts keyed by user email.
    // Extremely efficient for the mock product setup.
    private static final Map<String, List<Map<String, Object>>> userCarts = new ConcurrentHashMap<>();

    public CartController(ProductController productController) {
        this.productController = productController;
    }

    private String getEmail(Authentication auth) {
        return auth.getName();
    }

    @GetMapping
    public ResponseEntity<?> getCart(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(userCarts.getOrDefault(getEmail(auth), new ArrayList<>()));
    }

    @PostMapping("/merge")
    public ResponseEntity<?> mergeCart(Authentication auth, @RequestBody List<Map<String, Object>> guestCart) {
        String email = getEmail(auth);
        List<Map<String, Object>> cart = userCarts.computeIfAbsent(email, k -> new ArrayList<>());
        
        for (Map<String, Object> guestItem : guestCart) {
            boolean found = false;
            for (Map<String, Object> item : cart) {
                if (item.get("id").toString().equals(String.valueOf(guestItem.get("id")))) {
                    int prevQty = (Integer) item.getOrDefault("quantity", 1);
                    int guestQty = (Integer) guestItem.getOrDefault("quantity", 1);
                    item.put("quantity", prevQty + guestQty);
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Ensure name is mapped since Frontend CartItems use 'name' instead of 'title'
                if (guestItem.containsKey("title") && !guestItem.containsKey("name")) {
                    guestItem.put("name", guestItem.get("title"));
                }
                cart.add(guestItem);
            }
        }
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(Authentication auth, @RequestBody Map<String, Object> payload) {
        String email = getEmail(auth);
        List<Map<String, Object>> cart = userCarts.computeIfAbsent(email, k -> new ArrayList<>());
        
        int productId = (Integer) payload.get("productId");
        int quantity = (Integer) payload.getOrDefault("quantity", 1);

        for (Map<String, Object> item : cart) {
            if (item.get("id").toString().equals(String.valueOf(productId))) {
                int oldQty = (Integer) item.getOrDefault("quantity", 1);
                item.put("quantity", oldQty + quantity);
                return ResponseEntity.ok(cart);
            }
        }

        // Product not in cart, fetch its details from ProductController
        ResponseEntity<?> res = productController.getProductById(productId);
        if (res.getBody() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> prod = new HashMap<>((Map<String, Object>) res.getBody());
            prod.put("name", prod.getOrDefault("title", "Product " + productId)); 
            prod.put("quantity", quantity);
            cart.add(prod);
        }

        return ResponseEntity.ok(cart);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCart(Authentication auth, @RequestBody Map<String, Object> payload) {
        String email = getEmail(auth);
        List<Map<String, Object>> cart = userCarts.getOrDefault(email, new ArrayList<>());
        
        int productId = (Integer) payload.get("productId");
        int quantity = (Integer) payload.get("quantity");
        
        for (Map<String, Object> item : cart) {
            if (item.get("id").toString().equals(String.valueOf(productId))) {
                item.put("quantity", quantity);
                break;
            }
        }
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<?> removeProduct(Authentication auth, @PathVariable int productId) {
        String email = getEmail(auth);
        List<Map<String, Object>> cart = userCarts.getOrDefault(email, new ArrayList<>());
        cart.removeIf(item -> item.get("id").toString().equals(String.valueOf(productId)));
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/all")
    public ResponseEntity<?> clearCart(Authentication auth) {
        String email = getEmail(auth);
        userCarts.put(email, new ArrayList<>());
        return ResponseEntity.ok("Cleared");
    }
}
