package com.stayhealth.ui.consultant;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.stayhealth.R;

public class ConsultantDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultant_detail);
    }

    private String getTodayKey() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

}
