package com.example.skillocal_final;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobVacancyActivity extends AppCompatActivity {

    private LinearLayout layoutJobs;
    private ArrayList<JobVacancy> jobs;
    private List<Establishment> establishments;
    private List<Industry> industry;
    private List<String> empType;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;

    private Integer currentId;

    private static final String PREFS_NAME = "SkillocalPrefs";
    private static final String KEY_JOBS = "jobs_per_user";

    ApiServiceJobVacancy api = ApiInstance.getApiJobVacancy();
    ApiService apiExt = ApiInstance.getApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );
        setContentView(R.layout.activity_job_vacancy);
        empType = new ArrayList<>(
                List.of("Full-Time", "Part-Time", "Contract", "Internship", "Others")
        );

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
        api.getAllJobVacancyByUserId(
                "*",                     // select all columns
                "eq." + currentId,           // Supabase filter
                "vacancy_id.asc"     // order by work_experience_id ascending
        ).enqueue(new Callback<List<JobVacancy>>() {
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

        apiExt.getEstablishmentByUserId(
                "*",                   // select all
                "eq." + currentId,        // user_id = ?
                "not.eq.Deleted",      // status != "Deleted"
                "establishment_id.asc"

        ).enqueue(new Callback<List<Establishment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Establishment>> call, @NonNull Response<List<Establishment>> response) {
                if (response.isSuccessful()) {
                    establishments = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Establishment>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });

        apiExt.getAllIndustry(
                "*").enqueue(new Callback<List<Industry>>() {
            @Override
            public void onResponse(@NonNull Call<List<Industry>> call, @NonNull Response<List<Industry>> response) {
                if (response.isSuccessful()) {
                    industry = response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Industry>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void saveJobs(JobVacancy jobV) {
        api.insertJobVacancy(jobV).enqueue(new Callback<JobVacancy>() {
            @Override
            public void onResponse(@NonNull Call<JobVacancy> call, @NonNull Response<JobVacancy> response) {
                if (response.isSuccessful()) {
                    JobVacancy created = response.body();
                    // SUCCESS — the row was inserted
                    assert created != null;
                    Log.d("API", "Inserted: " + created.getCreated_date());
                    loadJobs();
                } else {
                    // ERROR — the server returned a bad status
                    Log.e("API", "Insert failed: " + response.code());
                    Log.e("API", "Error body: " + response.errorBody());
                    Log.e("API", "EstID: " + jobV.getEstablishment_id());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JobVacancy> call, @NonNull Throwable t) {
                // NETWORK / RUNTIME ERROR
                t.fillInStackTrace();
                Log.e("API", "Network error: " + t.getMessage());
                loadJobs();
            }
        });
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

    private void updateJobVacancy(JobVacancy jobV, int jobID){
        api.updateJobVacancy("eq." + jobID, jobV).enqueue(new Callback<JobVacancy>() {
            @Override
            public void onResponse(@NonNull Call<JobVacancy> call, @NonNull Response<JobVacancy> response) {
                if (response.isSuccessful()) {
                    JobVacancy created = response.body();

                    // REPLACE THE ASSERTION WITH PROPER NULL CHECK
                    if (created != null) {
                        // SUCCESS — the row was inserted
                        loadJobs();
                    } else {
                        // Handle null response body
                        Log.e("API", "Update successful but response body is null");
                        // You might still want to refresh the list if update was successful
                        loadJobs();
                    }
                } else {
                    // ERROR — the server returned a bad status
                    Log.e("API", "Update failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().toString();
                            Log.e("API", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("API", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JobVacancy> call, @NonNull Throwable t) {
                // NETWORK / RUNTIME ERROR
                Log.e("API", "Network error: " + t.getMessage());
                t.fillInStackTrace(); // Better than fillInStackTrace() for logging
            }
        });
    }

    private void deleteJobVacancy(int id){
        api.deleteJobVacancy("eq." + id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "User deleted");
                } else {
                    Log.d("API", "Delete failed: " + response.code());
                }
                loadJobs();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.fillInStackTrace();
                loadJobs();
            }
        });
    }

    private void filterJobs(String query) {
        layoutJobs.removeAllViews();
        for (JobVacancy job : jobs) {
            try {
                String name = job.getJob_title();
                if (name.toLowerCase().contains(query)) {
                    addJobToLayout(job);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAddJobDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Job Vacancy");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_job, null);

        EditText etJobName = view.findViewById(R.id.et_job_name);
        Spinner spnEst = view.findViewById(R.id.spinner_est);
        Spinner spnInd = view.findViewById(R.id.spinner_ind);
        Spinner spinnerStatus = view.findViewById(R.id.spinner_status);
        Spinner spinnerEmpType = view.findViewById(R.id.spinner_empType);
        EditText etReviewedBy = view.findViewById(R.id.et_reviewed_by);
        EditText etSubmissionDate = view.findViewById(R.id.et_submission_date);
        EditText etReviewDate = view.findViewById(R.id.et_review_date);
        // Create a dummy establishment as hint
        Establishment hint = new Establishment();
        Industry hintInd = new Industry();
        hint.setEstablishmentName("Please Select Establishment");
        hintInd.setIndustryName("Please Select Industry");

        // Add hint as the first item
        establishments.add(0, hint);
        industry.add(0, hintInd);

        ArrayAdapter<Establishment> estAdapter = new ArrayAdapter<Establishment>(
                this,
                android.R.layout.simple_spinner_item,
                establishments
        ) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the hint item
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY); // hint color
                } else {
                    tv.setTextColor(Color.WHITE);
                }

                return view;
            }
        };


        ArrayAdapter<Industry> indAdapter = new ArrayAdapter<Industry>(
                this,
                android.R.layout.simple_spinner_item,
                industry
        ) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the hint item
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY); // hint color
                } else {
                    tv.setTextColor(Color.WHITE);
                }

                return view;
            }
        };

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Active", "Closed"});
        ArrayAdapter<String> empTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, empType);
        estAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        empTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        spnEst.setAdapter(estAdapter);
        spnInd.setAdapter(indAdapter);
        spinnerEmpType.setAdapter(empTypeAdapter);

        etSubmissionDate.setOnClickListener(v -> showDatePicker(etSubmissionDate));
        etReviewDate.setOnClickListener(v -> showDatePicker(etReviewDate));

        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String jobName = etJobName.getText().toString().trim();
            String estName = spnEst.getSelectedItem().toString().trim();
            String indName = spnInd.getSelectedItem().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();
            String empTypeName = spinnerEmpType.getSelectedItem().toString();
            String reviewedBy = etReviewedBy.getText().toString().trim();
            String submissionDate = etSubmissionDate.getText().toString().trim();
            String reviewDate = etReviewDate.getText().toString().trim();
            Integer estID = findEstIdByName(establishments, estName);
            Integer indID = findIndIdByName(industry, indName);

            if (jobName.isEmpty() || estName.isEmpty() || indName.isEmpty() || indID == 0 || estID == 0 || submissionDate.isEmpty() || reviewDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
//                return;
            }

            try {
                JobVacancy input = new JobVacancy(
                        currentId,
                        estID,
                        status,
                        "None",
                        null,
                        reviewDate,
                        reviewedBy,
                        jobName,
                        indID,
                        empTypeName,
                        null
                );
                saveJobs(input);
            } catch (Exception e) {
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
        Log.e("API", "My jobEstID" +jobObj.getEstablishment_id());
        String estName = Objects.requireNonNull(findEstablishmentById(establishments, jobObj.getEstablishment_id())).getEstablishmentName();
        String reviewedBy = jobObj.getReviewed_by() != null ? jobObj.getReviewed_by().toString() : "None";
        String submission = jobObj.getCreated_date() != null ? dateStringToFormatString(jobObj.getCreated_date()) : "None";
        String review = jobObj.getReviewed_date() != null ? dateStringToFormatString(jobObj.getReviewed_date()) : "None";

        try {
            String info = jobObj.getJob_title() + " at " + estName
                    + " - " + jobObj.getStatus()
                    + "\nReviewed By: " + reviewedBy
                    + "\nSubmission: " + submission
                    + "\nReview: " + review;

            tvJobInfo.setText(info);
        } catch (Exception e) {
            Log.e("Info", "Info Error " + e.getMessage());
        }

        btnEdit.setOnClickListener(v -> showEditJobDialog(jobObj, tvJobInfo));
        btnDelete.setOnClickListener(v -> {
            deleteJobVacancy(jobObj.getVacancy_id());
            layoutJobs.removeView(itemView);
            jobs.remove(jobObj);
//            saveJobs();
        });

        layoutJobs.addView(itemView);
    }

    private void showEditJobDialog(JobVacancy jobObj, TextView tvJobInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Job Vacancy");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_job, null);

        EditText etJobName = view.findViewById(R.id.et_job_name);
        Spinner spnEst = view.findViewById(R.id.spinner_est);
        Spinner spnInd = view.findViewById(R.id.spinner_ind);
        Spinner spinnerStatus = view.findViewById(R.id.spinner_status);
        Spinner spinnerEmpType = view.findViewById(R.id.spinner_empType);
        EditText etReviewedBy = view.findViewById(R.id.et_reviewed_by);
        EditText etSubmissionDate = view.findViewById(R.id.et_submission_date);
        EditText etReviewDate = view.findViewById(R.id.et_review_date);

        ArrayAdapter<String> estAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, establishments.stream()
                .map(Establishment::getEstablishmentName)
                .collect(Collectors.toList()));
        ArrayAdapter<String> indAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, industry.stream()
                .map(Industry::getIndustry_name)
                .collect(Collectors.toList()));
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Active", "Closed"});
        ArrayAdapter<String> empTypeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, empType);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        estAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        empTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        spnEst.setAdapter(estAdapter);
        spnInd.setAdapter(indAdapter);
        spinnerEmpType.setAdapter(empTypeAdapter);

        try {
            Establishment myEstablishment = findEstablishmentById(establishments, jobObj.getEstablishment_id());
            assert myEstablishment != null;
            spnEst.setSelection(findIndexEst(establishments, jobObj.getEstablishment_id()));
            spnInd.setSelection(findIndexInd(industry, jobObj.getIndustry_id()));
            spinnerEmpType.setSelection(findIndexEmp(empType, jobObj.getEmployment_type()));
            etJobName.setText(jobObj.getJob_title());
//            etReviewedBy.setText(jobObj.getString("reviewedBy"));
            etSubmissionDate.setText(dateStringToFormatString(jobObj.getCreated_date()));
            etReviewDate.setText(dateStringToFormatString(jobObj.getReviewed_date()));
            String status = jobObj.getStatus();
            spinnerStatus.setSelection(status.equalsIgnoreCase("Active") ? 0 : 1);
            etReviewedBy.setText(jobObj.getReviewed_by());
        } catch (Exception e) {
            e.printStackTrace();
        }

        etSubmissionDate.setOnClickListener(v -> showDatePicker(etSubmissionDate));
        etReviewDate.setOnClickListener(v -> showDatePicker(etReviewDate));

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String jobName = etJobName.getText().toString().trim();
            String estName = spnEst.getSelectedItem().toString().trim();
            String indName = spnInd.getSelectedItem().toString().trim();
            String empTypeName = spinnerEmpType.getSelectedItem().toString();
            String status = spinnerStatus.getSelectedItem().toString();
            String reviewedBy = etReviewedBy.getText().toString().trim();
            String submissionDate = etSubmissionDate.getText().toString().trim();
            String reviewDate = etReviewDate.getText().toString().trim();
            int estID = findEstIdByName(establishments, estName);
            int indID = findIndIdByName(industry, indName);

            if (jobName.isEmpty() || estName.isEmpty() || submissionDate.isEmpty() || reviewDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
//                jobObj.put("jobName", jobName);
//                jobObj.put("estName", estName);
//                jobObj.put("status", status);
//                jobObj.put("reviewedBy", reviewedBy);
//                jobObj.put("submissionDate", submissionDate);
//                jobObj.put("reviewDate", reviewDate);
                JobVacancy input = new JobVacancy(
                        currentId,
                        estID,
                        status,
                        "None",
                        null,
                        reviewDate,
                        reviewedBy,
                        jobName,
                        indID,
                        empTypeName,
                        null
                );
                updateJobVacancy(input, jobObj.getVacancy_id());

                String info = jobName + " at " + estName
                        + " - " + status
                        + "\nReviewed By: " + reviewedBy
                        + "\nSubmission: " + submissionDate
                        + " | Review: " + reviewDate;
                tvJobInfo.setText(info);
//                saveJobs();
            } catch (Exception e) {
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

    private Establishment findEstablishmentById(List<Establishment> list, Integer id) {
        for (Establishment e : list) {
            if (Objects.equals(e.getEstablishment_id(), id)) {
                return e;
            }
        }
        return null; // Not found
    }

    private Industry findIndustryById(List<Industry> list, Integer id) {
        for (Industry e : list) {
            if (Objects.equals(e.getIndustry_id(), id)) {
                return e;
            }
        }
        return null; // Not found
    }

    private int findIndexEmp(List<String> list, String name){
        int count = 0;
        int index = 0;
        for (String e : list) {
            if (Objects.equals(e, name)) {
                index = count;
            }
            count++;
        }
        return index;
    }

    private int findIndexEst(List<Establishment> list, Integer id){
        int count = 0;
        int index = 0;
        for (Establishment e : list) {
            if (Objects.equals(e.getEstablishment_id(), id)) {
                index = count;
            }
            count++;
        }
        return index;
    }

    private int findIndexInd(List<Industry> list, Integer id){
        int count = 0;
        int index = 0;
        for (Industry e : list) {
            if (Objects.equals(e.getIndustry_id(), id)) {
                index = count;
            }
            count++;
        }
        return index;
    }

    private int findEstIdByName(List<Establishment> list, String name){
        int estID = 0;
        try{
            for (Establishment e : list) {
                if (e.getEstablishmentName() != null && Objects.equals(e.getEstablishmentName(), name)) {
                    estID = e.getEstablishment_id();
                    Log.e("NameTag", "passed "+ estID );
                }
            }
        }catch(Exception e){
            Log.d("Failed", "Can't Find the ID");
        }
        Log.e("NameTag", "Name: "+ name + "ID: " + estID );
        return estID;
    }
    private int findIndIdByName(List<Industry> list, String name){
        int indID = 0;
        try{
            for (Industry e : list) {
                if (e.getIndustry_name() != null && Objects.equals(e.getIndustry_name(), name)) {
                    indID = e.getIndustry_id();
                    Log.e("NameTag", "passed Industry "+ indID );
                }
            }
        }catch(Exception e){
            Log.d("Failed", "Can't Find the Industry  ID");
        }
        Log.e("NameTag", "Name: "+ name + " Industry ID: " + indID );
        return indID;
    }


    private String dateStringToFormatString(String input){
        try {
            // Extract year, month, day from "2025-11-17T05:20:43.237691+00:00"
            String[] parts = input.split("T")[0].split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];

            return month + "/" + day + "/" + year;
        } catch (Exception e) {
            e.printStackTrace();
            return "None";
        }

    }

}
