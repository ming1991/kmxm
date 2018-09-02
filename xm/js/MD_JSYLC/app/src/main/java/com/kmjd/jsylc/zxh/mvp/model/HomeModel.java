package com.kmjd.jsylc.zxh.mvp.model;

import com.kmjd.jsylc.zxh.mvp.model.bean.FunctionIsOpenBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.GiftBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.LogoutBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PrivilegeCodeBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api10;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api11;
import com.kmjd.jsylc.zxh.mvp.model.bean.RSABean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UserMessageBean;
import com.kmjd.jsylc.zxh.network.ApiService;
import com.kmjd.jsylc.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.jsylc.zxh.utils.ConstantUtil;

import io.reactivex.Observable;



public class HomeModel {
    public Observable<UserMessageBean> getUserMessage(String v) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getUserMessage(ConstantUtil.INTERFACEIDENTIFIER, "110", v, ConstantUtil.INTERFACE_F);
    }

    public Observable<LogoutBean> logOutRespon(String v) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.logOutRespon(ConstantUtil.INTERFACEIDENTIFIER, "113", v, ConstantUtil.INTERFACE_F);
    }

    public Observable<FunctionIsOpenBean> getFunctionIsOpenRespon(String verifyParm) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getFunctionIsOpenRespon(ConstantUtil.INTERFACEIDENTIFIER, "109",
                verifyParm, ConstantUtil.INTERFACE_F);
    }

    public Observable<PrivilegeCodeBean> getPrivilegeCodeRespon(String verifyParm, String code) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getPrivilegeCodeRespon(ConstantUtil.INTERFACEIDENTIFIER, "106", verifyParm,
                ConstantUtil.INTERFACE_F, code);
    }

    public Observable<PlatfromIntoBean> getPlatfromIntoBean() {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getPlatfromIntoBean(ConstantUtil.INTERFACEIDENTIFIER, "102");
    }

    public Observable<PlatfromIconTitleBean> getPlatfromIconTitle(String v, String f) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getPlatfromIconTitle(ConstantUtil.INTERFACEIDENTIFIER, "114", v, f);
    }

    public Observable<GiftBean> getGiftRespon(String verifyParm) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getGiftRespon(ConstantUtil.INTERFACEIDENTIFIER, "116",
                verifyParm, ConstantUtil.INTERFACE_F);
    }

    public Observable<QQCP_Bean_api10[]> getDZTData_api10(String verifyParm) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getDZTData_api10(ConstantUtil.INTERFACEIDENTIFIER, "103",
                verifyParm, ConstantUtil.INTERFACE_F);
    }

    public Observable<QQCP_Bean_api11> getData_api11(String verifyParm, String c) {
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        return apiService.getQQCPData_api11(ConstantUtil.INTERFACEIDENTIFIER, "104",
                verifyParm, ConstantUtil.INTERFACE_F, c);
    }

//    public Observable<QQCP_Bean_api11> getDZTCPData_api11(String verifyParm,String s) {
//        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
//        return apiService.getDZTCPData_api11(ConstantUtil.INTERFACEIDENTIFIER, "104",
//                verifyParm, ConstantUtil.INTERFACE_F, s);
//    }

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
