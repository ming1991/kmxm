package com.kmjd.jsylc.zxh.network;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface VelocityService {

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

    @GET
    Observable<ResponseBody> parseXML(@Url String url);
}
