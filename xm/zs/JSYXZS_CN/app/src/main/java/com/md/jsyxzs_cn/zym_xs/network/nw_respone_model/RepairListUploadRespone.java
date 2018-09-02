package com.md.jsyxzs_cn.zym_xs.network.nw_respone_model;

import java.util.List;

/**
 * Created by androidshuai on 2016/12/10.
 * ReturnJson 的摘要说明
 */

public class RepairListUploadRespone {

    /**
     * Status : 0
     * Drrdesc : 成功
     * RepairList : null
     * LinkList : null
     */

    //状态 0(成功) 1(失败) 2（参数错误） 3（手机号码格式不正确）
    private int Status;
    // 状态信息
    private String Drrdesc;
    // 报修单列表
    private List<RepairListInfo.RepairModelBean> RepairList;
    // 检测网址列表
    private List<RepairListLink> LinkList;

    public RepairListUploadRespone() {
    }

    public RepairListUploadRespone(int status, String drrdesc, List<RepairListInfo.RepairModelBean> repairList, List<RepairListLink> linkList) {
        Status = status;
        Drrdesc = drrdesc;
        RepairList = repairList;
        LinkList = linkList;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getDrrdesc() {
        return Drrdesc;
    }

    public void setDrrdesc(String drrdesc) {
        Drrdesc = drrdesc;
    }

    public List<RepairListInfo.RepairModelBean> getRepairList() {
        return RepairList;
    }

    public void setRepairList(List<RepairListInfo.RepairModelBean> repairList) {
        RepairList = repairList;
    }

    public List<RepairListLink> getLinkList() {
        return LinkList;
    }

    public void setLinkList(List<RepairListLink> linkList) {
        LinkList = linkList;
    }

    @Override
    public String toString() {
        return "RepairListUploadRespone{" +
                "Status=" + Status +
                ", Drrdesc='" + Drrdesc + '\'' +
                ", RepairList=" + RepairList +
                ", LinkList=" + LinkList +
                '}';
    }
}
