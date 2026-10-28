package com.siphosethu.smartpantrymanager;

import com.siphosethu.smartpantrymanager.util.IngredientNormalizer;
import com.siphosethu.smartpantrymanager.util.UnitConverter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PantryUtilitiesTest {

    @Test
    public void ingredientName_trimsSpacesAndNormalizesPlural() {
        assertEquals(
                "tomato",
                IngredientNormalizer.normalize("  Tomatoes  ")
        );
    }

    @Test
    public void ingredientName_collapsesRepeatedSpaces() {
        assertEquals(
                "bell pepper",
                IngredientNormalizer.normalize("BELL    PEPPERS")
        );
    }

    @Test
    public void ingredientName_preservesUnlistedWords() {
        assertEquals(
                "rice",
                IngredientNormalizer.normalize("Rice")
        );

        assertEquals(
                "couscous",
                IngredientNormalizer.normalize("Couscous")
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void ingredientName_rejectsBlankInput() {
        IngredientNormalizer.normalize("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ingredientName_rejectsNullInput() {
        IngredientNormalizer.normalize(null);
    }

    @Test
    public void unit_normalizesAliases() {
        assertEquals("g", UnitConverter.normalizeUnit(" Grams "));
        assertEquals("kg", UnitConverter.normalizeUnit("kilograms"));
        assertEquals("ml", UnitConverter.normalizeUnit("millilitres"));
        assertEquals("l", UnitConverter.normalizeUnit("liters"));
        assertEquals("piece", UnitConverter.normalizeUnit("pieces"));
    }

    @Test
    public void quantity_convertsKilogramsToGrams() {
        assertEquals(
                1500.0,
                UnitConverter.toBaseQuantity(1.5, "kg"),
                0.000001
        );
    }

    @Test
    public void quantity_convertsLitresToMillilitres() {
        assertEquals(
                750.0,
                UnitConverter.toBaseQuantity(0.75, "l"),
                0.000001
        );
    }

    @Test
    public void quantity_preservesBaseUnits() {
        assertEquals(
                250.0,
                UnitConverter.toBaseQuantity(250, "g"),
                0.000001
        );

        assertEquals(
                300.0,
                UnitConverter.toBaseQuantity(300, "ml"),
                0.000001
        );

        assertEquals(
                3.0,
                UnitConverter.toBaseQuantity(3, "piece"),
                0.000001
        );
    }

    @Test
    public void compatibility_acceptsCorrectMeasurementTypes() {
        assertTrue(UnitConverter.isCompatible("kg", "mass"));
        assertTrue(UnitConverter.isCompatible("ml", "volume"));
        assertTrue(UnitConverter.isCompatible("pieces", "count"));
    }

    @Test
    public void compatibility_rejectsDifferentMeasurementTypes() {
        assertFalse(UnitConverter.isCompatible("g", "volume"));
        assertFalse(UnitConverter.isCompatible("l", "mass"));
        assertFalse(UnitConverter.isCompatible("piece", "mass"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unit_rejectsUnsupportedUnits() {
        UnitConverter.normalizeUnit("cup");
    }

    @Test(expected = IllegalArgumentException.class)
    public void quantity_rejectsZero() {
        UnitConverter.toBaseQuantity(0, "g");
    }

    @Test(expected = IllegalArgumentException.class)
    public void quantity_rejectsNegativeValues() {
        UnitConverter.toBaseQuantity(-5, "g");
    }

    @Test(expected = IllegalArgumentException.class)
    public void quantity_rejectsNaN() {
        UnitConverter.toBaseQuantity(Double.NaN, "g");
    }

    @Test(expected = IllegalArgumentException.class)
    public void quantity_rejectsInfinity() {
        UnitConverter.toBaseQuantity(
                Double.POSITIVE_INFINITY,
                "g"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void quantity_rejectsConversionOverflow() {
        UnitConverter.toBaseQuantity(Double.MAX_VALUE, "kg");
    }
}