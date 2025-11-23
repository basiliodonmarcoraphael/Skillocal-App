package com.example.skillocal_final;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEligibilityActivity extends AppCompatActivity {

    ApiServiceWorker api = ApiInstance.getApiWorker();
    private int currentId;
    private EditText etName, etLicense, etDateTaken, etValidity;
    private ImageView btnBack;
    private Button btnSave;
    private static final String PREFS = "CareerData";
    private static final String KEY = "ELIGIBILITY_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );
        setContentView(R.layout.activity_add_eligibility);

        String name = (String) getIntent().getSerializableExtra("name");
        String licenseNumber = (String) getIntent().getSerializableExtra("licenseNumber");
        String dateTaken = (String) getIntent().getSerializableExtra("dateTaken");
        String validityDate = (String) getIntent().getSerializableExtra("validityDate");
        Integer eligibilityId = (Integer) getIntent().getSerializableExtra("eligibilityId");

        etName = findViewById(R.id.et_name);
        etLicense = findViewById(R.id.et_license);
        etDateTaken = findViewById(R.id.et_date_taken);
        etValidity = findViewById(R.id.et_validity);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        // ----------------- Date Picker -----------------
        etDateTaken.setOnClickListener(v -> Eligibility.showDatePicker(this, etDateTaken));
        etValidity.setOnClickListener(v -> Eligibility.showDatePicker(this, etValidity));
        // -----------------------------------------------

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveData(eligibilityId));

        if (name != null) {
            etName.setText(name);
        }
        if (licenseNumber != null) {
            etLicense.setText(licenseNumber);
        }
        if (dateTaken != null) {
            etDateTaken.setText(dateTaken);
        }
        if (validityDate != null) {
            etValidity.setText(validityDate);
        }
    }

    private void saveData(Integer eligibilityId) {


        String name = etName.getText().toString().trim();
        String license = etLicense.getText().toString().trim();
        String date_taken = etDateTaken.getText().toString();
        String validity = etValidity.getText().toString().trim();


        // VALIDATION
        if (name.isEmpty() || license.isEmpty() || date_taken.isEmpty() || validity.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return; // Do NOT dismiss screen
        }

        // SUCCESS — now allow closing
        Eligibility eligibility = new Eligibility(
                currentId,
                name,
                license,
                date_taken,
                validity
        );
        updateEligibility(eligibility, eligibilityId);

    }

    private void updateEligibility(Eligibility eligibility, Integer eligibilityId){

        String idFilter = "eq." + eligibilityId;

        api.updateEligibility(idFilter, eligibility).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditEligibilityActivity.this, "Eligibility Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditEligibilityActivity.this, "Update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("API", "Update failed: " + response.code());
                    Log.e("API", "Update failed: " + idFilter);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditEligibilityActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: ", t);
            }
        });
    }

}



