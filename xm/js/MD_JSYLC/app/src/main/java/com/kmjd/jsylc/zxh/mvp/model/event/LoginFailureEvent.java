package com.kmjd.jsylc.zxh.mvp.model.event;

/**
 * Created by androidshuai on 2017/12/22.
 */

public class LoginFailureEvent {

    private String verify;

    private String failuremessage;

    public LoginFailureEvent(String verify, String failuremessage) {
        this.verify = verify;
        this.failuremessage = failuremessage;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getFailuremessage() {
        return failuremessage;
    }

    public void setFailuremessage(String failuremessage) {
        this.failuremessage = failuremessage;
    }

    @Override
    public String toString() {
        return "LoginFailureEvent{" +
                "verify='" + verify + '\'' +
                ", failuremessage='" + failuremessage + '\'' +
                '}';
    }
}
