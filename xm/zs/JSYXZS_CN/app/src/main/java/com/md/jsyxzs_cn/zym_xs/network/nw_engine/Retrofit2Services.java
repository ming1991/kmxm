package com.md.jsyxzs_cn.zym_xs.network.nw_engine;


import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.NetWebsite;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListUploadRespone;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.ApiResult;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.ApiResultData;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.ApiResultListData;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.BackupLinkResModel;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.CheckVersionResModel;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.QueryRepairResModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by zym on 2016/12/28.
 */

public interface Retrofit2Services {
    @FormUrlEncoded
    @POST("LoadData/LineToolAPI.ashx")
    Call<RepairListUploadRespone> getRepairListUploadRespone(@Field("Value") String value);

    @FormUrlEncoded
    @POST("LoadData/LineToolAPI.ashx")
    Call<RepairListUploadRespone> getRepairListCheckAllRespone(@Field("Value") String value);

    @FormUrlEncoded
    @POST("LoadData/LineToolAPI.ashx")
    Call<RepairListUploadRespone> getLinklistRespone(@Field("Value") String value);

    @GET("loaddata/checkversion.ashx?webcode=26")
    Call<String> getUpdateInfoResponse();

    @FormUrlEncoded
    @POST("Browser/browseapi.ashx")
    Call<NetWebsite> getWebsiteRespone(@Field("gamecode") Integer code);

    /*--------------------------------------新的网络接口------------------------------------------*/

    //获取备用站点地址
    @GET("api/WebsiteUrl/GetBackupLink")
    Call<ApiResultListData<BackupLinkResModel>> getBackupLinkRespone(@Query("site") Integer site,
                                                                     @Query("type") Integer type);

    //获取报修单
    @FormUrlEncoded
    @POST("api/GameAssistant/QueryRepair")
    Call<ApiResultListData<QueryRepairResModel>> getQueryRepairResModel(@Field("ID") Integer id,
                                                                        @Field("Type") String type,
                                                                        @Field("RepairNo") String repairNo,
                                                                        @Field("StartTime") String startTime,
                                                                        @Field("EndTime") String endTime);

    //添加报修单
    @FormUrlEncoded
    @POST("api/GameAssistant/AddRepair")
    Call<ApiResult> addRepair(@Field("RepairNo") String repairNo,
                              @Field("Accounts") String accounts,
                              @Field("Phone") String phone,
                              @Field("Type") Integer type,
                              @Field("Content") String content,
                              @Field("WebSite") String website);

    //获取游戏助手客户端的版本号
    @GET("api/GameAssistant/CheckVersion")
    Call<ApiResultData<CheckVersionResModel>> checkVersion();
}
