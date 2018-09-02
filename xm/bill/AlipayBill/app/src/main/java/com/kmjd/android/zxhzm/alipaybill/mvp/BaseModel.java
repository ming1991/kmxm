package com.kmjd.android.zxhzm.alipaybill.mvp;

public interface BaseModel {
    /*此接口用于获取数据后的结果回调*/
    interface GetDataCallBack<T> {
        void onDataLoaded(T p);

        void onDataNotAvailable(T msg);
    }

    void onStart();
}
