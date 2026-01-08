package com.stayhealth.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;
import com.stayhealth.ui.profile.ProfileActivity;

/**
 * Dashboard shell for dietician role. Separated from model layer and
 * inflates its own layout before accessing views.
 */
public class DieticianDashboardActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView navChart, navChecklist, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_host);

        btnBack = findViewById(R.id.btnBack);
        navChart = findViewById(R.id.navChart);
        navChecklist = findViewById(R.id.navChecklist);
        navProfile = findViewById(R.id.navProfile);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (navChart != null) {
            navChart.setOnClickListener(v ->
                    Toast.makeText(this, "Dietician: Chart (yakında)", Toast.LENGTH_SHORT).show());
        }

        if (navChecklist != null) {
            navChecklist.setOnClickListener(v ->
                    Toast.makeText(this, "Dietician: Checklist (yakında)", Toast.LENGTH_SHORT).show());
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v ->
                    startActivity(new Intent(this, ProfileActivity.class)));
        }
    }
}
