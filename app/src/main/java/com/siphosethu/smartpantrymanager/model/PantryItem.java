package com.siphosethu.smartpantrymanager.model;

/**
 * Represents one saved pantry item together with its ingredient details.
 */
public class PantryItem {

    private final long id;
    private final long ingredientId;
    private final String ingredientName;
    private final String measurementType;
    private final double quantity;
    private final String unit;

    public PantryItem(
            long id,
            long ingredientId,
            String ingredientName,
            String measurementType,
            double quantity,
            String unit
    ) {
        this.id = id;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.measurementType = measurementType;
        this.quantity = quantity;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public long getIngredientId() {
        return ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}