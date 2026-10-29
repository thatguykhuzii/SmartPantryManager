package com.siphosethu.smartpantrymanager;

import com.siphosethu.smartpantrymanager.util.UnitConverter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RecipeMatchingQueryTest {

    @Test
    public void kilogramAvailabilitySatisfiesGramRequirement() {
        double availableGrams =
                UnitConverter.toBaseQuantity(1, "kg");

        double requiredGrams =
                UnitConverter.toBaseQuantity(500, "g");

        assertTrue(availableGrams >= requiredGrams);
    }

    @Test
    public void litreAvailabilitySatisfiesMillilitreRequirement() {
        double availableMillilitres =
                UnitConverter.toBaseQuantity(1, "l");

        double requiredMillilitres =
                UnitConverter.toBaseQuantity(500, "ml");

        assertTrue(availableMillilitres >= requiredMillilitres);
    }

    @Test
    public void insufficientQuantityFailsMatching() {
        double availableGrams =
                UnitConverter.toBaseQuantity(250, "g");

        double requiredGrams =
                UnitConverter.toBaseQuantity(500, "g");

        assertTrue(availableGrams < requiredGrams);
    }

    @Test
    public void exactQuantityPassesMatching() {
        double availableGrams =
                UnitConverter.toBaseQuantity(500, "g");

        double requiredGrams =
                UnitConverter.toBaseQuantity(0.5, "kg");

        assertEquals(availableGrams, requiredGrams, 0.000001);
    }
}