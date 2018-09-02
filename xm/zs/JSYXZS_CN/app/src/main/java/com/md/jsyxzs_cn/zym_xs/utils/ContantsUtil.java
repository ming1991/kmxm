package com.md.jsyxzs_cn.zym_xs.utils;

/**
 * Created by androidshuai on 2018/5/24.
 * 下载地址
 *  http://down.wb333.net/Download/app/app-tools_cn.apk
    http://down.wb333.net/Download/app/app-tools_tw.apk

 *
 * 测试的
 *  appid: TSTool
 *  appkey：AKID4TtwNUFJg8oU9m4XDiJAaFofQ0KJEpCQ
 *  apihost:http://119.81.201.156:8066/
 *
 * 正式的
 *  appid: TSTool
    appkey: AKID4TtwNUFJg8oU9m4XDiJAaFofQ0KJEpCQ
    apihost:
        http://cf.bvf11.com/
        http://in.bvf11.com/
        http://ec.bvf11.com/
        http://to.bvf11.com/
        http://aw.bvf11.com/
 */

public interface ContantsUtil {

    String APPID = "TSTool";

    String APPKEY = "AKID4TtwNUFJg8oU9m4XDiJAaFofQ0KJEpCQ";

    //获取备用地址的pathQuery
    String BACKUPLINKPATHQUERY = "api/WebsiteUrl/GetBackupLink?site={0}&type={1}";

    //查询保修单列表的path
    String QUERYREPAIRPATH = "api/GameAssistant/QueryRepair";

    //添加报修单的path
    String ADDREPAIRPATH = "api/GameAssistant/AddRepair";

    //获取游戏助手客户端的版本号的path
    String CHECKVERSIONPATH = "api/GameAssistant/CheckVersion";

    //http的GET请求
    String GET = "GET";

    //http的post请求
    String POST = "POST";
}
