package com.siphosethu.smartpantrymanager.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.siphosethu.smartpantrymanager.model.PantryItem;

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

    @Override
    public void close() {
        databaseHelper.close();
    }
}