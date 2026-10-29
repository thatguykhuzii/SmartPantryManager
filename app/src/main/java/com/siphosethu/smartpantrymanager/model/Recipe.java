package com.siphosethu.smartpantrymanager.model;

public class Recipe {

    private final long id;
    private final String name;
    private final String preparationSteps;

    public Recipe(
            long id,
            String name,
            String preparationSteps
    ) {
        this.id = id;
        this.name = name;
        this.preparationSteps = preparationSteps;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPreparationSteps() {
        return preparationSteps;
    }
}