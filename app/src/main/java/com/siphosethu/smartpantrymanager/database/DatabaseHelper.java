package com.siphosethu.smartpantrymanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_PANTRY_ITEMS = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(
                context.getApplicationContext(),
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        // Enforce relationships between the database tables.
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createIngredients =
                "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                        "id INTEGER PRIMARY KEY, " +
                        "canonical_name TEXT NOT NULL UNIQUE " +
                        "CHECK(length(trim(canonical_name)) > 0), " +
                        "measurement_type TEXT NOT NULL " +
                        "CHECK(measurement_type IN ('mass', 'volume', 'count'))" +
                        ")";

        String createPantryItems =
                "CREATE TABLE " + TABLE_PANTRY_ITEMS + " (" +
                        "id INTEGER PRIMARY KEY, " +
                        "ingredient_id INTEGER NOT NULL UNIQUE, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL " +
                        "CHECK(unit IN ('g', 'kg', 'ml', 'l', 'piece')), " +
                        "FOREIGN KEY(ingredient_id) REFERENCES " +
                        TABLE_INGREDIENTS + "(id)" +
                        ")";

        String createRecipes =
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        "id INTEGER PRIMARY KEY, " +
                        "name TEXT NOT NULL " +
                        "CHECK(length(trim(name)) > 0), " +
                        "preparation_steps TEXT NOT NULL " +
                        "CHECK(length(trim(preparation_steps)) > 0)" +
                        ")";

        String createRecipeIngredients =
                "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_id INTEGER NOT NULL, " +
                        "required_quantity REAL NOT NULL " +
                        "CHECK(required_quantity > 0), " +
                        "unit TEXT NOT NULL " +
                        "CHECK(unit IN ('g', 'kg', 'ml', 'l', 'piece')), " +
                        "PRIMARY KEY(recipe_id, ingredient_id), " +
                        "FOREIGN KEY(recipe_id) REFERENCES " +
                        TABLE_RECIPES + "(id) ON DELETE CASCADE, " +
                        "FOREIGN KEY(ingredient_id) REFERENCES " +
                        TABLE_INGREDIENTS + "(id)" +
                        ")";

        db.execSQL(createIngredients);
        db.execSQL(createPantryItems);
        db.execSQL(createRecipes);
        db.execSQL(createRecipeIngredients);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
      throw new IllegalStateException(
                "No database migration defined from version " +
                        oldVersion + " to " + newVersion
        );
    }
}