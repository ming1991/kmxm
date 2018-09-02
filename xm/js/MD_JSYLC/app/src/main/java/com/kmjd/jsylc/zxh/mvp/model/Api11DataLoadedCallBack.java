package com.kmjd.jsylc.zxh.mvp.model;

import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api11;

/**
 * Created by Android-d on 2017/12/14.
 */

public interface Api11DataLoadedCallBack {
    void onDataLoaded(QQCP_Bean_api11 qqcp_bean_api11);
    void onFailure(String msg);
}
