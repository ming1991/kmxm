package com.kmjd.jsylc.zxh.database.dbutil;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

/**
 * Created by androidshuai on 2017/11/25.
 */

public class DbGeneratorHelper {

    public static void main(String[] args) {

        try {
            //创建数据库
            Schema schema = new Schema(1, "com.kmjd.jsylc.zxh.database.dbbean");

            //保存用户登录信息
            Entity loginInformation = schema.addEntity("LoginInformation");
            loginInformation.addIdProperty();//自增的主键
            String[] strArray = new String[]{"accountId","password"};
            for (String str : strArray) {
                loginInformation.addStringProperty(str);
            }

            //创建会员信息表名
            Entity memberInformation = schema.addEntity("MemberInformation");
            memberInformation.addIdProperty();//自增的主键
            String[] strArray2 = new String[]{"level","money","user","verify"};
            for (String str : strArray2) {
                memberInformation.addStringProperty(str);
            }

            //保存公共消息的id
            Entity publicMessage = schema.addEntity("PublicMessage");
            publicMessage.addIdProperty();//自增的主键
            publicMessage.addStringProperty("publicMessageId");

            new DaoGenerator().generateAll(schema, "D:\\MD\\Projects\\oschina\\JSYLC_CN\\app\\src\\main\\java\\");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
