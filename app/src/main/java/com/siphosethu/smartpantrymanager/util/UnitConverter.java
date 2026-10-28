package com.siphosethu.smartpantrymanager.util;

import java.util.Locale;

public final class UnitConverter {

    private UnitConverter() {
        // Utility class: no objects need to be created.
    }

    public static String normalizeUnit(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Unit is required.");
        }

        String unit = input.trim().toLowerCase(Locale.ROOT);

        switch (unit) {
            case "g":
            case "gram":
            case "grams":
                return "g";

            case "kg":
            case "kilogram":
            case "kilograms":
                return "kg";

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";

            case "piece":
            case "pieces":
                return "piece";

            default:
                throw new IllegalArgumentException(
                        "Unsupported unit: " + input
                );
        }
    }

    public static String getMeasurementType(String inputUnit) {
        String unit = normalizeUnit(inputUnit);

        switch (unit) {
            case "g":
            case "kg":
                return "mass";

            case "ml":
            case "l":
                return "volume";

            case "piece":
                return "count";

            default:
                throw new IllegalArgumentException(
                        "Unsupported unit: " + inputUnit
                );
        }
    }

    public static boolean isCompatible(
            String inputUnit,
            String measurementType
    ) {
        return getMeasurementType(inputUnit).equals(measurementType);
    }

    public static double toBaseQuantity(
            double quantity,
            String inputUnit
    ) {
        if (Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be a finite number greater than zero."
            );
        }

        String unit = normalizeUnit(inputUnit);
        double result;

        switch (unit) {
            case "kg":
            case "l":
                result = quantity * 1000.0;
                break;

            default:
                result = quantity;
                break;
        }

        if (Double.isInfinite(result)) {
            throw new IllegalArgumentException(
                    "Quantity is too large."
            );
        }

        return result;
    }
}