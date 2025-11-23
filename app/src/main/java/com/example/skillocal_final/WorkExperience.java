package com.example.skillocal_final;
import java.io.Serializable;
//class or type for Work Experience
public class WorkExperience implements Serializable {
    private Integer work_experience_id;
    private Integer user_id;
    private String company;
    private String address;
    private Integer month_count;
    private String employment_status;
    private String position;

    public WorkExperience() {} // required for Retrofit

    // constructor for insert
    public WorkExperience(Integer user_id,
                          String company, String address, Integer month_count,
                          String employment_status, String position)
    {
//        this.work_experience_id = work_experience_id;
        this.user_id = user_id;
        this.company = company;
        this.address = address;
        this.month_count = month_count;
        this.employment_status = employment_status;
        this.position = position;
    }

    public Integer getWorkExperienceId(){return  work_experience_id;}
    public Integer getUserId(){return user_id;}
    public String getCompany(){return company;}

    public String getAddress(){return address;}
    public Integer getMonthCount(){return month_count;}
    public String getEmploymentStatus(){return employment_status;}
    public String getPosition(){return position;}

}
