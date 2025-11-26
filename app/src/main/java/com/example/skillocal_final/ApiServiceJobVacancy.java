package com.example.skillocal_final;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServiceJobVacancy {

    //This is for JobVacancy Table
    @GET("JobVacancy")
    Call<List<JobVacancy>> getAllJobVacancy(
            @Query("select") String select
    );

    @GET("JobVacancy")
    Call<List<JobVacancy>> getAllJobVacancyByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter,
            @Query("order") String order
    );

    @POST("JobVacancy")
    Call<JobVacancy> insertJobVacancy(@Body JobVacancy jobVacancy);

    // UPDATE JobVacancy - Use PATCH for partial updates
    @PATCH("JobVacancy")
    Call<JobVacancy> updateJobVacancy(
            @Query("vacancy_id") String eqFilter,  // example: "eq.123"
            @Body JobVacancy jobVacancy
    );

    @DELETE("JobVacancy")
    Call<Void> deleteJobVacancy(
            @Query("vacancy_id") String eqFilter // example: "eq.55"
    );
}
