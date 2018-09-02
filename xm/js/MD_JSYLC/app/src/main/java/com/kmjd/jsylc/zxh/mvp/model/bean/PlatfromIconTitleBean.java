package com.kmjd.jsylc.zxh.mvp.model.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by ufly on 2017-12-05.
 */

public class PlatfromIconTitleBean implements Serializable {

    /**
     * c : 0
     * msg : success
     * data : [{"c":"s1","t":"九卅體育","i":"/Mobile1/images/main/icon_liveGame_jiuzhou.png","d": "九卅體育提供超過100種投注玩法，超高水位，超快結算，隨時隨地都能感受體育賽事的熱血刺激。"}]
     * homedata : {"sport":{"name":"体育博彩","des":"九卅体育 亚投体育","children":["s1","g16"],"mtip": "<p>体育博彩正在维护中，</p><p>请至其他游戏投注，谢谢！</p>"}}
     */

    private int c;
    private String msg;
    private HomedataBean homedata;
    private List<DataBean> data;

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

    public HomedataBean getHomedata() {
        return homedata;
    }

    public void setHomedata(HomedataBean homedata) {
        this.homedata = homedata;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class HomedataBean {
        /**
         * sport : {"name":"体育博彩","des":","children":["s1","g16"],"mtip":""}
         * live : {"name":"真人游戏","des":"","children":["f1","g10","g14","g12","g11","g15"],"mtip":""}
         * games : {"name":"电子游戏","des":"","children":["g2","g18","g3","g5","g7","g17","g4","g8"],"mtip":"<p>体育博彩正在维护中，</p><p>请至其他游戏投注，谢谢！<\/p>"}
         * ball : {"name":"全球彩票","des":"","children":["g1"],"mtip":""}
         * person : {"name":"九卅真人","des":"","children":["g1"],"mtip":""}
         */

        private SportBean sport;
        private LiveBean live;
        private JzflashBean jzflash;
        private GamesBean games;
        private BallBean ball;


        public SportBean getSport() {
            return sport;
        }

        public void setSport(SportBean sport) {
            this.sport = sport;
        }

        public LiveBean getLive() {
            return live;
        }

        public void setLive(LiveBean live) {
            this.live = live;
        }

        public GamesBean getGames() {
            return games;
        }

        public void setGames(GamesBean games) {
            this.games = games;
        }

        public BallBean getBall() {
            return ball;
        }

        public void setBall(BallBean ball) {
            this.ball = ball;
        }

        public JzflashBean getJzflash() {
            return jzflash;
        }

        public void setJzflash(JzflashBean jzflash) {
            this.jzflash = jzflash;
        }

        public static class SportBean {
            /**
             * "name" : "体育博彩"
             * "des" : "九卅体育 亚投体育"
             * "children" : ["s1",g16]
             * "mtip" : "<p>体育博彩正在维护中，</p><p>请至其他游戏投注，谢谢！</p>"
             */

            private String name;
            private String des;
            private String mtip;
            private List<String> children;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }

            public String getMtip() {
                return mtip;
            }

            public void setMtip(String mtip) {
                this.mtip = mtip;
            }

            public List<String> getChildren() {
                return children;
            }

            public void setChildren(List<String> children) {
                this.children = children;
            }

            @Override
            public String toString() {
                return "SportBean{" +
                        "name='" + name + '\'' +
                        ", des='" + des + '\'' +
                        ", mtip='" + mtip + '\'' +
                        ", children=" + children +
                        '}';
            }
        }

        public static class LiveBean {
            /**
             * children : ["f1","g10","g14","g12","g11","g15"]
             */

            private String name;
            private String des;
            private String mtip;
            private List<String> children;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }

            public String getMtip() {
                return mtip;
            }

            public void setMtip(String mtip) {
                this.mtip = mtip;
            }

            public List<String> getChildren() {
                return children;
            }

            public void setChildren(List<String> children) {
                this.children = children;
            }

            @Override
            public String toString() {
                return "LiveBean{" +
                        "name='" + name + '\'' +
                        ", des='" + des + '\'' +
                        ", mtip='" + mtip + '\'' +
                        ", children=" + children +
                        '}';
            }
        }

        public static class GamesBean {
            /**
             * children : ["g2","g18","g3","g5","g7","g17","g4","g8"]
             */

            private String name;
            private String des;
            private String mtip;
            private List<String> children;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }

            public String getMtip() {
                return mtip;
            }

            public void setMtip(String mtip) {
                this.mtip = mtip;
            }

            public List<String> getChildren() {
                return children;
            }

            public void setChildren(List<String> children) {
                this.children = children;
            }

            @Override
            public String toString() {
                return "GamesBean{" +
                        "name='" + name + '\'' +
                        ", des='" + des + '\'' +
                        ", mtip='" + mtip + '\'' +
                        ", children=" + children +
                        '}';
            }
        }

        public static class BallBean {
            /**
             * children : ["g1"]
             */

            private String name;
            private String des;
            private String mtip;
            private List<String> children;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }

            public String getMtip() {
                return mtip;
            }

            public void setMtip(String mtip) {
                this.mtip = mtip;
            }

            public List<String> getChildren() {
                return children;
            }

            public void setChildren(List<String> children) {
                this.children = children;
            }

            @Override
            public String toString() {
                return "BallBean{" +
                        "name='" + name + '\'' +
                        ", des='" + des + '\'' +
                        ", mtip='" + mtip + '\'' +
                        ", children=" + children +
                        '}';
            }
        }

        public static class JzflashBean {
            /**
             * name : 九卅真人
             * children : ["f1"]
             * mtip : <p>涔濆崊鐪熶汉姝ｅ湪缁存姢涓紝</p><p>璇峰厛鑷虫湰缃戠珯鍏朵粬娓告垙鎶曟敞锛岃阿璋紒</p>
             */

            private String name;
            private String mtip;
            private List<String> children;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMtip() {
                return mtip;
            }

            public void setMtip(String mtip) {
                this.mtip = mtip;
            }

            public List<String> getChildren() {
                return children;
            }

            public void setChildren(List<String> children) {
                this.children = children;
            }

            @Override
            public String toString() {
                return "JzflashBean{" +
                        "name='" + name + '\'' +
                        ", mtip='" + mtip + '\'' +
                        ", children=" + children +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "HomedataBean{" +
                    "sport=" + sport +
                    ", live=" + live +
                    ", jzflash=" + jzflash +
                    ", games=" + games +
                    ", ball=" + ball +
                    '}';
        }
    }

    public static class DataBean implements Serializable {
        /**
         * c : s1
         * t : "九卅体育"
         * "st" : "主帐户"
         * "i" : "/Mobile/images/main/icon_liveGame_jiuzhou.png"
         * "d" : "九卅体育提供超过100种投注玩法，超高水位，超快结算，随时随地都能感受体育赛事的热血刺激。"
         */

        private String c;
        private String t;
        private String st;
        private String i;
        private String d;

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getT() {
            return t;
        }

        public void setT(String t) {
            this.t = t;
        }

        public String getSt() {
            return st;
        }

        public void setSt(String st) {
            this.st = st;
        }

        public String getI() {
            return i;
        }

        public void setI(String i) {
            this.i = i;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "c='" + c + '\'' +
                    ", t='" + t + '\'' +
                    ", st='" + st + '\'' +
                    ", i='" + i + '\'' +
                    ", d='" + d + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DataBean)) return false;

            DataBean dataBean = (DataBean) o;

            return c.equals(dataBean.c);
        }

        @Override
        public int hashCode() {
            return c.hashCode();
        }

    }

    @Override
    public String toString() {
        return "PlatfromIconTitleBean{" +
                "c=" + c +
                ", msg='" + msg + '\'' +
                ", homedata=" + homedata +
                ", data=" + data +
                '}';
    }
}
