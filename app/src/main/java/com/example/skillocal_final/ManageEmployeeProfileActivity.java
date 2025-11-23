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

public class ManageEmployeeProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etMiddleName, etLastName, etSuffix;
    private EditText etAddress, etPhone, etBirthday, etEmail;
    private Spinner spinnerSex, spinnerCivilStatus;
    private Button btnSave;
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
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etSuffix = findViewById(R.id.etSuffix);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etBirthday = findViewById(R.id.etBirthday);
        etEmail = findViewById(R.id.etEmail);
        spinnerSex = findViewById(R.id.spinnerSex);
        spinnerCivilStatus = findViewById(R.id.spinnerCivilStatus);
        btnSave = findViewById(R.id.btnSave);
        iconBack = findViewById(R.id.icon_back_employee);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Back button
        iconBack.setOnClickListener(v -> finish());

        // Populate Spinners
        setupSpinners();

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

        // Load last employee data
        loadLastEmployee();

        // Save button
        btnSave.setOnClickListener(v -> saveEmployee());
    }

    private void setupSpinners() {
        // Sex options
        String[] sexOptions = {"Male", "Female"};
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexOptions);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(sexAdapter);

        // Civil Status options
        String[] civilStatusOptions = {"Single", "Married", "Widowed", "Separated", "Divorced"};
        ArrayAdapter<String> civilStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, civilStatusOptions);
        civilStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCivilStatus.setAdapter(civilStatusAdapter);
    }

    private void loadLastEmployee() {
        String jsonString = sharedPreferences.getString(KEY_EMPLOYEES, "[]");
        try {
            JSONArray arr = new JSONArray(jsonString);
            if (arr.length() > 0) {
                currentEmployee = arr.getJSONObject(arr.length() - 1);

                etFirstName.setText(currentEmployee.optString("firstName"));
                etMiddleName.setText(currentEmployee.optString("middleName"));
                etLastName.setText(currentEmployee.optString("lastName"));
                etSuffix.setText(currentEmployee.optString("suffix"));
                etAddress.setText(currentEmployee.optString("address"));
                etPhone.setText(currentEmployee.optString("phone"));
                etBirthday.setText(currentEmployee.optString("birthday"));
                etEmail.setText(currentEmployee.optString("email"));

                // Set spinner selections
                String sex = currentEmployee.optString("sex");
                if (!sex.isEmpty()) {
                    int sexPosition = ((ArrayAdapter<String>) spinnerSex.getAdapter()).getPosition(sex);
                    spinnerSex.setSelection(sexPosition);
                }

                String civilStatus = currentEmployee.optString("civilStatus");
                if (!civilStatus.isEmpty()) {
                    int statusPosition = ((ArrayAdapter<String>) spinnerCivilStatus.getAdapter()).getPosition(civilStatus);
                    spinnerCivilStatus.setSelection(statusPosition);
                }

            } else {
                currentEmployee = new JSONObject();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveEmployee() {
        try {
            currentEmployee.put("firstName", etFirstName.getText().toString());
            currentEmployee.put("middleName", etMiddleName.getText().toString());
            currentEmployee.put("lastName", etLastName.getText().toString());
            currentEmployee.put("suffix", etSuffix.getText().toString());
            currentEmployee.put("address", etAddress.getText().toString());
            currentEmployee.put("phone", etPhone.getText().toString());
            currentEmployee.put("birthday", etBirthday.getText().toString());
            currentEmployee.put("sex", spinnerSex.getSelectedItem().toString());
            currentEmployee.put("civilStatus", spinnerCivilStatus.getSelectedItem().toString());
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
