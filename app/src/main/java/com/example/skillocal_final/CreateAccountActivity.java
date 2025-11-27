package com.example.skillocal_final;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText etFirstName, etMiddleInitial, etSurname, etAddress, etPhone, etBirthday, etEmail;
    private EditText etPassword, etConfirmPassword;
    private Spinner spinnerSex;
    private Button btnCreateAccount;
    private TextView tvSignIn;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "EmployeeProfilePrefs";
    private static final String KEY_EMPLOYEES = "employees";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        etFirstName = findViewById(R.id.etFirstName);
        etMiddleInitial = findViewById(R.id.etMiddleInitial);
        etSurname = findViewById(R.id.etSurname);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etBirthday = findViewById(R.id.etBirthday);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerSex = findViewById(R.id.spinnerSex);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        tvSignIn = findViewById(R.id.tvSignIn);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Gender Spinner
        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(genderAdapter);

        // Birthday Picker
        etBirthday.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateAccountActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        etBirthday.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Create Account Button
        btnCreateAccount.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String middleInitial = etMiddleInitial.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String birthday = etBirthday.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String gender = spinnerSex.getSelectedItem().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            // Validation
            if (firstName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save new employee to SharedPreferences
            try {
                String jsonString = sharedPreferences.getString(KEY_EMPLOYEES, "[]");
                JSONArray employeeArray = new JSONArray(jsonString);

                JSONObject employee = new JSONObject();
                employee.put("firstName", firstName);
                employee.put("middleInitial", middleInitial);
                employee.put("surname", surname);
                employee.put("address", address);
                employee.put("phone", phone);
                employee.put("birthday", birthday);
                employee.put("email", email);
                employee.put("gender", gender);
                employee.put("password", password); // optional, for login

                employeeArray.put(employee);

                sharedPreferences.edit().putString(KEY_EMPLOYEES, employeeArray.toString()).apply();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error saving employee", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Employee account created!", Toast.LENGTH_SHORT).show();

            // Open ManageEmployerProfileActivity
            startActivity(new Intent(CreateAccountActivity.this, ManageEmployerProfileActivity.class));
            finish();
        });

        // Go to Login Screen
        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
            finish();
        });
    }
}
