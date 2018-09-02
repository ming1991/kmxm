package com.kmjd.jsylc.zxh.network;

import com.kmjd.jsylc.zxh.mvp.model.bean.FunctionIsOpenBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.GiftBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.LogoutBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api10;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api11;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PrivilegeCodeBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.RSABean;
import com.kmjd.jsylc.zxh.mvp.model.bean.RollBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.TransferAccountBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UpdatePasswordBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UserMessageBean;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by androidshuai on 2017/11/23.
 */

public interface ApiService {

    @GET("ApiPlatform.aspx?")
    Observable<RollBean> getRollBean(@Query("r") String identifying,
                                     @Query("n") String order);

    @GET("ApiPlatform.aspx?")
    Observable<RSABean> getRSABean(@Query("r") String identifying,
                                   @Query("n") String order);

    @GET("ApiPlatform.aspx?")
    Observable<UserMessageBean> getUserMessage(@Query("r") String identifying,
                                               @Query("n") String n,
                                               @Query("v") String v,
                                               @Query("f") String f);


    @GET("ApiPlatform.aspx?")
    Observable<PlatfromIntoBean> getPlatfromIntoBean(@Query("r") String identifying,
                                                     @Query("n") String n);


    @GET("ApiPlatform.aspx?")
    Observable<PlatfromIconTitleBean> getPlatfromIconTitle(@Query("r") String identifying,
                                                            @Query("n") String n,
                                                            @Query("v") String v,
                                                            @Query("f") String f);

    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<LoginBean> getLoginRespon(@Field("r") String identifying,
                                         @Field("n") String order,
                                         @Field("user") String user,
                                         @Field("password") String password);

    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<LogoutBean> logOutRespon(@Field("r") String r,
                                        @Field("n") String n,
                                        @Field("v") String v,
                                        @Field("f") String ios);

    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<FunctionIsOpenBean> getFunctionIsOpenRespon(@Field("r") String r,
                                                           @Field("n") String n,
                                                           @Field("v") String v,
                                                           @Field("f") String ios);

    //十、获取带状体的第三方接口
    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<String> getQQCPData_api10(@Field("r") String r,
                                         @Field("n") String n,
                                         @Field("v") String v,
                                         @Field("f") String f);

    //十一、获取第三方额度接口
    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<QQCP_Bean_api11> getQQCPData_api11(@Field("r") String r,
                                                  @Field("n") String n,
                                                  @Field("v") String v,
                                                  @Field("f") String f,
                                                  @Field("c") String c);
    //十一、获取第三方额度接口
    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<QQCP_Bean_api11> getDZTCPData_api11(@Field("r") String r,
                                                  @Field("n") String n,
                                                  @Field("v") String v,
                                                  @Field("f") String f,
                                                  @Field("c") String c);

    //十二、快捷转帐
    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<String> getApi12Data(@Field("r") String r,
                                    @Field("n") String n,
                                    @Field("v") String v,
                                    @Field("f") String f,
                                    @Field("c") String c,
                                    @Field("m") String m);

    //十二、快速转账新版接口
    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<TransferAccountBean> getApi12DataNew(@Field("r") String r,
                                                    @Field("n") String n,
                                                    @Field("v") String v,
                                                    @Field("f") String f,
                                                    @Field("c") String c,
                                                    @Field("m") String m);

    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<PrivilegeCodeBean> getPrivilegeCodeRespon(@Field("r") String r,
                                                         @Field("n") String n,
                                                         @Field("v") String v,
                                                         @Field("f") String mdApp,
                                                         @Field("bc") String code);

    @GET("ApiPlatform.aspx?")
    Observable<UpdatePasswordBean> getUpdatePasswordRespon(@Query("r") String identifying,
                                                           @Query("n") String order,
                                                           @Query("v") String v,
                                                           @Query("f") String f,
                                                           @Query("p") String p);

    @GET("ApiPlatform.aspx?")
    Observable<UpdatePasswordBean> getPasswordCheckRespon(@Query("r") String identifying,
                                                          @Query("n") String order,
                                                          @Query("v") String v,
                                                          @Query("f") String f,
                                                          @Query("p") String p,
                                                          @Query("type") String type);

    @GET("ApiPlatform.aspx?")
    Observable<GiftBean> getGiftRespon(@Query("r") String identifying,
                                       @Query("n") String order,
                                       @Query("v") String v,
                                       @Query("f") String f);

    //十、获取带状体的第三方接口,返回对象数组
    @FormUrlEncoded
    @POST("ApiPlatform.aspx?")
    Observable<QQCP_Bean_api10[]> getDZTData_api10(@Field("r") String r,
                                                 @Field("n") String n,
                                                 @Field("v") String v,
                                                 @Field("f") String f);
}
