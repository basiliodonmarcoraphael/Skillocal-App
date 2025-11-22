package com.example.skillocal_final;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CareerPortfolioActivity extends AppCompatActivity {

    ApiServiceWorker api = ApiInstance.getApiWorker();

    private LinearLayout layoutWorkExperience;
    private List<WorkExperience> workExperiences;
    private LinearLayout layoutEligibility;
    private List<Eligibility> eligibilities;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;
    private int currentId;

    private static final String PREFS_NAME = "SkillocalPrefs";
    private static final String KEY_WorkExperienceS = "WorkExperiences_per_user";
    private ImageView btnBack;
    private Button btnAddWork, btnAddEligibility, btnAddTraining;

    @Override
    //refresh screen and reload fetches when returning here
    protected void onResume() {
        super.onResume();
        loadWorkExperience();
        loadEligibility();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );

        setContentView(R.layout.activity_career_portfolio);

        layoutWorkExperience = findViewById(R.id.layout_workExperience);
        layoutEligibility = findViewById(R.id.layout_eligibility);

        // Toolbar Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Portfolio buttons
        btnAddWork = findViewById(R.id.btn_add_work);
        btnAddEligibility = findViewById(R.id.btn_add_eligibility);
        btnAddTraining = findViewById(R.id.btn_add_training);

        // Launch Add Work Experience screen
        btnAddWork.setOnClickListener(v ->
                startActivity(new Intent(CareerPortfolioActivity.this, AddWorkExperienceActivity.class))

        );

        // Launch Add Eligibility screen
        btnAddEligibility.setOnClickListener(v ->
                startActivity(new Intent(CareerPortfolioActivity.this, AddEligibilityActivity.class))
        );

        // Launch Add Training screen
        btnAddTraining.setOnClickListener(v ->
                startActivity(new Intent(CareerPortfolioActivity.this, AddTrainingActivity.class))
        );

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserEmail = userPrefs.getString("email", "guest@user.com");
        workExperiences = new ArrayList<>();
        loadWorkExperience();
        eligibilities = new ArrayList<>();
        loadEligibility();
    }

    private void loadWorkExperience() {
        // CLEAR UI BEFORE LOADING — this forces redraw
        layoutWorkExperience.removeAllViews();
        workExperiences.clear();

        api.getWorkExperienceByUserId(
                "*",                     // select all columns
                "eq." + currentId           // Supabase filter
        ).enqueue(new Callback<List<WorkExperience>>() {
            @Override
            public void onResponse(@NonNull Call<List<WorkExperience>> call, @NonNull Response<List<WorkExperience>> response) {
                if (response.isSuccessful()) {
                    List<WorkExperience> resList = response.body();
                    layoutWorkExperience.removeAllViews();
                    workExperiences.clear();

                    assert resList != null;
                    for (WorkExperience e : resList) {
                        Log.d("API", "Company: " + e.getCompany());
                        workExperiences.add(e);
                        addWorkExperienceToLayout(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WorkExperience>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void addWorkExperienceToLayout(WorkExperience estObj) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_work_experience, layoutWorkExperience, false);

        TextView tvName = itemView.findViewById(R.id.tv_work_experience_name);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit_work_experience);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete_work_experience);

        String name = estObj.getCompany();

        tvName.setText(name);

//        btnEdit.setOnClickListener(v -> showEditWorkExperienceDialog(estObj, tvName));
        btnDelete.setOnClickListener(v -> {
            layoutWorkExperience.removeView(itemView);
            workExperiences.remove(estObj);
        });

        layoutWorkExperience.addView(itemView);
    }

    //list user's eligibility
    private void loadEligibility() {
        // CLEAR UI BEFORE LOADING — this forces redraw
        layoutEligibility.removeAllViews();
        eligibilities.clear();

        api.getEligibilityByUserId(
                "*",                     // select all columns
                "eq." + currentId           // Supabase filter
        ).enqueue(new Callback<List<Eligibility>>() {
            @Override
            public void onResponse(@NonNull Call<List<Eligibility>> call, @NonNull Response<List<Eligibility>> response) {
                if (response.isSuccessful()) {
                    List<Eligibility> resList = response.body();
                    layoutEligibility.removeAllViews();
                    eligibilities.clear();

                    assert resList != null;
                    for (Eligibility e : resList) {
                        Log.d("API", "Company: " + e.getName());
                        eligibilities.add(e);
                        addEligibilityToLayout(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Eligibility>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void addEligibilityToLayout(Eligibility estObj) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_eligibility, layoutEligibility, false);

        TextView tvName = itemView.findViewById(R.id.tv_eligibility_name);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit_eligibility);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete_eligibility);

        String name = estObj.getName();

        tvName.setText(name);

//        btnEdit.setOnClickListener(v -> showEditEligibilityDialog(estObj, tvName));
        btnDelete.setOnClickListener(v -> {
            layoutEligibility.removeView(itemView);
            eligibilities.remove(estObj);
        });

        layoutEligibility.addView(itemView);
    }


}
