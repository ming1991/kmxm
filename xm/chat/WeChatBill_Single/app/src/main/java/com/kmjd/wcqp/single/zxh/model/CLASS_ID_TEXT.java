package com.kmjd.wcqp.single.zxh.model;

/**
 * Created by Android-Star on 2017/8/23.
 */

public class CLASS_ID_TEXT {

    //微信包名
    public static final String PACKAGE_NAME_WX = "com.tencent.mm";
    //双开助手包名
    public static final String PACKAGE_NAME_SKZS = "com.excelliance.dualaid";

    //----------------所有的返回键--------------------------------------------------------------------
    //所有的返回键
    //TODO 1) 6.6.6 com.tencent.mm:id/hx -> com.tencent.mm:id/i1
    public static final String ID_ALL_BACK = "com.tencent.mm:id/i1";

    //----------------首页----------------------------------------------------------------------------
    //首页类名
    public static final String CLASS_SY_WX = "com.tencent.mm.ui.LauncherUI";
    //“通讯录”和“我”
    //TODO 2) 6.6.6 com.tencent.mm:id/ca5 -> com.tencent.mm:id/c9f
    public static final String ID_TXL_AND_ME = "com.tencent.mm:id/c9f";
    //"通讯录"
    public static final String TEXT_TXL = "通讯录";
    //"我”
    public static final String TEXT_ME = "我";

    //"新的朋友"
    public static final String TEXT_XDPY = "新的朋友";
    //"朋友推荐"
    public static final String TEXT_PYTJ = "朋友推荐";

    //“微信号”
    //TODO 3) 6.6.6 com.tencent.mm:id/cdh  -> com.tencent.mm:id/cby
    public static final String ID_WXH = "com.tencent.mm:id/cby";
    //"钱包“
    public static final String ID_QB = "android:id/title";
    public static final String TEXT_QB = "钱包";



    //-----------------新的朋友---------------------------------------------------------------------------
    //"新的朋友"类名
    public static final String CLASS_XDPY = "com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI";
    //"接受"
    public static final String TEXT_JS = "接受";
    //”已添加“
    public static final String TEXT_YTJ = "已添加";
    //"删除"
    public static final String TEXT_SC = "删除";



    //-----------------朋友验证---------------------------------------------------------------------------
    //"朋友验证"类名
    public static final String CLASS_PYYZ = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
    //“完成”
    //TODO 4) 6.6.6 com.tencent.mm:id/hd -> com.tencent.mm:id/hh
    public static final String ID_WC = "com.tencent.mm:id/hh";



    //-----------------详细资料---------------------------------------------------------------------------
    //“详细资料”
    public static final String CLASS_XXZL = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";



    //------------------我的钱包-------------------------------------------------------------------------
    //”我的钱包“类名
    public static final String CLASS_WDQB = "com.tencent.mm.plugin.mall.ui.MallIndexUI";
    //"零钱"id
    //TODO 5) 6.6.6 com.tencent.mm:id/c71 -> com.tencent.mm:id/c5b
    public static final String ID_LQ = "com.tencent.mm:id/c5b";

    //"零钱”文本
    public static final String TEXT_LQ = "零钱";

    //“零钱”RMB
    //TODO 6) 6.6.6 com.tencent.mm:id/c74 -> com.tencent.mm:id/c5e
    public static final String ID_LQ_RMB = "com.tencent.mm:id/c5e";



    //------------------零钱----------------------------------------------------------------------------
    //”零钱“类名
    public static final String CLASS_LQ = "com.tencent.mm.plugin.wallet.balance.ui.WalletBalanceManagerUI";
    //"零钱明细"
    //TODO 7) 6.6.6 com.tencent.mm:id/hc -> com.tencent.mm:id/hg
    public static final String ID_LQMX = "com.tencent.mm:id/hg";



    //------------------零钱明细------------------------------------------------------------------------
    //”零钱明细“类名
    public static final String CLASS_LQMX = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI";
    
    
    
    //------------------------------------------模拟点击异常情况--------------------------------------------------------
    //模拟点击异常出现在双开微信的首页面
    public static final String E_MNDJ_SKWXSYM = "emndjskwxsym";

    //”当前使用此业务的用户较多，请稍后再试“
    //TODO 8) 6.6.5 com.tencent.mm.ui.base.i 未修改
    public static final String E_CLASS_YHJD = "com.tencent.mm.ui.base.i";
    public static final String E_TEXT_YHJD = "确定";



    //---------------------------------------------------新增接收转账--------------------------------------------------------
    public static final String CLASS_TALLK_ITEM = "android.widget.FrameLayout";//微信首页”微信“页面，长按住item弹出的对话框的类名
    //TODO 8) 6.6.6 com.tencent.mm.ui.base.i 未修改
    public static final String CLASS_TALLK_ITEM_DELETE = "com.tencent.mm.ui.base.i";//是否删除该聊天的类名
    public static final String CLASS_WILL_RECEIVE_TRANSFER = "com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI";//"待确认收款"类名
    public static final String CLASS_RECEIVED_TRANSFER = "com.tencent.mm.plugin.remittance.ui.RemittanceDetailUI";//"已收钱"类名,同上
    public static final String TEXT_DELETE_CHAT = "删除该聊天";
    //TODO 9) 6.6.6 com.tencent.mm:id/aln -> com.tencent.mm:id/all
    public static final String ID_DELETE = "com.tencent.mm:id/all";//是否删除该聊天的”删除“
    //TODO 10) 6.6.6 com.tencent.mm:id/cy6 -> com.tencent.mm:id/cwi
    public static final String ID_WILL_RECEIVE_OR_RECEIVED = "com.tencent.mm:id/cwi";//待确认收款或者已经收取
    public static final String TEXT_NEED_SURE = "待确认收款";
    public static final String TEXT_RECEIVED = "已收钱";
    //TODO 11) 6.6.6 com.tencent.mm:id/cy8 -> com.tencent.mm:id/cwk
    public static final String ID_SURE_RECEIVE = "com.tencent.mm:id/cwk";//确认收钱按钮
    //TODO 12) 6.6.6 com.tencent.mm:id/hx -> com.tencent.mm:id/i1
    public static final String ID_RECEIVED = "com.tencent.mm:id/i1";//已收钱页面的返回
    public static final String TEXT_WECHAT = "微信";
    //TODO 13) 6.6.6 com.tencent.mm:id/aek -> com.tencent.mm:id/aei
    public static final String ID_TRANSFER_BACKUP = "com.tencent.mm:id/aei";//转账备注
    //TODO 14) 6.6.6 com.tencent.mm:id/hh -> com.tencent.mm:id/hl
    public static final String ID_CHAT_BACK = "com.tencent.mm:id/hl";//聊天界面的返回
    //TODO 15) 6.6.6 com.tencent.mm:id/apv -> com.tencent.mm:id/apt
    public static final String ID_TALLK_ITEM = "com.tencent.mm:id/apt";
    //TODO 16) 6.6.6 com.tencent.mm:id/j4 -> com.tencent.mm:id/jj
    public static final String ID_NEW_MSG_FLAG = "com.tencent.mm:id/jj";
    public static final String TEXT_HAVA_RECEIVED = "已被领取";
    public static final String TEXT_HAVA_RECEIVED_M = "已收钱";
    public static final String CLASS_TENCENT_NEWS = "com.tencent.mm.plugin.readerapp.ui.ReaderAppUI";//腾讯新闻类名
    //TODO 17) 6.6.6 com.tencent.mm:id/hx -> com.tencent.mm:id/i1
    public static final String ID_TENCENT_NEWS_BACKS = "com.tencent.mm:id/i1";
    //TODO 18) 6.6.6 com.tencent.mm:id/hj -> com.tencent.mm:id/hn
    public static final String ID_CHAT_WINDOW_WECHAT_ACCOUNT_BACKUP = "com.tencent.mm:id/hn";//聊天窗口给对方的微信备注的昵称或者原始昵称


    //TODO 19) 6.6.6 com.tencent.mm:id/ca4 -> com.tencent.mm:id/c9e
    public static final String WXWDXXS_ID = "com.tencent.mm:id/c9e";//微信首页面未读消息的数量的id
    //TODO 20) 6.6.6 com.tencent.mm:id/gw -> com.tencent.mm:id/h0
    public static final String WX_TOP_TOOLBAR_ID = "com.tencent.mm:id/h0";//微信首页头部toombar的控件id
}
