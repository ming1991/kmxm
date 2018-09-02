package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by Android-d on 2017/12/7.
 */

public class QQCP_Bean_api10 {

    /**
     * id : g1
     * sta : 2
     * tip : <p>您沒有進入六合彩球的權限，</p><p>請至本平臺其他項目投注，謝謝！</p>
     */

    private String id;
    private int sta;
    private String tip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSta() {
        return sta;
    }

    public void setSta(int sta) {
        this.sta = sta;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "QQCP_Bean_api10{" +
                "id='" + id + '\'' +
                ", sta=" + sta +
                ", tip='" + tip + '\'' +
                '}';
    }
}
