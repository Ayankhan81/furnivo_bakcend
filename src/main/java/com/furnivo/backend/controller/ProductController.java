package com.furnivo.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Simple mock products that match the frontend IDs for seamless testing
    @GetMapping
    public ResponseEntity<?> getAllProducts(@RequestParam(required = false) String search) {
        List<Map<String, Object>> products = new ArrayList<>();
        
        products.add(createProduct(1, "Modern Lighting Setup", "Lighting", 299.0, 350.0, "/assests/lightening1.webp", "Brighten up your space with this modern lighting setup."));
        products.add(createProduct(2, "Ergonomic Seating", "Seating", 450.0, 500.0, "/assests/seating.png", "Experience ultimate comfort with our ergonomic seating."));
        products.add(createProduct(3, "Minimalist Table", "Tables", 199.0, 250.0, "/assests/c2f9e1f5a3c1e9a81f5475b17309f068.jpg", "A perfect blend of functionality and style."));
        products.add(createProduct(4, "Home Accessories Set", "Accessories", 89.0, 120.0, "/assests/accessories.webp", "Complete your room's look with these sleek accessories."));
        products.add(createProduct(5, "Scandinavian Modern Bed", "Beds", 1500.0, 2000.0, "/assests/bed.png", "Blends modern scandinavian design."));
        
        if (search != null && !search.trim().isEmpty()) {
            final String s = search.toLowerCase();
            products.removeIf(p -> !((String)p.get("title")).toLowerCase().contains(s));
        }

        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        // Fallback for demo
        return ResponseEntity.ok(createProduct(id, "Product " + id, "General", 250.0, 300.0, "/assests/bed.png", "Dynamic description for testing"));
    }

    private Map<String, Object> createProduct(int id, String title, String category, double price, double oldPrice, String image, String description) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("title", title); // Note: backend sends 'title', frontend maps it to 'name'
        product.put("category", category);
        product.put("price", price);
        product.put("oldPrice", oldPrice);
        product.put("image", image);
        product.put("description", description);
        return product;
    }
}
