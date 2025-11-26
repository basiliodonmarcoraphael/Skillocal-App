package com.example.skillocal_final;

import androidx.annotation.NonNull;

public class Establishment {
    private Integer establishment_id;
    private String establishmentName;
    private String email;
    private String industryType;
    private String contactPerson;
    private String contactNumber;
    private String address;
    private String status;
    private String createdAt;
    private String modifiedAt;
    private Integer user_id;
    private Integer total_employee;

    public Establishment() {} // required for Retrofit

    // constructor for insert
    public Establishment(String establishmentName, String email,
                         String industryType, String contactPerson, String contactNumber,
                         String address, String status, String modifiedAt,
                         Integer user_id, Integer total_employee, String createdAt)
    {
        this.establishmentName = establishmentName;
        this.email = email;
        this.industryType = industryType;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.address = address;
        this.status = status;
        this.modifiedAt = modifiedAt;
        this.user_id = user_id;
        this.total_employee = total_employee;
        this.createdAt = createdAt;
    }

    public Integer getEstablishment_id(){return  establishment_id;}
    public String getEstablishmentName(){return  establishmentName;}
    public String getEmailInEstablishment(){return email;}
    public String getIndustryType(){return industryType;}
    public String getContactPerson(){return contactPerson;}
    public String getContactNumber(){return contactNumber;}
    public String getAddress(){return address;}
    public String getStatus(){return status;}
    public String getCreatedAt(){return createdAt;}
    public String getModifiedAt(){return modifiedAt;}
    public Integer getUser_id(){return user_id;}
    public Integer getTotal_employee(){return total_employee;}

    @NonNull
    @Override
    public String toString() {
        return establishmentName; // or getEstablishmentName()
    }

    public void setEstablishmentName(String input){
        this.establishmentName = input;
    }
}
