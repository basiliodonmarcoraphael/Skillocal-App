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

import java.util.Calendar;

public class JobApplicationActivity extends AppCompatActivity {

    private EditText etFullName, etBirthDate, etBirthPlace, etSex, etCivilStatus,
            etHeight, etWeight, etGraduateInstitution, etGraduateCourse,
            etTertiaryInstitution, etSecondaryInstitution, etPrimaryInstitution;
    private Button btnSubmit;
    private ImageView iconBack;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "JobApplicationPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_application);

        // Initialize fields
        etFullName = findViewById(R.id.et_full_name);
        etBirthDate = findViewById(R.id.et_birth_date);
        etBirthPlace = findViewById(R.id.et_birth_place);
        etSex = findViewById(R.id.et_sex);
        etCivilStatus = findViewById(R.id.et_civil_status);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        etGraduateInstitution = findViewById(R.id.et_graduate_institution);
        etGraduateCourse = findViewById(R.id.et_graduate_course);
        etTertiaryInstitution = findViewById(R.id.et_tertiary_institution);
        etSecondaryInstitution = findViewById(R.id.et_secondary_institution);
        etPrimaryInstitution = findViewById(R.id.et_primary_institution);
        btnSubmit = findViewById(R.id.btn_submit);
        iconBack = findViewById(R.id.icon_back_job_seeker);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Back button
        iconBack.setOnClickListener(v -> finish());

        // Birth date picker
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

        // Submit button saves temporarily in SharedPreferences
        btnSubmit.setOnClickListener(v -> saveJobApplication());
    }

    private void saveJobApplication() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("full_name", etFullName.getText().toString());
        editor.putString("birth_date", etBirthDate.getText().toString());
        editor.putString("birth_place", etBirthPlace.getText().toString());
        editor.putString("sex", etSex.getText().toString());
        editor.putString("civil_status", etCivilStatus.getText().toString());
        editor.putString("height", etHeight.getText().toString());
        editor.putString("weight", etWeight.getText().toString());
        editor.putString("graduate_institution", etGraduateInstitution.getText().toString());
        editor.putString("graduate_course", etGraduateCourse.getText().toString());
        editor.putString("tertiary_institution", etTertiaryInstitution.getText().toString());
        editor.putString("secondary_institution", etSecondaryInstitution.getText().toString());
        editor.putString("primary_institution", etPrimaryInstitution.getText().toString());
        editor.apply();

        Toast.makeText(this, "Job application saved temporarily!", Toast.LENGTH_SHORT).show();
    }
}
