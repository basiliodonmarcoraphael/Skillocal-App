package com.example.skillocal_final;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstablishmentsActivity extends AppCompatActivity {

    private LinearLayout layoutEstablishments;
    private List<Establishment> establishments;
    private SharedPreferences sharedPreferences;
    private String currentUserEmail;

    private static final String PREFS_NAME = "SkillocalPrefs";
    private static final String KEY_ESTABLISHMENTS = "establishments_per_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void loadEstablishments() {
        //New
        ApiService api = ApiInstance.getApi();

        api.getAllEstablishment("*").enqueue(new Callback<List<Establishment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Establishment>> call, @NonNull Response<List<Establishment>> response) {
                if (response.isSuccessful()) {
                    List<Establishment> resList = response.body();
                    layoutEstablishments.removeAllViews();
                    establishments.clear();

                    assert resList != null;
                    for (Establishment e : resList) {
                        Log.d("API", "User: " + e.getAddress());
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

    private void showAddEstablishmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Establishment");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_establishment, null);

        EditText etName = view.findViewById(R.id.et_est_name);
        EditText etTotalEmployees = view.findViewById(R.id.et_total_employees);
        Spinner spinnerStatus = view.findViewById(R.id.spinner_status);
        EditText etRegDate = view.findViewById(R.id.et_reg_date);

        // Status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Active", "Closed"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

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

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String totalEmp = etTotalEmployees.getText().toString().trim();
            String status = spinnerStatus.getSelectedItem().toString();
            String regDate = etRegDate.getText().toString().trim();

            if (name.isEmpty() || totalEmp.isEmpty() || regDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject estObj = new JSONObject();
            try {
                estObj.put("name", name);
                estObj.put("totalEmployees", totalEmp);
                estObj.put("status", status);
                estObj.put("regDate", regDate);

//                establishments.add(estObj);
//                addEstablishmentToLayout(estObj);
                saveEstablishments();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
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
            layoutEstablishments.removeView(itemView);
            establishments.remove(estObj);
            saveEstablishments();
        });

        layoutEstablishments.addView(itemView);
    }

    private void showEditEstablishmentDialog(Establishment estObj, TextView tvName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Establishment");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_establishment, null);

        EditText etName = view.findViewById(R.id.et_est_name);
        EditText etTotalEmployees = view.findViewById(R.id.et_total_employees);
        Spinner spinnerStatus = view.findViewById(R.id.spinner_status);
        EditText etRegDate = view.findViewById(R.id.et_reg_date);

        // Setup status spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Active", "Closed"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Fill current values
        etName.setText(estObj.getEstablishmentName());
        String status = estObj.getStatus();
        spinnerStatus.setSelection(status.equalsIgnoreCase("Active") ? 0 : 1);

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

//            try {
//                estObj.put("name", name);
//                estObj.put("totalEmployees", totalEmp);
//                estObj.put("status", status);
//                estObj.put("regDate", regDate);
//
//                tvName.setText(name + " (" + totalEmp + " employees) - " + status + " - " + regDate);
//                saveEstablishments();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
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
