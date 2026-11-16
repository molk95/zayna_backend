package com.zayna.marketplace.dto;

import com.zayna.marketplace.entity.Product;
import com.zayna.marketplace.enumeration.ProductCategory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        ProductCategory category,
        String imageUrl,
        List<String> sizes,
        int stock,
        boolean inStock,
        Instant createdAt) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl(),
                List.copyOf(product.getSizes()),
                product.getStock(),
                product.getStock() > 0,
                product.getCreatedAt());
    }
}
