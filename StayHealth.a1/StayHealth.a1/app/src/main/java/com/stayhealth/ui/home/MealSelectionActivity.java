package com.stayhealth.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import com.stayhealth.R;
import com.stayhealth.viewmodel.MealSelectionViewModel;

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

    private FoodAdapter adapter;
    private MealSelectionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_selection);

        String mealExtra = getIntent().getStringExtra("meal_type");
        if (mealExtra != null && !mealExtra.trim().isEmpty()) {
            currentMealType = mealExtra;
        }

        viewModel = new ViewModelProvider(this).get(MealSelectionViewModel.class);

        initViews();
        setupTopBar();
        setupRecycler();
        loadDateText();
        setupSearch();
        observeViewModel();
        viewModel.loadFoods(getAssets(), currentMealType, edtSearch.getText().toString().trim());

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
        adapter = new FoodAdapter(new ArrayList<>());
        rvMeals.setAdapter(adapter);
    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new SimpleTextWatcher(text ->
                viewModel.applySearch(text.trim())
        ));
    }

    private void loadDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM", Locale.ENGLISH);
        String dateStr = sdf.format(new Date());
        txtDate.setText(dateStr);
    }


    private void observeViewModel() {
        viewModel.getState().observe(this, mealState -> {
            if (mealState.loading) {
                // No loader in original UI; keep silent
            }

            if (mealState.error != null) {
                Toast.makeText(this, mealState.error, Toast.LENGTH_LONG).show();
                return;
            }

            if (adapter != null) {
                adapter.updateList(mealState.items);
            }
        });
    }

    // Lightweight TextWatcher wrapper
    private static class SimpleTextWatcher implements android.text.TextWatcher {
        private final java.util.function.Consumer<String> onChange;

        SimpleTextWatcher(java.util.function.Consumer<String> onChange) {
            this.onChange = onChange;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            onChange.accept(s == null ? "" : s.toString());
        }

        @Override public void afterTextChanged(android.text.Editable s) { }
    }
}