package com.example.skillocal_final;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
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

    //This is for Establishment Table
    @GET("Establishment")
    Call<List<Establishment>> getAllEstablishment(
            @Query("select") String select
    );

    @POST("Establishment")
    Call<Establishment> insertEstablishment(@Body Establishment establishment);

    // UPDATE Establishment - Use PATCH for partial updates
    @PATCH("Establishment")
    Call<Establishment> updateEstablishment(
            @Query("establishment_id") String eqFilter,  // example: "eq.123"
            @Body Establishment establishment
    );
}
