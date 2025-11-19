package com.example.skillocal_final;

public class User {

    private final int user_id;
    private final String email;
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final String suffix;
    private final String role;
    private final String status;
    private final String created_at;
    private final String password;
    private final String modifiedAt;

    public User(int user_id, String email, String firstName,
                String middleName, String lastName, String suffix, String role,String status,
                String created_at, String password, String modifiedAt)
    {
        this.user_id = user_id;
        this.email = email;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.suffix = suffix;
        this.role = role;
        this.status = status;
        this.created_at = created_at;
        this.password = password;
        this.modifiedAt = modifiedAt;
    }

    public int getUserId(){return user_id;}
    public String getEmail(){return email;}
    public String getFName(){ return firstName;}
    public String getMName(){return middleName;}
    public String getLName(){return  lastName;}
    public String getSuffix(){return suffix;}
    public String getRole(){return role;}
    public String getStatus(){return status;}
    public String getCreated_at(){return created_at;}
    public String getPassword(){return password;}
    public String getModifiedAt(){return modifiedAt;}
}
