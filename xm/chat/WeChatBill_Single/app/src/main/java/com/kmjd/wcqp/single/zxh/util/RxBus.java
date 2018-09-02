package com.kmjd.wcqp.single.zxh.util;


import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by androidshuai on 2017/6/19.
 */

public class RxBus {

    private static volatile RxBus mInstance;

    private final FlowableProcessor<Object> mBus;

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized();
    }

    /**
     * 单例模式创建RxBus
     * @return
     */
    public static RxBus getInstance(){
        if (mInstance == null){
            synchronized (RxBus.class){
                if (mInstance == null){
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送消息
     * @param object
     */
    public void post(Object object){
        mBus.onNext(object);
    }

    /**
     * 接收消息
     * @param clz
     * @param <T>
     * @return
     */
    public <T> Flowable<T> register(Class<T> clz){
        return mBus.ofType(clz);
    }

    public void unregisterAll(){
        //解除注册
        mBus.onComplete();
    }

    public boolean hasSubscribers(){
        return mBus.hasSubscribers();
    }
}
