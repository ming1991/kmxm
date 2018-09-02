package com.kmjd.jsylc.zxh.module.card.entity;

public class JISUResultEntity {
    private String status;

    private String msg;

    private CardDetailEntity result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * <p>isSuccess</p>
     * @return 请求是否成功
     */
    public boolean isSuccess(){
        return status.equals("0");
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CardDetailEntity getResult() {
        return result;
    }

    public void setResult(CardDetailEntity result) {
        this.result = result;
    }
}
