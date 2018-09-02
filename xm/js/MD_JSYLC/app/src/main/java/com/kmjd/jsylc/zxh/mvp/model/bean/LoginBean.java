package com.kmjd.jsylc.zxh.mvp.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by androidshuai on 2017/11/23.
 *
 *
 * LoginBean{c=-1, msg='一小时内错误次数超过5次，请一小时后重试', data=null}
 */

public class LoginBean {

    /**
     * c : 0
     * msg : success
     * data : {"level":"白金会员","money":"498,265","user":"498,265","verify":"gaYGVxQs9r6vCLfekLXFFRmC7GJWLJj+3EgisEuza5fDlEfZGc7+XdVnUDIDXryqtz+MkrGxqBP0/4xozP7WIA9Evq/IsLs+Aftwp6aaA0jVMe3IOs9DfL0yjTwwTy8lrJ8mR+uD6RSP+Skun66EIWw/Rs2c5+s4RY/eHUHAY4I="}
     */

    private int c;
    private String msg;
    private DataBean data;

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable {
        /**
         * level : 白金会员
         * money : 498,265
         * user : 498,265
         * verify : gaYGVxQs9r6vCLfekLXFFRmC7GJWLJj+3EgisEuza5fDlEfZGc7+XdVnUDIDXryqtz+MkrGxqBP0/4xozP7WIA9Evq/IsLs+Aftwp6aaA0jVMe3IOs9DfL0yjTwwTy8lrJ8mR+uD6RSP+Skun66EIWw/Rs2c5+s4RY/eHUHAY4I=
         */

        private String level;
        private String money;
        private String user;
        private String verify;
        private int ismajordomo;//是否是测试账号(1 测试账号  0 正常账号)

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getVerify() {
            return verify;
        }

        public void setVerify(String verify) {
            this.verify = verify;
        }

        public int getIsmajordomo() {
            return ismajordomo;
        }

        public void setIsmajordomo(int ismajordomo) {
            this.ismajordomo = ismajordomo;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "level='" + level + '\'' +
                    ", money='" + money + '\'' +
                    ", user='" + user + '\'' +
                    ", verify='" + verify + '\'' +
                    ", ismajordomo=" + ismajordomo +
                    '}';
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.level);
            dest.writeString(this.money);
            dest.writeString(this.user);
            dest.writeString(this.verify);
            dest.writeInt(this.ismajordomo);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.level = in.readString();
            this.money = in.readString();
            this.user = in.readString();
            this.verify = in.readString();
            this.ismajordomo = in.readInt();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "c=" + c +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
