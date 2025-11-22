package com.example.skillocal_final;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ManageEmployerProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etMiddleInitial, etSurname, etAddress, etPhone, etBirthday, etEmail;
    private Spinner spinnerGender;
    private Button btnSave;
    private ImageView iconBack;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "EmployeeProfilePrefs";
    private static final String KEY_EMPLOYEES = "employees";

    private JSONObject currentEmployee; // holds the last created employee

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_employer_profile);

        // Initialize fields
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleInitial = findViewById(R.id.etMiddleInitial);
        etSurname = findViewById(R.id.etSurname);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etBirthday = findViewById(R.id.etBirthday);
        etEmail = findViewById(R.id.etEmail);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnSave = findViewById(R.id.btnSave);
        iconBack = findViewById(R.id.icon_back_manage);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Back button functionality
        iconBack.setOnClickListener(v -> finish());

        // Set up gender spinner
        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Load the last created employee
        loadLastEmployee();

        // Birthday picker
        etBirthday.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        etBirthday.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Save changes button
        btnSave.setOnClickListener(v -> saveEmployeeChanges());
    }

    private void loadLastEmployee() {
        String jsonString = sharedPreferences.getString(KEY_EMPLOYEES, "[]");
        try {
            JSONArray arr = new JSONArray(jsonString);
            if (arr.length() > 0) {
                currentEmployee = arr.getJSONObject(arr.length() - 1); // get last created employee

                etFirstName.setText(currentEmployee.getString("firstName"));
                etMiddleInitial.setText(currentEmployee.getString("middleInitial"));
                etSurname.setText(currentEmployee.getString("surname"));
                etAddress.setText(currentEmployee.getString("address"));
                etPhone.setText(currentEmployee.getString("phone"));
                etBirthday.setText(currentEmployee.getString("birthday"));
                etEmail.setText(currentEmployee.getString("email"));

                // Set spinner selection
                String gender = currentEmployee.getString("gender");
                for (int i = 0; i < spinnerGender.getCount(); i++) {
                    if (spinnerGender.getItemAtPosition(i).toString().equalsIgnoreCase(gender)) {
                        spinnerGender.setSelection(i);
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveEmployeeChanges() {
        if (currentEmployee == null) return;

        try {
            currentEmployee.put("firstName", etFirstName.getText().toString());
            currentEmployee.put("middleInitial", etMiddleInitial.getText().toString());
            currentEmployee.put("surname", etSurname.getText().toString());
            currentEmployee.put("address", etAddress.getText().toString());
            currentEmployee.put("phone", etPhone.getText().toString());
            currentEmployee.put("birthday", etBirthday.getText().toString());
            currentEmployee.put("email", etEmail.getText().toString());
            currentEmployee.put("gender", spinnerGender.getSelectedItem().toString());

            // Save back to SharedPreferences (overwrite last employee)
            String jsonString = sharedPreferences.getString(KEY_EMPLOYEES, "[]");
            JSONArray arr = new JSONArray(jsonString);
            arr.put(arr.length() - 1, currentEmployee);
            sharedPreferences.edit().putString(KEY_EMPLOYEES, arr.toString()).apply();

            Toast.makeText(this, "Employee updated!", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
