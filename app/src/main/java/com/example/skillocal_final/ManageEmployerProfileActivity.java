package com.example.skillocal_final;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageEmployerProfileActivity extends AppCompatActivity {
    ApiService api = ApiInstance.getApi();
    private int currentId;
    private EditText etFirstName, etMiddleName, etLastName, etSuffix, etAddress, etPhone, etBirthday, etEmail;
    private Spinner spinnerSex, spinnerCivilStatus;
    private Button btnSave;
    private ImageView iconBack;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "EmployeeProfilePrefs";
    private static final String KEY_EMPLOYEES = "employees";

    private JSONObject currentEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentId = getSharedPreferences("MyRole", MODE_PRIVATE)
                .getInt("userId", 0 );
        // Make sure the XML name matches this file
        setContentView(R.layout.activity_manage_employer_profile);

        // Initialize fields
        etFirstName = findViewById(R.id.etFirstName);
        etMiddleName = findViewById(R.id.etMiddleName);
        etLastName = findViewById(R.id.etLastName);
        etSuffix = findViewById(R.id.etSuffix);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etBirthday = findViewById(R.id.etBirthday);
        etEmail = findViewById(R.id.etEmail);
        spinnerSex = findViewById(R.id.spinnerSex);
        spinnerCivilStatus = findViewById(R.id.spinnerCivilStatus);
        btnSave = findViewById(R.id.btnSave);
        iconBack = findViewById(R.id.icon_back_manage);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Back button
        iconBack.setOnClickListener(v -> finish());

        // Sex dropdown
        String[] SexOptions = {"Male", "Female"};
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, SexOptions);
        sexAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSex.setAdapter(sexAdapter);

        // Civil Status dropdown
        String[] civilStatusOptions = {"Single", "Married", "Widowed", "Divorced", "Annulled", "Legally Separated"};
        ArrayAdapter<String> civilAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, civilStatusOptions);
        civilAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCivilStatus.setAdapter(civilAdapter);

        // Birthday picker
        etBirthday.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                        etBirthday.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

//        loadLastEmployee();
        loadUser();
        btnSave.setOnClickListener(v -> saveEmployer(currentId));
    }

//    private void loadLastEmployee() {
//        String jsonString = sharedPreferences.getString(KEY_EMPLOYEES, "[]");
//        try {
//            JSONArray arr = new JSONArray(jsonString);
//            if (arr.length() > 0) {
//                currentEmployee = arr.getJSONObject(arr.length() - 1);
//
//                etFirstName.setText(currentEmployee.optString("firstName"));
//                etMiddleName.setText(currentEmployee.optString("middleName"));
//                etLastName.setText(currentEmployee.optString("lastName"));
//                etSuffix.setText(currentEmployee.optString("suffix"));
//                etAddress.setText(currentEmployee.optString("address"));
//                etPhone.setText(currentEmployee.optString("phone"));
//                etBirthday.setText(currentEmployee.optString("birthday"));
//                etEmail.setText(currentEmployee.optString("email"));
//
//                setSpinnerSelection(spinnerSex, currentEmployee.optString("gender"));
//                setSpinnerSelection(spinnerCivilStatus, currentEmployee.optString("civilStatus"));
//            } else {
//                currentEmployee = new JSONObject();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void saveEmployer(Integer userId) {
        String firstName = etFirstName.getText().toString().trim();
        String middleName = etMiddleName.getText().toString().trim();
        String lastName = etLastName.getText().toString();
        String suffix = etSuffix.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String sex = spinnerSex.getSelectedItem().toString();
        String civilStatus = spinnerCivilStatus.getSelectedItem().toString();
        String birthDate = etBirthday.getText().toString().trim();
        String email = etEmail.getText().toString().trim();


        // VALIDATION — dialog stays open
        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty() || birthDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return; // Do NOT dismiss dialog
        }

        // SUCCESS — now allow closing
        User user = new User(
                email,
                firstName,
                middleName,
                lastName,
                suffix,
                birthDate,
                sex,
                civilStatus,
                address,
                phone
        );

        updateUser(user, userId);
    }

    private void loadUser() {
        //fetch details of user based on user_id from db
        api.getUserByUserId(
                "*",                     // select all columns
                "eq." + currentId         // Supabase filter
        ).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> resList = response.body();

                    assert resList != null;
                    for (User e : resList) {
                        Log.d("API", "First Name: " + e.getFName());
                        if (e.getFName() != null) {
                            etFirstName.setText(e.getFName());
                        }
                        if (e.getMName() != null) {
                            etMiddleName.setText(e.getMName());
                        }
                        if (e.getLName() != null) {
                            etLastName.setText(e.getLName());
                        }
                        if (e.getSuffix() != null) {
                            etSuffix.setText(e.getSuffix());
                        }
                        if (e.getAddress() != null) {
                            etAddress.setText(e.getAddress());
                        }

                        if (e.getContactNumber() != null) {
                            etPhone.setText(String.valueOf(e.getContactNumber()));
                        }

                        // Sex options
                        String[] sexOptions = {"Male", "Female"};

                        // Civil Status options
                        String[] civilStatusOptions = {"Single", "Married", "Widowed", "Divorced", "Annulled", "Legally Separated"};

                        if (e.getSex() != null) {
                            int index = -1;
                            for (int i = 0; i < sexOptions.length; i++) {
                                if (sexOptions[i].equalsIgnoreCase(e.getSex())) {
                                    index = i;
                                    break;
                                }
                            }

                            // If found, set selection
                            if (index != -1) {
                                spinnerSex.setSelection(index);
                            }
                        }

                        if (e.getCivilStatus() != null) {
                            int index = -1;
                            for (int i = 0; i < civilStatusOptions.length; i++) {
                                if (civilStatusOptions[i].equalsIgnoreCase(e.getCivilStatus())) {
                                    index = i;
                                    break;
                                }
                            }

                            // If found, set selection
                            if (index != -1) {
                                spinnerCivilStatus.setSelection(index);
                            }
                        }

                        if (e.getBirthDate() != null) {
                            etBirthday.setText(e.getBirthDate());
                        }
                        if (e.getEmail() != null) {
                            etEmail.setText(e.getEmail());
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.e("API", "Failed: " + t.getMessage());
            }
        });
    }

    private void updateUser(User user, Integer userId){

        String idFilter = "eq." + userId;

        api.updateUser(idFilter, user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageEmployerProfileActivity.this, "Employer Profile Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ManageEmployerProfileActivity.this, "Update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("API", "Update failed: " + response.code());

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageEmployerProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API", "Error: ", t);
            }
        });
    }
}
