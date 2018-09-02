package com.kmjd.jsylc.zxh.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.utils.StringConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by androidshuai on 2017/11/23.
 */

public class Retrofit2GenerateHelper {

    private static Retrofit mRetrofit;

    private static OkHttpClient mOkHttpClient;

    public static OkHttpClient genericClient() {
        if (null == mOkHttpClient){
            mOkHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request()
                            .newBuilder()
                            .header("User-Agent", "Android App")
                            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .build();
                    return chain.proceed(request);
                }
            }).connectTimeout(5, TimeUnit.SECONDS).build();
        }
        return mOkHttpClient;
    }

    public static Retrofit getRetrofitInstance() {
        mRetrofit = null;
        mRetrofit = new Retrofit.Builder()
                .baseUrl(SPUtils.getString(MainApplication.applicationContext,SPUtils.SP_KEY.FAST_DOMAIN.toString()))
                .client(genericClient())
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return mRetrofit;
    }
}
