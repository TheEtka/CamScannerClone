package com.aek.camscanner.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitClient = null;
    public static Retrofit getClient()
    {
        if (retrofitClient == null)
        {
            retrofitClient = new Retrofit.Builder()
                    .baseUrl("http://54.167.93.163")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofitClient;
    }
}
