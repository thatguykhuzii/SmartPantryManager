package com.siphosethu.smartpantrymanager.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.siphosethu.smartpantrymanager.model.PantryItem;
import android.content.ContentValues;

import com.siphosethu.smartpantrymanager.util.IngredientNormalizer;
import com.siphosethu.smartpantrymanager.util.UnitConverter;

import java.util.ArrayList;
import java.util.List;

public class PantryRepository implements AutoCloseable {

    private final DatabaseHelper databaseHelper;

    public PantryRepository(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public List<PantryItem> getAllPantryItems(
            boolean sortAscending
    ) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        List<PantryItem> items = new ArrayList<>();

        String sortDirection = sortAscending ? "ASC" : "DESC";

        String sql =
                "SELECT " +
                        "p.id AS pantry_id, " +
                        "p.ingredient_id, " +
                        "i.canonical_name, " +
                        "i.measurement_type, " +
                        "p.quantity, " +
                        "p.unit " +
                        "FROM " + DatabaseHelper.TABLE_PANTRY_ITEMS + " p " +
                        "INNER JOIN " +
                        DatabaseHelper.TABLE_INGREDIENTS + " i " +
                        "ON p.ingredient_id = i.id " +
                        "ORDER BY i.canonical_name COLLATE NOCASE " +
                        sortDirection;

        try (Cursor cursor = db.rawQuery(sql, null)) {

            int idColumn =
                    cursor.getColumnIndexOrThrow("pantry_id");

            int ingredientIdColumn =
                    cursor.getColumnIndexOrThrow("ingredient_id");

            int nameColumn =
                    cursor.getColumnIndexOrThrow("canonical_name");

            int measurementTypeColumn =
                    cursor.getColumnIndexOrThrow("measurement_type");

            int quantityColumn =
                    cursor.getColumnIndexOrThrow("quantity");

            int unitColumn =
                    cursor.getColumnIndexOrThrow("unit");

            while (cursor.moveToNext()) {
                PantryItem item = new PantryItem(
                        cursor.getLong(idColumn),
                        cursor.getLong(ingredientIdColumn),
                        cursor.getString(nameColumn),
                        cursor.getString(measurementTypeColumn),
                        cursor.getDouble(quantityColumn),
                        cursor.getString(unitColumn)
                );

                items.add(item);
            }
        }

        return items;
    }

    public boolean deletePantryItem(long pantryItemId) {
        if (pantryItemId <= 0) {
            throw new IllegalArgumentException(
                    "Pantry item ID must be greater than zero."
            );
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        int deletedRows = db.delete(
                DatabaseHelper.TABLE_PANTRY_ITEMS,
                "id = ?",
                new String[]{String.valueOf(pantryItemId)}
        );

        return deletedRows == 1;
    }
    public long addPantryItem(
            String ingredientName,
            double quantity,
            String inputUnit
    ) {
        return savePantryItem(
                null,
                ingredientName,
                quantity,
                inputUnit
        );
    }

    public boolean updatePantryItem(
            long pantryItemId,
            String ingredientName,
            double quantity,
            String inputUnit
    ) {
        if (pantryItemId <= 0) {
            throw new IllegalArgumentException(
                    "Pantry item ID must be greater than zero."
            );
        }

        savePantryItem(
                pantryItemId,
                ingredientName,
                quantity,
                inputUnit
        );

        return true;
    }

    private long savePantryItem(
            Long pantryItemId,
            String ingredientName,
            double quantity,
            String inputUnit
    ) {
        String canonicalName =
                IngredientNormalizer.normalize(ingredientName);

        String unit = UnitConverter.normalizeUnit(inputUnit);

        // Validate quantity and check conversion does not overflow.
        UnitConverter.toBaseQuantity(quantity, unit);

        String measurementType =
                UnitConverter.getMeasurementType(unit);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            if (pantryItemId != null) {
                requirePantryItemExists(db, pantryItemId);
            }

            long ingredientId = findOrCreateIngredient(
                    db,
                    canonicalName,
                    measurementType
            );

            requireNoDuplicatePantryItem(
                    db,
                    ingredientId,
                    pantryItemId
            );

            ContentValues values = new ContentValues();
            values.put("ingredient_id", ingredientId);
            values.put("quantity", quantity);
            values.put("unit", unit);

            long savedId;

            if (pantryItemId == null) {
                savedId = db.insertOrThrow(
                        DatabaseHelper.TABLE_PANTRY_ITEMS,
                        null,
                        values
                );
            } else {
                int updatedRows = db.update(
                        DatabaseHelper.TABLE_PANTRY_ITEMS,
                        values,
                        "id = ?",
                        new String[]{String.valueOf(pantryItemId)}
                );

                if (updatedRows != 1) {
                    throw new IllegalStateException(
                            "The pantry item could not be updated."
                    );
                }

                savedId = pantryItemId;
            }

            db.setTransactionSuccessful();

            return savedId;

        } finally {
            db.endTransaction();
        }
    }

    private void requirePantryItemExists(
            SQLiteDatabase db,
            long pantryItemId
    ) {
        String sql =
                "SELECT id FROM " +
                        DatabaseHelper.TABLE_PANTRY_ITEMS +
                        " WHERE id = ?";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{String.valueOf(pantryItemId)}
        )) {
            if (!cursor.moveToFirst()) {
                throw new IllegalArgumentException(
                        "This pantry item no longer exists."
                );
            }
        }
    }

    private long findOrCreateIngredient(
            SQLiteDatabase db,
            String canonicalName,
            String measurementType
    ) {
        String sql =
                "SELECT id, measurement_type FROM " +
                        DatabaseHelper.TABLE_INGREDIENTS +
                        " WHERE canonical_name = ?";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{canonicalName}
        )) {
            if (cursor.moveToFirst()) {
                long ingredientId = cursor.getLong(0);
                String storedMeasurementType = cursor.getString(1);

                if (!storedMeasurementType.equals(measurementType)) {
                    throw new IllegalArgumentException(
                            "This ingredient uses " +
                                    storedMeasurementType +
                                    " units. Choose a compatible unit."
                    );
                }

                return ingredientId;
            }
        }

        ContentValues ingredientValues = new ContentValues();
        ingredientValues.put("canonical_name", canonicalName);
        ingredientValues.put("measurement_type", measurementType);

        return db.insertOrThrow(
                DatabaseHelper.TABLE_INGREDIENTS,
                null,
                ingredientValues
        );
    }

    private void requireNoDuplicatePantryItem(
            SQLiteDatabase db,
            long ingredientId,
            Long currentPantryItemId
    ) {
        String sql =
                "SELECT id FROM " +
                        DatabaseHelper.TABLE_PANTRY_ITEMS +
                        " WHERE ingredient_id = ?";

        try (Cursor cursor = db.rawQuery(
                sql,
                new String[]{String.valueOf(ingredientId)}
        )) {
            if (cursor.moveToFirst()) {
                long existingPantryItemId = cursor.getLong(0);

                if (currentPantryItemId == null
                        || existingPantryItemId
                        != currentPantryItemId.longValue()) {
                    throw new IllegalArgumentException(
                            "This ingredient is already in your pantry. " +
                                    "Edit its existing quantity instead."
                    );
                }
            }
        }
    }

    @Override
    public void close() {
        databaseHelper.close();
    }
}