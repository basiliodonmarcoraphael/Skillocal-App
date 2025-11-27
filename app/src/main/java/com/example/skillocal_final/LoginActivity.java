package com.example.skillocal_final;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvCreateAccount;
    private ProgressBar prgLogin;

    private void fetchLogin(String inputEmail, String pass, Context cont) {
        ApiService api = ApiInstance.getApi();
        api.loginUser(inputEmail, pass, "*").enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                SharedPreferences prefs = getSharedPreferences("MyRole", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                if (response.isSuccessful()) {
                    List<User> users = response.body();
                    if (users != null) {
                        Log.d("API", "Users fetched: " + users.size());
                        for (User u : users) {
                            Log.d("API", "User: " + u.getFName());
                        }
                        if (!users.isEmpty()) {
                            final User userMe = users.get(0);
                            editor.putInt("userId", userMe.getUserId());
                            editor.putString("fName", userMe.getFName());
                            editor.putString("mName", userMe.getMName());
                            editor.putString("lName", userMe.getLName());
                            editor.putString("role", userMe.getRole());
                            editor.apply();
                            String savedEmail = getSharedPreferences("UserAccount", MODE_PRIVATE).getString("email", "");
                            String savedPassword = getSharedPreferences("UserAccount", MODE_PRIVATE).getString("password", "");
                            Toast.makeText(cont, "Login successful", Toast.LENGTH_SHORT).show();
                            // Go to MainActivity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            btnLogin.setVisibility(VISIBLE);
                            prgLogin.setVisibility(INVISIBLE);
                            finish();
                        } else {
                            Toast.makeText(cont, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            btnLogin.setVisibility(VISIBLE);
                            prgLogin.setVisibility(INVISIBLE);
                        }
                    } else {
                        Toast.makeText(cont, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        btnLogin.setVisibility(VISIBLE);
                        prgLogin.setVisibility(INVISIBLE);
                    }
                } else {
                    Toast.makeText(cont, "Error API", Toast.LENGTH_SHORT).show();
                    btnLogin.setVisibility(VISIBLE);
                    prgLogin.setVisibility(INVISIBLE);
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        prgLogin = findViewById(R.id.progressBar);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        etEmail.setText("joshuagarcia@gmail.com");
//        etEmail.setText("test@mail.com");
        etPassword.setText("1234");

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setVisibility(INVISIBLE);
            prgLogin.setVisibility(VISIBLE);

            fetchLogin("eq." + email, "eq." + password, this);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }
}
