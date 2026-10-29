package com.siphosethu.smartpantrymanager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.siphosethu.smartpantrymanager.database.RecipeRepository;
import com.siphosethu.smartpantrymanager.model.Recipe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeDetailActivity
        extends AppCompatActivity {

    private static final String TAG = "RecipeDetailActivity";

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private RecipeRepository recipeRepository;

    private TextView textRecipeName;
    private TextView textRecipeIngredients;
    private TextView textRecipeSteps;
    private TextView textRecipeStatus;

    private long recipeId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_detail);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.recipeDetailRoot),
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

        recipeRepository = new RecipeRepository(
                getApplicationContext()
        );

        textRecipeName = findViewById(R.id.textRecipeName);
        textRecipeIngredients = findViewById(
                R.id.textRecipeIngredients
        );
        textRecipeSteps = findViewById(R.id.textRecipeSteps);
        textRecipeStatus = findViewById(R.id.textRecipeStatus);

        Button backButton = findViewById(
                R.id.buttonBackToSuggestedRecipes
        );

        backButton.setOnClickListener(view -> finish());

        recipeId = getIntent().getLongExtra(
                SuggestedRecipesActivity.EXTRA_RECIPE_ID,
                -1
        );

        if (recipeId > 0) {
            loadRecipe();
        } else {
            showRecipeError();
        }
    }

    private void loadRecipe() {
        if (databaseExecutor.isShutdown()) {
            return;
        }

        textRecipeStatus.setText(R.string.recipe_loading);
        textRecipeStatus.setVisibility(View.VISIBLE);

        databaseExecutor.execute(() -> {
            try {
                Recipe recipe =
                        recipeRepository.getRecipeById(recipeId);

                String ingredientsText =
                        recipeRepository.getRecipeIngredientsText(
                                recipeId
                        );

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    if (recipe == null) {
                        showRecipeError();
                        return;
                    }

                    textRecipeName.setText(recipe.getName());
                    textRecipeIngredients.setText(ingredientsText);
                    textRecipeSteps.setText(
                            recipe.getPreparationSteps()
                    );

                    textRecipeStatus.setVisibility(View.GONE);
                });

            } catch (Exception exception) {
                Log.e(
                        TAG,
                        "Failed to load recipe detail",
                        exception
                );

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    showRecipeError();
                });
            }
        });
    }

    private void showRecipeError() {
        textRecipeName.setText(R.string.recipe_detail_title);
        textRecipeIngredients.setText("");
        textRecipeSteps.setText("");

        textRecipeStatus.setText(R.string.recipe_load_error);
        textRecipeStatus.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (recipeRepository != null) {
            databaseExecutor.execute(recipeRepository::close);
        }

        databaseExecutor.shutdown();

        super.onDestroy();
    }
}
