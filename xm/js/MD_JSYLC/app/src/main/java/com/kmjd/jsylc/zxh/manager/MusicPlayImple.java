package com.kmjd.jsylc.zxh.manager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.kmjd.jsylc.zxh.database.SPManager;
import com.kmjd.jsylc.zxh.mvp.model.bean.AudioEntity;
import com.kmjd.jsylc.zxh.mvp.model.bean.MusicInfoBean;
import com.kmjd.jsylc.zxh.utils.MP3DurationUtil;
import com.kmjd.jsylc.zxh.utils.MyLogger;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;

import static android.os.Environment.getExternalStorageDirectory;


public class MusicPlayImple implements AudioService {
    // 类对象实例
    private static AudioService mInstance;
    //上下文
    private Activity mContext;
    //sharepreference管理实例
    private SPManager spManager;
    private SoundPool mPool;
    private HashMap<String, Integer> mPoolMap;
    private HashMap<String, Long> mAudioDurationMap;
    //正在播放背景音乐streamID
    private int musicStreamID = -1;
    //正在播放音效streamID
    private int soundStreamID = -1;
    // 游戏标记
    private String mGameFlag;
    // 背景音乐音量
    private float musiceVolume;
    // 游戏音效音量
    private float soundVolume;
    //前后台切换的标志, 后台为true
    public boolean isBack = false;
    //下一个音频播放时间
    private long nextPlayTime;
    // 后台时记录背景音乐音量
    private float musiceVolumeBack;
    // 后台时记录游戏音效音量
    private float soundVolumeBack;


    private MusicPlayImple(Activity context) {
        mContext = context;
        mPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 100);
        mPoolMap = new HashMap<>();
        mAudioDurationMap = new HashMap<>();
        spManager = SPManager.getInstance();
        AudioEntity entity = spManager.getAudioEntity(context);
        musiceVolume = entity.getMusiceVolume();
        soundVolume = entity.getSoundVolume();
    }

    /**
     * <p>getInstance</p>
     *
     * @return 返回当前对象实例
     */
    public static AudioService getInstance(Activity context) {
        if (mInstance == null) {
            synchronized (MusicPlayImple.class) {
                if (mInstance == null) {
                    mInstance = new MusicPlayImple(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * <p>onEnterGame</p>
     *
     * @param gameFlag
     * @Description 每次进入一个新的游戏
     */
    @Override
    public void onEnterGame(String gameFlag) {
        if (!TextUtils.isEmpty(mGameFlag) && mGameFlag.equals(gameFlag)) {
            return;
        }
        mGameFlag = gameFlag;
        initSoundHashMap(gameFlag);

        mPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == mPoolMap.size()) {
                    musicStreamID = mPool.play(mPoolMap.get("bgSound.mp3"), musiceVolume, musiceVolume, 1, -1, 1);
                }
            }
        });
    }

    /**
     * <p>hasGameFlag</p>
     *
     * @param gameFlag
     * @return 本地是否存在相应游戏标记
     */
    @Override
    public boolean hasGameFlag(String gameFlag) {
        try {
            String[] games = mContext.getAssets().list("GameSound");
            for (String game : games) {
                if (game.equals(gameFlag)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * <p>onSJDTSoundPlay</p>
     * @param gameFlag
     * @param mp3Name
     * @Description 针对大厅做特殊处理
     */
    @Override
    public void onSJDTSoundPlay(String gameFlag, final String mp3Name) {
        mGameFlag = gameFlag;
        initSoundHashMap(gameFlag);

        mPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                int soundId = mPoolMap.get(mp3Name);
                if (sampleId == soundId) {
                    if(nextPlayTime<System.currentTimeMillis()){
                        nextPlayTime = System.currentTimeMillis();
                    }
                    long soundTime = mAudioDurationMap.get(mp3Name);
                   onDelayAudioPlay(soundId, soundTime);
                }
            }
        });
    }

    @Override
    public boolean isSJDTGame() {
        return mGameFlag.equals("SJDT");
    }

    /**
     * <p>initSoundHashMap</p>
     *
     * @param gameFlag
     * @Description 初始化hashMap数据
     */
    private void initSoundHashMap(String gameFlag) {
        mPoolMap.clear();
        mAudioDurationMap.clear();
        //mPool.release();
        try {
            String[] mp3s = mContext.getAssets().list("GameSound/" + gameFlag.toUpperCase());
            MusicInfoBean infoBean = MP3DurationUtil.getMusicInfoBean(mContext);

            //拿到对应游戏标记的对象并存储在hashMap中
            MusicInfoBean.GameTypeBean gameTypeBean= infoBean.new GameTypeBean();
            for(MusicInfoBean.GameTypeBean bean:infoBean.getList()){
                if(bean.getName().equals(gameFlag)){
                    gameTypeBean = bean;
                }
            }
            int soundSize = gameTypeBean.getList().size();
            for(int i = 0; i < soundSize; i++){
                MusicInfoBean.GameTypeBean.MusicDurationBean bean = gameTypeBean.getList().get(i);
                mAudioDurationMap.put(bean.getName(), bean.getTime());
            }

            //加载对应游戏标记下所有音效
            for (String mp3 : mp3s) {
                String soundPath = "GameSound/" + gameFlag.toUpperCase() + "/" + mp3;
                AssetFileDescriptor afd = mContext.getAssets().openFd(soundPath);
                int soundId = mPool.load(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength(), 1);
                //int tTime = getTimeByMediaPlay(afd);
                mPoolMap.put(mp3, soundId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>onSoundPlayStart</p>
     *
     * @param gameFlag
     * @param mp3Name
     * @Description 游戏音效播放
     */
    @Override
    public void onSoundPlayStart(String gameFlag, final String mp3Name) {
            if (!gameFlag.equals(mGameFlag)) {
                initSoundHashMap(gameFlag);
            }
            //onStopSound();
            int soundId = mPoolMap.get(mp3Name);
            //soundStreamID = mPool.play(soundId, soundVolume, soundVolume, 1, 0, 1);
            long soundTime = mAudioDurationMap.get(mp3Name);
            if(nextPlayTime<System.currentTimeMillis()){
                nextPlayTime = System.currentTimeMillis();
            }
            onDelayAudioPlay(soundId, soundTime);
    }

    /**
     * <p>onDelayAudioPlay</p>
     * @param playId
     * @param soundTime
     * @Description 音效延时播放
     */
    private void onDelayAudioPlay(final int playId, long soundTime){
        //计算最后一个播放完距离当前时间差，如果是负数，延时1毫秒
        long playTime = nextPlayTime - System.currentTimeMillis()< 0? 1:nextPlayTime - System.currentTimeMillis();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mPool!=null)
                    soundStreamID = mPool.play(playId, soundVolume, soundVolume, 1, 0, 1);
            }
        }, playTime);
        //将下一个播放的时间加上当前音效时间
        nextPlayTime += soundTime;
    }

    /**
     * <p>onMusicVolume</p>
     *
     * @param volume
     * @Description 设置背景音乐音量
     */
    @Override
    public void setMusicVolume(Float volume) {
        musiceVolume = volume;
        AudioEntity mEntity = spManager.getAudioEntity(mContext);
        mEntity.setMusiceVolume(musiceVolume);
        spManager.setAudioEntity(mContext, mEntity);
        mPool.setVolume(musicStreamID, musiceVolume, musiceVolume);
    }

    /**
     * <p>setSoundVolume</p>
     *
     * @param volume
     * @Description 设置音效音量
     */
    @Override
    public void setSoundVolume(Float volume) {
        soundVolume = volume;
        AudioEntity mEntity = spManager.getAudioEntity(mContext);
        mEntity.setSoundVolume(soundVolume);
        spManager.setAudioEntity(mContext, mEntity);
        mPool.setVolume(soundStreamID, soundVolume, soundVolume);
    }


    @Override
    public void onStopMusic() {
        mPool.stop(musicStreamID);
    }

    @Override
    public void onStopSound() {
        if(soundStreamID<0){
            return;
        }
        mPool.stop(soundStreamID);
    }

    @Override
    public void onRelease() {
        mInstance = null;
        if (mPool!=null){
            onStopMusic();
            onStopSound();
            mPool.release();
            mPool = null;
            mGameFlag = null;
            nextPlayTime = 0L;
            MyLogger.mLog().e("onRelease == mPool = null---mInstance = null");
        }
    }

    /**
     * 按home键时回调, 全部音频音量设为0
     */
    @Override
    public void onAllPause() {
        isBack = true;
        musiceVolumeBack = musiceVolume;
        soundVolumeBack = soundVolume;
        musiceVolume = 0L;
        soundVolume = 0L;
        mPool.setVolume(musicStreamID, musiceVolume, musiceVolume);
        mPool.setVolume(soundStreamID, soundVolume, soundVolume);
    }

    /**
     * 从后台返回前台时回调, 全部音频音量还原
     */
    @Override
    public void onAllResume() {
        isBack = false;
        musiceVolume = musiceVolumeBack;
        soundVolume = soundVolumeBack;
        musiceVolumeBack = 0L;
        soundVolumeBack = 0L;
        mPool.setVolume(musicStreamID, musiceVolume, musiceVolume);
        mPool.setVolume(soundStreamID, soundVolume, soundVolume);
    }
}
