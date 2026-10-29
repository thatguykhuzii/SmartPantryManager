package com.siphosethu.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.siphosethu.smartpantrymanager.database.PantryRepository;
import com.siphosethu.smartpantrymanager.model.PantryItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditIngredientActivity
        extends AppCompatActivity {

    private static final String TAG =
            "AddEditIngredientActivity";

    public static final String EXTRA_PANTRY_ITEM_ID =
            "com.siphosethu.smartpantrymanager.EXTRA_PANTRY_ITEM_ID";

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private PantryRepository pantryRepository;

    private EditText editIngredientName;
    private EditText editIngredientQuantity;
    private Spinner spinnerIngredientUnit;
    private TextView textIngredientFormStatus;
    private Button buttonSaveIngredient;

    private long pantryItemId = -1;
    private boolean saving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.ingredientFormRoot),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        pantryRepository = new PantryRepository(
                getApplicationContext()
        );

        TextView title = findViewById(
                R.id.textIngredientFormTitle
        );

        editIngredientName = findViewById(
                R.id.editIngredientName
        );

        editIngredientQuantity = findViewById(
                R.id.editIngredientQuantity
        );

        spinnerIngredientUnit = findViewById(
                R.id.spinnerIngredientUnit
        );

        textIngredientFormStatus = findViewById(
                R.id.textIngredientFormStatus
        );

        buttonSaveIngredient = findViewById(
                R.id.buttonSaveIngredient
        );

        Button cancelButton = findViewById(
                R.id.buttonCancelIngredient
        );

        ArrayAdapter<CharSequence> unitAdapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.pantry_units,
                        android.R.layout.simple_spinner_item
                );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerIngredientUnit.setAdapter(unitAdapter);

        pantryItemId = getIntent().getLongExtra(
                EXTRA_PANTRY_ITEM_ID,
                -1
        );

        if (pantryItemId > 0) {
            title.setText(
                    R.string.ingredient_form_edit_title
            );

            loadPantryItem();
        } else {
            pantryItemId = -1;
        }

        buttonSaveIngredient.setOnClickListener(
                view -> saveIngredient()
        );

        cancelButton.setOnClickListener(
                view -> finish()
        );
    }

    private void loadPantryItem() {
        if (databaseExecutor.isShutdown()) {
            return;
        }

        setSaving(true);

        textIngredientFormStatus.setText(R.string.pantry_loading);
        textIngredientFormStatus.setVisibility(View.VISIBLE);

        databaseExecutor.execute(() -> {
            try {
                PantryItem item = pantryRepository
                        .getAllPantryItems(true)
                        .stream()
                        .filter(existing ->
                                existing.getId() == pantryItemId)
                        .findFirst()
                        .orElse(null);

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    setSaving(false);

                    if (item == null) {
                        showStatus(
                                "This pantry item no longer exists."
                        );

                        buttonSaveIngredient.setEnabled(false);
                        return;
                    }

                    editIngredientName.setText(
                            item.getIngredientName()
                    );

                    editIngredientQuantity.setText(
                            String.valueOf(item.getQuantity())
                    );

                    setUnitSelection(item.getUnit());
                    textIngredientFormStatus.setVisibility(View.GONE);
                });

            } catch (Exception exception) {
                Log.e(
                        TAG,
                        "Failed to load pantry item",
                        exception
                );

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    setSaving(false);

                    showStatus(
                            "Could not load this pantry item. " +
                                    "Please try again."
                    );
                });
            }
        });
    }

    private void setUnitSelection(String unit) {
        for (int position = 0;
             position < spinnerIngredientUnit.getCount();
             position++) {

            String availableUnit = String.valueOf(
                    spinnerIngredientUnit.getItemAtPosition(position)
            );

            if (availableUnit.equals(unit)) {
                spinnerIngredientUnit.setSelection(position);
                return;
            }
        }
    }

    private void saveIngredient() {
        if (saving || databaseExecutor.isShutdown()) {
            return;
        }

        String name = editIngredientName.getText()
                .toString()
                .trim();

        String quantityText = editIngredientQuantity.getText()
                .toString()
                .trim();

        String unit = String.valueOf(
                spinnerIngredientUnit.getSelectedItem()
        );

        if (name.isEmpty()) {
            showStatus("Enter an ingredient name.");
            return;
        }

        Double quantity = parseQuantity(quantityText);

        if (quantity == null) {
            showStatus(
                    "Enter a quantity greater than zero."
            );

            return;
        }

        setSaving(true);

        textIngredientFormStatus.setText(R.string.pantry_loading);
        textIngredientFormStatus.setVisibility(View.VISIBLE);

        databaseExecutor.execute(() -> {
            try {
                if (pantryItemId > 0) {
                    pantryRepository.updatePantryItem(
                            pantryItemId,
                            name,
                            quantity,
                            unit
                    );
                } else {
                    pantryRepository.addPantryItem(
                            name,
                            quantity,
                            unit
                    );
                }

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    setResult(RESULT_OK);
                    finish();
                });

            } catch (Exception exception) {
                Log.e(
                        TAG,
                        "Failed to save pantry item",
                        exception
                );

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    setSaving(false);

                    String message = exception.getMessage();

                    showStatus(
                            message != null && !message.isBlank()
                                    ? message
                                    : "Could not save this ingredient."
                    );
                });
            }
        });
    }

    private Double parseQuantity(String input) {
        if (input.isEmpty()) {
            return null;
        }

        try {
            double value = Double.parseDouble(input);

            if (Double.isNaN(value)
                    || Double.isInfinite(value)
                    || value <= 0) {
                return null;
            }

            return value;

        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void showStatus(String message) {
        textIngredientFormStatus.setText(message);
        textIngredientFormStatus.setVisibility(View.VISIBLE);
    }

    private void setSaving(boolean isSaving) {
        saving = isSaving;

        buttonSaveIngredient.setEnabled(!isSaving);
    }

    @Override
    protected void onDestroy() {
        if (pantryRepository != null) {
            databaseExecutor.execute(pantryRepository::close);
        }

        databaseExecutor.shutdown();

        super.onDestroy();
    }
}