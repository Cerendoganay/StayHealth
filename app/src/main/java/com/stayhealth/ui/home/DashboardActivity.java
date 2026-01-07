package com.stayhealth.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.stayhealth.ui.auth.RoleSelectActivity;
import com.stayhealth.util.SessionManager;
import com.stayhealth.ui.auth.LoginActivity;


import com.stayhealth.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private View addBreakfast, addLunch, addDinner, addSnack, addExercise;

    private TextView txtBreakfastSummary, txtLunchSummary, txtDinnerSummary, txtSnackSummary;
    private TextView txtDailyKcal;

    private static final int DAILY_TARGET_KCAL = 2000;

    // Her öğün için seçilen yiyecekler
    private List<FoodItem> breakfastFoods = new ArrayList<>();
    private List<FoodItem> lunchFoods     = new ArrayList<>();
    private List<FoodItem> dinnerFoods    = new ArrayList<>();
    private List<FoodItem> snackFoods     = new ArrayList<>();

    private ActivityResultLauncher<Intent> mealSelectionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {

            // Firebase oturumunu kapat
            FirebaseAuth.getInstance().signOut();

            // Session bilgilerini temizle
            SessionManager.clear(this);

            // Selection sayfasına yönlendir
            Intent intent = new Intent(this, RoleSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        initViews();
        setupMealSelectionLauncher();
        setupClickListeners();
        setupDeleteListeners();
        recalculateTotalKcal();
    }

    private void initViews() {
        addBreakfast = findViewById(R.id.addBreakfast);
        addLunch     = findViewById(R.id.addLunch);
        addDinner    = findViewById(R.id.addDinner);
        addSnack     = findViewById(R.id.addSnack);
        addExercise  = findViewById(R.id.addExercise); // varsa

        txtBreakfastSummary = findViewById(R.id.txtBreakfastSummary);
        txtLunchSummary     = findViewById(R.id.txtLunchSummary);
        txtDinnerSummary    = findViewById(R.id.txtDinnerSummary);
        txtSnackSummary     = findViewById(R.id.txtSnackSummary);

        txtDailyKcal        = findViewById(R.id.txtDailyKcal);
    }

    /**
     * MealSelectionActivity’den dönen yeni seçimleri yakalıyoruz.
     */
    private void setupMealSelectionLauncher() {
        mealSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) return;

                    Intent data = result.getData();

                    String mealType = data.getStringExtra("meal_type");
                    @SuppressWarnings("unchecked")
                    ArrayList<FoodItem> selectedItems =
                            (ArrayList<FoodItem>) data.getSerializableExtra("selected_food_items");

                    if (mealType == null || selectedItems == null || selectedItems.isEmpty()) {
                        return;
                    }

                    updateMealSummary(mealType, selectedItems);
                    recalculateTotalKcal();
                }
        );
    }

    /**
     * Add Meal tıklamaları → MealSelectionActivity aç.
     */
    private void setupClickListeners() {
        addBreakfast.setOnClickListener(v -> openMealSelection("Breakfast"));
        addLunch.setOnClickListener(v -> openMealSelection("Lunch"));
        addDinner.setOnClickListener(v -> openMealSelection("Dinner"));
        addSnack.setOnClickListener(v -> openMealSelection("Snack"));
        if (addExercise != null) {
            addExercise.setOnClickListener(v -> {
                // şimdilik boş
            });
        }
    }

    private void openMealSelection(String mealType) {
        Intent i = new Intent(this, MealSelectionActivity.class);
        i.putExtra("meal_type", mealType);
        mealSelectionLauncher.launch(i);
    }

    /**
     * Özet alanına uzun basarak eklenen yiyeceği silme.
     */
    private void setupDeleteListeners() {
        txtBreakfastSummary.setOnLongClickListener(v -> {
            showDeleteDialog("Breakfast");
            return true;
        });

        txtLunchSummary.setOnLongClickListener(v -> {
            showDeleteDialog("Lunch");
            return true;
        });

        txtDinnerSummary.setOnLongClickListener(v -> {
            showDeleteDialog("Dinner");
            return true;
        });

        txtSnackSummary.setOnLongClickListener(v -> {
            showDeleteDialog("Snack");
            return true;
        });
    }

    /**
     * İlgili öğünün listesini getir, bir dialog aç,
     * seçilen item’ı listeden sil.
     */
    private void showDeleteDialog(String mealType) {
        List<FoodItem> foods = getMealList(mealType);
        if (foods == null || foods.isEmpty()) {
            Toast.makeText(this, "Bu öğünde silinecek yiyecek yok.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = new String[foods.size()];
        for (int i = 0; i < foods.size(); i++) {
            FoodItem f = foods.get(i);
            int grams = (int) (f.getSelectedGrams() > 0 ? f.getSelectedGrams() : f.getGrams());
            int kcal  = (int) f.getSelectedCalories();
            items[i] = f.getName() + " - " + grams + " g, " + kcal + " kcal";
        }

        new AlertDialog.Builder(this)
                .setTitle("Remove from " + mealType)
                .setItems(items, (dialog, which) -> {
                    foods.remove(which);
                    refreshMealSummary(mealType);
                    recalculateTotalKcal();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private List<FoodItem> getMealList(String mealType) {
        switch (mealType) {
            case "Breakfast":
                return breakfastFoods;
            case "Lunch":
                return lunchFoods;
            case "Dinner":
                return dinnerFoods;
            case "Snack":
                return snackFoods;
            default:
                return null;
        }
    }

    private void refreshMealSummary(String mealType) {
        String summary;
        switch (mealType) {
            case "Breakfast":
                summary = buildMealSummaryText(breakfastFoods);
                txtBreakfastSummary.setText(summary);
                break;
            case "Lunch":
                summary = buildMealSummaryText(lunchFoods);
                txtLunchSummary.setText(summary);
                break;
            case "Dinner":
                summary = buildMealSummaryText(dinnerFoods);
                txtDinnerSummary.setText(summary);
                break;
            case "Snack":
                summary = buildMealSummaryText(snackFoods);
                txtSnackSummary.setText(summary);
                break;
        }
    }

    /**
     * MealSelection’dan gelen yeni listeyi kaydet + ekranda göster.
     */
    private void updateMealSummary(String mealType, List<FoodItem> items) {
        String summary = buildMealSummaryText(items);

        switch (mealType) {
            case "Breakfast":
                breakfastFoods = new ArrayList<>(items);
                txtBreakfastSummary.setText(summary);
                break;
            case "Lunch":
                lunchFoods = new ArrayList<>(items);
                txtLunchSummary.setText(summary);
                break;
            case "Dinner":
                dinnerFoods = new ArrayList<>(items);
                txtDinnerSummary.setText(summary);
                break;
            case "Snack":
                snackFoods = new ArrayList<>(items);
                txtSnackSummary.setText(summary);
                break;
        }
    }

    /**
     * "Green Tea - 250 g, 3 kcal\nBoiled Egg - 50 g, 78 kcal" gibi metin.
     */
    private String buildMealSummaryText(List<FoodItem> items) {
        if (items == null || items.isEmpty()) return "";
        List<String> lines = new ArrayList<>();
        for (FoodItem item : items) {
            double grams = item.getSelectedGrams() > 0 ? item.getSelectedGrams() : item.getGrams();
            double kcal  = item.getSelectedCalories();
            lines.add(item.getName() + " - " + (int) grams + " g, " + (int) kcal + " kcal");
        }
        return TextUtils.join("\n", lines);
    }

    /**
     * Tüm öğünlerdeki seçili yiyeceklerin toplam kalorisi.
     */
    private void recalculateTotalKcal() {
        double total = 0;
        total += sumCalories(breakfastFoods);
        total += sumCalories(lunchFoods);
        total += sumCalories(dinnerFoods);
        total += sumCalories(snackFoods);

        int totalInt = (int) total;
        if (txtDailyKcal != null) {
            txtDailyKcal.setText(totalInt + " / " + DAILY_TARGET_KCAL + " kcal");
        }
    }

    private double sumCalories(List<FoodItem> foods) {
        double sum = 0;
        if (foods == null) return 0;
        for (FoodItem item : foods) {
            sum += item.getSelectedCalories();
        }
        return sum;
    }


}
