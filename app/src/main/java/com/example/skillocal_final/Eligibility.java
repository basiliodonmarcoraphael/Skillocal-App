package com.example.skillocal_final;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.io.Serializable;
import java.util.Calendar;

// class or type for Work Experience
public class Eligibility implements Serializable {
    private Integer eligibility_id;
    private Integer user_id;
    private String name;
    private String license_number;
    private String date_taken;
    private String validity_date;

    public Eligibility() {} // required for Retrofit

    // constructor for insert
    public Eligibility(Integer user_id,
                       String name, String license_number, String date_taken,
                       String validity_date)
    {
        this.user_id = user_id;
        this.name = name;
        this.license_number = license_number;
        this.date_taken = date_taken;
        this.validity_date = validity_date;
    }

    public Integer getEligibilityId(){return eligibility_id;}
    public Integer getUserId(){return user_id;}
    public String getName(){return name;}
    public String getLicenseNumber(){return license_number;}
    public String getDateTaken(){return date_taken;}
    public String getValidityDate(){return validity_date;}

    // ----------------- DatePicker Helper -----------------
    // Use this method in your Activity to show a calendar
    public static void showDatePicker(Context context, EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year1, month1, dayOfMonth) -> {
                    // Format date as YYYY-MM-DD
                    String date = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    editText.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
