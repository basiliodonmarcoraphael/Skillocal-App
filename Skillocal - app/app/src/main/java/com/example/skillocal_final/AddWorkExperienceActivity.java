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

public class AddWorkExperienceActivity extends AppCompatActivity {

    private EditText etCompany, etAddress, etPosition, etMonths, etStatus;
    private Button btnSave;
    private ImageView btnBack;
    private static final String PREFS = "CareerData";
    private static final String KEY = "WORK_EXPERIENCE_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_work_experience);

        etCompany = findViewById(R.id.et_company);
        etAddress = findViewById(R.id.et_address);
        etPosition = findViewById(R.id.et_position);
        etMonths = findViewById(R.id.et_months);
        etStatus = findViewById(R.id.et_status);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = sp.getString(KEY, "[]");

        try {
            JSONArray arr = new JSONArray(json);
            JSONObject obj = new JSONObject();
            obj.put("company", etCompany.getText().toString());
            obj.put("address", etAddress.getText().toString());
            obj.put("position", etPosition.getText().toString());
            obj.put("months", etMonths.getText().toString());
            obj.put("status", etStatus.getText().toString());
            arr.put(obj);
            sp.edit().putString(KEY, arr.toString()).apply();
            Toast.makeText(this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
