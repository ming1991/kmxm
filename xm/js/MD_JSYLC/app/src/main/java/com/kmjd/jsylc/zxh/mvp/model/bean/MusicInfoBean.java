package com.kmjd.jsylc.zxh.mvp.model.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Android-mwb on 2018/7/14.
 */
public class MusicInfoBean implements Serializable{
    private List<GameTypeBean> list;

    public MusicInfoBean(){}

    public List<GameTypeBean> getList() {
        return list;
    }

    public void setList(List<GameTypeBean> list) {
        this.list = list;
    }

    public class GameTypeBean {
        private String name;
        private List<MusicDurationBean> list;

        public GameTypeBean(){}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<MusicDurationBean> getList() {
            return list;
        }

        public void setList(List<MusicDurationBean> list) {
            this.list = list;
        }

        public class MusicDurationBean {
            private String name;
            private long time;

            public MusicDurationBean() {}

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }

        }

    }
}
