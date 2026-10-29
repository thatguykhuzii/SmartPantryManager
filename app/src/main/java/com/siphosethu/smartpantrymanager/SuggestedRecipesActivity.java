package com.siphosethu.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.siphosethu.smartpantrymanager.adapter.RecipeAdapter;
import com.siphosethu.smartpantrymanager.database.RecipeRepository;
import com.siphosethu.smartpantrymanager.model.Recipe;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuggestedRecipesActivity
        extends AppCompatActivity {

    private static final String TAG =
            "SuggestedRecipesActivity";

    public static final String EXTRA_RECIPE_ID =
            "com.siphosethu.smartpantrymanager.EXTRA_RECIPE_ID";

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private RecipeRepository recipeRepository;
    private RecipeAdapter recipeAdapter;

    private ListView listSuggestedRecipes;
    private TextView textNoSuggestedRecipes;
    private TextView textSuggestedRecipesStatus;

    private boolean loading;
    private boolean loadFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_suggested_recipes);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.suggestedRecipesRoot),
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

        recipeAdapter = new RecipeAdapter(this);

        listSuggestedRecipes = findViewById(
                R.id.listSuggestedRecipes
        );

        textNoSuggestedRecipes = findViewById(
                R.id.textNoSuggestedRecipes
        );

        textSuggestedRecipesStatus = findViewById(
                R.id.textSuggestedRecipesStatus
        );

        listSuggestedRecipes.setAdapter(recipeAdapter);

        listSuggestedRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Recipe selectedRecipe =
                            recipeAdapter.getItem(position);

                    openRecipeDetail(selectedRecipe.getId());
                }
        );

        textSuggestedRecipesStatus.setOnClickListener(view -> {
            if (loadFailed && !loading) {
                loadSuggestedRecipes();
            }
        });

        Button backButton = findViewById(
                R.id.buttonBackToPantry
        );

        backButton.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        if (loading || databaseExecutor.isShutdown()) {
            return;
        }

        loading = true;
        loadFailed = false;

        textSuggestedRecipesStatus.setText(
                R.string.suggested_recipes_loading
        );

        textSuggestedRecipesStatus.setClickable(false);

        listSuggestedRecipes.setVisibility(View.GONE);
        textNoSuggestedRecipes.setVisibility(View.GONE);

        databaseExecutor.execute(() -> {
            try {
                List<Recipe> recipes =
                        recipeRepository.getQualifyingRecipes();

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loading = false;

                    recipeAdapter.replaceRecipes(recipes);

                    if (recipes.isEmpty()) {
                        listSuggestedRecipes.setVisibility(View.GONE);
                        textNoSuggestedRecipes.setVisibility(
                                View.VISIBLE
                        );

                        textSuggestedRecipesStatus.setText("");
                    } else {
                        textNoSuggestedRecipes.setVisibility(View.GONE);
                        listSuggestedRecipes.setVisibility(View.VISIBLE);

                        textSuggestedRecipesStatus.setText(
                                "Matching recipes: " + recipes.size()
                        );
                    }
                });

            } catch (Exception exception) {
                Log.e(
                        TAG,
                        "Failed to load suggested recipes",
                        exception
                );

                runOnUiThread(() -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    loading = false;
                    loadFailed = true;

                    listSuggestedRecipes.setVisibility(View.GONE);
                    textNoSuggestedRecipes.setVisibility(View.GONE);

                    textSuggestedRecipesStatus.setText(
                            R.string.suggested_recipes_error
                    );

                    textSuggestedRecipesStatus.setClickable(true);
                });
            }
        });
    }

    private void openRecipeDetail(long recipeId) {
        Intent intent = new Intent(
                this,
                RecipeDetailActivity.class
        );

        intent.putExtra(EXTRA_RECIPE_ID, recipeId);

        startActivity(intent);
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