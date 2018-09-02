package com.kmjd.jsylc.zxh.mvp.model.bean;

import java.util.List;

/**
 * Created by ufly on 2017-12-07.
 */

public class PlatfromIntoBean {
    /**
     * ids : 开放的第三方平台编码
     * m   : 维护中的第三方平台编码
     * w   : 敬请期待第三方平台编码
     */
    private List<String> ids;
    private List<String> m;
    private List<String> w;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getM() {
        return m;
    }

    public void setM(List<String> m) {
        this.m = m;
    }

    public List<String> getW() {
        return w;
    }

    public void setW(List<String> w) {
        this.w = w;
    }

    @Override
    public String toString() {
        return "PlatfromIntoBean{" +
                "ids=" + ids +
                ", m=" + m +
                ", w=" + w +
                '}';
    }
}
