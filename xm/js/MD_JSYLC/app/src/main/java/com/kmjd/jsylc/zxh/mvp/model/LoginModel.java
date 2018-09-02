package com.kmjd.jsylc.zxh.mvp.model;

import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.RSABean;
import com.kmjd.jsylc.zxh.network.ApiService;
import com.kmjd.jsylc.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.jsylc.zxh.utils.ConstantUtil;

import io.reactivex.Observable;

/**
 * Created by androidshuai on 2017/12/22.
 */

public class LoginModel {

    public Observable<RSABean> getRSABean(){
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getRSABean(ConstantUtil.INTERFACEIDENTIFIER, "111");
    }

    public Observable<LoginBean> getLoginBean(String accountId, String password){
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getLoginRespon(ConstantUtil.INTERFACEIDENTIFIER, "112",
                accountId, password);
    }
}
