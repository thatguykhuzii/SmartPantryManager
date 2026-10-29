package com.siphosethu.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;

import com.siphosethu.smartpantrymanager.adapter.PantryAdapter;
import com.siphosethu.smartpantrymanager.database.PantryRepository;
import com.siphosethu.smartpantrymanager.model.PantryItem;
import com.siphosethu.smartpantrymanager.database.RecipeRepository;
import com.siphosethu.smartpantrymanager.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private PantryRepository pantryRepository;
    private RecipeRepository recipeRepository;
    private PantryAdapter pantryAdapter;

    private ListView listPantry;
    private TextView textEmptyPantry;
    private TextView textPantryStatus;
    private View buttonAddIngredient;

    private boolean loading;
    private boolean loadFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
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

        listPantry = findViewById(R.id.listPantry);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);
        textPantryStatus = findViewById(R.id.textPantryStatus);

        pantryRepository = new PantryRepository(
                getApplicationContext()
        );
        recipeRepository = new RecipeRepository(
                getApplicationContext()
        );

        pantryAdapter = new PantryAdapter(this);
        listPantry.setAdapter(pantryAdapter);
        listPantry.setOnItemClickListener(
                (parent, view, position, id) -> {
                    PantryItem selectedItem =
                            pantryAdapter.getItem(position);

                    openEditIngredient(selectedItem.getId());
                }
        );

        listPantry.setOnItemLongClickListener(
                (parent, view, position, id) -> {
                    PantryItem selectedItem =
                            pantryAdapter.getItem(position);

                    confirmDeletePantryItem(selectedItem);

                    return true;
                }
        );
        buttonAddIngredient = findViewById(R.id.buttonAddIngredient);

        buttonAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });
        Button suggestedRecipesButton = new Button(this);
        suggestedRecipesButton.setText("Suggested recipes");
        suggestedRecipesButton.setMinHeight(48);

        suggestedRecipesButton.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });
        Button settingsButton = new Button(this);
        settingsButton.setText("Settings");
        settingsButton.setMinHeight(48);

        settingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });

        ((android.view.ViewGroup) findViewById(R.id.main))
                .addView(settingsButton, 3);
        ((android.view.ViewGroup) findViewById(R.id.main))
                .addView(suggestedRecipesButton);

        buttonAddIngredient.setEnabled(true);

        textPantryStatus.setOnClickListener(view -> {
            if (loadFailed && !loading) {
                loadPantry();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {

        if (loading || databaseExecutor.isShutdown()) {
            return;
        }

        loading = true;
        loadFailed = false;

        textPantryStatus.setText(R.string.pantry_loading);
        textPantryStatus.setClickable(false);

        listPantry.setVisibility(View.GONE);
        textEmptyPantry.setVisibility(View.GONE);

        databaseExecutor.execute(() -> {
            try {
                recipeRepository.ensureRecipesAreSeeded();
                // Use ascending order until Settings is implemented.
                boolean sortAscending =
                        SettingsActivity.readSortAscending(this);

                List<PantryItem> items =
                        pantryRepository.getAllPantryItems(sortAscending);

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loading = false;

                    pantryAdapter.replaceItems(items);

                    textPantryStatus.setText(
                            getString(
                                    R.string.pantry_item_count,
                                    items.size()
                            )
                    );

                    if (items.isEmpty()) {
                        listPantry.setVisibility(View.GONE);
                        textEmptyPantry.setVisibility(View.VISIBLE);
                    } else {
                        textEmptyPantry.setVisibility(View.GONE);
                        listPantry.setVisibility(View.VISIBLE);
                    }
                });

            } catch (Exception exception) {
                Log.e(
                        TAG,
                        "Failed to load pantry items",
                        exception
                );

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loading = false;
                    loadFailed = true;

                    listPantry.setVisibility(View.GONE);
                    textEmptyPantry.setVisibility(View.GONE);

                    textPantryStatus.setText(
                            R.string.pantry_load_error
                    );

                    textPantryStatus.setClickable(true);
                });
            }
        });
    }
    private void openEditIngredient(long pantryItemId) {
        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );

        intent.putExtra(
                AddEditIngredientActivity.EXTRA_PANTRY_ITEM_ID,
                pantryItemId
        );

        startActivity(intent);
    }

    private void confirmDeletePantryItem(PantryItem item) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_ingredient)
                .setMessage(R.string.delete_ingredient_confirmation)
                .setPositiveButton(
                        android.R.string.ok,
                        (dialog, which) ->
                                deletePantryItem(item)
                )
                .setNegativeButton(
                        android.R.string.cancel,
                        null
                )
                .show();
    }

    private void deletePantryItem(PantryItem item) {
        if (loading || databaseExecutor.isShutdown()) {
            return;
        }

        loading = true;

        textPantryStatus.setText(R.string.pantry_loading);
        textPantryStatus.setClickable(false);

        databaseExecutor.execute(() -> {
            try {
                boolean deleted =
                        pantryRepository.deletePantryItem(
                                item.getId()
                        );

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loading = false;

                    if (deleted) {
                        textPantryStatus.setText(
                                R.string.delete_ingredient_success
                        );

                        loadPantry();
                    } else {
                        textPantryStatus.setText(
                                R.string.delete_ingredient_error
                        );

                        textPantryStatus.setClickable(true);
                    }
                });

            } catch (Exception exception) {
                Log.e(
                        TAG,
                        "Failed to delete pantry item",
                        exception
                );

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loading = false;

                    textPantryStatus.setText(
                            R.string.delete_ingredient_error
                    );

                    textPantryStatus.setClickable(true);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (pantryRepository != null) {
            // Queue cleanup after previously submitted database work.
            databaseExecutor.execute(pantryRepository::close);
        }
        if (recipeRepository != null) {
            databaseExecutor.execute(recipeRepository::close);
        }
        databaseExecutor.shutdown();

        super.onDestroy();
    }
}