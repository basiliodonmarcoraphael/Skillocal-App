package com.example.skillocal_final;
//class or type for Work Experience
public class Eligibility {
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

    public Integer getEligibilityId(){return  eligibility_id;}
    public Integer getUserId(){return user_id;}
    public String getName(){return name;}
    public String getLicenseNumber(){return license_number;}
    public String getDateTaken(){return date_taken;}
    public String getValidity_date(){return validity_date;}


}
