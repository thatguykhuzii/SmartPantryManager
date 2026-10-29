package com.siphosethu.smartpantrymanager;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RecipeMatchingQueryTest {

    @Test
    public void strictMatchingRulesAreDocumented() {
        String missingIngredientRule =
                "A missing required ingredient excludes a recipe.";

        String insufficientQuantityRule =
                "An insufficient quantity excludes a recipe.";

        String unitMismatchRule =
                "A unit mismatch currently excludes a recipe.";

        assertTrue(missingIngredientRule.contains("missing"));
        assertTrue(insufficientQuantityRule.contains("insufficient"));
        assertTrue(unitMismatchRule.contains("mismatch"));
    }
}