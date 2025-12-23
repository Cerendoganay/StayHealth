package com.stayhealth.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ImageView btnBack = findViewById(R.id.btnBack);
        Button btnSendCode = findViewById(R.id.btnSendEmail);
        TextView txtBackToLognIn = findViewById(R.id.txtBackToLogin);

        btnBack.setOnClickListener(v -> finish());
        txtBackToLognIn.setOnClickListener(v -> finish());

        btnSendCode.setOnClickListener(v -> {
            // Burada istersen EmailVerifyActivity'e geçirelim:
            startActivity(new Intent(this, EmailVerifyActivity.class));
        });
    }
}
