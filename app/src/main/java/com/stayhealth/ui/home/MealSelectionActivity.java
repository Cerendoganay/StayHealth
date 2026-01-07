package com.stayhealth.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhealth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MealSelectionActivity extends AppCompatActivity {

    private RecyclerView rvMeals;
    private EditText edtSearch;
    private TextView btnClose, txtMealTitle, txtDate, txtNextPage;

    private String currentMealType = "Breakfast";

    // JSON’dan gelen tüm yiyecekler
    private final List<FoodItem> allFoods = new ArrayList<>();
    // MealType’a göre filtrelenmiş liste
    private final List<FoodItem> mealFilteredFoods = new ArrayList<>();
    // Ekranda gösterilen liste (search sonrası)
    private final List<FoodItem> displayFoods = new ArrayList<>();

    private FoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_selection);

        String mealExtra = getIntent().getStringExtra("meal_type");
        if (mealExtra != null && !mealExtra.trim().isEmpty()) {
            currentMealType = mealExtra;
        }

        initViews();
        setupTopBar();
        setupRecycler();
        setupSearch();
        loadDateText();
        loadFoodsFromJson();

        // Yeni geri davranışı (gesture + tuş)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void initViews() {
        rvMeals      = findViewById(R.id.rvMeals);
        edtSearch    = findViewById(R.id.edtSearch);
        btnClose     = findViewById(R.id.btnClose);      // üstteki ✕
        txtMealTitle = findViewById(R.id.txtMealTitle);
        txtDate      = findViewById(R.id.txtDate);
        txtNextPage  = findViewById(R.id.txtNextPage);
    }

    private void setupTopBar() {
        txtMealTitle.setText(currentMealType + " ▾");

        // ✕ butonu
        btnClose.setOnClickListener(v -> {
            // BURASI MUTLAKA ÇALIŞMALI – ilk denemede Toast’a bak
            Toast.makeText(this, "Close clicked", Toast.LENGTH_SHORT).show();

            setResult(RESULT_CANCELED);
            finish();
        });

        // Next Page → seçili yiyecekleri geri döndür
        txtNextPage.setOnClickListener(v -> {
            if (adapter == null) return;

            List<FoodItem> selectedItems = adapter.getSelectedItems();
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Lütfen en az bir yiyecek seç.", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> selectedNames = new ArrayList<>();
            for (FoodItem item : selectedItems) {
                selectedNames.add(item.getName());
            }

            Intent data = new Intent();
            data.putExtra("meal_type", currentMealType);
            data.putExtra("selected_food_items", new ArrayList<>(selectedItems)); // Serializable
            data.putStringArrayListExtra("selected_food_names", selectedNames);

            setResult(RESULT_OK, data);
            finish();
        });
    }

    private void setupRecycler() {
        rvMeals.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FoodAdapter(displayFoods);
        rvMeals.setAdapter(adapter);
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applySearchFilter(s.toString().trim());
            }

            @Override public void afterTextChanged(Editable s) { }
        });
    }

    private void loadDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM", Locale.ENGLISH);
        String dateStr = sdf.format(new Date());
        txtDate.setText(dateStr);
    }

    private void loadFoodsFromJson() {
        try {
            String jsonStr = readJsonFromAssets("food_data.json");
            if (jsonStr == null) {
                Toast.makeText(this, "food_data.json okunamadı (assets klasörünü kontrol et)", Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject root = new JSONObject(jsonStr);

            if (!root.has("foods")) {
                Toast.makeText(this, "JSON içinde 'foods' alanı yok", Toast.LENGTH_LONG).show();
                return;
            }

            JSONArray foodsArray = root.getJSONArray("foods");

            allFoods.clear();

            for (int i = 0; i < foodsArray.length(); i++) {
                JSONObject obj = foodsArray.getJSONObject(i);

                String id        = obj.optString("id", "food_" + i);
                String name      = obj.optString("name", "Unknown");
                String category  = obj.optString("category", "other");
                String portion   = obj.optString("portion", "1 serving");
                double grams     = obj.optDouble("grams", 0);

                double calories  = obj.optDouble("kcal", obj.optDouble("calories", 0));
                double protein   = obj.optDouble("protein_g", obj.optDouble("protein", 0));
                double carbs     = obj.optDouble("carb_g", obj.optDouble("carbs", 0));
                double fat       = obj.optDouble("fat_g", obj.optDouble("fat", 0));

                List<String> mealTypes = new ArrayList<>();
                JSONArray mealTypesJson = obj.optJSONArray("mealTypes");
                if (mealTypesJson != null) {
                    for (int j = 0; j < mealTypesJson.length(); j++) {
                        String mt = mealTypesJson.optString(j);
                        if (mt != null && !mt.trim().isEmpty()) {
                            mealTypes.add(mt.trim());
                        }
                    }
                }

                FoodItem item = new FoodItem(
                        id,
                        name,
                        category,
                        portion,
                        grams,
                        calories,
                        protein,
                        carbs,
                        fat,
                        mealTypes
                );
                allFoods.add(item);
            }

            if (allFoods.isEmpty()) {
                Toast.makeText(this, "JSON yüklendi ama hiç yiyecek yok.", Toast.LENGTH_LONG).show();
            }

            applyMealTypeFilter();
            applySearchFilter(edtSearch.getText().toString().trim());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "JSON parse hatası: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String readJsonFromAssets(String fileName) throws IOException {
        InputStream is = getAssets().open(fileName);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
        );
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        is.close();
        return sb.toString();
    }

    private void applyMealTypeFilter() {
        mealFilteredFoods.clear();

        String mealLower = currentMealType.toLowerCase(Locale.ROOT);

        for (FoodItem item : allFoods) {
            List<String> mts = item.getMealTypes();

            if (mts == null || mts.isEmpty()) {
                mealFilteredFoods.add(item);
                continue;
            }

            boolean match = false;

            for (String mt : mts) {
                if (mt == null) continue;
                String mtLower = mt.toLowerCase(Locale.ROOT);

                if (mtLower.equals("any")) {
                    match = true;
                    break;
                }

                if (mtLower.equals(mealLower)) {
                    match = true;
                    break;
                }
            }

            if (match) {
                mealFilteredFoods.add(item);
            }
        }

        if (mealFilteredFoods.isEmpty()) {
            mealFilteredFoods.addAll(allFoods);
        }
    }

    private void applySearchFilter(String query) {
        String q = query.toLowerCase(Locale.ROOT);

        displayFoods.clear();

        if (q.isEmpty()) {
            displayFoods.addAll(mealFilteredFoods);
        } else {
            for (FoodItem item : mealFilteredFoods) {
                if (item.getName().toLowerCase(Locale.ROOT).contains(q)) {
                    displayFoods.add(item);
                }
            }
        }

        if (adapter != null) {
            adapter.updateList(displayFoods);
        }
    }
}
