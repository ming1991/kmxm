package com.kmjd.jsylc.zxh.mvp.model;

import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api11;
import com.kmjd.jsylc.zxh.network.ApiService;
import com.kmjd.jsylc.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.jsylc.zxh.utils.LoginVerification;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class HomeFragmentModel {

    public interface GetDataCallBack<T>{
        void onDataLoaded(T t);
        void onDataNotAvailable(String msg);
    }
    public HomeFragmentModel() {
    }
    //十一、获取第三方额度接口
    public void getApi11Date(final GetDataCallBack<QQCP_Bean_api11> getDataCallBack) {
        //可以网络请求
        //......
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        Observable<QQCP_Bean_api11> observable_qqcp_data = apiService.getQQCPData_api11("m", "104", LoginVerification.getLoginVerification(), "mdApp", "g1");
        observable_qqcp_data.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<QQCP_Bean_api11>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(QQCP_Bean_api11 qqcp_beanApi11) {
                        //加载成功
                        getDataCallBack.onDataLoaded(qqcp_beanApi11);
                    }

                    @Override
                    public void onError(Throwable e) {

                        //加载失败,传入失败原因，例如做网络请求可能超时，可能404
                        getDataCallBack.onDataNotAvailable(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取Api12接口数据
    public void getApi12Data(final GetDataCallBack<String> getDataCallBack,QQCP_Bean_api11 qqcp_bean_api11,String inputContent){
        ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);
        apiService.getApi12Data("m", "101", LoginVerification.getLoginVerification(), "mdApp", qqcp_bean_api11.getId(), inputContent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        getDataCallBack.onDataLoaded(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getDataCallBack.onDataNotAvailable(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
