package com.kmjd.jsylc.zxh.mvp.model;

import com.kmjd.jsylc.zxh.mvp.model.bean.RollBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UpdatePasswordBean;
import com.kmjd.jsylc.zxh.network.ApiService;
import com.kmjd.jsylc.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.jsylc.zxh.utils.ConstantUtil;

import io.reactivex.Observable;

/**
 * Created by androidshuai on 2017/12/22.
 */

public class LoginFragmentModel {

    public Observable<RollBean> getRollBean(){
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getRollBean(ConstantUtil.INTERFACEIDENTIFIER, "105");
    }

    public Observable<UpdatePasswordBean> getUpdatePasswordRespon(String v, String password){
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getUpdatePasswordRespon(ConstantUtil.INTERFACEIDENTIFIER, "115",
                v, ConstantUtil.INTERFACE_F, password);
    }

    public Observable<UpdatePasswordBean> getPasswordCheckRespon(String v, String password){
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getPasswordCheckRespon(ConstantUtil.INTERFACEIDENTIFIER, "115",
                v, ConstantUtil.INTERFACE_F, password, "check");
    }
}
