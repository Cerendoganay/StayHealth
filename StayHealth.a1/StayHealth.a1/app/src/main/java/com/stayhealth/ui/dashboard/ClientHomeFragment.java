package com.stayhealth.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.stayhealth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.stayhealth.repository.ProgressRepository;
import com.stayhealth.ui.home.FoodItem;
import com.stayhealth.ui.home.MealSelectionActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClientHomeFragment extends Fragment {

    private View addBreakfast, addLunch, addDinner, addSnack ;
    private TextView txtBreakfastSummary, txtLunchSummary, txtDinnerSummary, txtSnackSummary;
    private TextView txtDailyKcal;

    private TextView dayS0, dayM, dayT, dayW, dayT2, dayF, dayS;
    private final Calendar selectedDay = Calendar.getInstance();
    private static final int DAILY_TARGET_KCAL = 2000;

    private List<FoodItem> breakfastFoods = new ArrayList<>();
    private List<FoodItem> lunchFoods = new ArrayList<>();
    private List<FoodItem> dinnerFoods = new ArrayList<>();
    private List<FoodItem> snackFoods = new ArrayList<>();

    private ActivityResultLauncher<Intent> mealSelectionLauncher;
    private final ProgressRepository progressRepository = new ProgressRepository();
    private final SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Nullable

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_client_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupMealSelectionLauncher();
        setupClickListeners();
        setupDeleteListeners();
        updateWeekHeader();
        recalculateTotalKcal();
    }

    private void initViews(View root) {
        addBreakfast = root.findViewById(R.id.addBreakfast);
        addLunch = root.findViewById(R.id.addLunch);
        addDinner = root.findViewById(R.id.addDinner);
        addSnack = root.findViewById(R.id.addSnack);

        txtBreakfastSummary = root.findViewById(R.id.txtBreakfastSummary);
        txtLunchSummary = root.findViewById(R.id.txtLunchSummary);
        txtDinnerSummary = root.findViewById(R.id.txtDinnerSummary);
        txtSnackSummary = root.findViewById(R.id.txtSnackSummary);
        txtDailyKcal = root.findViewById(R.id.txtDailyKcal);

        dayS0 = root.findViewById(R.id.dayS0);
        dayM  = root.findViewById(R.id.dayM);
        dayT  = root.findViewById(R.id.dayT);
        dayW  = root.findViewById(R.id.dayW);
        dayT2 = root.findViewById(R.id.dayT2);
        dayF  = root.findViewById(R.id.dayF);
        dayS  = root.findViewById(R.id.dayS);



        Toast.makeText(getContext(),
                "Home loaded ✅ addBreakfast null? " + (addBreakfast == null),
                Toast.LENGTH_LONG).show();

    }

    private void setupMealSelectionLauncher() {
        mealSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != android.app.Activity.RESULT_OK || result.getData() == null) return;

                    Intent data = result.getData();
                    String mealType = data.getStringExtra("meal_type");
                    @SuppressWarnings("unchecked")
                    ArrayList<FoodItem> selectedItems =
                            (ArrayList<FoodItem>) data.getSerializableExtra("selected_food_items");
                    if (mealType == null || selectedItems == null) return;

                    updateMealSummary(mealType, selectedItems);
                    recalculateTotalKcal();
                }
        );
    }

    private void setupClickListeners() {
        if (addBreakfast != null) addBreakfast.setOnClickListener(v -> openMealSelection("Breakfast"));
        if (addLunch != null) addLunch.setOnClickListener(v -> openMealSelection("Lunch"));
        if (addDinner != null) addDinner.setOnClickListener(v -> openMealSelection("Dinner"));
        if (addSnack != null) addSnack.setOnClickListener(v -> openMealSelection("Snack"));
    }

    private void openMealSelection(String mealType) {
        if (getActivity() == null) return;
        Intent i = new Intent(getActivity(), MealSelectionActivity.class);
        i.putExtra("meal_type", mealType);
        mealSelectionLauncher.launch(i);
    }

    private void setupDeleteListeners() {
        if (txtBreakfastSummary != null) {
            txtBreakfastSummary.setOnLongClickListener(v -> { showDeleteDialog("Breakfast"); return true; });
        }
        if (txtLunchSummary != null) {
            txtLunchSummary.setOnLongClickListener(v -> { showDeleteDialog("Lunch"); return true; });
        }
        if (txtDinnerSummary != null) {
            txtDinnerSummary.setOnLongClickListener(v -> { showDeleteDialog("Dinner"); return true; });
        }
        if (txtSnackSummary != null) {
            txtSnackSummary.setOnLongClickListener(v -> { showDeleteDialog("Snack"); return true; });
        }
    }

    private void showDeleteDialog(String mealType) {
        List<FoodItem> foods = getMealList(mealType);
        if (foods == null || foods.isEmpty()) {
            Toast.makeText(getContext(), "Bu öğünde silinecek yiyecek yok.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = new String[foods.size()];
        for (int i = 0; i < foods.size(); i++) {
            FoodItem f = foods.get(i);
            int grams = (int) (f.getSelectedGrams() > 0 ? f.getSelectedGrams() : f.getGrams());
            int kcal = (int) f.getSelectedCalories();
            items[i] = f.getName() + " - " + grams + " g, " + kcal + " kcal";
        }

        new AlertDialog.Builder(requireContext())
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
            case "Breakfast": return breakfastFoods;
            case "Lunch": return lunchFoods;
            case "Dinner": return dinnerFoods;
            case "Snack": return snackFoods;
            default: return null;
        }
    }

    private void refreshMealSummary(String mealType) {
        switch (mealType) {
            case "Breakfast":
                if (txtBreakfastSummary != null) txtBreakfastSummary.setText(buildMealSummaryText(breakfastFoods));
                break;
            case "Lunch":
                if (txtLunchSummary != null) txtLunchSummary.setText(buildMealSummaryText(lunchFoods));
                break;
            case "Dinner":
                if (txtDinnerSummary != null) txtDinnerSummary.setText(buildMealSummaryText(dinnerFoods));
                break;
            case "Snack":
                if (txtSnackSummary != null) txtSnackSummary.setText(buildMealSummaryText(snackFoods));
                break;
        }
    }

    private void updateMealSummary(String mealType, List<FoodItem> items) {
        String summary = buildMealSummaryText(items);
        switch (mealType) {
            case "Breakfast":
                breakfastFoods = new ArrayList<>(items);
                if (txtBreakfastSummary != null) txtBreakfastSummary.setText(summary);
                break;
            case "Lunch":
                lunchFoods = new ArrayList<>(items);
                if (txtLunchSummary != null) txtLunchSummary.setText(summary);
                break;
            case "Dinner":
                dinnerFoods = new ArrayList<>(items);
                if (txtDinnerSummary != null) txtDinnerSummary.setText(summary);
                break;
            case "Snack":
                snackFoods = new ArrayList<>(items);
                if (txtSnackSummary != null) txtSnackSummary.setText(summary);
                break;
        }
    }

    private String buildMealSummaryText(List<FoodItem> items) {
        if (items == null || items.isEmpty()) return "";
        List<String> lines = new ArrayList<>();
        for (FoodItem item : items) {
            double grams = item.getSelectedGrams() > 0 ? item.getSelectedGrams() : item.getGrams();
            double kcal = item.getSelectedCalories();
            lines.add(item.getName() + " - " + (int) grams + " g, " + (int) kcal + " kcal");
        }
        return TextUtils.join("\n", lines);
    }

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
        persistDailyCalories(totalInt);
    }

    private void persistDailyCalories(int totalInt) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();
        String date = ymd.format(selectedDay.getTime());
        progressRepository.saveDailyCalories(uid, date, totalInt);
    }

    private double sumCalories(List<FoodItem> foods) {
        double sum = 0;
        if (foods == null) return 0;
        for (FoodItem item : foods) sum += item.getSelectedCalories();
        return sum;
    }

    private void updateWeekHeader() {
        Calendar week = (Calendar) selectedDay.clone();
        week.setFirstDayOfWeek(Calendar.SUNDAY);
        week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        TextView[] views = new TextView[]{dayS0, dayM, dayT, dayW, dayT2, dayF, dayS};
        SimpleDateFormat letterFmt = new SimpleDateFormat("EEEEE", Locale.ENGLISH);

        for (TextView tv : views) {
            if (tv == null) { week.add(Calendar.DAY_OF_YEAR, 1); continue; }

            tv.setText(letterFmt.format(week.getTime()));
            tv.setBackgroundResource(sameDay(week, selectedDay)
                    ? R.drawable.bg_day_selected
                    : R.drawable.bg_day_normal);

            final Calendar tapped = (Calendar) week.clone();
            tv.setOnClickListener(v -> {
                selectedDay.setTime(tapped.getTime());
                updateWeekHeader();
            });

            week.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private boolean sameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }
}
