package com.stayhealth.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.stayhealth.R;
import com.stayhealth.ui.consultant.ConsultantListActivity;
import com.stayhealth.ui.home.DashboardActivity;
import com.stayhealth.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.logoutPreviousSession();

        Button btnNext = findViewById(R.id.btnNext);
        TextView txtForgot = findViewById(R.id.txtForgot);
        TextView txtSignIn = findViewById(R.id.txtSignIn);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnNext.setOnClickListener(v -> doLogin());
        txtForgot.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        txtSignIn.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));

        observeViewModel();
    }


    @Override
    protected void onStart() {
        super.onStart();
        
    }


    private void doLogin() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Email ve şifre zorunlu.", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.login(email, pass);
    }

    private void observeViewModel() {
        viewModel.getState().observe(this, state -> {
            if (state.loading) {
                // No progress UI in original screen; keep silent or add later
                return;
            }

            if (state.error != null) {
                Toast.makeText(this, "Giriş başarısız ❌ " + state.error, Toast.LENGTH_LONG).show();
                return;
            }

            if (state.user != null) {
                String role = state.role == null ? "client" : state.role;
                Toast.makeText(this, "Welcome! Role: " + role, Toast.LENGTH_SHORT).show();

                Class<?> target = "dietician".equalsIgnoreCase(role)
                        ? ConsultantListActivity.class
                        : DashboardActivity.class;

                Intent i = new Intent(LoginActivity.this, target);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }
}
