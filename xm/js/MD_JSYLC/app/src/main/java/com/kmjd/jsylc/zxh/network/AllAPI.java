package com.kmjd.jsylc.zxh.network;


/**
 * Created by Android-d on 2017/11/23.
 */

public interface AllAPI {

    //测试站的domin
    String[] DOMAIN_NAME_CN_TEST = new String[]{
            "http://test26.tz111.net/",
            "http://119.81.201.156:6026/"
    };

    //正式站的domin
    String[] DOMAIN_NAME_CN_REAL = new String[]{
            "http://161.202.204.53:6017/"
    };
    //http://wv1.tp33.net:66/CNGameTypeXML.xml 正试站66
    //http://wv1.tp33.net:67/CNGameTypeXML.xml 测试站67
    String PARSEURL_REAL = "http://wv1.tp33.net:66/CNGameTypeXML.xml";
    String PARSEURL_TEST = "http://wv1.tp33.net:67/CNGameTypeXML.xml";
    
    String BASE_MIDDLE_URL = "ApiPlatform.aspx?f=mdApp&";
    String BASE_MIDDLE_V_URL = "ApiPlatform.aspx?f=mdApp&v=";
    String LOGOUT = "Mobile/login.aspx?type=Out";//登出
    String QQ_PTSY = "apiplatform.aspx?go=m&n=20";//全球彩票web里面的平台首页
    String QQ_ICON = "Mobile/Aspx/M_Index.aspx";//全球彩票web里面的首页图标返回按钮
    String LIVE_PERSON_ICON ="Mobile/Aspx/M_Index.aspx";//九卅真人web的主要图标
    String EAMIL_GG = "https://activity.sifuhe.cn/activity/index?";//广告地址
    String OFFLINE = "about:out";//被挤下线(点击原生控件加载web)
    String OFFLINE2 = "Mobile/login.aspx";//被挤下线(在web里点击加载web)

    //讨论区
    String TLQ_URL = BASE_MIDDLE_URL + "go=b&n=1&originURL=JiuSaj://";
    //忘记密码
    String WJMM_URL = BASE_MIDDLE_URL + "go=m&n=1&originURL=JiuSaj://";
    //会员注册
    String HYZC_URL = BASE_MIDDLE_URL + "go=m&n=2&originURL=JiuSaj://";
    //产品信息
    String CPXX_URL = BASE_MIDDLE_URL + "go=m&n=3&originURL=JiuSaj://";
    //最新优惠
    String ZXYH_URL = BASE_MIDDLE_URL + "go=m&n=4&originURL=JiuSaj://";
    //关于我们
    String GYWM_URL = BASE_MIDDLE_URL + "go=m&n=5&originURL=JiuSaj://";
    //帮助中心
    String BZZX_URL = BASE_MIDDLE_URL + "go=m&n=6&originURL=JiuSaj://";
    //客服中心
    String KFZX_URL_LOGIN = BASE_MIDDLE_URL + "go=m&n=7&originURL=JiuSaj://";

    //讨论区V
    String TLQ_V_URL = "&go=b&n=1&originURL=JiuSaj://";
    //最新优惠V <WebFragment>
    String ZXYH_V_URL = "&go=m&n=4&originURL=JiuSaj://";
    //关于我们V <WebFragment>
    String GYWM_V_URL = "&go=m&n=5&originURL=JiuSaj://";
    //帮助中心V <WebFragment>
    String BZZX_V_URL = "&go=m&n=6&originURL=JiuSaj://";
    //APP下载V <WebFragment>
    String APP_V_URL = "&go=m&n=8&originURL=JiuSaj://";
    //返回电脑版V
   // String FHDNB_URL = "&go=m&n=9&originURL=JiuSaj://";


    //变更资料(会员资料) <WebFragment>
    String BGZL_URL = "&go=m&n=11&originURL=JiuSaj://";
    //交易记录 <WebFragment>
    String JYJL_URL = "&go=m&n=12&originURL=JiuSaj://";
    //会员互转 <WebFragment>
    String HYHZ_URL = "&go=m&n=13&originURL=JiuSaj://";
    //公告专区 <WebFragment>
    String GGZQ_URL = "&go=m&n=14&originURL=JiuSaj://";
    //活动点数 <WebFragment>
    String HDDS_URL = "&go=m&n=15&originURL=JiuSaj://";
    //投诉箱 <WebFragment>
    String TSX_URL = "&go=m&n=10&originURL=JiuSaj://";
    //现场转播
    String XCZB_URL = "&go=s&n=3&originURL=JiuSaj://";
    //免费影城
    String MFYC_URL = "&go=b&n=2&originURL=JiuSaj://";

    //web版的首页面
    String WEB_HOME_URL = "Mobile/Aspx/M_Index.aspx";
}
