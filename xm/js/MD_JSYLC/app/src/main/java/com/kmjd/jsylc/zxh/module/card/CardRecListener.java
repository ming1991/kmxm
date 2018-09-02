package com.kmjd.jsylc.zxh.module.card;


import com.kmjd.jsylc.zxh.module.card.entity.CardDetailEntity;

public class CardRecListener {

    //识别监听回调接口
    public interface RecognitionListener {
        /**
         * <p>onRecognitionSuccess</p>
         * @param entity
         * @Description  识别成功
         */
        void onRecognitionSuccess(CardDetailEntity entity);

        /**
         * <p>onRecognitionError</p>
         * @Description 识别失败
         */
        void onRecognitionError(String error);
    }

    public interface GetBDYTokenListener{
        //获取token成功
        void onGetTokenSuccess(String access_token);

        //获取token失败
        void onGetTokenFailure(String msg);
    }
}
