package com.kmjd.android.zxhzm.alipaybill.utils;

/**
 * Created by androidshuai on 2017/3/17.
 */

public interface ContantsUtil {

    /**
     * 支付宝的包名
     */
    String ZFB_PACKAGENAME = "com.eg.android.AlipayGphone";

    /**
     * 当前支付宝昵称
     */
    String CURRENT_ALIPAY_NICKNAME = "currentAlipayNickname";

    /**
     *当前账本名称
     */
    String CURRENT_BOOK_NAME = "current_book_name";
    /**
     * 新的最新的订单号
     */
    String NEW_NEWEST_ORDER_NUMBER = "new_newest_order_number";

    /**
     * 上次保存的最新的订单号
     */
    String OLD_NEWEST_ORDER_NUMBER = "old_newest_order_number";

    /**
     * 支付宝是否有新的消息
     */
    String ZFB_IS_HAVA_NEW_MESSAGE = "zfb_is_hava_new_message";
    /**
     * 保存是否是第一次打开的应用的boolean值
     */
    String FIRST_OPEN = "first_open";
    /**
     * 用户输入的支付宝账号
     */
    String CURRENT_ALIPAY_ACCOUNT = "alipay_account";

    /**
     * 交易类型：收款
     */
    String JYLX_SK = "收款";

    /**
     * 交易类型：转账
     */
    String JYLX_ZZ = "转账";

    /**
     * 交易类型：提现
     */
    String JYLX_TX = "提现";

    /**
     * 交易类型：服务费
     */

    String JYLX_FWF = "服务费";

    /**
     * 当前站点编号
     */
    String CURRENT_STORAGE_NUM = "current_storage_num";

    /**
     * 交易状态：处理中
     */
    String JYZT_CLZ = "处理中";

    /**
     * 交易状态：成功
     */
    String JYZT_CG = "成功";

    /**
     * 交易状态：失败
     */
    String JYZT_SB = "失败";

    /**
     * 交易状态：交易成功
     */
    String JYZT_JYCG = "交易成功";

    /**
     * 交易状态：回冲
     */
    String JYZT_HC = "回冲";

    /**
     * 支付宝通知：进度提醒
     */
    String ZFBTZ_JDTX = "进度提醒";

    /**
     * 支付宝是否有新的进度进度提醒
     */
    String ZFB_IS_HAVA_NEW_PROGRESS = "zfb_is_hava_new_progress";

    /**
     * 支付宝处理中的订单
     */
    String ZFB_CLZ_BILL = "ZFB_CLZ_BILL";

    /**
     * 最新的流水号
     */
    String NEW_NEWEST_LSH = "new_newest_lsh";

    /**
     * 之前保存的最新的流水号
     */
    String OLD_NEWEST_LSH = "old_newest_lsh";

    /**
     * 支付宝收支详情的类型：提现
     */
    String SZXQ_TX = "提现";

    /**
     * 支付宝收支详情的类型：收费
     */
    String SZXQ_SF = "收费";

    /**
     * 支付宝收支详情的类型：提现失败退回
     */
    String SZXQ_TXSBTH = "提现失败退回";

    /**
     * 支付宝收支详情的类型：退费
     */
    String SZXQ_TF = "退费";

    /**
     * 支付宝流水号收支详情类型集合
     */
    String LSH_TYPE_LIST = "lsh_type_list";
}
