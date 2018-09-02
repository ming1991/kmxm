package com.kmjd.jsylc.zxh.mvp.view;

import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;

/**
 * Created by Android-d on 2017/11/21.
 */

public interface BaseView<P extends BasePresenter> {
    void setPresenter(P presenter);
}
