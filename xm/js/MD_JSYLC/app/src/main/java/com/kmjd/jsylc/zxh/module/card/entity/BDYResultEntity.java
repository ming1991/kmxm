package com.kmjd.jsylc.zxh.module.card.entity;

public class BDYResultEntity {
    private String string;

    private Result result;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        private String bank_card_number;

        private String bank_name;

        //银行卡类型：0：不能识别， 1：借记卡， 2：信用卡
        private int bank_card_type;

        public String getBank_card_number() {
            return bank_card_number;
        }

        public void setBank_card_number(String bank_card_number) {
            this.bank_card_number = bank_card_number;
        }

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public int getBank_card_type() {
            return bank_card_type;
        }

        public void setBank_card_type(int bank_card_type) {
            this.bank_card_type = bank_card_type;
        }

        public String getCardType(){
            String result = "";
            switch (bank_card_type){
                case 0:
                    result = "不能识别";
                    break;
                case 1:
                    result = "借记卡";
                    break;
                case 2:
                    result = "信用卡";
                    break;
            }
            return result;
        }
    }

}
