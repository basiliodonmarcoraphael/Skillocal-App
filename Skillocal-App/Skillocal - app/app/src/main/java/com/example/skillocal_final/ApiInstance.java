package com.example.skillocal_final;

public class ApiInstance {
    private static ApiService api;
    private static ApiServiceWorker apiWorker;

    public static ApiService getApi() {
        if (api == null) {
            api = SupabaseClient.getRetrofit().create(ApiService.class);
        }
        return api;
    }

    public static ApiServiceWorker getApiWorker() {
        if (apiWorker == null) {
            apiWorker = SupabaseClient.getRetrofit().create(ApiServiceWorker.class);
        }
        return apiWorker;
    }
}
