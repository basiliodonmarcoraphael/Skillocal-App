package com.example.skillocal_final;

public class User {

    private Integer user_id;

    private String role;
    private String email;
    private String firstName;
    private String middleName;
    private  String lastName;
    private  String suffix;
    private  String status;
    private  String birthDate;

    private  String sex;
    private  String civilStatus;
    private  String address;
    private  String contact_number;




    public User(
            String email,
            String firstName,
            String middleName, String lastName, String suffix,
            String birthDate, String sex, String civilStatus, String address, String contact_number)
    {

//        this.user_id = user_id;
        this.email = email;

        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.status = status;
        this.birthDate = birthDate;
        this.sex = sex;
        this.civilStatus = civilStatus;
        this.address = address;
        this.contact_number = contact_number;

    }

    public Integer getUserId(){return user_id;}
    public String getEmail(){return email;}

    public String getFName(){ return firstName;}
    public String getMName(){return middleName;}
    public String getLName(){return  lastName;}
    public String getSuffix(){return suffix;}

    public String getStatus(){return status;}
    public String getBirthDate(){return birthDate;}

    public String getSex(){return sex;}
    public String getCivilStatus(){return civilStatus;}

    public String getAddress(){return address;}
    public String getContactNumber(){return contact_number;}

    public String getRole(){return role;}

}
