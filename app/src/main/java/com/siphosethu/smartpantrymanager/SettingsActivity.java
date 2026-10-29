package com.siphosethu.smartpantrymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFERENCES_FILE =
            "smart_pantry_settings";

    public static final String KEY_SORT_ASCENDING =
            "sort_ascending";

    private RadioButton radioSortAscending;
    private RadioButton radioSortDescending;
    private TextView textSettingsStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.settingsRoot),
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

        radioSortAscending = findViewById(
                R.id.radioSortAscending
        );

        radioSortDescending = findViewById(
                R.id.radioSortDescending
        );

        textSettingsStatus = findViewById(
                R.id.textSettingsStatus
        );

        RadioGroup sortGroup = findViewById(
                R.id.radioGroupSortOrder
        );

        Button backButton = findViewById(
                R.id.buttonBackToPantryFromSettings
        );

        boolean sortAscending = readSortAscending(this);

        if (sortAscending) {
            radioSortAscending.setChecked(true);
        } else {
            radioSortDescending.setChecked(true);
        }

        sortGroup.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    boolean newSortAscending =
                            checkedId == R.id.radioSortAscending;

                    saveSortAscending(newSortAscending);

                    textSettingsStatus.setText(
                            R.string.settings_saved
                    );

                    textSettingsStatus.setVisibility(View.VISIBLE);
                }
        );

        backButton.setOnClickListener(view -> finish());
    }

    public static boolean readSortAscending(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_FILE,
                Context.MODE_PRIVATE
        );

        return preferences.getBoolean(KEY_SORT_ASCENDING, true);
    }

    private void saveSortAscending(boolean sortAscending) {
        SharedPreferences preferences = getSharedPreferences(
                PREFERENCES_FILE,
                Context.MODE_PRIVATE
        );

        preferences
                .edit()
                .putBoolean(KEY_SORT_ASCENDING, sortAscending)
                .apply();
    }
}