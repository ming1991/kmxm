package com.md.jsyxzs_cn.zym_xs.network.nw_respone_model;

/**
 * Created by androidshuai on 2016/12/9.
 *
 * f_repairNo 生成报修单号，要求唯一
 f_date 日期
 f_phone 手机
 f_type  报修类型
 f_content 报修内容
 f_website 报修网站
 repaireno  报修单号

 GameCode 可以使用26，35，17之一 。单号可以一样，同一部手机使用一个
 */

public class RepairListInfo {

    /**
     * Type : addrepair
     * GameCode : 3
     * RepairModel : {"f_id":0,"f_accounts":"aaaaa","f_repairNo":"1737130806320161209160733","f_date":"2016-12-9 16:08:33","f_time":"1900-1-1 0:00:00","f_state":0,"f_phone":"17371308063","f_admin":"","f_replyAdmin":"","f_type":1,"f_content":"有点卡","f_del":0,"f_replyContent":"","f_webSite":"http://www.baidu.com"}
     * RepairNo : 1737130806320161209160733
     * StartTime : null
     * EndTime : null
     * ID : 0
     */

    private String Type;
    //站点号
    private int GameCode;
    //报修单表的实体
    private RepairModelBean RepairModel;
    //报修的机器码
    private String RepairNo;
    //开始时间
    private String StartTime;
    //结束时间
    private String EndTime;
    //报修单ID
    private int ID;

    public RepairListInfo() {
    }

    public RepairListInfo(int gameCode, String type) {
        GameCode = gameCode;
        Type = type;
    }

    public RepairListInfo(String startTime, String endTime, String repairNo, String type, int gameCode) {
        StartTime = startTime;
        EndTime = endTime;
        RepairNo = repairNo;
        Type = type;
        GameCode = gameCode;
    }

    public RepairListInfo(String type , String repairNo, RepairModelBean repairModel, int id, int gameCode) {
        Type =type;
        RepairNo = repairNo;
        RepairModel = repairModel;
        ID =id;
        GameCode=gameCode;
    }

    public RepairListInfo(String type, int gameCode, RepairModelBean repairModel, String repairNo, String startTime, String endTime, int ID) {
        Type = type;
        GameCode = gameCode;
        RepairModel = repairModel;
        RepairNo = repairNo;
        StartTime = startTime;
        EndTime = endTime;
        this.ID = ID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public int getGameCode() {
        return GameCode;
    }

    public void setGameCode(int GameCode) {
        this.GameCode = GameCode;
    }

    public RepairModelBean getRepairModel() {
        return RepairModel;
    }

    public void setRepairModel(RepairModelBean RepairModel) {
        this.RepairModel = RepairModel;
    }

    public String getRepairNo() {
        return RepairNo;
    }

    public void setRepairNo(String RepairNo) {
        this.RepairNo = RepairNo;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String StartTime) {
        this.StartTime = StartTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String EndTime) {
        this.EndTime = EndTime;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public String toString() {
        return "RepairListInfo{" +
                "Type='" + Type + '\'' +
                ", GameCode=" + GameCode +
                ", RepairModel=" + RepairModel +
                ", RepairNo='" + RepairNo + '\'' +
                ", StartTime='" + StartTime + '\'' +
                ", EndTime='" + EndTime + '\'' +
                ", ID=" + ID +
                '}';
    }

    public static class RepairModelBean {
        /**
         * f_id : 0
         * f_accounts : aaaaa
         * f_repairNo : 1737130806320161209160733
         * f_date : 2016-12-9 16:08:33
         * f_time : 1900-1-1 0:00:00
         * f_state : 0
         * f_phone : 17371308063
         * f_admin :
         * f_replyAdmin :
         * f_type : 1
         * f_content : 有点卡
         * f_del : 0
         * f_replyContent :
         * f_webSite : http://www.baidu.com
         */

        private int f_id;
        //账户
        private String f_accounts;
        //报修单号
        private String f_repairNo;
        //建立日期
        private String f_date;
        //更新日期
        private String f_time;
        //0未处理 1已解决 2处理中 3无法解决
        private int f_state;
        //手机号
        private String f_phone;
        // 处理者
        private String f_admin;
        // 回复者
        private String f_replyAdmin;
        // 问题类型
        private int f_type;
        // 内容
        private String f_content;
        private int f_del;
        // 回复内容
        private String f_replyContent;
        // 网址
        private String f_webSite;

        public RepairModelBean() {
        }

        public RepairModelBean(String f_accounts,String f_repairNo, String f_date, String f_phone, int f_type, String f_content, String f_webSite) {
            this.f_accounts=f_accounts;
            this.f_repairNo = f_repairNo;
            this.f_date = f_date;
            this.f_phone = f_phone;
            this.f_type = f_type;
            this.f_content = f_content;
            this.f_webSite = f_webSite;
        }

        public RepairModelBean(int f_id, String f_accounts, String f_repairNo, String f_date, String f_time, int f_state, String f_phone, String f_admin, String f_replyAdmin, int f_type, String f_content, int f_del, String f_replyContent, String f_webSite) {
            this.f_id = f_id;
            this.f_accounts = f_accounts;
            this.f_repairNo = f_repairNo;
            this.f_date = f_date;
            this.f_time = f_time;
            this.f_state = f_state;
            this.f_phone = f_phone;
            this.f_admin = f_admin;
            this.f_replyAdmin = f_replyAdmin;
            this.f_type = f_type;
            this.f_content = f_content;
            this.f_del = f_del;
            this.f_replyContent = f_replyContent;
            this.f_webSite = f_webSite;
        }

        public int getF_id() {
            return f_id;
        }

        public void setF_id(int f_id) {
            this.f_id = f_id;
        }

        public String getF_accounts() {
            return f_accounts;
        }

        public void setF_accounts(String f_accounts) {
            this.f_accounts = f_accounts;
        }

        public String getF_repairNo() {
            return f_repairNo;
        }

        public void setF_repairNo(String f_repairNo) {
            this.f_repairNo = f_repairNo;
        }

        public String getF_date() {
            return f_date;
        }

        public void setF_date(String f_date) {
            this.f_date = f_date;
        }

        public String getF_time() {
            return f_time;
        }

        public void setF_time(String f_time) {
            this.f_time = f_time;
        }

        public int getF_state() {
            return f_state;
        }

        public void setF_state(int f_state) {
            this.f_state = f_state;
        }

        public String getF_phone() {
            return f_phone;
        }

        public void setF_phone(String f_phone) {
            this.f_phone = f_phone;
        }

        public String getF_admin() {
            return f_admin;
        }

        public void setF_admin(String f_admin) {
            this.f_admin = f_admin;
        }

        public String getF_replyAdmin() {
            return f_replyAdmin;
        }

        public void setF_replyAdmin(String f_replyAdmin) {
            this.f_replyAdmin = f_replyAdmin;
        }

        public int getF_type() {
            return f_type;
        }

        public void setF_type(int f_type) {
            this.f_type = f_type;
        }

        public String getF_content() {
            return f_content;
        }

        public void setF_content(String f_content) {
            this.f_content = f_content;
        }

        public int getF_del() {
            return f_del;
        }

        public void setF_del(int f_del) {
            this.f_del = f_del;
        }

        public String getF_replyContent() {
            return f_replyContent;
        }

        public void setF_replyContent(String f_replyContent) {
            this.f_replyContent = f_replyContent;
        }

        public String getF_webSite() {
            return f_webSite;
        }

        public void setF_webSite(String f_webSite) {
            this.f_webSite = f_webSite;
        }

        @Override
        public String toString() {
            return "RepairModelBean{" +
                    "f_id=" + f_id +
                    ", f_accounts='" + f_accounts + '\'' +
                    ", f_repairNo='" + f_repairNo + '\'' +
                    ", f_date='" + f_date + '\'' +
                    ", f_time='" + f_time + '\'' +
                    ", f_state=" + f_state +
                    ", f_phone='" + f_phone + '\'' +
                    ", f_admin='" + f_admin + '\'' +
                    ", f_replyAdmin='" + f_replyAdmin + '\'' +
                    ", f_type=" + f_type +
                    ", f_content='" + f_content + '\'' +
                    ", f_del=" + f_del +
                    ", f_replyContent='" + f_replyContent + '\'' +
                    ", f_webSite='" + f_webSite + '\'' +
                    '}';
        }
    }
}
