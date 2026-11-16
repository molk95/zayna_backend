package com.zayna.marketplace.controller;

import com.zayna.marketplace.dto.ProductRequest;
import com.zayna.marketplace.dto.ProductResponse;
import com.zayna.marketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /** GET /api/marketplace/products?category=ROBES&query=rob&sort=price_asc */
    @GetMapping("/products")
    public List<ProductResponse> list(@RequestParam(required = false) String category,
                                      @RequestParam(required = false) String query,
                                      @RequestParam(required = false) String sort) {
        return service.search(category, query, sort);
    }

    @GetMapping("/products/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/products/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public List<String> categories() {
        return service.categories();
    }
}
