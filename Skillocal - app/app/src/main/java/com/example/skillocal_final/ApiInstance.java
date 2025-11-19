package com.example.skillocal_final;

public class ApiInstance {
    private static ApiService api;

    public static ApiService getApi() {
        if (api == null) {
            api = SupabaseClient.getRetrofit().create(ApiService.class);
        }
        return api;
    }
}
