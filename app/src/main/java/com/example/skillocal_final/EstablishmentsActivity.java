package com.example.skillocal_final;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstablishmentsActivity extends AppCompatActivity {
    ApiService api = ApiInstance.getApi();

    private LinearLayout layoutEstablishments;
    private List<Establishment> establishments;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;
    private int currentId;

    private static final String PREFS_NAME = "SkillocalPrefs";
    private static final String KEY_ESTABLISHMENTS = "establishments_per_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );
        setContentView(R.layout.activity_establishments);

        Toolbar toolbar = findViewById(R.id.toolbar_est);
        setSupportActionBar(toolbar);

        ImageView backIcon = findViewById(R.id.icon_menu_est);
        backIcon.setOnClickListener(v -> finish());

        layoutEstablishments = findViewById(R.id.layout_establishments);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add_establishment);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        currentUserEmail = userPrefs.getString("email", "guest@user.com");

        establishments = new ArrayList<>();
        loadEstablishments();

        fabAdd.setOnClickListener(v -> showAddEstablishmentDialog());
    }

    private void updateEstablishment(Establishment est, Integer id) {
        api.updateEstablishment("eq." + id.toString(), est).enqueue(new Callback<Establishment>() {
            @Override
            public void onResponse(@NonNull Call<Establishment> call, @NonNull Response<Establishment> response) {
                if (response.isSuccessful()) {
                    Establishment created = response.body();

                    // REPLACE THE ASSERTION WITH PROPER NULL CHECK
                    if (created != null) {
                        // SUCCESS — the row was inserted
                        Log.d("API", "Updated: " + created.getEstablishmentName());
                        loadEstablishments();
                    } else {
                        // Handle null response body
                        Log.e("API", "Update successful but response body is null");
                        // You might still want to refresh the list if update was successful
                        loadEstablishments();
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
            public void onFailure(@NonNull Call<Establishment> call, @NonNull Throwable t) {
                // NETWORK / RUNTIME ERROR
                Log.e("API", "Network error: " + t.getMessage());
                t.fillInStackTrace(); // Better than fillInStackTrace() for logging
            }
        });
    }

//    private void deleteEstablishment(Integer id, Context cont, String name){
//        api.deleteEstablishment("eq." + id).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d("API", "User deleted");
//                } else {
//                    Log.d("API", "Delete failed: " + response.code());
//                    Toast.makeText(cont, "This "+name+" is in used. Can't be deleted.", Toast.LENGTH_SHORT).show();
//                }
//                loadEstablishments();
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
//                t.fillInStackTrace();
//                Toast.makeText(cont, "This Establishment is in used. Can't be deleted.", Toast.LENGTH_SHORT).show();
//                loadEstablishments();
//            }
//        });
//    }

    private void deleteEstablishment(Establishment est, Integer id) {
        api.updateEstablishment("eq." + id, est).enqueue(new Callback<Establishment>() {
            @Override
            public void onResponse(@NonNull Call<Establishment> call, @NonNull Response<Establishment> response) {
                if (response.isSuccessful()) {
                    Establishment created = response.body();

                    // REPLACE THE ASSERTION WITH PROPER NULL CHECK
                    if (created != null) {
                        // SUCCESS — the row was inserted
                        Log.d("API", "Updated: " + created.getEstablishmentName());
                        loadEstablishments();
                    } else {
                        // Handle null response body
                        Log.e("API", "Update successful but response body is null");
                        // You might still want to refresh the list if update was successful
                        loadEstablishments();
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
            public void onFailure(@NonNull Call<Establishment> call, @NonNull Throwable t) {
                // NETWORK / RUNTIME ERROR
                Log.e("API", "Network error: " + t.getMessage());
                t.fillInStackTrace(); // Better than fillInStackTrace() for logging
            }
        });
    }








    private void insertEstablishment(Establishment est){

        Log.d("API", "Entry: " + est.getEstablishment_id());
        api.insertEstablishment(est).enqueue(new Callback<Establishment>() {
            @Override
            public void onResponse(@NonNull Call<Establishment> call, @NonNull Response<Establishment> response) {
                if (response.isSuccessful()) {
                    Establishment created = response.body();
                    // SUCCESS — the row was inserted
                    assert created != null;
                    Log.d("API", "Inserted: " + created.getEstablishmentName());
                    loadEstablishments();
                } else {
                    // ERROR — the server returned a bad status
                    Log.e("API", "Insert failed: " + response.code());
                    Log.e("API", "Error body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Establishment> call, @NonNull Throwable t) {
                // NETWORK / RUNTIME ERROR
                t.fillInStackTrace();
                Log.e("API", "Network error: " + t.getMessage());
                loadEstablishments();
            }
        });
    }



    private void loadEstablishments() {
        // CLEAR UI BEFORE LOADING — this forces redraw
        layoutEstablishments.removeAllViews();
        establishments.clear();

        api.getEstablishmentByUserId(
                "*",                     // select all columns
                "eq." + currentId,
                "not.eq.Deleted",  // Supabase filter
                "establishment_id.asc"     // order by
        ).enqueue(new Callback<List<Establishment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Establishment>> call, @NonNull Response<List<Establishment>> response) {
                if (response.isSuccessful()) {
                    List<Establishment> resList = response.body();
                    layoutEstablishments.removeAllViews();
                    establishments.clear();

                    assert resList != null;
                    for (Establishment e : resList) {
//                        Log.d("API", "User: " + e.getAddress());
                        establishments.add(e);
                        addEstablishmentToLayout(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Establishment>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void saveEstablishments() {
        JSONArray userArray = new JSONArray(establishments);
        try {
            String jsonString = sharedPreferences.getString(KEY_ESTABLISHMENTS, "{}");
            JSONObject json = new JSONObject(jsonString);
            json.put(currentUserEmail, userArray);
            sharedPreferences.edit().putString(KEY_ESTABLISHMENTS, json.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            return null;
        }

    }

    private void showAddEstablishmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Establishment");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_establishment, null);
        builder.setView(view);

        EditText etName = view.findViewById(R.id.et_est_name);
        EditText etTotalEmployees = view.findViewById(R.id.et_total_employees);
        Spinner spinnerStatus = view.findViewById(R.id.spinner_status);
        EditText etRegDate = view.findViewById(R.id.et_reg_date);
        EditText txtIndType = view.findViewById(R.id.et_industry_type);
        EditText txtEmail = view.findViewById(R.id.et_email_est);
        EditText txtContactP = view.findViewById(R.id.et_contact_person_est);
        EditText txtContactN = view.findViewById(R.id.et_contact_number_est);
        EditText txtAddress = view.findViewById(R.id.et_address_est);

        // Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Active", "Pending"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Date picker
        etRegDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(this,
                    (view1, y, m, d) -> etRegDate.setText((m + 1) + "/" + d + "/" + y),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH));
            dp.show();
        });

        // Set buttons first — BUT do not attach logic here!
        builder.setPositiveButton("Add", null);
        builder.setNegativeButton("Cancel", (dialog, w) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnShowListener(d -> {

            // Override the positive button
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                String name = etName.getText().toString().trim();
                String totalEmp = etTotalEmployees.getText().toString().trim();
                String status = spinnerStatus.getSelectedItem().toString();
                String regDate = etRegDate.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String indType = txtIndType.getText().toString().trim();
                String contactP = txtContactP.getText().toString().trim();
                String contactN = txtContactN.getText().toString().trim();
                String address = txtAddress.getText().toString().trim();

                // VALIDATION — dialog stays open
                if (name.isEmpty() || totalEmp.isEmpty() || regDate.isEmpty()) {
                    Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    return; // Do NOT dismiss dialog
                }

                // SUCCESS — now allow closing
                Establishment est = new Establishment(
                        name,
                        email,
                        indType,
                        contactP,
                        contactN,
                        address,
                        status,
                        null,
                        currentId, //userId current account
                        Integer.parseInt(totalEmp),
                        null
                );

                insertEstablishment(est);

                dialog.dismiss(); // Only close AFTER success
            });
        });

        dialog.show();
    }


    private void addEstablishmentToLayout(Establishment estObj) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_establishment, layoutEstablishments, false);

        TextView tvName = itemView.findViewById(R.id.tv_est_name);
        ImageView btnEdit = itemView.findViewById(R.id.btn_edit_est);
        ImageView btnDelete = itemView.findViewById(R.id.btn_delete_est);

        String name = estObj.getEstablishmentName();

        tvName.setText(name);

        btnEdit.setOnClickListener(v -> showEditEstablishmentDialog(estObj, tvName));
        btnDelete.setOnClickListener(v -> {

            String deletedStatus = "Deleted";
            Integer idToDelete = estObj.getEstablishment_id();
            String eName = estObj.getEstablishmentName();
            String email = estObj.getEmailInEstablishment();
            String industry = estObj.getIndustryType();
            String contactP = estObj.getContactPerson();
            String contactN = estObj.getContactNumber();
            String address = estObj.getAddress();
            Integer total_employee = estObj.getTotal_employee();

            Establishment est = new Establishment(
                    eName,
                    email,
                    industry,
                    contactP,
                    contactN,
                    address,
                    deletedStatus,
                    null,
                    currentId,
                    total_employee,
                    null
            );

            deleteEstablishment(est, idToDelete);
//            layoutEstablishments.removeView(itemView);
//            establishments.remove(estObj);
//            saveEstablishments();
            loadEstablishments();
        });

        layoutEstablishments.addView(itemView);
    }

    @SuppressLint("SetTextI18n")
    private void showEditEstablishmentDialog(Establishment estObj, TextView tvName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Establishment");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_establishment, null);

        EditText etName = view.findViewById(R.id.et_est_name);
        EditText etTotalEmployees = view.findViewById(R.id.et_total_employees);
        Spinner spinnerStatus = view.findViewById(R.id.spinner_status);
        EditText etRegDate = view.findViewById(R.id.et_reg_date);
        EditText txtIndType = view.findViewById(R.id.et_industry_type);
        EditText txtEmail = view.findViewById(R.id.et_email_est);
        EditText txtContactP = view.findViewById(R.id.et_contact_person_est);
        EditText txtContactN = view.findViewById(R.id.et_contact_number_est);
        EditText txtAddress = view.findViewById(R.id.et_address_est);

        // Setup status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Active", "Pending"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Fill current values
        etName.setText(estObj.getEstablishmentName());
        etTotalEmployees.setText(estObj.getTotal_employee().toString());
        spinnerStatus.setSelection(estObj.getStatus().equalsIgnoreCase("Active") ? 0 : 1);
        etRegDate.setText(dateStringToFormatString(estObj.getCreatedAt()));
        txtIndType.setText(estObj.getIndustryType());
        txtEmail.setText(estObj.getEmailInEstablishment());
        txtContactP.setText(estObj.getContactPerson());
        txtContactN.setText(estObj.getContactNumber());
        txtAddress.setText(estObj.getAddress());

        // Registration date picker
        etRegDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        etRegDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String totalEmp = etTotalEmployees.getText().toString().trim();
//            String status = spinnerStatus.getSelectedItem().toString();
            String regDate = etRegDate.getText().toString().trim();

            if (name.isEmpty() || totalEmp.isEmpty() || regDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = txtEmail.getText().toString().trim();
            String indType = txtIndType.getText().toString().trim();
            String contactP = txtContactP.getText().toString().trim();
            String contactN = txtContactN.getText().toString().trim();
            String address = txtAddress.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString().trim();

            // SUCCESS — now allow closing
            Establishment est = new Establishment(
                    name,
                    email,
                    indType,
                    contactP,
                    contactN,
                    address,
                    status,
                    new Date().toString(),
                    currentId,
                    Integer.parseInt(totalEmp),
                    regDate
            );
            updateEstablishment(est, estObj.getEstablishment_id());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
