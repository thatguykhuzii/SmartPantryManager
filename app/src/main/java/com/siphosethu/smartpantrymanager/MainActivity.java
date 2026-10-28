package com.siphosethu.smartpantrymanager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.siphosethu.smartpantrymanager.adapter.PantryAdapter;
import com.siphosethu.smartpantrymanager.database.PantryRepository;
import com.siphosethu.smartpantrymanager.model.PantryItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    private PantryRepository pantryRepository;
    private PantryAdapter pantryAdapter;

    private ListView listPantry;
    private TextView textEmptyPantry;
    private TextView textPantryStatus;

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

        pantryAdapter = new PantryAdapter(this);
        listPantry.setAdapter(pantryAdapter);

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
                // Use ascending order until Settings is implemented.
                List<PantryItem> items =
                        pantryRepository.getAllPantryItems(true);

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

    @Override
    protected void onDestroy() {
        if (pantryRepository != null) {
            // Queue cleanup after previously submitted database work.
            databaseExecutor.execute(pantryRepository::close);
        }

        databaseExecutor.shutdown();

        super.onDestroy();
    }
}