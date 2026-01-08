package com.stayhealth.ui.home;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;
import com.stayhealth.ui.home.DashboardActivity;
import com.stayhealth.ui.profile.ProfileActivity;

public class ProgressChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_chart);

        ImageView navChecklist = findViewById(R.id.navChecklist);
        ImageView navChart = findViewById(R.id.navChart);
        ImageView navProfile = findViewById(R.id.navProfile);

        if (navChecklist != null) {
            navChecklist.setOnClickListener(v -> startActivity(new android.content.Intent(this, DashboardActivity.class)));
        }
        if (navChart != null) {
            navChart.setOnClickListener(v -> { /* already here */ });
            navChart.setColorFilter(0xFF111111);
        }
        if (navProfile != null) {
            navProfile.setOnClickListener(v -> startActivity(new android.content.Intent(this, ProfileActivity.class)));
        }
    }
}
