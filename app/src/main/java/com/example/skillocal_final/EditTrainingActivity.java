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

public class EditTrainingActivity extends AppCompatActivity {
    ApiServiceWorker api = ApiInstance.getApiWorker();
    private int currentId;
    private EditText etName, etHours, etInstitution, etSkills;
    private ImageView btnBack;
    private Button btnSave;
    private static final String PREFS = "CareerData";
    private static final String KEY = "TRAINING_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );
        setContentView(R.layout.activity_add_training);

        String name = (String) getIntent().getSerializableExtra("name");
        Integer hours = (Integer) getIntent().getSerializableExtra("hours");
        String institution = (String) getIntent().getSerializableExtra("institution");
        String skillsAcquired = (String) getIntent().getSerializableExtra("skillsAcquired");
        Integer trainingId = (Integer) getIntent().getSerializableExtra("trainingId");

        etName = findViewById(R.id.et_name);
        etHours = findViewById(R.id.et_hours);
        etInstitution = findViewById(R.id.et_institution);
        etSkills = findViewById(R.id.et_skills);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveData(trainingId));

        if (name != null) {
            etName.setText(name);
        }
        if (hours != null) {
            etHours.setText(String.valueOf(hours));
        }
        if (institution != null) {
            etInstitution.setText(institution);
        }
        if (skillsAcquired != null) {
            etSkills.setText(skillsAcquired);
        }
    }

    private void saveData(Integer trainingId) {

        String name = etName.getText().toString().trim();
        String hours = etHours.getText().toString().trim();
        String institution = etInstitution.getText().toString();
        String skills = etSkills.getText().toString().trim();

        // VALIDATION — dialog stays open
        if (name.isEmpty() || hours.isEmpty() || institution.isEmpty() || skills.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return; // Do NOT dismiss dialog
        }

        // SUCCESS — now allow closing
        Training training = new Training(
                currentId,
                name,
                Integer.parseInt(hours),
                institution,
                skills
        );

        updateTraining(training, trainingId);
    }


    private void updateTraining(Training training, Integer trainingId){

        String idFilter = "eq." + trainingId;

        api.updateTraining(idFilter, training).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditTrainingActivity.this, "Training Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditTrainingActivity.this, "Update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("API", "Update failed: " + response.code());
                    Log.e("API", "Update failed: " + idFilter);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditTrainingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: ", t);
            }
        });
    }
}
