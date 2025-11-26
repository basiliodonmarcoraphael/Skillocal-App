package com.example.skillocal_final;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
public interface ApiService {
    //This is for Users Table
    @GET("Users")
    Call<List<User>> loginUser(
            @Query("email") String emailFilter,      // example: "eq.kenth@gmail.com"
            @Query("password") String passwordFilter, // example: "eq.1234"
            @Query("select") String select            // usually: "*"
    );

    //    FOR USERS TABLE - Manage Employee Profile
    @GET("Users")
    Call<List<User>> getUserByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter
    );


    // UPDATE Training - Use PATCH for partial updates
    @PATCH("Users")
    Call<Void> updateUser(
            @Query("user_id") String eqFilter,  // example: "eq.123"
            @Body User user
    );

    //This is for Establishment Table
    @GET("Establishment")
    Call<List<Establishment>> getAllEstablishment(
            @Query("select") String select,
            @Query("order") String order
    );

    @GET("Establishment")
    Call<List<Establishment>> getEstablishmentByUserId(
            @Query("select") String select,
            @Query("user_id") String userIdFilter,
            @Query("status") String statusFilter,
            @Query("order") String order
    );

    @POST("Establishment")
    Call<Establishment> insertEstablishment(@Body Establishment establishment);

    // UPDATE Establishment - Use PATCH for partial updates
    @PATCH("Establishment")
    Call<Establishment> updateEstablishment(
            @Query("establishment_id") String eqFilter,  // example: "eq.123"
            @Body Establishment establishment
    );

    @PATCH("Establishment")
    Call<Establishment> deleteEstablishment(
            @Query("establishment_id") String eqFilter,  // example: "eq.123"
            @Body Establishment establishment
    );

//    @DELETE("Establishment")
//    Call<Void> deleteEstablishment(
//            @Query("establishment_id") String eqFilter // example: "eq.55"
//    );

    @GET("Industry")
    Call<List<Industry>> getAllIndustry(
            @Query("select") String select
    );




}
