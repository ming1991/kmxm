package com.kmjd.jsylc.zxh.database;

import android.content.Context;

import com.google.gson.Gson;
import com.kmjd.jsylc.zxh.mvp.model.bean.AudioEntity;
import com.kmjd.jsylc.zxh.utils.SPUtils;

public class SPManager {

    //当前类实例
    private static SPManager mInstance;
    //音频设置key
    private String KEY_AUDIO_SETTING = "KEY_AUDIO_SETTING";

    /**
     * <p>getInstance</p>
     * @return 返回当前实例
     */
    public static SPManager getInstance(){
        if(mInstance== null){
            synchronized (SPManager.class){
                if(mInstance==null)
                    mInstance = new SPManager();
            }
        }
        return mInstance;
    }

    /**
     * <p>getAudioEntity</p>
     * @param context
     * @return  获取音频对象
     */
    public AudioEntity getAudioEntity(Context context){
        AudioEntity entity = new AudioEntity();
        String adaption = SPUtils.getString(context, KEY_AUDIO_SETTING);
        Gson gson = new Gson();
        if(gson.fromJson(adaption, AudioEntity.class) == null) {
            return entity;
        }
        return gson.fromJson(adaption, AudioEntity.class);
    }

    /**
     * <p>setAudioEntity</p>
     * @param context
     * @param entity
     * @Description  保存音频对象信息
     */
    public void setAudioEntity(Context context, AudioEntity entity){
        Gson gson = new Gson();
        String jsonEntity = gson.toJson(entity);
        SPUtils.put(context, KEY_AUDIO_SETTING, jsonEntity);
    }
}
