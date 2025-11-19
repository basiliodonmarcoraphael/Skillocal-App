package com.example.skillocal_final;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ManageEmployeeProfileActivity extends AppCompatActivity {

    private EditText etFullName, etBirthDate, etBirthPlace, etSex, etCivilStatus, etHeight, etWeight, etEmail;
    private Button btnSubmit;
    private ImageView iconBack;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "EmployeeProfilePrefs";
    private static final String KEY_EMPLOYEES = "employees";

    private JSONObject currentEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employee_profile);

        // Initialize fields
        etFullName = findViewById(R.id.etFullName);
        etBirthDate = findViewById(R.id.etBirthDate);
        etBirthPlace = findViewById(R.id.etBirthPlace);
        etSex = findViewById(R.id.etSex);
        etCivilStatus = findViewById(R.id.etCivilStatus);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        iconBack = findViewById(R.id.icon_back_employee);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Back button
        iconBack.setOnClickListener(v -> finish());

        // Birthday picker
        etBirthDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        etBirthDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Load last employee data
        loadLastEmployee();

        // Submit button
        btnSubmit.setOnClickListener(v -> saveEmployee());
    }

    private void loadLastEmployee() {
        String jsonString = sharedPreferences.getString(KEY_EMPLOYEES, "[]");
        try {
            JSONArray arr = new JSONArray(jsonString);
            if (arr.length() > 0) {
                currentEmployee = arr.getJSONObject(arr.length() - 1);

                etFullName.setText(currentEmployee.optString("fullName"));
                etBirthDate.setText(currentEmployee.optString("birthDate"));
                etBirthPlace.setText(currentEmployee.optString("birthPlace"));
                etSex.setText(currentEmployee.optString("sex"));
                etCivilStatus.setText(currentEmployee.optString("civilStatus"));
                etHeight.setText(currentEmployee.optString("height"));
                etWeight.setText(currentEmployee.optString("weight"));
                etEmail.setText(currentEmployee.optString("email"));
            } else {
                currentEmployee = new JSONObject();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveEmployee() {
        try {
            currentEmployee.put("fullName", etFullName.getText().toString());
            currentEmployee.put("birthDate", etBirthDate.getText().toString());
            currentEmployee.put("birthPlace", etBirthPlace.getText().toString());
            currentEmployee.put("sex", etSex.getText().toString());
            currentEmployee.put("civilStatus", etCivilStatus.getText().toString());
            currentEmployee.put("height", etHeight.getText().toString());
            currentEmployee.put("weight", etWeight.getText().toString());
            currentEmployee.put("email", etEmail.getText().toString());

            String jsonString = sharedPreferences.getString(KEY_EMPLOYEES, "[]");
            JSONArray arr = new JSONArray(jsonString);

            if (arr.length() > 0) {
                arr.put(arr.length() - 1, currentEmployee);
            } else {
                arr.put(currentEmployee);
            }

            sharedPreferences.edit().putString(KEY_EMPLOYEES, arr.toString()).apply();
            Toast.makeText(this, "Employee profile saved!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
