package com.kmjd.wcqp.single.zxh.database;

public class PersonInfom {
    public static final int TYPE_ONE=0;
    public static final int TYPE_TWO=1;
    private int type=TYPE_ONE;
    private String leftContent;
    private String rightContent;
    private int image;

    public PersonInfom(int type, String leftContent,String rightContent, int image) {
        this.type = type;
        this.leftContent = leftContent;
        this.rightContent = rightContent;
        this.image = image;
    }

    public static int getTypeOne() {
        return TYPE_ONE;
    }

    public static int getTypeTwo() {
        return TYPE_TWO;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLeftContent() {
        return leftContent;
    }

    public void setLeftContent(String leftContent) {
        this.leftContent = leftContent;
    }

    public String getRightContent() {
        return rightContent;
    }

    public void setRightContent(String rightContent) {
        this.rightContent = rightContent;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
