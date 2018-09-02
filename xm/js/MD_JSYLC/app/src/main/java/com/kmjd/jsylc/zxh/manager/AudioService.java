package com.kmjd.jsylc.zxh.manager;

public interface AudioService {

    void onEnterGame(String gameFlag);

    boolean hasGameFlag(String gameFlag);

    void onSJDTSoundPlay(String gameFlag, String mp3Name);

    boolean isSJDTGame();

    void onSoundPlayStart(String gameFlag, final String mp3Name);

    void setMusicVolume(Float volume);

    void setSoundVolume(Float volume);

    void onStopMusic();

    void onStopSound();

    void onRelease();

    void onAllPause();

    void onAllResume();

}
