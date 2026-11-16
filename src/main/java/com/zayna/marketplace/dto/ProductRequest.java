package com.zayna.marketplace.dto;

import com.zayna.marketplace.enumeration.ProductCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank(message = "Le nom est obligatoire")
        @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
        String name,

        @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
        String description,

        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.0", message = "Le prix ne peut pas être négatif")
        BigDecimal price,

        @NotNull(message = "La catégorie est obligatoire")
        ProductCategory category,

        @Size(max = 500, message = "L'URL de l'image est trop longue")
        String imageUrl,

        List<String> sizes,

        @Min(value = 0, message = "Le stock ne peut pas être négatif")
        Integer stock) {
}
