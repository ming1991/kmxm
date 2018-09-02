package com.kmjd.android.zxhzm.alipaybill.network;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetWorkHelper {

    public static Retrofit getmRetrofit(String HOST,String userAgent) {
        return new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient(userAgent))
                .build();
    }

    private static OkHttpClient getOkHttpClient(final String userAgent) {

        return new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    Request request = null;
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        request = chain.request()
                                .newBuilder()
                                .header("User-Agent", userAgent)
                                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .header("Connection", "keep-alive")
                                .header("Accept", "*/*")
                                .build();
                        return chain.proceed(request);
                    }

                }).connectTimeout(3, TimeUnit.SECONDS)
                .build();
    }


    public static Retrofit getmRetrofit(String HOST) {
        return new Retrofit.Builder()
                .baseUrl(HOST)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().connectTimeout(3,TimeUnit.SECONDS).build())
                .build();
    }
}
