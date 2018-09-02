package com.kmjd.jsylc.zxh.mvp.model.event;

/**
 * Created by androidshuai on 2017/12/8.
 */

public class GiftEvent {

    private Boolean isShowBirthday;

    public GiftEvent(Boolean isShowBirthday) {
        this.isShowBirthday = isShowBirthday;
    }

    public Boolean getShowBirthday() {
        return isShowBirthday;
    }

    public void setShowBirthday(Boolean showBirthday) {
        isShowBirthday = showBirthday;
    }

    @Override
    public String toString() {
        return "GiftEvent{" +
                "isShowBirthday=" + isShowBirthday +
                '}';
    }
}
