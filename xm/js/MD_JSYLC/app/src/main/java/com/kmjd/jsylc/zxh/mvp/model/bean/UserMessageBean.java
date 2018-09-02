package com.kmjd.jsylc.zxh.mvp.model.bean;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ufly on 2017-12-01.
 */

public class UserMessageBean {

    /**
     * personal : 个人讯息  （personal=null 时无未读个人讯息）
     * c : 0   状态码（0=成功，-1=失败，1=无讯息）
     * public : 公共讯息集合(公共讯息已读未读需在客端记录)
     */

    private PersonalBean personal;
    private int c;
    @SerializedName("public")
    private List<PublicBean> publicX;
    private ImptBean impt;

    public PersonalBean getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalBean personal) {
        this.personal = personal;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public List<PublicBean> getPublicX() {
        return publicX;
    }

    public void setPublicX(List<PublicBean> publicX) {
        this.publicX = publicX;
    }

    public ImptBean getImpt() {
        return impt;
    }

    public void setImpt(ImptBean impt) {
        this.impt = impt;
    }

    public static class PersonalBean {
        /**
         * id : 讯息的id
         * title : 讯息的标题
         * total : 个人讯息未读数
         * firstshow : firstshow=1 时优先显示， firstshow=0 时根据个人讯息和公共讯息的id大小比较，id值大的优先显示
         */

        private int id;
        private String title;
        private int total;
        private int firstshow;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getFirstshow() {
            return firstshow;
        }

        public void setFirstshow(int firstshow) {
            this.firstshow = firstshow;
        }
    }

    public static class PublicBean {
        /**
         * id : 公共讯息Id
         * title : 公共讯息标题
         */

        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "PublicBean{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
    public static class ImptBean {
        private String id;
        private String content;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "ImptBean{" +
                    "id='" + id + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserMessageBean{" +
                "personal=" + personal +
                ", c=" + c +
                ", publicX=" + publicX +
                ", impt=" + impt +
                '}';
    }
}
