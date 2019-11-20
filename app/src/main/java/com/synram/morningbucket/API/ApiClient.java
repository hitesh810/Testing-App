package com.synram.morningbucket.API;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dell on 7/2/2018.
 */

public class ApiClient {
//    public static final String BASE_URL = "http://morningbucket.in/";
    public static final String BASE_URL = "https://www.morningbucket.in/";
    private static Retrofit retrofit = null;
    HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.BODY;
   public static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//interceptor.setLevel(logLevel);
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
