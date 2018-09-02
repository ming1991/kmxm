package com.kmjd.jsylc.zxh.mvp.model;

import com.kmjd.jsylc.zxh.mvp.model.bean.LogoutBean;
import com.kmjd.jsylc.zxh.network.ApiService;
import com.kmjd.jsylc.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.jsylc.zxh.utils.ConstantUtil;

import io.reactivex.Observable;

/**
 * Created by androidshuai on 2017/11/23.
 */

public class LogoutModel {

    public Observable<LogoutBean> logOutRespon(String v, String ios) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.logOutRespon(ConstantUtil.INTERFACEIDENTIFIER, "113", v, ios);
    }
}
