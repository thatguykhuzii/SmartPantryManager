package com.siphosethu.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.siphosethu.smartpantrymanager.model.Recipe;
import com.siphosethu.smartpantrymanager.util.UnitConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRepository implements AutoCloseable {

    private final DatabaseHelper databaseHelper;

    public RecipeRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void ensureRecipesAreSeeded() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            if (countRecipes(db) > 0) {
                db.setTransactionSuccessful();
                return;
            }

            Map<String, Long> ingredientIds =
                    seedIngredients(db);

            seedRecipes(db, ingredientIds);

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    private int countRecipes(SQLiteDatabase db) {
        String sql = "SELECT COUNT(*) FROM " +
                DatabaseHelper.TABLE_RECIPES;

        try (Cursor cursor = db.rawQuery(sql, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    private Map<String, Long> seedIngredients(
            SQLiteDatabase db
    ) {
        Map<String, Long> ingredientIds = new HashMap<>();

        for (RecipeSeedData.SeedIngredient seed
                : RecipeSeedData.getIngredients()) {

            ContentValues values = new ContentValues();
            values.put("canonical_name", seed.name);
            values.put("measurement_type", seed.measurementType);

            long id = db.insertWithOnConflict(
                    DatabaseHelper.TABLE_INGREDIENTS,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_IGNORE
            );

            if (id == -1) {
                id = findIngredientId(db, seed.name);
            }

            ingredientIds.put(seed.name, id);
        }

        return ingredientIds;
    }

    private long findIngredientId(
            SQLiteDatabase db,
            String canonicalName
    ) {
        try (Cursor cursor = db.rawQuery(
                "SELECT id FROM " +
                        DatabaseHelper.TABLE_INGREDIENTS +
                        " WHERE canonical_name = ?",
                new String[]{canonicalName}
        )) {
            if (!cursor.moveToFirst()) {
                throw new IllegalStateException(
                        "Seed ingredient was not found: " +
                                canonicalName
                );
            }

            return cursor.getLong(0);
        }
    }

    private void seedRecipes(
            SQLiteDatabase db,
            Map<String, Long> ingredientIds
    ) {
        for (RecipeSeedData.SeedRecipe recipe
                : RecipeSeedData.getRecipes()) {

            ContentValues recipeValues = new ContentValues();
            recipeValues.put("name", recipe.name);
            recipeValues.put(
                    "preparation_steps",
                    recipe.steps
            );

            long recipeId = db.insert(
                    DatabaseHelper.TABLE_RECIPES,
                    null,
                    recipeValues
            );

            for (int index = 0;
                 index < recipe.ingredientNames.length;
                 index++) {

                String ingredientName =
                        recipe.ingredientNames[index];

                Long ingredientId =
                        ingredientIds.get(ingredientName);

                if (ingredientId == null) {
                    throw new IllegalStateException(
                            "Missing seed ingredient: " +
                                    ingredientName
                    );
                }

                ContentValues requirement =
                        new ContentValues();

                requirement.put("recipe_id", recipeId);
                requirement.put("ingredient_id", ingredientId);
                requirement.put(
                        "required_quantity",
                        recipe.quantities[index]
                );
                requirement.put("unit", recipe.units[index]);

                db.insertOrThrow(
                        DatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                        null,
                        requirement
                );
            }
        }
    }
    public List<Recipe> getQualifyingRecipes() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        List<Recipe> qualifyingRecipes = new ArrayList<>();

        Map<Long, Double> availableBaseQuantities =
                loadPantryBaseQuantities(db);

        String recipeSql =
                "SELECT id, name, preparation_steps " +
                        "FROM " + DatabaseHelper.TABLE_RECIPES + " " +
                        "ORDER BY name COLLATE NOCASE ASC";

        try (Cursor recipeCursor = db.rawQuery(recipeSql, null)) {
            int recipeIdColumn =
                    recipeCursor.getColumnIndexOrThrow("id");

            int recipeNameColumn =
                    recipeCursor.getColumnIndexOrThrow("name");

            int recipeStepsColumn =
                    recipeCursor.getColumnIndexOrThrow(
                            "preparation_steps"
                    );

            while (recipeCursor.moveToNext()) {
                long recipeId =
                        recipeCursor.getLong(recipeIdColumn);

                if (recipeMatchesPantry(
                        db,
                        recipeId,
                        availableBaseQuantities
                )) {
                    qualifyingRecipes.add(new Recipe(
                            recipeId,
                            recipeCursor.getString(recipeNameColumn),
                            recipeCursor.getString(recipeStepsColumn)
                    ));
                }
            }
        }

        return qualifyingRecipes;
    }

    private Map<Long, Double> loadPantryBaseQuantities(
            SQLiteDatabase db
    ) {
        Map<Long, Double> pantry = new HashMap<>();

        String sql =
                "SELECT ingredient_id, quantity, unit " +
                        "FROM " + DatabaseHelper.TABLE_PANTRY_ITEMS;

        try (Cursor cursor = db.rawQuery(sql, null)) {
            int ingredientIdColumn =
                    cursor.getColumnIndexOrThrow("ingredient_id");

            int quantityColumn =
                    cursor.getColumnIndexOrThrow("quantity");

            int unitColumn =
                    cursor.getColumnIndexOrThrow("unit");

            while (cursor.moveToNext()) {
                long ingredientId =
                        cursor.getLong(ingredientIdColumn);

                double baseQuantity = UnitConverter.toBaseQuantity(
                        cursor.getDouble(quantityColumn),
                        cursor.getString(unitColumn)
                );

                pantry.put(ingredientId, baseQuantity);
            }
        }

        return pantry;
    }

    private boolean recipeMatchesPantry(
            SQLiteDatabase db,
            long recipeId,
            Map<Long, Double> availableBaseQuantities
    ) {
        String sql =
                "SELECT ingredient_id, required_quantity, unit " +
                        "FROM " + DatabaseHelper.TABLE_RECIPE_INGREDIENTS +
                        " WHERE recipe_id = ?";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{String.valueOf(recipeId)}
        )) {
            if (!cursor.moveToFirst()) {
                // A recipe with no requirements cannot qualify.
                return false;
            }

            int ingredientIdColumn =
                    cursor.getColumnIndexOrThrow("ingredient_id");

            int requiredQuantityColumn =
                    cursor.getColumnIndexOrThrow("required_quantity");

            int unitColumn =
                    cursor.getColumnIndexOrThrow("unit");

            do {
                long ingredientId =
                        cursor.getLong(ingredientIdColumn);

                if (!availableBaseQuantities.containsKey(ingredientId)) {
                    return false;
                }

                double requiredQuantity =
                        UnitConverter.toBaseQuantity(
                                cursor.getDouble(requiredQuantityColumn),
                                cursor.getString(unitColumn)
                        );

                double availableQuantity =
                        availableBaseQuantities.get(ingredientId);

                if (availableQuantity < requiredQuantity) {
                    return false;
                }

            } while (cursor.moveToNext());

            return true;
        }
    }

    @Override
    public void close() {
        databaseHelper.close();
    }
}