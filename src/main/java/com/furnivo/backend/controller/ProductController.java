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

    private static final List<Map<String, Object>> mockProducts = new ArrayList<>();
    
    static {
        mockProducts.add(createProductStatic(1, "Modern Lighting Setup", "Lighting", 299.0, 350.0, "/assests/lightening1.webp", "Brighten up your space with this modern lighting setup."));
        mockProducts.add(createProductStatic(2, "Ergonomic Seating", "Seating", 450.0, 500.0, "/assests/seating.png", "Experience ultimate comfort with our ergonomic seating."));
        mockProducts.add(createProductStatic(3, "Minimalist Table", "Tables", 199.0, 250.0, "/assests/c2f9e1f5a3c1e9a81f5475b17309f068.jpg", "A perfect blend of functionality and style."));
        mockProducts.add(createProductStatic(4, "Home Accessories Set", "Accessories", 89.0, 120.0, "/assests/accessories.webp", "Complete your room's look with these sleek accessories."));
        mockProducts.add(createProductStatic(5, "Scandinavian Modern Bed", "Beds", 1500.0, 2000.0, "/assests/bed.png", "Blends modern scandinavian design."));
    }

    private static Map<String, Object> createProductStatic(int id, String title, String category, double price, double oldPrice, String image, String description) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", id);
        product.put("title", title);
        product.put("category", category);
        product.put("price", price);
        product.put("oldPrice", oldPrice);
        product.put("image", image);
        product.put("description", description);
        return product;
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(@RequestParam(required = false) String search) {
        List<Map<String, Object>> products = new ArrayList<>(mockProducts);
        
        if (search != null && !search.trim().isEmpty()) {
            final String s = search.toLowerCase();
            products.removeIf(p -> !((String)p.get("title")).toLowerCase().contains(s));
        }

        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        for (Map<String, Object> p : mockProducts) {
            if (p.get("id").toString().equals(String.valueOf(id))) {
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.status(404).body("Product not found");
    }

}
