package com.stayhealth.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TodayDietPlanActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "today_diet_plan";
    private static final String PREF_BREAKFAST = "pref_breakfast";
    private static final String PREF_LUNCH = "pref_lunch";
    private static final String PREF_DINNER = "pref_dinner";

    private Calendar currentDay;
    private TextView txtDate;

    private ImageView imgBreakfastStatus, imgLunchStatus, imgDinnerStatus;

    // Seçili / seçili değil durumları
    private boolean breakfastSelected = true;   // ekran görüntüsünde kahvaltı yeşil
    private boolean lunchSelected = false;
    private boolean dinnerSelected = false;

    private SharedPreferences prefs;

    //tarihi değiştirdikçe lunch dinner vs. kısmı da değişecek
    private TextView txtBreakfastItems, txtBreakfastMacros;
    private TextView txtLunchItems, txtLunchMacros;
    private TextView txtDinnerItems, txtDinnerMacros;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_diet_plan);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        txtBreakfastItems = findViewById(R.id.txtBreakfastItems);
        txtBreakfastMacros = findViewById(R.id.txtBreakfastMacros);

        txtLunchItems = findViewById(R.id.txtLunchItems);
        txtLunchMacros = findViewById(R.id.txtLunchMacros);

        txtDinnerItems = findViewById(R.id.txtDinnerItems);
        txtDinnerMacros = findViewById(R.id.txtDinnerMacros);


        // Date
        txtDate = findViewById(R.id.txtDate);
        currentDay = Calendar.getInstance();
        updateDateText();
        updatePlanUIForCurrentDay();


        // Back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Prev / Next day  (XML’de < için &lt; , > için &gt; kullan!)
        TextView btnPrevDay = findViewById(R.id.btnPrevDay);
        TextView btnNextDay = findViewById(R.id.btnNextDay);

        btnPrevDay.setOnClickListener(v -> {
            currentDay.add(Calendar.DAY_OF_YEAR, -1);
            updateDateText();
            updatePlanUIForCurrentDay();

        });

        btnNextDay.setOnClickListener(v -> {
            currentDay.add(Calendar.DAY_OF_YEAR, 1);
            updateDateText();
            updatePlanUIForCurrentDay();

        });

        // Status icons (select / deselect)
        imgBreakfastStatus = findViewById(R.id.imgBreakfastStatus);
        imgLunchStatus = findViewById(R.id.imgLunchStatus);
        imgDinnerStatus = findViewById(R.id.imgDinnerStatus);

        loadSelectionFromPrefs();
        ensureAtLeastOneSelected();
        updateStatusUI();

        imgBreakfastStatus.setOnClickListener(v -> {
            breakfastSelected = !breakfastSelected;
            ensureAtLeastOneSelected();
            updateStatusUI();
            saveSelectionToPrefs();
        });

        imgLunchStatus.setOnClickListener(v -> {
            lunchSelected = !lunchSelected;
            ensureAtLeastOneSelected();
            updateStatusUI();
            saveSelectionToPrefs();
        });

        imgDinnerStatus.setOnClickListener(v -> {
            dinnerSelected = !dinnerSelected;
            ensureAtLeastOneSelected();
            updateStatusUI();
            saveSelectionToPrefs();
        });

        // Bottom nav
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class))
        );

        findViewById(R.id.navChart).setOnClickListener(v ->
                startActivity(new Intent(this, ProgressChartActivity.class))
        );

        findViewById(R.id.navProfile).setOnClickListener(v -> {
            // ProfileActivity varsa aç:
            // startActivity(new Intent(this, ProfileActivity.class));
        });
    }


    private void updateDateText() {
        // İstersen Locale("tr") yapabiliriz ama tasarım İngilizce görünüyor
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        SimpleDateFormat dayFmt  = new SimpleDateFormat("EEEE", Locale.ENGLISH);

        String date = dateFmt.format(currentDay.getTime());
        String day  = dayFmt.format(currentDay.getTime());

        if (txtDate != null) {
            txtDate.setText(date + "      " + day);
        }
    }

    private void updateStatusUI() {
        setStatus(imgBreakfastStatus, breakfastSelected);
        setStatus(imgLunchStatus, lunchSelected);
        setStatus(imgDinnerStatus, dinnerSelected);
    }

    private void setStatus(ImageView img, boolean selected) {
        if (img == null) return;

        if (selected) {
            img.setBackgroundResource(R.drawable.bg_circle_green);
            img.setImageResource(R.drawable.ic_check_white);
            int p = dp(4);
            img.setPadding(p, p, p, p);
        } else {
            img.setBackgroundResource(R.drawable.bg_circle_grey);
            img.setImageDrawable(null);
            img.setPadding(0, 0, 0, 0);
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }


    private void updatePlanUIForCurrentDay() {
        // Şimdilik “boş” plan: istersen placeholder yazı koy, istersen tamamen boş bırak.
        if (txtBreakfastItems != null) txtBreakfastItems.setText("");
        if (txtBreakfastMacros != null) txtBreakfastMacros.setText("");

        if (txtLunchItems != null) txtLunchItems.setText("");
        if (txtLunchMacros != null) txtLunchMacros.setText("");

        if (txtDinnerItems != null) txtDinnerItems.setText("");
        if (txtDinnerMacros != null) txtDinnerMacros.setText("");

        // Refresh selection state but keep at least one meal selected.
        ensureAtLeastOneSelected();
        updateStatusUI();
    }

    private void ensureAtLeastOneSelected() {
        if (!breakfastSelected && !lunchSelected && !dinnerSelected) {
            // Default fallback is breakfast if user deselects everything.
            breakfastSelected = true;
        }
    }

    private void loadSelectionFromPrefs() {
        breakfastSelected = prefs.getBoolean(PREF_BREAKFAST, breakfastSelected);
        lunchSelected = prefs.getBoolean(PREF_LUNCH, lunchSelected);
        dinnerSelected = prefs.getBoolean(PREF_DINNER, dinnerSelected);
    }

    private void saveSelectionToPrefs() {
        prefs.edit()
                .putBoolean(PREF_BREAKFAST, breakfastSelected)
                .putBoolean(PREF_LUNCH, lunchSelected)
                .putBoolean(PREF_DINNER, dinnerSelected)
                .apply();
    }

}
