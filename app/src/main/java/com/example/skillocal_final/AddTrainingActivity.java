package com.example.skillocal_final;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTrainingActivity extends AppCompatActivity {
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

        etName = findViewById(R.id.et_name);
        etHours = findViewById(R.id.et_hours);
        etInstitution = findViewById(R.id.et_institution);
        etSkills = findViewById(R.id.et_skills);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
//        SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
//        String json = sp.getString(KEY, "[]");
//
//        try {
//            JSONArray arr = new JSONArray(json);
//            JSONObject obj = new JSONObject();
//            obj.put("name", etName.getText().toString());
//            obj.put("hours", etHours.getText().toString());
//            obj.put("institution", etInstitution.getText().toString());
//            obj.put("skills", etSkills.getText().toString());
//            arr.put(obj);
//            sp.edit().putString(KEY, arr.toString()).apply();
//            Toast.makeText(this, "Training Saved!", Toast.LENGTH_SHORT).show();
//            finish();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

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

        insertTraining(training);
    }


    private void insertTraining(Training training){

        api.insertTraining("*", training).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    Log.d("API", "Inserted Training");
                    Toast.makeText(AddTrainingActivity.this, "Training Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        Log.e("API", "Insert error");
                        Toast.makeText(AddTrainingActivity.this, "Insert failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(AddTrainingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
