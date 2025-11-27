package com.example.skillocal_final;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditWorkExperienceActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_edit_work_experience);

        String companyName = (String) getIntent().getSerializableExtra("companyName");
        String address = (String) getIntent().getSerializableExtra("address");
        String position = (String) getIntent().getSerializableExtra("position");
        Integer monthCount = (Integer) getIntent().getSerializableExtra("monthCount");
        String status = (String) getIntent().getSerializableExtra("status");
        Integer workExperienceId = (Integer) getIntent().getSerializableExtra("workExperienceId");

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

        btnSave.setOnClickListener(v -> saveData(workExperienceId));

        if (companyName != null) {
            etCompany.setText(companyName);
        }
        if (address != null) {
            etAddress.setText(address);
        }
        if (position != null) {
            etPosition.setText(position);
        }
        if (monthCount != null) {
            etMonths.setText(String.valueOf(monthCount));
        }
        if (status != null) {
            //for employment status drop down

            // Find index of currentStatus in statuses
            int index = -1;
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equalsIgnoreCase(status)) {
                    index = i;
                    break;
                }
            }

            // If found, set selection
            if (index != -1) {
                spStatus.setSelection(index);
            }
        }

    }

    private void saveData(Integer workExperienceId) {
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

        updateWorkExperience(workExperience, workExperienceId);
    }

    private void updateWorkExperience(WorkExperience workExperience, Integer workExperienceId){

        String idFilter = "eq." + workExperienceId;

        api.updateWorkExperience(idFilter, workExperience).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditWorkExperienceActivity.this, "Work Experience Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditWorkExperienceActivity.this, "Update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("API", "Update failed: " + response.code());
                    Log.e("API", "Update failed: " + idFilter);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditWorkExperienceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: ", t);
            }
        });
    }
}
