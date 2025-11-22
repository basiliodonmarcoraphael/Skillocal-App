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

    private EditText etFirstName, etMiddleName, etLastName, etSuffix, etAddress, etPhone, etBirthday, etEmail;
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

        // Make sure the XML name matches this file
        setContentView(R.layout.activity_manage_employer_profile);

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
        iconBack = findViewById(R.id.icon_back_manage);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Back button
        iconBack.setOnClickListener(v -> finish());

        // Sex dropdown
        String[] sexes = {"Male", "Female", "Other"};
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sexes);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(sexAdapter);

        // Civil Status dropdown
        String[] civilStatuses = {"Single", "Married", "Widowed", "Annulled", "Legally Separated"};
        ArrayAdapter<String> civilAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, civilStatuses);
        civilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCivilStatus.setAdapter(civilAdapter);

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

        loadLastEmployee();

        btnSave.setOnClickListener(v -> saveEmployeeChanges());
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

                setSpinnerSelection(spinnerSex, currentEmployee.optString("gender"));
                setSpinnerSelection(spinnerCivilStatus, currentEmployee.optString("civilStatus"));
            } else {
                currentEmployee = new JSONObject();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void saveEmployeeChanges() {
        try {
            currentEmployee.put("firstName", etFirstName.getText().toString());
            currentEmployee.put("middleName", etMiddleName.getText().toString());
            currentEmployee.put("lastName", etLastName.getText().toString());
            currentEmployee.put("suffix", etSuffix.getText().toString());
            currentEmployee.put("address", etAddress.getText().toString());
            currentEmployee.put("phone", etPhone.getText().toString());
            currentEmployee.put("birthday", etBirthday.getText().toString());
            currentEmployee.put("email", etEmail.getText().toString());
            currentEmployee.put("gender", spinnerSex.getSelectedItem().toString());
            currentEmployee.put("civilStatus", spinnerCivilStatus.getSelectedItem().toString());

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
