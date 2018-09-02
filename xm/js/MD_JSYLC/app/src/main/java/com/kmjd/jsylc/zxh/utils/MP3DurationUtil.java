package com.kmjd.jsylc.zxh.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kmjd.jsylc.zxh.manager.MP3DurationIntentService;
import com.kmjd.jsylc.zxh.mvp.model.bean.MusicInfoBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android-mwb on 2018/7/14.
 */
public class MP3DurationUtil {
    private static MediaPlayer mediaPlayer;
    public static final String MP3DURATIONFILENAME = "mp3_duration_filename";
    public static final String MP3DURATIONKEY = "mp3_duration_key";
    public static final String MP3DURATIONVERSIONCODE = "mp3_duration_versioncode";

    public static void getMusicFile(Context context) {
        MusicInfoBean musicInfoBean = new MusicInfoBean();
        try {
            String[] gameArr = context.getAssets().list("GameSound");
            List<MusicInfoBean.GameTypeBean> gameTypeBeanList = new ArrayList<>();
            for (String game : gameArr) {
                MusicInfoBean.GameTypeBean gameTypeBean = musicInfoBean.new GameTypeBean();
                gameTypeBean.setName(game);
                String[] mp3Arr = context.getAssets().list("GameSound/" + game);
                List<MusicInfoBean.GameTypeBean.MusicDurationBean> musicDurationBeanList = new ArrayList<>();
                for (String mp3 : mp3Arr) {
                    MusicInfoBean.GameTypeBean.MusicDurationBean musicDurationBean = gameTypeBean.new MusicDurationBean();
                    musicDurationBean.setName(mp3);
                    String musicPath = "GameSound/" + game + "/" + mp3;
                    AssetFileDescriptor afd = context.getAssets().openFd(musicPath);
                    long musicDuration = getMusicDuration(afd);
                    musicDurationBean.setTime(musicDuration);
                    musicDurationBeanList.add(musicDurationBean);
                }
                gameTypeBean.setList(musicDurationBeanList);
                gameTypeBeanList.add(gameTypeBean);
            }
            musicInfoBean.setList(gameTypeBeanList);
            writeToFile(musicInfoBean, context);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getMusicDuration(AssetFileDescriptor afd) {
        long duration = 0;
        if (mediaPlayer ==null){
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return duration;
    }

    public static void writeToFile(MusicInfoBean musicInfoBean, Context context){
        Gson gson = new Gson();
        String jsonStr = gson.toJson(musicInfoBean);
        SharedPreferences sp = context.getSharedPreferences(MP3DURATIONFILENAME, Context.MODE_PRIVATE);
        sp.edit().putString(MP3DURATIONKEY, jsonStr).apply();
    }

    public static MusicInfoBean getMusicInfoBean(Context context) {
        SharedPreferences sp = context.getSharedPreferences(MP3DURATIONFILENAME, Context.MODE_PRIVATE);
        String content = sp.getString(MP3DURATIONKEY, "");
        Gson gson = new Gson();
        MusicInfoBean musicInfoBean = gson.fromJson(content, MusicInfoBean.class);
        return musicInfoBean;
    }

    public static void printMusicInfoBean(MusicInfoBean musicInfoBean) {
        List<MusicInfoBean.GameTypeBean> gameTypeBeanList = musicInfoBean.getList();
        for (MusicInfoBean.GameTypeBean gameTypeBean : gameTypeBeanList){
            List<MusicInfoBean.GameTypeBean.MusicDurationBean> musicDurationBeanList = gameTypeBean.getList();
            String game = gameTypeBean.getName();
            for (MusicInfoBean.GameTypeBean.MusicDurationBean musicDurationBean : musicDurationBeanList){
                String mp3 = musicDurationBean.getName();
                long time = musicDurationBean.getTime();
                MyLogger.mLog().e(" game = " + game + " mp3 = " + mp3 + " time = " + time);
            }
        }
    }

    public static int getLocalVersionCode(Context context) {
        int localVersionCode = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersionCode = packageInfo.versionCode;
            MyLogger.mLog().e("本软件的版本号。。" + localVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersionCode;
    }

    public static void updateMP3Duration(Context context) {
        SharedPreferences sp = context.getSharedPreferences(MP3DURATIONFILENAME, Context.MODE_PRIVATE);
        int spVersionCode = sp.getInt(MP3DURATIONVERSIONCODE, 0);
        MyLogger.mLog().e("updateMP3Duration spVersionCode = " + spVersionCode);
        String content = sp.getString(MP3DURATIONKEY, "");
        MyLogger.mLog().e("updateMP3Duration content = " + content);
        int localVersionCode = getLocalVersionCode(context);
        if (localVersionCode > spVersionCode || TextUtils.isEmpty(content)){
            sp.edit().putInt(MP3DURATIONVERSIONCODE, localVersionCode).apply();
            MP3DurationIntentService.startIntentService(context);
            MyLogger.mLog().e("updateMP3Duration 更新 mp3时间");
        }
    }
}
