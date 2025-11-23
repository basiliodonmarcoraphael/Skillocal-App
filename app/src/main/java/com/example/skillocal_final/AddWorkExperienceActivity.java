package com.example.skillocal_final;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWorkExperienceActivity extends AppCompatActivity {

    ApiServiceWorker api = ApiInstance.getApiWorker();

    private int currentId;
    private EditText etCompany, etAddress, etPosition, etMonths;
    private Spinner spStatus; // changed from EditText to Spinner
    private Button btnSave;
    private ImageView btnBack;
    private static final String PREFS = "CareerData";
    private static final String KEY = "WORK_EXPERIENCE_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );
        setContentView(R.layout.activity_add_work_experience);

        etCompany = findViewById(R.id.et_company);
        etAddress = findViewById(R.id.et_address);
        etPosition = findViewById(R.id.et_position);
        etMonths = findViewById(R.id.et_months);
        spStatus = findViewById(R.id.sp_status); // spinner initialization
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        // ----------------- Setup Spinner -----------------
        String[] statuses = {"Employed", "Terminated", "Redundated", "Ended", "AWOL"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(adapter);
        // --------------------------------------------------

        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String company = etCompany.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String position = etPosition.getText().toString();
        String months = etMonths.getText().toString().trim();
        String status = spStatus.getSelectedItem().toString(); // get selected status

        // VALIDATION — dialog stays open
        if (company.isEmpty() || address.isEmpty() || position.isEmpty() || months.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return; // Do NOT dismiss dialog
        }

        // SUCCESS — now allow closing
        WorkExperience workExperience = new WorkExperience(
                currentId,
                company,
                address,
                Integer.parseInt(months),
                status,
                position
        );

        insertWorkExperience(workExperience);
    }

    private void insertWorkExperience(WorkExperience workExperience){

        api.insertWorkExperience("*", workExperience).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Log.d("API", "Inserted Work Experience");
                    Toast.makeText(AddWorkExperienceActivity.this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        Log.e("API", "Insert error");
                        Toast.makeText(AddWorkExperienceActivity.this, "Insert failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(AddWorkExperienceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
