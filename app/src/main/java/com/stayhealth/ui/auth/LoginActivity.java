package com.stayhealth.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;
import com.stayhealth.ui.home.DashboardActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnNext = findViewById(R.id.btnNext);
        TextView txtForgot = findViewById(R.id.txtForgot);
        TextView txtSignIn = findViewById(R.id.txtSignIn);

        btnNext.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));

        txtForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        txtSignIn.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }
}
