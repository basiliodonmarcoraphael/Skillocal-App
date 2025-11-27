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
import android.widget.Toast;

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
    private LinearLayout layoutTraining;
    private List<Training> trainings;
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
        loadTraining();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );

        setContentView(R.layout.activity_career_portfolio);

        layoutWorkExperience = findViewById(R.id.layout_workExperience);
        layoutEligibility = findViewById(R.id.layout_eligibility);
        layoutTraining = findViewById(R.id.layout_training);

        // Toolbar Back Button
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

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
        trainings = new ArrayList<>();
        loadTraining();

    }

    private void loadWorkExperience() {
        // CLEAR UI BEFORE LOADING — this forces redraw
        layoutWorkExperience.removeAllViews();
        workExperiences.clear();

        api.getWorkExperienceByUserId(
                "*",                     // select all columns
                "eq." + currentId,           // Supabase filter
                "work_experience_id.asc"     // order by work_experience_id ascending
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

        String companyName = estObj.getCompany();
        String address = estObj.getAddress();
        String position = estObj.getPosition();
        Integer monthCount = estObj.getMonthCount();
        String status = estObj.getEmploymentStatus();
        Integer workExperienceId = estObj.getWorkExperienceId();

        String name = estObj.getCompany();

        tvName.setText(name);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(CareerPortfolioActivity.this, EditWorkExperienceActivity.class);
            intent.putExtra("companyName", companyName);
            intent.putExtra("address", address);
            intent.putExtra("position", position);
            intent.putExtra("monthCount", monthCount);  // pass object
            intent.putExtra("status", status);  // pass object
            intent.putExtra("workExperienceId", workExperienceId);  // pass object
            startActivity(intent);
        });
        btnDelete.setOnClickListener(v -> {
            deleteWorkExperience(estObj); //delete work experience function

        });

        layoutWorkExperience.addView(itemView);
    }

    private void deleteWorkExperience(WorkExperience estObj) {
        Integer workExperienceId = estObj.getWorkExperienceId();
        String workExpId = "eq." + workExperienceId; //change to string
        Call<Void> call = api.deleteWorkExperience(workExpId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Work Experience deleted successfully");
                    Toast.makeText(CareerPortfolioActivity.this, "Work Experience deleted successfully", Toast.LENGTH_SHORT).show();
                    loadWorkExperience();
                } else {
                    Log.e("API", "Delete failed: " + response.code());
                    Toast.makeText(CareerPortfolioActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
                loadWorkExperience();
            }

        });
    }

    //list user's eligibility
    private void loadEligibility() {
        // CLEAR UI BEFORE LOADING — this forces redraw
        layoutEligibility.removeAllViews();
        eligibilities.clear();

        api.getEligibilityByUserId(
                "*",                     // select all columns
                "eq." + currentId,           // Supabase filter
                "eligibility_id.asc"     // order by work_experience_id ascending
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
        String licenseNumber = estObj.getLicenseNumber();
        String dateTaken = estObj.getDateTaken();
        String validityDate = estObj.getValidityDate();
        Integer eligibilityId = estObj.getEligibilityId();

        tvName.setText(name);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(CareerPortfolioActivity.this, EditEligibilityActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("licenseNumber", licenseNumber);
            intent.putExtra("dateTaken", dateTaken);
            intent.putExtra("validityDate", validityDate);  // pass object
            intent.putExtra("eligibilityId", eligibilityId);  // pass object
            startActivity(intent);
        });
        btnDelete.setOnClickListener(v -> {
            deleteEligibility(estObj);

        });

        layoutEligibility.addView(itemView);
    }

    private void deleteEligibility(Eligibility estObj) {
        Integer eligibilityId = estObj.getEligibilityId();
        String eId = "eq." + eligibilityId; //change to string
        Call<Void> call = api.deleteEligibility(eId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Eligibility deleted successfully");
                    Toast.makeText(CareerPortfolioActivity.this, "Eligibility deleted successfully", Toast.LENGTH_SHORT).show();
                    loadEligibility();
                } else {
                    Log.e("API", "Delete failed: " + response.code());
                    Toast.makeText(CareerPortfolioActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
                loadEligibility();
            }

        });
    }

    //list user's training
    private void loadTraining() {
        // CLEAR UI BEFORE LOADING — this forces redraw
        layoutTraining.removeAllViews();
        trainings.clear();

        api.getTrainingByUserId(
                "*",                     // select all columns
                "eq." + currentId,           // Supabase filter
                "training_id.asc"     // order by work_experience_id ascending
        ).enqueue(new Callback<List<Training>>() {
            @Override
            public void onResponse(@NonNull Call<List<Training>> call, @NonNull Response<List<Training>> response) {
                if (response.isSuccessful()) {
                    List<Training> resList = response.body();
                    layoutTraining.removeAllViews();
                    trainings.clear();

                    assert resList != null;
                    for (Training e : resList) {
                        Log.d("API", "Training Name: " + e.getName());
                        trainings.add(e);
                        addTrainingToLayout(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Training>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());

            }
        });
    }

    private void addTrainingToLayout(Training estObj) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_training, layoutTraining, false);

        TextView tvName = itemView.findViewById(R.id.tv_training_name);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit_training);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete_training);

        String name = estObj.getName();
        Integer hours = estObj.getHours();
        String institution = estObj.getInstitution();
        String skillsAcquired = estObj.getSkillsAcquired();
        Integer trainingId = estObj.getTrainingId();

        tvName.setText(name);

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(CareerPortfolioActivity.this, EditTrainingActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("hours", hours);
            intent.putExtra("institution", institution);
            intent.putExtra("skillsAcquired", skillsAcquired);  // pass object
            intent.putExtra("trainingId", trainingId);  // pass object
            Log.e("API", "Training Id: " + trainingId);
            startActivity(intent);
        });
        btnDelete.setOnClickListener(v -> {
            deleteTraining(estObj); //delete training function

        });

        layoutTraining.addView(itemView);
    }

    private void deleteTraining(Training estObj) {
        Integer trainingId = estObj.getTrainingId();
        String tId = "eq." + trainingId; //change to string
        Call<Void> call = api.deleteTraining(tId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "Training deleted successfully");
                    Toast.makeText(CareerPortfolioActivity.this, "Training deleted successfully", Toast.LENGTH_SHORT).show();
                    loadTraining();
                } else {
                    Log.e("API", "Delete failed: " + response.code());
                    Toast.makeText(CareerPortfolioActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
                loadTraining();
            }

        });
    }

}
