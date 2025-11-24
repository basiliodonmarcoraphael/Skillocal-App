package com.example.skillocal_final;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobVacancyActivity extends AppCompatActivity {

    private LinearLayout layoutJobs;
    private ArrayList<JobVacancy> jobs;
    private ArrayList<Establishment> establishments;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;

    private static final String PREFS_NAME = "SkillocalPrefs";
    private static final String KEY_JOBS = "jobs_per_user";

    ApiServiceJobVacancy api = ApiInstance.getApiJobVacancy();
    ApiService apiExt = ApiInstance.getApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_vacancy);

        Toolbar toolbar = findViewById(R.id.toolbar_job);
        setSupportActionBar(toolbar);

        ImageView backIcon = findViewById(R.id.icon_back_job);
        backIcon.setOnClickListener(v -> finish());

        layoutJobs = findViewById(R.id.layout_jobs);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_job);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserEmail = userPrefs.getString("email", "guest@user.com");

        jobs = new ArrayList<>();
        loadJobs();

        fabAdd.setOnClickListener(v -> showAddJobDialog());

        // Search functionality
        EditText etSearch = findViewById(R.id.et_search_job);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase();
                filterJobs(query);
            }
        });

        ImageView searchIcon = findViewById(R.id.search_icon_job);
        searchIcon.setOnClickListener(v -> {
            String query = etSearch.getText().toString().toLowerCase();
            filterJobs(query);
        });
    }

    private void loadJobs() {
        api.getAllJobVacancy(
                "*").enqueue(new Callback<List<JobVacancy>>() {
            @Override
            public void onResponse(@NonNull Call<List<JobVacancy>> call, @NonNull Response<List<JobVacancy>> response) {
                if (response.isSuccessful()) {
                    List<JobVacancy> resList = response.body();
                    layoutJobs.removeAllViews();
                    jobs.clear();

                    assert resList != null;
                    for (JobVacancy e : resList) {
                        jobs.add(e);
                        addJobToLayout(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<JobVacancy>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });

        apiExt.getAllEstablishment(
                "*").enqueue(new Callback<List<Establishment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Establishment>> call, @NonNull Response<List<Establishment>> response) {
                if (response.isSuccessful()) {
                    List<Establishment> resList = response.body();

                    assert resList != null;
                    for (Establishment e : resList) {
                        Log.d("API", "User: " + e.getAddress());
                        establishments.add(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Establishment>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void saveJobs() {
        JSONArray userArray = new JSONArray(jobs);
        try {
            String jsonString = sharedPreferences.getString(KEY_JOBS, "{}");
            JSONObject json = new JSONObject(jsonString);
            json.put(currentUserEmail, userArray);
            sharedPreferences.edit().putString(KEY_JOBS, json.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void filterJobs(String query) {
        layoutJobs.removeAllViews();
        for (JSONObject job : jobs) {
            try {
                String name = job.getString("jobName");
                if (name.toLowerCase().contains(query)) {
                    addJobToLayout(job);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAddJobDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Job Vacancy");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_job, null);

        EditText etJobName = view.findViewById(R.id.et_job_name);
        EditText etEstName = view.findViewById(R.id.et_est_name);
        Spinner spinnerStatus = view.findViewById(R.id.spinner_status);
        EditText etReviewedBy = view.findViewById(R.id.et_reviewed_by);
        EditText etSubmissionDate = view.findViewById(R.id.et_submission_date);
        EditText etReviewDate = view.findViewById(R.id.et_review_date);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Active", "Closed"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        etSubmissionDate.setOnClickListener(v -> showDatePicker(etSubmissionDate));
        etReviewDate.setOnClickListener(v -> showDatePicker(etReviewDate));

        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String jobName = etJobName.getText().toString().trim();
            String estName = etEstName.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();
            String reviewedBy = etReviewedBy.getText().toString().trim();
            String submissionDate = etSubmissionDate.getText().toString().trim();
            String reviewDate = etReviewDate.getText().toString().trim();

            if (jobName.isEmpty() || estName.isEmpty() || submissionDate.isEmpty() || reviewDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject jobObj = new JSONObject();
            try {
                jobObj.put("jobName", jobName);
                jobObj.put("estName", estName);
                jobObj.put("status", status);
                jobObj.put("reviewedBy", reviewedBy);
                jobObj.put("submissionDate", submissionDate);
                jobObj.put("reviewDate", reviewDate);

                jobs.add(jobObj);
                addJobToLayout(jobObj);
                saveJobs();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addJobToLayout(JobVacancy jobObj) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_job, layoutJobs, false);

        TextView tvJobInfo = itemView.findViewById(R.id.tv_job_info);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit_job);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete_job);

        try {
            String info = jobObj.getJob_title() + " at " + jobObj.getEstablishment_id().toString()
                    + " - " + jobObj.getStatus()
                    + "\nReviewed By: " + jobObj.getReviewed_by().toString()
                    + "\nSubmission: " + jobObj.getCreated_date()
                    + " | Review: " + jobObj.getReviewed_date();

            tvJobInfo.setText(info);
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnEdit.setOnClickListener(v -> showEditJobDialog(jobObj, tvJobInfo));
        btnDelete.setOnClickListener(v -> {
            layoutJobs.removeView(itemView);
            jobs.remove(jobObj);
            saveJobs();
        });

        layoutJobs.addView(itemView);
    }

    private void showEditJobDialog(JobVacancy jobObj, TextView tvJobInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Job Vacancy");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_job, null);

        EditText etJobName = view.findViewById(R.id.et_job_name);
        EditText etEstName = view.findViewById(R.id.et_est_name);
        Spinner spinnerStatus = view.findViewById(R.id.spinner_status);
        EditText etReviewedBy = view.findViewById(R.id.et_reviewed_by);
        EditText etSubmissionDate = view.findViewById(R.id.et_submission_date);
        EditText etReviewDate = view.findViewById(R.id.et_review_date);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Active", "Closed"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        try {
            Establishment myEstablishment = findEstablishmentById(establishments, jobObj.getEstablishment_id());
            etJobName.setText(jobObj.getJob_title());
            assert myEstablishment != null;
            etEstName.setText(myEstablishment.getEstablishmentName());
//            etReviewedBy.setText(jobObj.getString("reviewedBy"));
            etSubmissionDate.setText(jobObj.getCreated_date());
            etReviewDate.setText(jobObj.getReviewed_date());
            String status = jobObj.getStatus();
            spinnerStatus.setSelection(status.equalsIgnoreCase("Active") ? 0 : 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        etSubmissionDate.setOnClickListener(v -> showDatePicker(etSubmissionDate));
        etReviewDate.setOnClickListener(v -> showDatePicker(etReviewDate));

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String jobName = etJobName.getText().toString().trim();
            String estName = etEstName.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();
            String reviewedBy = etReviewedBy.getText().toString().trim();
            String submissionDate = etSubmissionDate.getText().toString().trim();
            String reviewDate = etReviewDate.getText().toString().trim();

            if (jobName.isEmpty() || estName.isEmpty() || submissionDate.isEmpty() || reviewDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                jobObj.put("jobName", jobName);
                jobObj.put("estName", estName);
                jobObj.put("status", status);
                jobObj.put("reviewedBy", reviewedBy);
                jobObj.put("submissionDate", submissionDate);
                jobObj.put("reviewDate", reviewDate);

                String info = jobName + " at " + estName
                        + " - " + status
                        + "\nReviewed By: " + reviewedBy
                        + "\nSubmission: " + submissionDate
                        + " | Review: " + reviewDate;
                tvJobInfo.setText(info);
                saveJobs();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDatePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) ->
                        editText.setText((selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear),
                year, month, day);
        datePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private Establishment findEstablishmentById(List<Establishment> list, int id) {
        for (Establishment e : list) {
            if (e.getEstablishment_id() != null && e.getEstablishment_id() == id) {
                return e;
            }
        }
        return null; // Not found
    }

}
