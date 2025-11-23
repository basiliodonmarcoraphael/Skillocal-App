package com.example.skillocal_final;

import java.io.Serializable;

//class or type for Work Experience
public class Training implements Serializable {
    private Integer training_id;
    private Integer user_id;
    private String name;
    private Integer hours;
    private String institution;
    private String skills_acquired;

    public Training() {} // required for Retrofit

    // constructor for insert
    public Training(Integer user_id,
                    String name, Integer hours, String institution,
                    String skills_acquired)
    {
        this.user_id = user_id;
        this.name = name;
        this.hours = hours;
        this.institution = institution;
        this.skills_acquired = skills_acquired;

    }

    public Integer getTrainingId(){return training_id;}
    public Integer getUserId(){return user_id;}
    public String getName(){return name;}
    public Integer getHours(){return hours;}
    public String getInstitution(){return institution;}
    public String getSkillsAcquired(){return skills_acquired;}


}
