package com.example.skillocal_final;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class JobMatchingActivity extends AppCompatActivity {

    private LinearLayout layoutMatching;
    private ArrayList<JSONObject> matches;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;

    private static final String PREFS_NAME = "SkillocalPrefs";
    private static final String KEY_MATCHES = "matches_per_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_matching);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_matching);
        setSupportActionBar(toolbar);

        ImageView backIcon = findViewById(R.id.icon_back_matching);
        backIcon.setOnClickListener(v -> finish());
        
        layoutMatching = findViewById(R.id.layout_matching);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        currentUserEmail = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getString("email", "guest@user.com");

        matches = new ArrayList<>();
        loadMatches();

        // Search
        EditText etSearch = findViewById(R.id.et_search_matching);
        ImageView searchIcon = findViewById(R.id.search_icon_matching);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                filterMatches(s.toString().toLowerCase());
            }
        });

        searchIcon.setOnClickListener(v -> {
            String query = etSearch.getText().toString().toLowerCase();
            filterMatches(query);
        });
    }

    private void loadMatches() {
        if (layoutMatching == null) return;

        layoutMatching.removeAllViews();
        matches.clear();

        String jsonString = sharedPreferences.getString(KEY_MATCHES, "{}");
        try {
            JSONObject json = new JSONObject(jsonString);

            if (json.has(currentUserEmail)) {
                JSONArray arr = json.getJSONArray(currentUserEmail);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject match = arr.getJSONObject(i);
                    matches.add(match);
                    addMatchToLayout(match);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveMatches() {
        JSONArray userArray = new JSONArray(matches);

        try {
            String jsonString = sharedPreferences.getString(KEY_MATCHES, "{}");
            JSONObject json = new JSONObject(jsonString);

            json.put(currentUserEmail, userArray);
            sharedPreferences.edit().putString(KEY_MATCHES, json.toString()).apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void filterMatches(String query) {
        layoutMatching.removeAllViews();

        for (JSONObject match : matches) {
            try {
                if (match.getString("vacancyId").toLowerCase().contains(query)
                        || match.getString("jobName").toLowerCase().contains(query)
                        || match.getString("estName").toLowerCase().contains(query)
                        || match.getString("applicantName").toLowerCase().contains(query)) {

                    addMatchToLayout(match);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAddMatchingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Job Matching");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_matching, null);

        EditText etVacancyId = view.findViewById(R.id.et_vacancy_id);
        EditText etJobName = view.findViewById(R.id.et_job_name);
        EditText etEstName = view.findViewById(R.id.et_est_name);
        EditText etApplicantName = view.findViewById(R.id.et_applicant_name);
        EditText etSubmissionDate = view.findViewById(R.id.et_submission_date);

        etSubmissionDate.setOnClickListener(v -> showDatePicker(etSubmissionDate));

        builder.setView(view);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String id = etVacancyId.getText().toString().trim();
            String jobName = etJobName.getText().toString().trim();
            String estName = etEstName.getText().toString().trim();
            String applicant = etApplicantName.getText().toString().trim();
            String submission = etSubmissionDate.getText().toString().trim();

            if (id.isEmpty() || jobName.isEmpty() || estName.isEmpty() || applicant.isEmpty() || submission.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject matchObj = new JSONObject();
                matchObj.put("vacancyId", id);
                matchObj.put("jobName", jobName);
                matchObj.put("estName", estName);
                matchObj.put("applicantName", applicant);
                matchObj.put("submissionDate", submission);

                matches.add(matchObj);
                addMatchToLayout(matchObj);
                saveMatches();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void addMatchToLayout(JSONObject matchObj) {
        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_matching, layoutMatching, false);

        TextView tvInfo = itemView.findViewById(R.id.tv_match_info);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit_matching);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete_matching);

        try {
            String info = "Vacancy ID: " + matchObj.getString("vacancyId")
                    + "\nJob: " + matchObj.getString("jobName")
                    + " at " + matchObj.getString("estName")
                    + "\nApplicant: " + matchObj.getString("applicantName")
                    + "\nSubmitted: " + matchObj.getString("submissionDate");

            tvInfo.setText(info);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnEdit.setOnClickListener(v -> showEditMatchingDialog(matchObj, tvInfo));
        btnDelete.setOnClickListener(v -> {
            layoutMatching.removeView(itemView);
            matches.remove(matchObj);
            saveMatches();
        });

        layoutMatching.addView(itemView);
    }

    private void showEditMatchingDialog(JSONObject matchObj, TextView tvInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Job Matching");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_matching, null);

        EditText etVacancyId = view.findViewById(R.id.et_vacancy_id);
        EditText etJobName = view.findViewById(R.id.et_job_name);
        EditText etEstName = view.findViewById(R.id.et_est_name);
        EditText etApplicantName = view.findViewById(R.id.et_applicant_name);
        EditText etSubmissionDate = view.findViewById(R.id.et_submission_date);

        etSubmissionDate.setOnClickListener(v -> showDatePicker(etSubmissionDate));

        try {
            etVacancyId.setText(matchObj.getString("vacancyId"));
            etJobName.setText(matchObj.getString("jobName"));
            etEstName.setText(matchObj.getString("estName"));
            etApplicantName.setText(matchObj.getString("applicantName"));
            etSubmissionDate.setText(matchObj.getString("submissionDate"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            try {
                String id = etVacancyId.getText().toString();
                String job = etJobName.getText().toString();
                String est = etEstName.getText().toString();
                String applicant = etApplicantName.getText().toString();
                String submission = etSubmissionDate.getText().toString();

                matchObj.put("vacancyId", id);
                matchObj.put("jobName", job);
                matchObj.put("estName", est);
                matchObj.put("applicantName", applicant);
                matchObj.put("submissionDate", submission);

                tvInfo.setText("Vacancy ID: " + id +
                        "\nJob: " + job +
                        " at " + est +
                        "\nApplicant: " + applicant +
                        "\nSubmitted: " + submission);

                saveMatches();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) ->
                        editText.setText((month + 1) + "/" + day + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }
}
