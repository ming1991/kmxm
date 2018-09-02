package com.kmjd.jsylc.zxh.listener;

import com.kmjd.jsylc.zxh.mvp.model.bean.GameVideoBean;

import java.util.List;

public class CallListener {
    //测速回调
    public interface OnVelocityListener {

        /**
         * <p>onVelocitySuccess</p>
         *
         * @param urls
         * @Description 测速成功
         */
        void onVelocitySuccess(List<String> urls);

        /**
         * <p>onAnalyzeSuccess</p>
         *
         * @param gameVideoBean
         * @Description 解析xml成功
         */
        void onAnalyzeSuccess(GameVideoBean gameVideoBean);

        /**
         * <p>onVelocityFailed</p>
         *
         * @Description 测速失败
         */
        void onVelocityFailed(int type, String error);
    }
}
