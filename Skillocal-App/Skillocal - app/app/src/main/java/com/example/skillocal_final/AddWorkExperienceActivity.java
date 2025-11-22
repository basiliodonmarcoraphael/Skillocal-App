package com.example.skillocal_final;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWorkExperienceActivity extends AppCompatActivity {

    ApiServiceWorker api = ApiInstance.getApiWorker();

    private int currentId;
    private EditText etCompany, etAddress, etPosition, etMonths, etStatus;
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
        etStatus = findViewById(R.id.et_status);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveData());
    }



    private void saveData() {
//        SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
//        String json = sp.getString(KEY, "[]");
//        try {
//            JSONArray arr = new JSONArray(json);
//            JSONObject obj = new JSONObject();
//            obj.put("company", etCompany.getText().toString());
//            obj.put("address", etAddress.getText().toString());
//            obj.put("position", etPosition.getText().toString());
//            obj.put("months", etMonths.getText().toString());
//            obj.put("status", etStatus.getText().toString());
//            arr.put(obj);
//            sp.edit().putString(KEY, arr.toString()).apply();
//            Toast.makeText(this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
//            finish();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        String company = etCompany.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String position = etPosition.getText().toString();
        String months = etMonths.getText().toString().trim();
        String status = etStatus.getText().toString().trim();


        // VALIDATION — dialog stays open
        if (company.isEmpty() || address.isEmpty() || position.isEmpty() || months.isEmpty() || status.isEmpty()) {
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

        api.insertWorkExperience("*", workExperience).enqueue(new Callback<List<WorkExperience>>() {
            @Override
            public void onResponse(Call<List<WorkExperience>> call, Response<List<WorkExperience>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkExperience> inserted = response.body();
                    WorkExperience newRow = inserted.get(0);

                    Log.d("API", "Inserted Work Experience ID: " + newRow.getWorkExperienceId());
                    Log.d("API", "Company: " + newRow.getCompany());
                    Toast.makeText(AddWorkExperienceActivity.this, "Work Experience Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        Log.e("API", "Insert error: " + response.errorBody().string());
                        Toast.makeText(AddWorkExperienceActivity.this, "Insert failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }

            @Override
            public void onFailure(Call<List<WorkExperience>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(AddWorkExperienceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }




}
