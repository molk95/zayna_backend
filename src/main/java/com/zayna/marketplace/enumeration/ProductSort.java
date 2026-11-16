package com.zayna.marketplace.enumeration;

import org.springframework.data.domain.Sort;

import java.util.Locale;

public enum ProductSort {
    NONE, PRICE_ASC, PRICE_DESC, NAME, NEWEST;

    /** Valeurs acceptées : none, price_asc, price_desc, name, newest. */
    public static ProductSort fromParam(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "none" -> NONE;
            case "price_asc" -> PRICE_ASC;
            case "price_desc" -> PRICE_DESC;
            case "name" -> NAME;
            case "newest" -> NEWEST;
            default -> throw new IllegalArgumentException("Tri inconnu : " + value);
        };
    }

    public Sort toSort() {
        return switch (this) {
            case NONE -> Sort.by("id").ascending();
            case PRICE_ASC -> Sort.by("price").ascending();
            case PRICE_DESC -> Sort.by("price").descending();
            case NAME -> Sort.by("name").ascending();
            case NEWEST -> Sort.by("createdAt").descending();
        };
    }
}
