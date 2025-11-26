package com.example.skillocal_final;

public class JobVacancy {
    private Integer vacancy_id;
    private Integer user_id;
    private Integer establishment_id;
    private String status;
    private String remarks;
    private String created_date;
    private String reviewed_date;
    private String reviewed_by;
    private String job_title;
    private Integer industry_id;
    private String employment_type;
    private String location;

    public JobVacancy(){} //for retrofit

    public JobVacancy(Integer user_id, Integer establishment_id, String status, String remarks,
                      String created_date, String reviewed_date, String reviewed_by,
                      String job_title, Integer industry_id, String employment_type,
                      String location)
    {
        this.user_id = user_id;
        this.establishment_id = establishment_id;
        this.status = status;
        this.remarks = remarks;
        this.created_date = created_date;
        this.reviewed_date = reviewed_date;
        this.reviewed_by = reviewed_by;
        this.job_title = job_title;
        this.industry_id = industry_id;
        this.employment_type = employment_type;
        this.location = location;
    }

    public Integer getVacancy_id(){return vacancy_id;}
    public Integer getUser_id(){return user_id;}
    public Integer getEstablishment_id(){return establishment_id;}
    public String getStatus(){return status;}
    public String getRemarks(){return remarks;}
    public String getCreated_date(){return created_date;}
    public String getReviewed_date(){return reviewed_date;}
    public String getReviewed_by(){return reviewed_by;}
    public String getJob_title(){return job_title;}
    public Integer getIndustry_id(){return industry_id;}
    public String getEmployment_type(){return employment_type;}
    public String getLocation(){return location;}
}
