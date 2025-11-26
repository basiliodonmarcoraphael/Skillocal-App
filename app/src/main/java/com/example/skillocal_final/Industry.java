package com.example.skillocal_final;

import androidx.annotation.NonNull;

public class Industry {
    private int industry_id;
    private String industry_key_tag;
    private String industry_name;
    private String description;
    private String createdAt;
    private String modifiedAt;
    private Integer user_id;

    public Industry(){}

    public Industry(String industry_key_tag, String industry_name, String description,
                    String createdAt, String modifiedAt, Integer user_id){
        this.industry_key_tag = industry_key_tag;
        this.industry_name = industry_name;
        this.description = description;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public String toString() {
        return industry_name;
    }

    public int getIndustry_id(){return industry_id;}
    public String getIndustry_key_tag(){return industry_key_tag;}
    public String getIndustry_name(){return industry_name;}
    public String getDescription(){return description;}
    public String getCreatedAt(){return createdAt;}
    public String getModifiedAt(){return modifiedAt;}
    public Integer getUser_id(){return user_id;}

    public void setIndustryName(String input){
        this.industry_name = input;
    }
}
