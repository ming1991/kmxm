package com.kmjd.jsylc.zxh.module.card;

import com.kmjd.jsylc.zxh.module.card.entity.BDYResultEntity;
import com.kmjd.jsylc.zxh.module.card.entity.JISUResultEntity;
import com.kmjd.jsylc.zxh.module.card.entity.RecResultEntity;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface RecognitionService {

    @Headers({
            "Content-Type:application/json; charset=UTF-8",
            "Authorization:APPCODE 59a5219ac9fa4f46ae2c8daf289079c7"})
    @POST("ocr_bank_card.json")
    Observable<RecResultEntity> recognitionByAliYun(@Body RequestBody body);

    @Headers({
            "Content-Type:application/x-www-form-urlencoded; charset=UTF-8",
            "Authorization:APPCODE 59a5219ac9fa4f46ae2c8daf289079c7"})
    @FormUrlEncoded
    @POST
    Observable<JISUResultEntity> recognitionByJISUAliYun(@Url String url, @Field("pic") String pic);


    @GET("https://aip.baidubce.com/oauth/2.0/token")
    Observable<ResponseBody> getBDYAccessToken(@QueryMap Map<String, String> params);


    @Headers({"Content-Type:application/x-www-form-urlencoded; charset=UTF-8"})
    @FormUrlEncoded
    @POST("https://aip.baidubce.com/rest/2.0/ocr/v1/bankcard")
    Observable<BDYResultEntity> recongnitionByBDY(@Query("access_token") String path, @Field("image") String image);
}
