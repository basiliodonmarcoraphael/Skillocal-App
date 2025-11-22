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

public class AddEligibilityActivity extends AppCompatActivity {

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

        etName = findViewById(R.id.et_name);
        etLicense = findViewById(R.id.et_license);
        etDateTaken = findViewById(R.id.et_date_taken);
        etValidity = findViewById(R.id.et_validity);
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
            obj.put("license", etLicense.getText().toString());
            obj.put("date_taken", etDateTaken.getText().toString());
            obj.put("validity", etValidity.getText().toString());
            arr.put(obj);
            sp.edit().putString(KEY, arr.toString()).apply();
            Toast.makeText(this, "Eligibility Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
