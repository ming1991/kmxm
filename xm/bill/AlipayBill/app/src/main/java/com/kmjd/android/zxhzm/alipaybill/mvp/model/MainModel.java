package com.kmjd.android.zxhzm.alipaybill.mvp.model;

import android.support.annotation.NonNull;

import com.kmjd.android.zxhzm.alipaybill.mvp.BaseModel;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.MainContact;
import com.kmjd.android.zxhzm.alipaybill.network.NetWorkHelper;
import com.kmjd.android.zxhzm.alipaybill.network.NetWorkInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public  class MainModel implements MainContact.Model{

    @Override
    public void onStart() {

    }


    @Override
    public void getData(final GetDataCallBack<String> getDataCallBack) {
        NetWorkHelper.getmRetrofit("","").create(NetWorkInterface.class)
                .toUploadAlipayBill(new HashMap<String, String>(10))
                .enqueue(new Callback<String>() {
                    @Override

                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.code() == 200){
                            getDataCallBack.onDataLoaded("");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        getDataCallBack.onDataNotAvailable("");
                    }
                });
    }
}
