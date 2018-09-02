package com.kmjd.jsylc.zxh.mvp.model;


import com.kmjd.jsylc.zxh.mvp.contact.WebContact;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api11;

/**
 * Created by Android-d on 2017/11/24.
 */

public class WebModel implements WebContact.Model {


    @Override
    public void getData(WebContact.GetDataCallBack<String> getDataCallBack) {
        //这里真正的执行获取数据资源
        //可以网络请求
        //......
        //也可以加载本地
        //......
        //加载成功
        getDataCallBack.onDataLoaded("");
        //加载失败,传入失败原因，例如做网络请求可能超时，可能404
        getDataCallBack.onDataNotAvailable("404");
    }


    public void getData1(WebContact.GetDataCallBack<String> getDataCallBack) {
        //这里真正的执行获取数据资源
        //可以网络请求
        //......
        //也可以加载本地
        //......
        //加载成功
        getDataCallBack.onDataLoaded("");
        //加载失败,传入失败原因，例如做网络请求可能超时，可能404
        getDataCallBack.onDataNotAvailable("404");
    }

    @Override
    public void onStart() {

    }
}
