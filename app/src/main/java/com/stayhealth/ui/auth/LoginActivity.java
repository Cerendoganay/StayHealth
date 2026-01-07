package com.stayhealth.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stayhealth.R;
import com.stayhealth.ui.home.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button btnNext      = findViewById(R.id.btnNext);
        TextView txtForgot  = findViewById(R.id.txtForgot);
        TextView txtSignIn  = findViewById(R.id.txtSignIn);  // senin kullandığın id

        edtEmail    = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        // ▶ LOGIN → Firebase ile giriş
        btnNext.setOnClickListener(v -> doLogin());

        // Forgot Password → ForgotPasswordActivity
        txtForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        // "Sign up" → SignUpActivity
        txtSignIn.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Eğer kullanıcı zaten giriş yapmışsa, direkt Dashboard'a gönder
        FirebaseUser current = mAuth.getCurrentUser();
        if (current != null) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }

    private void doLogin() {
        String email = edtEmail.getText().toString().trim();
        String pass  = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Email ve şifre zorunlu.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Başarılı giriş → Dashboard
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this,
                                "Giriş başarısız: " +
                                        (task.getException() != null
                                                ? task.getException().getMessage()
                                                : ""),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
