package com.example.skillocal_final;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServiceWorker {

    //This is for WorkExperience Table
    @GET("WorkExperience")
    Call<List<WorkExperience>> getAllWorkExperience(
            @Query("select") String select
    );

    @GET("WorkExperience")
    Call<List<WorkExperience>> getWorkExperienceByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter
    );

    @POST("WorkExperience")
    Call<List<WorkExperience>> insertWorkExperience(
            @Query("select") String select,
            @Body WorkExperience workExperience
    );

    // UPDATE WorkExperience - Use PATCH for partial updates
    @PATCH("WorkExperience")
    Call<WorkExperience> updateWorkExperience(
            @Query("user_id") String eqFilter,  // example: "eq.123"
            @Body WorkExperience workExperience
    );



    /// ////////////

    //This is for Eligibility Table
    @GET("Eligibility")
    Call<List<Eligibility>> getAllEligibility(
            @Query("select") String select
    );

    @GET("Eligibility")
    Call<List<Eligibility>> getEligibilityByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter
    );

    @POST("Eligibility")
    Call<List<Eligibility>> insertEligibility(
            @Query("select") String select,
            @Body Eligibility eligibility
    );

    // UPDATE Eligibility - Use PATCH for partial updates
    @PATCH("Eligibility")
    Call<Eligibility> updateEligibility(
            @Query("user_id") String eqFilter,  // example: "eq.123"
            @Body Eligibility eligibility
    );
}
