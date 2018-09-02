package com.md.jsyxzs_cn.zym_xs.network.nw_respone_model;

/**
 * Created by androidshuai on 2016/12/12.
 */

public class RepairListLink {
    // 友情连接ID(int(4))
    private int f_Id;
    // 友情连接图片(nvarchar(300))
    private String f_ImageUrl;
    /// 友情连接URL(nvarchar(300))
    private String f_LinkUrl;
    // 排列序号(int(4))
    private int f_Number;

    public RepairListLink() {
    }

    public RepairListLink(int f_Id, String f_ImageUrl, String f_LinkUrl, int f_Number) {
        this.f_Id = f_Id;
        this.f_ImageUrl = f_ImageUrl;
        this.f_LinkUrl = f_LinkUrl;
        this.f_Number = f_Number;
    }

    public int getF_Id() {
        return f_Id;
    }

    public void setF_Id(int f_Id) {
        this.f_Id = f_Id;
    }

    public String getF_ImageUrl() {
        return f_ImageUrl;
    }

    public void setF_ImageUrl(String f_ImageUrl) {
        this.f_ImageUrl = f_ImageUrl;
    }

    public String getF_LinkUrl() {
        return f_LinkUrl;
    }

    public void setF_LinkUrl(String f_LinkUrl) {
        this.f_LinkUrl = f_LinkUrl;
    }

    public int getF_Number() {
        return f_Number;
    }

    public void setF_Number(int f_Number) {
        this.f_Number = f_Number;
    }

    @Override
    public String toString() {
        return "RepairListLink{" +
                "f_Id=" + f_Id +
                ", f_ImageUrl='" + f_ImageUrl + '\'' +
                ", f_LinkUrl='" + f_LinkUrl + '\'' +
                ", f_Number=" + f_Number +
                '}';
    }
}
