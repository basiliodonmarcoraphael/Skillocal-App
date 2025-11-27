package com.example.skillocal_final;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etForgotEmail;
    private Button btnResetPassword;
    private ImageView iconBackForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        iconBackForgot = findViewById(R.id.icon_back_forgot);
        iconBackForgot.setOnClickListener(v -> finish());

        btnResetPassword.setOnClickListener(v -> {
            String email = etForgotEmail.getText().toString().trim();

            if(email.isEmpty()) {
                etForgotEmail.setError("Email is required");
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etForgotEmail.setError("Enter a valid email");
                return;
            }

            SharedPreferences prefs = getSharedPreferences("UserAccount", MODE_PRIVATE);
            String savedEmail = prefs.getString("email", "");

            if(email.equalsIgnoreCase(savedEmail)) {
                Toast.makeText(this, "Password reset link sent to " + email, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Email not found in our records", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
