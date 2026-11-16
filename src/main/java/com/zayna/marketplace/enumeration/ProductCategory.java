package com.zayna.marketplace.enumeration;

import java.util.Locale;

/** À aligner avec la liste _categories de marketplace_screen.dart. */
public enum ProductCategory {
    ROBES, HAUTS, BAS, VESTES, CHAUSSURES, ACCESSOIRES;

    /** Convertit le paramètre de l'URL. "ALL", vide ou absent = pas de filtre (retourne null). */
    public static ProductCategory fromParam(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("ALL")) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Catégorie inconnue : " + value);
        }
    }
}
