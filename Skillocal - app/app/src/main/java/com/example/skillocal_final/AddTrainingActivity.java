package com.example.skillocal_final;

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

public class AddTrainingActivity extends AppCompatActivity {

    private EditText etName, etHours, etInstitution, etSkills;
    private ImageView btnBack;
    private Button btnSave;
    private static final String PREFS = "CareerData";
    private static final String KEY = "TRAINING_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = sp.getString(KEY, "[]");

        try {
            JSONArray arr = new JSONArray(json);
            JSONObject obj = new JSONObject();
            obj.put("name", etName.getText().toString());
            obj.put("hours", etHours.getText().toString());
            obj.put("institution", etInstitution.getText().toString());
            obj.put("skills", etSkills.getText().toString());
            arr.put(obj);
            sp.edit().putString(KEY, arr.toString()).apply();
            Toast.makeText(this, "Training Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
