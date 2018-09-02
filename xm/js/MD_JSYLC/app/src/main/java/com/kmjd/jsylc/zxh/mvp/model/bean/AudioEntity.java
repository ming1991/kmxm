package com.kmjd.jsylc.zxh.mvp.model.bean;

import java.io.Serializable;

public class AudioEntity implements Serializable{
    /** 背景音乐音量*/
    private float musiceVolume = 0.2f;

    /** 游戏音效音量*/
    private float soundVolume = 0.2f;

    public float getMusiceVolume() {
        return musiceVolume;
    }

    public void setMusiceVolume(float musiceVolume) {
        this.musiceVolume = musiceVolume;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float soundVolume) {
        this.soundVolume = soundVolume;
    }
}
