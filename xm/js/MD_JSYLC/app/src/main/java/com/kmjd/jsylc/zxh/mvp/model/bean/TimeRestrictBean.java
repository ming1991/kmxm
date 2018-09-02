package com.kmjd.jsylc.zxh.mvp.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by androidshuai on 2017/12/12.
 *
 * 进入免费影城和讨论区是否超过10秒的时间限制的javabean
 */

public class TimeRestrictBean implements Parcelable {
    /**
     * title
     * url
     * isable
     * superTime
     */
    private String title;

    private String url;

    private Boolean isable;

    private String superTime;

    public TimeRestrictBean(String title, String url, Boolean isable, String superTime) {
        this.title = title;
        this.url = url;
        this.isable = isable;
        this.superTime = superTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsable() {
        return isable;
    }

    public void setIsable(Boolean isable) {
        this.isable = isable;
    }

    public String getSuperTime() {
        return superTime;
    }

    public void setSuperTime(String superTime) {
        this.superTime = superTime;
    }

    @Override
    public String toString() {
        return "TimeRestrictBean{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", isable=" + isable +
                ", superTime='" + superTime + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeValue(this.isable);
        dest.writeString(this.superTime);
    }

    protected TimeRestrictBean(Parcel in) {
        this.title = in.readString();
        this.url = in.readString();
        this.isable = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.superTime = in.readString();
    }

    public static final Creator<TimeRestrictBean> CREATOR = new Creator<TimeRestrictBean>() {
        @Override
        public TimeRestrictBean createFromParcel(Parcel source) {
            return new TimeRestrictBean(source);
        }

        @Override
        public TimeRestrictBean[] newArray(int size) {
            return new TimeRestrictBean[size];
        }
    };
}
