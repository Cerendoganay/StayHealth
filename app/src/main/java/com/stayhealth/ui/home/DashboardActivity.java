package com.stayhealth.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;
import com.stayhealth.ui.consultant.ConsultantListActivity;

public class DashboardActivity extends AppCompatActivity {

    // Meal kartlarının içindeki “+ Add Meal” textleri de var
    private View addBreakfast, addLunch, addDinner, addSnack, addExercise;

    // Bottom ikonlar (senin XML’deki id’lere göre)
    private ImageView navChart, navChecklist, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Meal add buton/text id’leri (XML’de verdiğim id’ler)
        addBreakfast = findViewById(R.id.addBreakfast);
        addLunch     = findViewById(R.id.addLunch);
        addDinner    = findViewById(R.id.addDinner);
        addSnack     = findViewById(R.id.addSnack);
        addExercise  = findViewById(R.id.addExercise);

        // Bottom bar ikonları (custom bottom bar kullanıyorsan)
        navChart     = findViewById(R.id.navChart);
        navChecklist = findViewById(R.id.navChecklist);
        navProfile   = findViewById(R.id.navProfile);

        // Meal sayfasına geçiş (hangi meal tıklandı bilgisini de yolluyoruz)
        addBreakfast.setOnClickListener(v -> openMealSelection("Breakfast"));
        addLunch.setOnClickListener(v -> openMealSelection("Lunch"));
        addDinner.setOnClickListener(v -> openMealSelection("Dinner"));
        addSnack.setOnClickListener(v -> openMealSelection("Snack"));

        // Exercise şimdilik aynı sayfaya/başka sayfaya yönlendirilebilir
        addExercise.setOnClickListener(v ->
                openMealSelection("Exercise") // istersen ExerciseActivity yaparız
        );

        // Bottom ikonlar
        navChart.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ProgressChartActivity.class))
        );

        navChecklist.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, TodayDietPlanActivity.class))
        );

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, ConsultantListActivity.class))
        );
    }

    private void openMealSelection(String mealType) {
        Intent intent = new Intent(DashboardActivity.this, MealSelectionActivity.class);
        intent.putExtra("meal_type", mealType);
        startActivity(intent);
    }
}
