package com.stayhealth.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;

public class RoleSelectActivity extends AppCompatActivity {

    private LinearLayout cardClient;
    private LinearLayout cardDietitian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);

        // XML'deki kartları bul
        cardClient    = findViewById(R.id.cardClient);
        cardDietitian = findViewById(R.id.cardDietitian);

        // Danışan seçildi
        cardClient.setOnClickListener(v -> openLoginWithRole("client"));

        // Diyetisyen seçildi
        cardDietitian.setOnClickListener(v -> openLoginWithRole("dietitian"));
    }

    private void openLoginWithRole(String userType) {
        Intent intent = new Intent(RoleSelectActivity.this, LoginActivity.class);
        intent.putExtra("user_type", userType);   // Login’e rolü gönderiyoruz
        startActivity(intent);
    }
}
