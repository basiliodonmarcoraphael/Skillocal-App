package com.example.skillocal_final;

public class Establishment {
    private final int establishment_id;
    private final String establishmentName;
    private final String email;
    private final String industryType;
    private final String contactPerson;
    private final String contactNumber;
    private final String address;
    private final String status;
    private final String createdAt;
    private final String modifiedAt;
    private final String user_id;

    public Establishment(int establishment_id, String establishmentName, String email,
                         String industryType, String contactPerson, String contactNumber,
                         String address, String status, String createdAt, String modifiedAt,
                         String user_id)
    {
        this.establishment_id = establishment_id;
        this.establishmentName = establishmentName;
        this.email = email;
        this.industryType = industryType;
        this.contactPerson = contactPerson;
        this.contactNumber = contactNumber;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.user_id = user_id;
    }

    public int getEstablishment_id(){return  establishment_id;}
    public String getEstablishmentName(){return  establishmentName;}
    public String getEmailInEstablishment(){return email;}
    public String getIndustryType(){return industryType;}
    public String getContactPerson(){return contactPerson;}
    public String getContactNumber(){return contactNumber;}
    public String getAddress(){return address;}
    public String getStatus(){return status;}
    public String getCreatedAt(){return createdAt;}
    public String getModifiedAt(){return modifiedAt;}
    public String getUser_id(){return user_id;}
}
