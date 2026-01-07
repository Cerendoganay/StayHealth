package com.stayhealth.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProgressChartActivity extends AppCompatActivity {

    private Spinner spinnerRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        // BACK
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // SPINNER
        spinnerRange = findViewById(R.id.spinnerRange);

        ArrayList<String> weekItems = buildLast4WeeksLabels();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.item_spinner_week_selected,   // seçili görünüm
                        weekItems
                );

        adapter.setDropDownViewResource(R.layout.item_spinner_week_dropdown);
        spinnerRange.setAdapter(adapter);

        // Varsayılan: This Week
        loadWeekData(0);

        spinnerRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadWeekData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Spinner için:
     * This Week
     * Last Week
     * 2 Weeks Ago
     * 3 Weeks Ago
     */
    private ArrayList<String> buildLast4WeeksLabels() {
        ArrayList<String> items = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("MMM d", Locale.ENGLISH);

        for (int i = 0; i < 4; i++) {
            Calendar start = startOfWeek();
            start.add(Calendar.WEEK_OF_YEAR, -i);

            Calendar end = (Calendar) start.clone();
            end.add(Calendar.DAY_OF_YEAR, 6);

            String title;
            if (i == 0) title = "This Week";
            else if (i == 1) title = "Last Week";
            else title = i + " Weeks Ago";

            items.add(title + " (" +
                    fmt.format(start.getTime()) + " – " +
                    fmt.format(end.getTime()) + ")");
        }
        return items;
    }

    private Calendar startOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Seçilen haftaya göre chart verisini yükle
     * 0 = this week
     * 1 = last week
     * 2 = 2 weeks ago
     * 3 = 3 weeks ago
     */
    private void loadWeekData(int weekIndex) {

        // ŞİMDİLİK DUMMY DATA
        int[][] last4WeeksCalories = {
                {700, 1600, 1200, 2000, 900, 1400, 0},
                {900, 1400, 1300, 1700, 800, 1200, 600},
                {600, 1100, 900, 1500, 700, 900, 500},
                {800, 900, 1000, 1200, 650, 700, 400}
        };

        int[] selectedWeek = last4WeeksCalories[weekIndex];

        // TODO:
        // updateBars(selectedWeek);
        // updateSummary(selectedWeek);
    }
}