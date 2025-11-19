package com.example.skillocal_final;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface ApiService {
    @GET("Users")
    Call<List<User>> loginUser(
            @Query("email") String emailFilter,      // example: "eq.kenth@gmail.com"
            @Query("password") String passwordFilter, // example: "eq.1234"
            @Query("select") String select            // usually: "*"
    );
}
