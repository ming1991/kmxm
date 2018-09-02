package com.kmjd.jsylc.zxh.mvp.model.bean;

import java.util.List;

/**
 * Created by androidshuai on 2017/12/4.
 */

public class FunctionIsOpenBean {

    /**
     * c : 0
     * msg : success
     * data : {"trans":1,"deposit":0,"deposit_p":1,"purse":1,"withdraw":1,"withdraw_p":0,"member":1,"validate":0,"spotlive":1,"tv":0,"bbs":0,"complant":1,"discountcode":1,"tip":["","","","","","","","bgfbfgb23","","","您的手机未验证，请先进行手机验证。4222"],"memberinfo":{"level":3,"money":"198,516"}}
     * purse : 1
     * deposit : 0
     * trans : 1
     */

    private int c;
    private String msg;
    private String tip;
    private DataBean data;
    private int purse;
    private int deposit;
    private int trans;

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

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getPurse() {
        return purse;
    }

    public void setPurse(int purse) {
        this.purse = purse;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public int getTrans() {
        return trans;
    }

    public void setTrans(int trans) {
        this.trans = trans;
    }

    public static class DataBean {
        /**
         * trans : 1
         * deposit : 0
         * deposit_p : 1
         * purse : 1
         * withdraw : 1
         * withdraw_p : 0
         * member : 1
         * validate : 0
         * spotlive : 1
         * tv : 0
         * bbs : 0
         * complant : 1
         * discountcode : 1
         * tip : ["","","","","","","","bgfbfgb23","","","您的手机未验证，请先进行手机验证。4222"]
         * memberinfo : {"level":3,"money":"198,516"}
         */

        private int trans;
        private int deposit;
        private int deposit_p;
        private int purse;
        private int withdraw;
        private int withdraw_p;
        private int member;
        private int validate;
        private int spotlive;
        private int tv;
        private int bbs;
        private int complant;
        private int discountcode;
        private MemberinfoBean memberinfo;
        private List<String> tip;

        public int getTrans() {
            return trans;
        }

        public void setTrans(int trans) {
            this.trans = trans;
        }

        public int getDeposit() {
            return deposit;
        }

        public void setDeposit(int deposit) {
            this.deposit = deposit;
        }

        public int getDeposit_p() {
            return deposit_p;
        }

        public void setDeposit_p(int deposit_p) {
            this.deposit_p = deposit_p;
        }

        public int getPurse() {
            return purse;
        }

        public void setPurse(int purse) {
            this.purse = purse;
        }

        public int getWithdraw() {
            return withdraw;
        }

        public void setWithdraw(int withdraw) {
            this.withdraw = withdraw;
        }

        public int getWithdraw_p() {
            return withdraw_p;
        }

        public void setWithdraw_p(int withdraw_p) {
            this.withdraw_p = withdraw_p;
        }

        public int getMember() {
            return member;
        }

        public void setMember(int member) {
            this.member = member;
        }

        public int getValidate() {
            return validate;
        }

        public void setValidate(int validate) {
            this.validate = validate;
        }

        public int getSpotlive() {
            return spotlive;
        }

        public void setSpotlive(int spotlive) {
            this.spotlive = spotlive;
        }

        public int getTv() {
            return tv;
        }

        public void setTv(int tv) {
            this.tv = tv;
        }

        public int getBbs() {
            return bbs;
        }

        public void setBbs(int bbs) {
            this.bbs = bbs;
        }

        public int getComplant() {
            return complant;
        }

        public void setComplant(int complant) {
            this.complant = complant;
        }

        public int getDiscountcode() {
            return discountcode;
        }

        public void setDiscountcode(int discountcode) {
            this.discountcode = discountcode;
        }

        public MemberinfoBean getMemberinfo() {
            return memberinfo;
        }

        public void setMemberinfo(MemberinfoBean memberinfo) {
            this.memberinfo = memberinfo;
        }

        public List<String> getTip() {
            return tip;
        }

        public void setTip(List<String> tip) {
            this.tip = tip;
        }

        public static class MemberinfoBean {
            /**
             * level : 3
             * money : 198,516
             */

            private int level;
            private String money;

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public String getMoney() {
                return money;
            }

            public void setMoney(String money) {
                this.money = money;
            }

            @Override
            public String toString() {
                return "MemberinfoBean{" +
                        "level=" + level +
                        ", money='" + money + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "trans=" + trans +
                    ", deposit=" + deposit +
                    ", deposit_p=" + deposit_p +
                    ", purse=" + purse +
                    ", withdraw=" + withdraw +
                    ", withdraw_p=" + withdraw_p +
                    ", member=" + member +
                    ", validate=" + validate +
                    ", spotlive=" + spotlive +
                    ", tv=" + tv +
                    ", bbs=" + bbs +
                    ", complant=" + complant +
                    ", discountcode=" + discountcode +
                    ", memberinfo=" + memberinfo +
                    ", tip=" + tip +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FunctionIsOpenBean{" +
                "c=" + c +
                ", msg='" + msg + '\'' +
                ", tip='" + tip + '\'' +
                ", data=" + data +
                ", purse=" + purse +
                ", deposit=" + deposit +
                ", trans=" + trans +
                '}';
    }
}
