package com.example.skillocal_final;

public class JobVacancy {
    private Integer vacancy_id;
    private Integer establishment_id;
    private String status;
    private String remarks;
    private String created_date;
    private String reviewed_date;
    private Integer reviewed_by;
    private String job_title;

    public JobVacancy(){} //for retrofit

    public JobVacancy(Integer establishment_id, String status, String remarks,
                      String created_date, String reviewed_date, Integer reviewed_by,
                      String job_title)
    {
        this.establishment_id = establishment_id;
        this.status = status;
        this.remarks = remarks;
        this.created_date = created_date;
        this.reviewed_date = reviewed_date;
        this.reviewed_by = reviewed_by;
        this.job_title = job_title;
    }

    public Integer getVacancy_id(){return vacancy_id;}

    public Integer getEstablishment_id(){return establishment_id;}
    public String getStatus(){return status;}
    public String getRemarks(){return remarks;}
    public String getCreated_date(){return created_date;}
    public String getReviewed_date(){return reviewed_date;}
    public Integer getReviewed_by(){return reviewed_by;}
    public String getJob_title(){return job_title;}
}
