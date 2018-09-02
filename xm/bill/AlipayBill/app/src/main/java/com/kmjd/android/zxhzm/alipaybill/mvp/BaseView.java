package com.kmjd.android.zxhzm.alipaybill.mvp;

public interface BaseView<T extends BasePresenter>{
    void setPresenter(T presenter);
}
