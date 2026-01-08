package com.stayhealth.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.stayhealth.R;
import com.stayhealth.ui.dashboard.ClientHomeFragment;
import com.stayhealth.ui.profile.ProfileActivity;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private ImageView navChecklist, navChart, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "onCreate() called");
        
        try {
            setContentView(R.layout.activity_dashboard_host);
            Log.d(TAG, "Layout inflated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout", e);
            Toast.makeText(this, "Layout error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        navChecklist = findViewById(R.id.navChecklist);
        navChart = findViewById(R.id.navChart);
        navProfile = findViewById(R.id.navProfile);

        // güvenlik: id'lerden biri yoksa crash olmasın
        if (navChecklist == null || navChart == null || navProfile == null) {
            Log.e(TAG, "Navigation views not found! navChecklist=" + navChecklist + 
                       ", navChart=" + navChart + ", navProfile=" + navProfile);
            Toast.makeText(this, "Navigation layout hatası - görünümler bulunamadı", Toast.LENGTH_LONG).show();
            // activity_dashboard_host.xml içinde bu id'ler yoksa burada patlardı
            return;
        }
        
        Log.d(TAG, "Navigation views found successfully");

        if (savedInstanceState == null) {
            Log.d(TAG, "First launch - loading ClientHomeFragment");
            openFragment(new ClientHomeFragment());
            highlight("home");
        }

        navChecklist.setOnClickListener(v -> {
            Log.d(TAG, "navChecklist clicked");
            openFragment(new ClientHomeFragment());
            highlight("home");
        });

        navChart.setOnClickListener(v -> {
            Log.d(TAG, "navChart clicked");
            startActivity(new android.content.Intent(this, com.stayhealth.ui.home.ProgressChartActivity.class));
        });

        navProfile.setOnClickListener(v -> {
            Log.d(TAG, "navProfile clicked");
            startActivity(new android.content.Intent(this, com.stayhealth.ui.profile.ProfileActivity.class));
        });
        
        Log.d(TAG, "onCreate() completed successfully");
    }

    private void openFragment(Fragment f) {
        try {
            Log.d(TAG, "Opening fragment: " + f.getClass().getSimpleName());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, f)
                    .commit();
            Log.d(TAG, "Fragment transaction committed");
        } catch (Exception e) {
            Log.e(TAG, "Error opening fragment", e);
            Toast.makeText(this, "Fragment error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void highlight(String which) {
        int selected = 0xFF111111;
        int unselected = 0xFF888888;

        navChecklist.setColorFilter("home".equals(which) ? selected : unselected);
        navChart.setColorFilter("progress".equals(which) ? selected : unselected);
        navProfile.setColorFilter("profile".equals(which) ? selected : unselected);
        
        Log.d(TAG, "Highlighted: " + which);
    }
}
