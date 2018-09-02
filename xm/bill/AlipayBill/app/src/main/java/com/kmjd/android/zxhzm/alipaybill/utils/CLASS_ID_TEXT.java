package com.kmjd.android.zxhzm.alipaybill.utils;

/**
 * Created by androidshuai on 2018/4/26.
 *
 * 当前支付宝的版本：10.1.22
 * 订单号确实要特殊处理，收入 订单号前面 添加 "E",支出订单号前面添加 "I",服务费订单号前面添加 "S"
 */

public interface CLASS_ID_TEXT {

    //================================支付宝首页面=================================
    //支付宝首页类名
    String ZFB_SY_CLASS = "com.eg.android.AlipayGphone.AlipayLogin";

    //支付宝首页“我的”的控件id
    String ZFB_SY_WD_ID = "com.alipay.android.phone.wealth.home:id/sigle_tab_bg";

    //支付宝首页底部RadioGroup的id
    String ZFB_SY_BOTTOM_GROUP_ID = "android:id/tabs";

    //支付宝首页“我的”文本
    String ZFB_SY_WD_TEXT = "我的";

    //支付宝的昵称ID
    String ZFB_NC_ID = "com.alipay.android.phone.wealth.home:id/left_textview";

    //支付宝首页“账单”的id
    String ZFB_SY_ZD_ID = "com.alipay.android.phone.wealth.home:id/tab_title";

    //支付宝首页“账单”文本
    String ZFB_SY_ZD_TEXT = "账单";

    //支付宝首页“余额”文本
    String ZFB_SY_YE_TEXT = "余额";

    //=================================账单页面=================================
    //支付宝"账单"类名
    String ZFB_ZD_CLASS = "com.alipay.mobile.bill.list.ui.BillListActivity_";

    //支付宝“账单”listview的id
    String ZFB_ZD_LISTVIEW_ID = "com.alipay.mobile.bill.list:id/bill_list_view";

    //账单条目id
    String ZFB_ZD_ITEM_ID = "com.alipay.mobile.bill.list:id/listItem";

    //账单条目类型的id
    String ZFB_ZD_ITEM_TYPE_ID = "com.alipay.mobile.bill.list:id/billName";

    //账单条目金额id
    String ZFB_ZD_ITEM_AMOUNT_ID = "com.alipay.mobile.bill.list:id/billAmount";

    //账单条目时间id
    String ZFB_ZD_ITEM_TIME_ID = "com.alipay.mobile.bill.list:id/timeInfoContainer";

    //=============================支付宝账单详情页面============================
    //支付宝“账单详情”类名
    String ZFB_ZDXQ_CLASS = "com.alipay.mobile.nebulacore.ui.H5Activity";

    //支付宝“账单详情”web的id
    String ZFB_ZDXQ_WEB_ID = "com.alipay.mobile.nebula:id/h5_pc_container";

    String ZFB_ZDXQ_SPSM_TEXT = "商品说明";

    String ZFB_ZDXQ_ZZBZ_TEXT = "转账备注";

    String ZFB_ZDXQ_ZDFL_TEXT = "账单分类";

    String ZFB_ZDXQ_CJSJ_TEXT = "创建时间";

    String ZFB_ZDXQ_DDH_TEXT = "订单号";

    String ZFB_ZDXQ_FKFS_TEXT = "付款方式";

    String ZFB_ZFXQ_SKFS_TEXT = "收款方式";

    String ZFB_ZDXQ_DFZH_TEXT = "对方账户";

    String ZFB_ZDXQ_ZCSM_TEXT = "转出说明";

    String ZFB_ZDXQ_TXSM_TEXT = "提现说明";

    String ZFB_ZDXQ_CZSM_TEXT = "充值说明";

    String ZFB_ZDXQ_TXD_TEXT = "提现到";

    String ZFB_ZDXQ_FWF_TEXT = "服务费";

    String ZFB_ZDXQ_DDJE_TEXT = "订单金额";

    String ZFB_ZDXQ_JLJDQ_TEXT = "奖励金抵扣";

    String ZFB_ZDXQ_SJDJJ_TEXT = "商家代金劵";

    String ZFB_ZDXQ_HB_TEXT = "红包";

    //=============================支付宝余额页面============================
    //支付宝“余额”类名
    String ZFB_YE_CLASS = "com.alipay.android.app.birdnest.ui.BNTplActivity";

    //支付宝余额页面“余额”文本
    String ZFB_YE_MX_TEXT = "明细";

    //=============================支付宝余额明细页面============================
    //支付宝“余额明细”类名
    String ZFB_YEMX_CLASS = "com.alipay.mobile.accountdetail.ui.DealListActivity_";

    //支付宝余额明细listview的id
    String ZFB_YEMX_LISTVIEW_ID = "com.alipay.android.phone.wealth.banlance:id/dealListView";

    //支付宝余额明细listview的条目的id
    String ZFB_YEMX_ITEM_ID = "com.alipay.android.phone.wealth.banlance:id/DetailInfoItemCanvas";

    //支付宝余额明细条目内容类型id
    String ZFB_YEMX_ITEM_TYPE_ID = "com.alipay.android.phone.wealth.banlance:id/noteText";

    //支付宝余额明细条目内容余额id
    String ZFB_YEMX_ITEM_YE_ID = "com.alipay.android.phone.wealth.banlance:id/balanceText";

    //支付宝余额明细条目内容日期id
    String ZFB_YEMX_ITEM_DATE_ID = "com.alipay.android.phone.wealth.banlance:id/dateText";

    //支付宝余额明细条目内容金额id
    String ZFB_YEMX_ITEM_MONEY_ID = "com.alipay.android.phone.wealth.banlance:id/moneyText";

    //支付宝余额明细返回
    String ZFB_YEMX_BACK_ID = "com.alipay.mobile.ui:id/title_bar_back_button";

    //=============================支付宝收支详情页面============================
    String ZFB_SZXQ_CLASS = "com.alipay.mobile.accountdetail.ui.DealDetailActivity_";

    //支付宝收支详情流水号id
    String ZFB_SZXQ_LSH_ID = "com.alipay.android.phone.wealth.banlance:id/AQOFlow";

    //支付宝收支详情类型id
    String ZFB_SZXQ_TYPE_ID = "com.alipay.android.phone.wealth.banlance:id/AQOType";

    //支付宝收支详情余额id
    String ZFB_SZXQ_YE_ID = "com.alipay.android.phone.wealth.banlance:id/AQOBalance";

    //支付宝收支详情界面返回
    String ZFB_SZXQ_BACK_ID = "com.alipay.mobile.ui:id/title_bar_back_button";

    //=============================小米MIUI通知提示栏的className============================
    String NOTIFICATION_CLASS = "android.widget.FrameLayout";

    //=============================余额明细界面ProgressDialog的className============================
    String PROGRESSDIALOG_CLASS = "com.alipay.mobile.framework.app.ui.DialogHelper$APGenericProgressDialog";

    //=============================系统多任务栏的界面classname============================
    String SYSTEMUI_RECENTS = "com.android.systemui.recents.RecentsActivity";
}
