package com.example.skillocal_final;

public class ApiInstance {
    private static ApiService api;
    private static ApiServiceWorker apiWorker;
    private static ApiServiceJobVacancy apiJobVacancy;

    public static ApiService getApi() {
        if (api == null) {
            api = SupabaseClient.getRetrofit().create(ApiService.class);
        }
        return api;
    }


    //for worker account api
    public static ApiServiceWorker getApiWorker() {
        if (apiWorker == null) {
            apiWorker = SupabaseClient.getRetrofit().create(ApiServiceWorker.class);
        }
        return apiWorker;
    }
    //ApiServiceJobVacancy
    public static ApiServiceJobVacancy getApiJobVacancy() {
        if (apiJobVacancy == null) {
            apiJobVacancy = SupabaseClient.getRetrofit().create(ApiServiceJobVacancy.class);
        }
        return apiJobVacancy;
    }
}
