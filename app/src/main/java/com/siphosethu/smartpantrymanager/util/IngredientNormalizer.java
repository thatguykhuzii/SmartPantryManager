package com.siphosethu.smartpantrymanager.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class IngredientNormalizer {

    private static final Map<String, String> ALIASES;

    static {
        Map<String, String> aliases = new HashMap<>();

        aliases.put("tomatoes", "tomato");
        aliases.put("potatoes", "potato");
        aliases.put("eggs", "egg");
        aliases.put("onions", "onion");
        aliases.put("carrots", "carrot");
        aliases.put("bananas", "banana");
        aliases.put("apples", "apple");
        aliases.put("peppers", "pepper");
        aliases.put("bell peppers", "bell pepper");

        ALIASES = Collections.unmodifiableMap(aliases);
    }

    private IngredientNormalizer() {
        // Utility class: no instances are needed.
    }

    public static String normalize(String input) {
        if (input == null) {
            throw new IllegalArgumentException(
                    "Ingredient name is required."
            );
        }

        String name = input.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        if (name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Ingredient name cannot be blank."
            );
        }

        String canonicalName = ALIASES.get(name);

        return canonicalName != null ? canonicalName : name;
    }
}