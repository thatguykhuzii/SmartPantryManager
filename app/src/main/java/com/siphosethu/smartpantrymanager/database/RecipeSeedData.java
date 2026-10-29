package com.siphosethu.smartpantrymanager.database;

import java.util.ArrayList;
import java.util.List;

final class RecipeSeedData {

    static final class SeedIngredient {
        final String name;
        final String measurementType;

        SeedIngredient(String name, String measurementType) {
            this.name = name;
            this.measurementType = measurementType;
        }
    }

    static final class SeedRecipe {
        final String name;
        final String steps;
        final String[] ingredientNames;
        final double[] quantities;
        final String[] units;

        SeedRecipe(
                String name,
                String steps,
                String[] ingredientNames,
                double[] quantities,
                String[] units
        ) {
            this.name = name;
            this.steps = steps;
            this.ingredientNames = ingredientNames;
            this.quantities = quantities;
            this.units = units;
        }
    }

    private RecipeSeedData() {
    }

    static List<SeedIngredient> getIngredients() {
        List<SeedIngredient> ingredients = new ArrayList<>();

        String[][] data = {
                {"rice", "mass"},
                {"pasta", "mass"},
                {"bread", "count"},
                {"egg", "count"},
                {"milk", "volume"},
                {"butter", "mass"},
                {"cheese", "mass"},
                {"chicken", "mass"},
                {"beef mince", "mass"},
                {"tomato", "count"},
                {"onion", "count"},
                {"garlic", "count"},
                {"potato", "count"},
                {"carrot", "count"},
                {"spinach", "mass"},
                {"beans", "mass"},
                {"salt", "mass"},
                {"black pepper", "mass"},
                {"cooking oil", "volume"},
                {"flour", "mass"}
        };

        for (String[] row : data) {
            ingredients.add(
                    new SeedIngredient(row[0], row[1])
            );
        }

        return ingredients;
    }

    static List<SeedRecipe> getRecipes() {
        List<SeedRecipe> recipes = new ArrayList<>();

        recipes.add(new SeedRecipe(
                "Scrambled Eggs",
                "1. Beat the eggs.\n2. Heat butter in a pan.\n3. Cook the eggs while stirring.\n4. Season with salt and black pepper.",
                new String[]{"egg", "butter", "salt", "black pepper"},
                new double[]{3, 20, 1, 1},
                new String[]{"piece", "g", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Cheese Toast",
                "1. Toast the bread.\n2. Add butter and cheese.\n3. Grill until the cheese melts.",
                new String[]{"bread", "butter", "cheese"},
                new double[]{2, 15, 50},
                new String[]{"piece", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Tomato Pasta",
                "1. Cook the pasta.\n2. Fry onion and garlic in oil.\n3. Add chopped tomatoes.\n4. Mix with pasta and season.",
                new String[]{"pasta", "tomato", "onion", "garlic", "cooking oil", "salt"},
                new double[]{200, 3, 1, 2, 30, 2},
                new String[]{"g", "piece", "piece", "piece", "ml", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Chicken and Rice",
                "1. Cook rice.\n2. Fry chicken with onion and garlic.\n3. Combine with rice and season.",
                new String[]{"rice", "chicken", "onion", "garlic", "cooking oil", "salt"},
                new double[]{200, 300, 1, 2, 30, 2},
                new String[]{"g", "g", "piece", "piece", "ml", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Beef Mince Pasta",
                "1. Cook pasta.\n2. Brown beef mince with onion and garlic.\n3. Add tomatoes and simmer.\n4. Mix with pasta.",
                new String[]{"pasta", "beef mince", "tomato", "onion", "garlic", "cooking oil"},
                new double[]{200, 300, 3, 1, 2, 30},
                new String[]{"g", "g", "piece", "piece", "piece", "ml"}
        ));

        recipes.add(new SeedRecipe(
                "Vegetable Soup",
                "1. Chop carrot, potato and onion.\n2. Boil with water and salt.\n3. Add spinach near the end.",
                new String[]{"carrot", "potato", "onion", "spinach", "salt"},
                new double[]{2, 3, 1, 100, 2},
                new String[]{"piece", "piece", "piece", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Garlic Butter Rice",
                "1. Cook rice.\n2. Melt butter with garlic.\n3. Mix through the rice and season.",
                new String[]{"rice", "butter", "garlic", "salt"},
                new double[]{200, 30, 2, 2},
                new String[]{"g", "g", "piece", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Bean and Spinach Stew",
                "1. Fry onion and garlic.\n2. Add beans and tomatoes.\n3. Simmer, then add spinach.",
                new String[]{"beans", "spinach", "onion", "garlic", "tomato", "cooking oil"},
                new double[]{400, 100, 1, 2, 2, 30},
                new String[]{"g", "g", "piece", "piece", "piece", "ml"}
        ));

        recipes.add(new SeedRecipe(
                "Cheese Omelette",
                "1. Beat eggs.\n2. Cook in butter.\n3. Add cheese and fold.",
                new String[]{"egg", "cheese", "butter", "salt"},
                new double[]{3, 40, 15, 1},
                new String[]{"piece", "g", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Potato and Carrot Fry",
                "1. Chop potato and carrot.\n2. Fry in oil with onion.\n3. Season and cook until soft.",
                new String[]{"potato", "carrot", "onion", "cooking oil", "salt", "black pepper"},
                new double[]{3, 2, 1, 45, 2, 1},
                new String[]{"piece", "piece", "piece", "ml", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Tomato Cheese Toast",
                "1. Toast bread.\n2. Add butter, tomato and cheese.\n3. Grill until melted.",
                new String[]{"bread", "butter", "tomato", "cheese"},
                new double[]{2, 15, 1, 40},
                new String[]{"piece", "g", "piece", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Chicken Garlic Pasta",
                "1. Cook pasta.\n2. Fry chicken with garlic and oil.\n3. Mix with pasta and season.",
                new String[]{"pasta", "chicken", "garlic", "cooking oil", "salt", "black pepper"},
                new double[]{200, 250, 3, 30, 2, 1},
                new String[]{"g", "g", "piece", "ml", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Mince and Bean Stew",
                "1. Brown mince with onion and garlic.\n2. Add beans and tomatoes.\n3. Simmer and season.",
                new String[]{"beef mince", "beans", "onion", "garlic", "tomato", "cooking oil"},
                new double[]{300, 400, 1, 2, 2, 30},
                new String[]{"g", "g", "piece", "piece", "piece", "ml"}
        ));

        recipes.add(new SeedRecipe(
                "Simple Fried Rice",
                "1. Cook rice and allow it to cool.\n2. Fry egg, onion and garlic.\n3. Add rice and season.",
                new String[]{"rice", "egg", "onion", "garlic", "cooking oil", "salt"},
                new double[]{250, 2, 1, 2, 30, 2},
                new String[]{"g", "piece", "piece", "piece", "ml", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Spinach and Cheese Pasta",
                "1. Cook pasta.\n2. Wilt spinach with garlic and butter.\n3. Add cheese and mix with pasta.",
                new String[]{"pasta", "spinach", "cheese", "garlic", "butter", "salt"},
                new double[]{200, 100, 50, 2, 20, 2},
                new String[]{"g", "g", "g", "piece", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Chicken and Vegetable Tray",
                "1. Chop potato, carrot and onion.\n2. Add chicken, oil and seasoning.\n3. Bake until cooked.",
                new String[]{"chicken", "potato", "carrot", "onion", "cooking oil", "salt", "black pepper"},
                new double[]{400, 3, 2, 1, 45, 2, 1},
                new String[]{"g", "piece", "piece", "piece", "ml", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Garlic Tomato Beans",
                "1. Fry garlic and onion.\n2. Add beans and tomatoes.\n3. Simmer and season.",
                new String[]{"beans", "tomato", "garlic", "onion", "cooking oil", "salt"},
                new double[]{400, 2, 2, 1, 30, 2},
                new String[]{"g", "piece", "piece", "piece", "ml", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Buttered Pasta",
                "1. Cook pasta.\n2. Melt butter with garlic.\n3. Toss pasta through the butter and season.",
                new String[]{"pasta", "butter", "garlic", "salt", "black pepper"},
                new double[]{200, 30, 2, 2, 1},
                new String[]{"g", "g", "piece", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Cheese and Tomato Omelette",
                "1. Beat eggs.\n2. Cook in butter.\n3. Add tomato and cheese, then fold.",
                new String[]{"egg", "tomato", "cheese", "butter", "salt"},
                new double[]{3, 1, 40, 15, 1},
                new String[]{"piece", "piece", "g", "g", "g"}
        ));

        recipes.add(new SeedRecipe(
                "Rice and Beans",
                "1. Cook rice.\n2. Heat beans with onion and garlic.\n3. Serve beans over rice.",
                new String[]{"rice", "beans", "onion", "garlic", "cooking oil", "salt"},
                new double[]{200, 400, 1, 2, 30, 2},
                new String[]{"g", "g", "piece", "piece", "ml", "g"}
        ));

        return recipes;
    }
}