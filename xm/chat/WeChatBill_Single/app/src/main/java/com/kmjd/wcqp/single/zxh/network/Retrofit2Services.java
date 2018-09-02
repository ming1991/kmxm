package com.kmjd.wcqp.single.zxh.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by zym on 2016/12/28.
 */

public interface Retrofit2Services {

	@FormUrlEncoded
	@POST("Ashx/WeiChatDetail.ashx")
	Call<String> uploadWeChatChangeDetailInfo(@Field("detail") String detail,
	                                          @Field("surplus") String surplus,
	                                          @Field("account") String account/*,
	                                          @Field("debug") String debug*/);
	@GET("version.html")
	Call<String> getLastVersionInfo();


	@GET(".")
	Call<String> isUploadSwitch(@Query("account") String account);

}
