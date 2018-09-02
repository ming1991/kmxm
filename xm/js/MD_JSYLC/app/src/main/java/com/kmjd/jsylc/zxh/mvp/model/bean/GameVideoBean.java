package com.kmjd.jsylc.zxh.mvp.model.bean;

import java.util.List;

public class GameVideoBean {
    //CDN线路
    private OtherLineBean otherLineBean;
    //备用线路
    private SpareLineBean spareLineBean;
    //房间集合
    private List<TypeBean> typeBeanList;

    public GameVideoBean() {
    }

    public OtherLineBean getOtherLineBean() {
        return otherLineBean;
    }

    public void setOtherLineBean(OtherLineBean otherLineBean) {
        this.otherLineBean = otherLineBean;
    }

    public SpareLineBean getSpareLineBean() {
        return spareLineBean;
    }

    public void setSpareLineBean(SpareLineBean spareLineBean) {
        this.spareLineBean = spareLineBean;
    }

    public List<TypeBean> getTypeBeanList() {
        return typeBeanList;
    }

    public void setTypeBeanList(List<TypeBean> typeBeanList) {
        this.typeBeanList = typeBeanList;
    }

    @Override
    public String toString() {
        return "GameVideoBean{" +
                "otherLineBean=" + otherLineBean +
                ", spareLineBean=" + spareLineBean +
                ", typeBeanList=" + typeBeanList +
                '}';
    }

    public class OtherLineBean {
   /* <OTHER_Line help="其他线路" type="CDN" folder="test">
        <V>sun01.acc77.net/385214775</V>
    </OTHER_Line>*/

        private String type;
        private String folder;
        private List<String> v;

        public OtherLineBean(String type, String folder, List<String> v) {
            this.type = type;
            this.folder = folder;
            this.v = v;
        }

        public OtherLineBean(){}

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }

        public List<String> getV() {
            return v;
        }

        public void setV(List<String> v) {
            this.v = v;
        }
    }

    public class SpareLineBean {
//    <SPARE_Line help="备用线路" type="NO" folder="test">
//        <V>jv2.tp33.net:66</V>
//        <V>wv5.tp33.net:66</V>
//        <V>hv1.tp33.net:66</V>
//        <V>hkvd1.tp33.net:66</V>
//        <V>svd1.tp33.net:66</V>
//    </SPARE_Line>

        private String type;
        private String folder;
        private List<String> vBean;

        public SpareLineBean() {
        }

        public SpareLineBean(String type, String folder, List<String> vBean) {
            this.type = type;
            this.folder = folder;
            this.vBean = vBean;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }

        public List<String> getvBean() {
            return vBean;
        }

        public void setvBean(List<String> vBean) {
            this.vBean = vBean;
        }
    }

    public class TypeBean {
        /*<Type name="LP" gameFQ="1" line="5" liveFQ="*/

        private String name;
        private List<FNbean> liveFQ;

        public TypeBean(){}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<FNbean> getLiveFQ() {
            return liveFQ;
        }

        public void setLiveFQ(List<FNbean> liveFQ) {
            this.liveFQ = liveFQ;
        }

        public class FNbean {
            private String folder;
            private String live;

            public FNbean() {
            }

            public FNbean(String folder, String live) {
                this.folder = folder;
                this.live = live;
            }

            public String getFolder() {
                return folder;
            }

            public void setFolder(String folder) {
                this.folder = folder;
            }

            public String getLive() {
                return live;
            }

            public void setLive(String name) {
                this.live = name;
            }
        }
    }
}
