package com.kmjd.android.zxhzm.alipaybill.services;

import android.accessibilityservice.AccessibilityService;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.kmjd.android.zxhzm.alipaybill.bean.OriginalAlipayBillDetail;
import com.kmjd.android.zxhzm.alipaybill.bean.event.AliPayNotificationEvent;
import com.kmjd.android.zxhzm.alipaybill.bean.event.BillDetailEvent;
import com.kmjd.android.zxhzm.alipaybill.bean.event.ShowBillDetailEvent;
import com.kmjd.android.zxhzm.alipaybill.utils.CLASS_ID_TEXT;
import com.kmjd.android.zxhzm.alipaybill.utils.ContantsUtil;
import com.kmjd.android.zxhzm.alipaybill.utils.MyLogger;
import com.kmjd.android.zxhzm.alipaybill.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * 通知抓取“账单”来获取支付宝数据：余额通过程序内部计算获得
 */

public class AlipayBillAccessibilityService extends AccessibilityService {

    //间隔时间
    private static final int waitTime = 2000;
    //支付宝账单界面可见未读的条目数
    private int currentVisibileUnReadItemCount = 0;
    //是首页面进入账单界面还是账单详情回退到账单界面(true:首页面进入账单 false:账单详情回退到账单)
    private boolean syToZdOrZdxqBackToZd = true;
    //是否是滚动后的账单
    private boolean rollToZd = false;//一般情况都是false
    //是否是余额到的首页面(默认为false)
    private boolean yeToSy = false;
    //最新的一条记录是否读取
    private boolean isReadNewest = false;
    //是否所有新的数据已经读取
    private boolean isAllNewRead = false;
    //是否是自动化点击进入的账单页面(自动化点击进入的账单赋值true，自动化出账单赋值false),如果进入账单和账单详情是false代表是手动点击进入的
    private boolean isAutoToZd = false;
    //缓存账单详情页抓取的数据
    private ArrayList<OriginalAlipayBillDetail> mOriginalAlipayBillDetails = new ArrayList<>();
    //总的余额
    private Double totalBalance;

    /**
     * 就支付宝提现的问题创建缓存集合：
     *      inHandBill
     *      第一个缓存集合将本次账单列表里“处理中”的账单提现加入缓存，每次从自动化从首页面到账单页时清空
     *
     *      superInHandBill
     *      第二个缓存集合是每次账单刷完之后将inHandBill集合里所有内容加入到改集合
     *          如果有新的进度通知需要检查，就将这个缓存集合清空，赋值给第三个缓存集合(检查集合)
     *          所有的检查集合都检查完了之后将第一个集合里面的内容全部添加到该集合
     *
     *      checkInHandBill
     *      第三缓存集合检查集合，每次必须将检查集合中的数据都读取且所有新的数据已经读取才算把账单都读取完成，
     *          所有交易状态从“处理中”到其他状态的交易，都重新生成账单加入到上传集合重新提交
     *
     */
    //本次账单列表里“处理中”的账单订单号
    private ArrayList<String> inHandBill = new ArrayList<>();

    //之前账单列表里“处理中”的账单订单号
    private ArrayList<String> superInHandBill = new ArrayList<>();

    //本次需要检查的“处理中”的账单订单号
    private ArrayList<String> checkInHandBill = new ArrayList<>();

    //保存上一次窗口变化所在的页面
    private int superWindow = -1;
    private static final int ZFB_SY_WINDOW = 0;//支付宝首页面
    private static final int ZFB_ZD_WINDOW = 1;//支付宝账单页面
    private static final int ZFB_ZDQX_WINDOW = 2;//支付宝账单详情页面
    private static final int ZFB_YE_WINDOW = 3;//支付宝余额页面

    /**
     * 当启动服务的时候调用
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        MyLogger.xLog().d("支付宝无障碍服务启动");

        //注册EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType){
            //当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                MyLogger.xLog().d("窗口的状态className: " + className);
                switch (className){
                    case CLASS_ID_TEXT.ZFB_SY_CLASS://支付宝首页面
                        if (superWindow == ZFB_SY_WINDOW){
                            return;//如果上一次窗口状态变化的页面和这一次的是同一个界面不再重复执行，解决小米手机窗口变化多次触发的问题
                        }else {
                            superWindow = ZFB_SY_WINDOW;
                        }
                        //点击支付宝首页面“我的”
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_SY_CLASS, CLASS_ID_TEXT.ZFB_SY_WD_ID));
                        break;
                    case CLASS_ID_TEXT.ZFB_ZD_CLASS://支付宝账单页面
                        if (superWindow == ZFB_ZD_WINDOW){
                            return;//如果上一次窗口状态变化的页面和这一次的是同一个界面不再重复执行，解决小米手机窗口变化多次触发的问题
                        }else {
                            superWindow = ZFB_ZD_WINDOW;
                        }
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_ZD_CLASS, CLASS_ID_TEXT.ZFB_ZD_ITEM_ID));
                        break;
                    case CLASS_ID_TEXT.ZFB_ZDXQ_CLASS://支付宝账单详情页面
                        if (superWindow == ZFB_ZDQX_WINDOW){
                            return;//如果上一次窗口状态变化的页面和这一次的是同一个界面不再重复执行，解决小米手机窗口变化多次触发的问题
                        }else {
                            superWindow = ZFB_ZDQX_WINDOW;
                        }
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_ZDXQ_CLASS, CLASS_ID_TEXT.ZFB_ZDXQ_WEB_ID));
                        break;
                    case CLASS_ID_TEXT.ZFB_YE_CLASS://支付宝余额页面
                        if (superWindow == ZFB_YE_WINDOW){
                            return;//如果上一次窗口状态变化的页面和这一次的是同一个界面不再重复执行，解决小米手机窗口变化多次触发的问题
                        }else {
                            superWindow = ZFB_YE_WINDOW;
                        }
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_YE_CLASS, ""));
                        break;
                    case CLASS_ID_TEXT.NOTIFICATION_CLASS://小米MIUI通知提示栏,解决小米手机停在首页面的问题
                        break;
                    default://进入了其他界面
                        superWindow = -1;
                        break;
                }
                break;
        }
    }

    /**
     * 接受EventBus发送的支付宝来通知信息刷账单
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AliPayNotificationEvent event) {
        //有支付宝的通知且当前是在支付宝的首页面，立即执行刷账
        if (superWindow == ZFB_SY_WINDOW){
            excute(new BillRunnable(CLASS_ID_TEXT.ZFB_SY_CLASS, CLASS_ID_TEXT.ZFB_SY_WD_ID));
        }
    }

    //线程池
    private ExecutorService mExecutorService = null;
    private Future mFuture = null;
    private Runnable mRunnable = null;

    /**
     * 执行异步任务
     * @param targetRunnable
     */
    private synchronized void excute(Runnable targetRunnable){
        if (null == mExecutorService){
            mExecutorService = Executors.newCachedThreadPool();
        }

        if (null == mRunnable){
            mRunnable = targetRunnable;
            mFuture = mExecutorService.submit(mRunnable);
        }else if (!mFuture.isDone()){
            if (mFuture.cancel(true)){
                MyLogger.xLog().d("excute: 可以取消");
                mRunnable = null;
                mRunnable = targetRunnable;
                mFuture = mExecutorService.submit(mRunnable);
            }
        }else {
            mRunnable = null;
            mRunnable = targetRunnable;
            mFuture = mExecutorService.submit(mRunnable);
        }
    }

    //#######################支付宝账单#######################
    private class BillRunnable implements Runnable{

        private String classNameOrFunctionName;
        private String idOrText;

        public BillRunnable(String classNameOrFunctionName, String idOrText) {
            this.classNameOrFunctionName = classNameOrFunctionName;
            this.idOrText = idOrText;
        }

        @Override
        public void run() {
            MyLogger.xLog().d("BillRunnable->classNameOrFunctionName：" + classNameOrFunctionName + "->idOrText：" + idOrText);
            if (classNameOrFunctionName.equals(CLASS_ID_TEXT.ZFB_SY_CLASS) && idOrText.equals(CLASS_ID_TEXT.ZFB_SY_WD_ID)){
                //支付宝的首页
                //判断是否有新的支付宝的消息
                if (!SPUtils.getBoolean(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_MESSAGE, true)){
                    //如果没有新的消息
                    return;
                }

                //点击我的
                SystemClock.sleep(waitTime / 2);
                ClickMe();

                SystemClock.sleep(waitTime);
                if (yeToSy){//余额页面到首页面
                    //获取余额
                    totalBalance = getBalance();
                    MyLogger.xLog().d("balance: " + totalBalance);

                    //初始化数据
                    mOriginalAlipayBillDetails.clear();//清除缓存的数据
                    inHandBill.clear();//将inHandBill清空
                    //将SP中的数据存放在superInHandBill集合中
                    sPToList();
                    if (SPUtils.getBoolean(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_PROGRESS, false)){
                        //有新的提现进度改变的通知
                        checkInHandBill.clear();
                        checkInHandBill.addAll(superInHandBill);
                        superInHandBill.clear();
                        SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_PROGRESS, false);
                    }else {
                        //没有新的提现进度改变的通知
                        checkInHandBill.clear();
                    }
                    SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_MESSAGE, false);
                    syToZdOrZdxqBackToZd = true;
                    rollToZd = false;
                    isAllNewRead = false;
                    isReadNewest = false;
                    yeToSy = false;
                    isAutoToZd = true;

                    //点击账单
                    MyLogger.xLog().d("点击账单");
                    clickParentByChaildText(CLASS_ID_TEXT.ZFB_SY_ZD_TEXT);
                }else {//其他页面到的首页面
                    //点击余额
                    MyLogger.xLog().d("点击余额");
                    clickParentByChaildText(CLASS_ID_TEXT.ZFB_SY_YE_TEXT);
                }
            }else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.ZFB_ZD_CLASS) && idOrText.equals(CLASS_ID_TEXT.ZFB_ZD_ITEM_ID)){
                //如果不是自动化操作进入的账单，不做任何逻辑处理
                if (!isAutoToZd){return;}

                SystemClock.sleep(waitTime);
                //获取当前页面账单条目的个数
                int currentVisibleItemCount = getCurrentVisibleItemCount();
                MyLogger.xLog().d("currentVisibleItemCount: " + currentVisibleItemCount);
                if (currentVisibleItemCount == 0){
                    currentVisibileUnReadItemCount = 0;
                    isAutoToZd = false;
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    return;
                }

                if (syToZdOrZdxqBackToZd){
                    //首页面进入到账单页面(当前可见的未读条目个数 = 当前页面可见的条目个数)
                    currentVisibileUnReadItemCount = currentVisibleItemCount;
                    //点击第一个条目
                    if (!clickZDItem(currentVisibleItemCount - currentVisibileUnReadItemCount)){
                        currentVisibileUnReadItemCount = 0;
                        isAutoToZd = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                }else if (rollToZd){
                    //账单页面滚动后到账单
                    currentVisibileUnReadItemCount = currentVisibleItemCount - 1;
                    //点击第二个条目
                    if (!clickZDItem(currentVisibleItemCount - currentVisibileUnReadItemCount)){
                        currentVisibileUnReadItemCount = 0;
                        isAutoToZd = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    rollToZd = false;
                }else {
                    //账单详情页面返回到账单页面
                    if (isAllNewRead && checkInHandBill.isEmpty()){
                        //如果所有新的数据都已经读取且checkInHandBill为空
                        //TODO Eventbus发送通知
                        EventBus.getDefault().post(new BillDetailEvent(mOriginalAlipayBillDetails));//不管数据是不是空都发送通知上传
                        if (!mOriginalAlipayBillDetails.isEmpty()){//数据不为空才发送显示
                            MyLogger.xLog().d("原始的数据：" + mOriginalAlipayBillDetails);
                            EventBus.getDefault().post(new ShowBillDetailEvent(mOriginalAlipayBillDetails));
                        }

                        //将inHandBill加入到superInHandBill
                        superInHandBill.addAll(inHandBill);
                        //将superInHandBill集合中的数据存放在SP中
                        listToSp();

                        //返回
                        SystemClock.sleep(waitTime);
                        currentVisibileUnReadItemCount = 0;
                        isAutoToZd = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        return;
                    }

                    //当前界面没有可见的未读消息但是还有未读消息或“处理中”的体现帐单交易没有完成，滚动到下一屏的位置
                    if (currentVisibileUnReadItemCount == 0 && (!isAllNewRead || !checkInHandBill.isEmpty())){
                        RollBill();
                        rollToZd = true;
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_ZD_CLASS, CLASS_ID_TEXT.ZFB_ZD_ITEM_ID));
                        return;
                    }

                    //点击最近的未读的条目
                    if (!clickZDItem(currentVisibleItemCount - currentVisibileUnReadItemCount)){
                        currentVisibileUnReadItemCount = 0;
                        isAutoToZd = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                }
            }else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.ZFB_ZDXQ_CLASS) && idOrText.equals(CLASS_ID_TEXT.ZFB_ZDXQ_WEB_ID)){
                //如果不是自动化操作进入的账单，不做任何逻辑处理
                if (!isAutoToZd){return;}

                //获取账单详情的所有信息
                SystemClock.sleep(waitTime * 2);
                if (getBillDetail()){//成功获取到账单详情页面的账单信息
                    currentVisibileUnReadItemCount--;
                }

                //返回到账单页面
                SystemClock.sleep(waitTime);
                syToZdOrZdxqBackToZd = false;
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.ZFB_YE_CLASS)){
                if (SPUtils.getBoolean(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_MESSAGE, true)){
                    //如果是来了新消息自动化操作进入的余额
                    SystemClock.sleep(waitTime / 2);
                    //点击返回
                    yeToSy = true;//余额界面返回到首页
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                }else {
                    /**
                     * 非自动化操作进入的余额:
                     *      1.提现进入的余额，需手动点击返回到支付宝首页
                     *      2.其他触发页面变化的操作，需手动返回到支付宝首页
                     */
                }
            }
        }
    }

    /**
     * 点击首页面“我的”
     */
    private void ClickMe() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_SY_BOTTOM_GROUP_ID);
        if (!list.isEmpty()){
            AccessibilityNodeInfo radioGroupNodeInfo = list.get(0);
            if (radioGroupNodeInfo.getChildCount() > 4){
                AccessibilityNodeInfo meNodeInfo = radioGroupNodeInfo.getChild(4);
                meNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 获取余额
     */
    private double getBalance() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(CLASS_ID_TEXT.ZFB_SY_YE_TEXT);
        //余额节点
        AccessibilityNodeInfo balanceNodeInfo = nodeInfos.get(0);
        //余额节点的父节点
        AccessibilityNodeInfo parent = balanceNodeInfo.getParent();
        if (null != parent && parent.getChildCount() > 1){
            AccessibilityNodeInfo balanceAmountNodeInfo = parent.getChild(1);
            String balance = balanceAmountNodeInfo.getText().toString();
            if (!TextUtils.isEmpty(balance)){
                balance  = balance.substring(0, balance.length() - 2).trim();
                return Double.valueOf(balance.replace(",", ""));
            }
        }
        return 0;
    }

    /**
     * 向下滚动账单
     */
    private void RollBill() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> listNodes = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_ZD_LISTVIEW_ID);
        if (!listNodes.isEmpty()){
            AccessibilityNodeInfo listNode = listNodes.get(0);
            listNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }
    }

    /**
     * 获取账单详情界面的信息  ---->  “目前只考虑余额的情况”
     * 支付宝账单详情所有的情况（收款 转账 充值 提现 转出）
     *       转账：
     *           自带符号+-，
     *           付款方式
     *           收款方式
     *           转账备注（特征）
     *           对方账户
     *           账单分类
     *       收款：
     *          自带符号+-
     *          付款方式
     *          商品说明（特征）：目前只考虑余额的情况所以含有商品说明就当它时收款
     *          账单分类
     *       提现：
     *          服务费
     *          提现说明(-)（特征）
     *          提现到
     *          账单分类
     *      充值：（不上传 但是有可能是余额充值所以考虑余额  还有手机充值等暂考虑不了）
     *          付款方式
     *          充值说明(+)（特征）
     *          账单分类
     *      转出：（不考虑）
     *          转出说明(-):基本和余额无关（特征）
     *          账单分类
     *
     *    提现的时候
     *          状态为"处理中"和"成功"
     *          设置为"交易成功"
     *          状态为失败：
     *          交易号头部在原有基础上添加 F
     *          并设置为"回冲"
     */
    private boolean getBillDetail() {
        //获取到能拿到数据信息的节点
        AccessibilityNodeInfo webviewNodeInfo = getWebViewNodeInfo();

        OriginalAlipayBillDetail originalAlipayBillDetail = new OriginalAlipayBillDetail();
        int childCount = webviewNodeInfo.getChildCount();

        //当前账单详情主金额
        double mainAmount = 0;
        //服务费
        String serviceCharge = "";
        //交易状态变化(0：交易状态不变 1：交易状态变为成功 2：交易状态变为失败)
        int transactionStatusChange = 0;

        //先确定并拿到金额
        for (int i = 1; i < childCount; i++) {
            String text = webviewNodeInfo.getChild(i).getContentDescription().toString();
            if (!TextUtils.isEmpty(text) && text.length() > 3){
                //通过正则判断该节点是否是金额
                String regex = "^((-|\\+)?[0-9]{1,3}(,[0-9]{3})*)(.[0-9]{2})$";
                Pattern pattern = Pattern.compile(regex);
                boolean matches = pattern.matcher(text).matches();
                if (matches){
                    //当前这个节点时金额
                    if (text.startsWith("-") || text.startsWith("+")){
                        mainAmount = Double.valueOf(text.substring(1).replace(",",""));
                    }else {
                        mainAmount = Double.valueOf(text.replace(",",""));
                    }
                    originalAlipayBillDetail.setAmount(text.replace(",",""));
                    //这个账单的余额
                    originalAlipayBillDetail.setSurplus(totalBalance + "");
                    //上一个节点时昵称
                    originalAlipayBillDetail.setNickName(webviewNodeInfo.getChild(i - 1).getContentDescription().toString());
                    //下一个节点时交易状态
                    if (childCount > i){
                        originalAlipayBillDetail.setTradeState(webviewNodeInfo.getChild(i + 1).getContentDescription().toString());
                    }
                    break;
                }
            }
        }

        //获取对应的信息
        for (int i = 0; i < childCount; i++) {
            String text = webviewNodeInfo.getChild(i).getContentDescription().toString();
            String value = childCount > i+1 ? webviewNodeInfo.getChild(i+1).getContentDescription().toString() : "";
            switch (text){
                case CLASS_ID_TEXT.ZFB_ZDXQ_SPSM_TEXT://商品说明
                    originalAlipayBillDetail.setActionDescription(CLASS_ID_TEXT.ZFB_ZDXQ_SPSM_TEXT);
                    originalAlipayBillDetail.setTransactionType(ContantsUtil.JYLX_SK);
                    originalAlipayBillDetail.setRemark(value);
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_ZZBZ_TEXT://转账备注
                    originalAlipayBillDetail.setActionDescription(CLASS_ID_TEXT.ZFB_ZDXQ_ZZBZ_TEXT);
                    originalAlipayBillDetail.setTransactionType(ContantsUtil.JYLX_ZZ);
                    originalAlipayBillDetail.setRemark(value);
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_ZDFL_TEXT://账单分类
                    originalAlipayBillDetail.setBillClassification(value);
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_CJSJ_TEXT://创建时间
                    originalAlipayBillDetail.setCreationTime(value);
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_DDH_TEXT://订单号
                    originalAlipayBillDetail.setOrderNumber(value);
                    if (!TextUtils.isEmpty(originalAlipayBillDetail.getWithdrawCashTo()) &&
                            !TextUtils.isEmpty(originalAlipayBillDetail.getTradeState())){
                        MyLogger.xLog().d("这笔账单是提现账单");
                        //交易状态是“处理中”将这一笔订单加入“inHandBill”集合
                        if (ContantsUtil.JYZT_CLZ.equals(originalAlipayBillDetail.getTradeState())){
                            if (!superInHandBill.contains(value)){
                                inHandBill.add(value);
                            }
                            MyLogger.xLog().d("订单号：" + value +" inHandBill: " + inHandBill.size());
                        }
                        //如果这笔账单在需要检查的集合“处理中”出现
                        if (checkInHandBill.contains(value)){
                            checkInHandBill.remove(value);//从缓存表中删除该笔数据
                            if (!ContantsUtil.JYZT_CLZ.equals(originalAlipayBillDetail.getTradeState())){
                                //该笔数据交易状态发生改变需要重新提交
                                if (originalAlipayBillDetail.getTradeState().contains(ContantsUtil.JYZT_CG)){
                                    //交易成功
                                    transactionStatusChange = 1;
                                }else if (originalAlipayBillDetail.getTradeState().contains(ContantsUtil.JYZT_SB)){
                                    //失败
                                    transactionStatusChange = 2;
                                    //将金额和服务费的符号改为“+”
                                    if (!TextUtils.isEmpty(originalAlipayBillDetail.getAmount()) && originalAlipayBillDetail.getAmount().length() > 1){
                                        originalAlipayBillDetail.setAmount("+" + originalAlipayBillDetail.getAmount().substring(1));
                                    }
                                    if (!TextUtils.isEmpty(serviceCharge) && serviceCharge.length() > 1){
                                        serviceCharge = "+" + serviceCharge.substring(1);
                                    }
                                }
                            }
                        }
                        /**
                         * 提现的时候
                         状态为"处理中"和"成功"
                         设置为"交易成功"
                         状态为失败：
                         交易号头部在原有基础上添加 F
                         并设置为"回冲"
                         */
                        if (ContantsUtil.JYZT_CLZ.equals(originalAlipayBillDetail.getTradeState())
                                || originalAlipayBillDetail.getTradeState().contains(ContantsUtil.JYZT_CG)){
                            originalAlipayBillDetail.setTradeState(ContantsUtil.JYZT_JYCG);
                        }else if (originalAlipayBillDetail.getTradeState().contains(ContantsUtil.JYZT_SB)){
                            originalAlipayBillDetail.setTradeState(ContantsUtil.JYZT_HC);
                        }
                    }

                    //如果所有新的账单都已经读取且这条账单的交易状态不是失败则该条账单不上传,将这个对象的昵称置空
                    if (isAllNewRead && transactionStatusChange != 2){
                        originalAlipayBillDetail.setNickName("");
                    }

                    //如果是最新的第一条数据数据，将其的订单号存在SP中
                    if (!isReadNewest && !TextUtils.isEmpty(value)){
                        SPUtils.put(getApplicationContext(), ContantsUtil.NEW_NEWEST_ORDER_NUMBER, value);
                        isReadNewest = true;
                    }
                    //如果当前订单号和之前保存的最新的订单号一致
                    if (!TextUtils.isEmpty(value) && value.equals(SPUtils.getString(getApplicationContext(), ContantsUtil.OLD_NEWEST_ORDER_NUMBER))){
                        isAllNewRead = true;
                        //将最新的第一条数据存放在旧的最新的数据里
                        SPUtils.put(getApplicationContext(), ContantsUtil.OLD_NEWEST_ORDER_NUMBER,
                                SPUtils.getString(getApplicationContext(), ContantsUtil.NEW_NEWEST_ORDER_NUMBER));
                        //这条数据之前读取过且这条账单的交易状态不是失败，不需要添加到集合上传，将这个对象的昵称置空
                        if (transactionStatusChange != 2){
                            originalAlipayBillDetail.setNickName("");
                        }
                    }
                    //如果时第一进入的话，旧的最新的数据SP里是没有数据的，所以第一次进入账单只读一屏的数据
                    if (currentVisibileUnReadItemCount == 1 &&
                            TextUtils.isEmpty(SPUtils.getString(getApplicationContext(), ContantsUtil.OLD_NEWEST_ORDER_NUMBER))){
                        //当读取的时可见的最后一个且是第一进入账单
                        isAllNewRead = true;
                        //将最新的第一条数据存放在旧的最新的数据里
                        SPUtils.put(getApplicationContext(), ContantsUtil.OLD_NEWEST_ORDER_NUMBER,
                                SPUtils.getString(getApplicationContext(), ContantsUtil.NEW_NEWEST_ORDER_NUMBER));
                    }
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_FKFS_TEXT://付款方式
                    originalAlipayBillDetail.setExpenditureMethod(value);
                    break;
                case CLASS_ID_TEXT.ZFB_ZFXQ_SKFS_TEXT://收款方式
                    originalAlipayBillDetail.setInComeMethod(value);
                    if (!originalAlipayBillDetail.getAmount().startsWith("-") && !originalAlipayBillDetail.getAmount().startsWith("+")){
                        originalAlipayBillDetail.setAmount("+" + originalAlipayBillDetail.getAmount());
                    }
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_DFZH_TEXT://对方账户
                    originalAlipayBillDetail.setReciprocalAccount(value);
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_ZCSM_TEXT://转出说明(没有“+-”但不会是余额)
                    //这条数据不上传
                    originalAlipayBillDetail.setNickName("");
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_TXSM_TEXT://提现说明
                    originalAlipayBillDetail.setActionDescription(CLASS_ID_TEXT.ZFB_ZDXQ_TXSM_TEXT);
                    originalAlipayBillDetail.setTransactionType(ContantsUtil.JYLX_TX);
                    originalAlipayBillDetail.setRemark(value);
                    if (!originalAlipayBillDetail.getAmount().startsWith("-") && !originalAlipayBillDetail.getAmount().startsWith("+")){
                        originalAlipayBillDetail.setAmount("-" + originalAlipayBillDetail.getAmount());
                    }
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_TXD_TEXT://提现到
                    originalAlipayBillDetail.setWithdrawCashTo(value);
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_CZSM_TEXT://充值说明(暂时只能考虑余额充值)
                    originalAlipayBillDetail.setActionDescription(CLASS_ID_TEXT.ZFB_ZDXQ_CZSM_TEXT);
                    originalAlipayBillDetail.setTransactionType(ContantsUtil.JYLX_ZZ);
                    originalAlipayBillDetail.setRemark(value);
                    if (!originalAlipayBillDetail.getAmount().startsWith("-") && !originalAlipayBillDetail.getAmount().startsWith("+")){
                        originalAlipayBillDetail.setAmount("+" + originalAlipayBillDetail.getAmount());
                    }
                    //这条数据不上传
                    originalAlipayBillDetail.setNickName("");
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_FWF_TEXT://服务费
                    serviceCharge = "-" + value.replace(",", "");
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_DDJE_TEXT://订单金额
                    originalAlipayBillDetail.setAmount("-" + value.replace(",", ""));
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_JLJDQ_TEXT://奖励金抵扣
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_SJDJJ_TEXT://商家代金劵
                    break;
                case CLASS_ID_TEXT.ZFB_ZDXQ_HB_TEXT://红包
                    break;
            }
        }

        //计算余额(不上传不计算余额)
        if (!TextUtils.isEmpty(originalAlipayBillDetail.getAmount())
                && !TextUtils.isEmpty(originalAlipayBillDetail.getNickName())
                && null != totalBalance){
            //(为了保证计算的精度，需要用BigDecimal进行封装)
            BigDecimal totalBalanceBigDecimal = new BigDecimal(Double.toString(totalBalance));
            BigDecimal mainAmountBigDecimal = new BigDecimal(Double.toString(mainAmount));
            if (originalAlipayBillDetail.getAmount().startsWith("+")){
                //金额是"+",总的余额是-
                totalBalance = totalBalanceBigDecimal.subtract(mainAmountBigDecimal).doubleValue();
            }else if (originalAlipayBillDetail.getAmount().startsWith("-")){
                //金额是"-",总的余额是+
                totalBalance = totalBalanceBigDecimal.add(mainAmountBigDecimal).doubleValue();
            }
        }

        //将抓取到的信息添加到集合缓存中
        if (!TextUtils.isEmpty(originalAlipayBillDetail.getAmount())
                && !TextUtils.isEmpty(originalAlipayBillDetail.getNickName())
                && null != totalBalance){//按照流程获取的才加入到集合
            //金额不为空表明获取到了,昵称不为空可以添加到集合
            if (originalAlipayBillDetail.getAmount().startsWith("+")){
                originalAlipayBillDetail.setType("1");
            }else if (originalAlipayBillDetail.getAmount().startsWith("-")){
                originalAlipayBillDetail.setType("2");
            }

            if (!TextUtils.isEmpty(serviceCharge)){
                //该订单是提现服务费，拆分成一个新的账单
                OriginalAlipayBillDetail serviceChargeBillDetail = copyOriginalAlipayBillDetail(originalAlipayBillDetail);
                serviceChargeBillDetail.setAmount(serviceCharge);
                serviceChargeBillDetail.setTransactionType(ContantsUtil.JYLX_FWF);
                //总的余额里处理服务费
                if (serviceCharge.startsWith("+")){
                    totalBalance = new BigDecimal(Double.toString(totalBalance)).subtract(new BigDecimal(Double.toString(Double.valueOf(serviceCharge.substring(1))))).doubleValue();
                }else if (serviceCharge.startsWith("-")){
                    totalBalance = new BigDecimal(Double.toString(totalBalance)).add(new BigDecimal(Double.toString(Double.valueOf(serviceCharge.substring(1))))).doubleValue();
                }
                MyLogger.xLog().d("serviceChargeBillDetail: " + serviceChargeBillDetail.toString());
                mOriginalAlipayBillDetails.add(serviceChargeBillDetail);
            }

            if (!TextUtils.isEmpty(serviceCharge)){
                String surplus = originalAlipayBillDetail.getSurplus();
                if (serviceCharge.startsWith("+")){
                    surplus = new BigDecimal(Double.toString(Double.valueOf(surplus))).subtract(new BigDecimal(Double.toString(Double.valueOf(serviceCharge.substring(1))))).doubleValue() + "";
                }else if (serviceCharge.startsWith("-")){
                    surplus = new BigDecimal(Double.toString(Double.valueOf(surplus))).add(new BigDecimal(Double.toString(Double.valueOf(serviceCharge.substring(1))))).doubleValue() + "";
                }
                originalAlipayBillDetail.setSurplus(surplus);
            }
            MyLogger.xLog().d("originalAlipayBillDetail: " + originalAlipayBillDetail.toString());
            mOriginalAlipayBillDetails.add(originalAlipayBillDetail);
        }
        return !TextUtils.isEmpty(originalAlipayBillDetail.getAmount());
    }

    /**
     * 复制一个账单对象
     */
    private OriginalAlipayBillDetail copyOriginalAlipayBillDetail(OriginalAlipayBillDetail originalAlipayBillDetail){
        return new OriginalAlipayBillDetail(
                originalAlipayBillDetail.getNickName(),
                originalAlipayBillDetail.getAmount(),
                originalAlipayBillDetail.getSurplus(),
                originalAlipayBillDetail.getType(),
                originalAlipayBillDetail.getTradeState(),
                originalAlipayBillDetail.getActionDescription(),
                originalAlipayBillDetail.getRemark(),
                originalAlipayBillDetail.getTransactionType(),
                originalAlipayBillDetail.getBillClassification(),
                originalAlipayBillDetail.getCreationTime(),
                originalAlipayBillDetail.getOrderNumber(),
                originalAlipayBillDetail.getExpenditureMethod(),
                originalAlipayBillDetail.getInComeMethod(),
                originalAlipayBillDetail.getReciprocalAccount(),
                originalAlipayBillDetail.getWithdrawCashTo(),
                originalAlipayBillDetail.getSzxqType());
    }

    /**
     * 整理抓取到的数据：去掉重复的数据
     */
    private ArrayList<OriginalAlipayBillDetail> clearUpData(ArrayList<OriginalAlipayBillDetail> billDetails) {
        for (int i = 0; i < billDetails.size() - 1; i++) {
            for(int j = billDetails.size() -1; j > i; j--){
                if (billDetails.get(j).getOrderNumber().equals(billDetails.get(i).getOrderNumber())){
                    billDetails.remove(j);
                }
            }
        }
        return billDetails;
    }

    /**
     * 整理抓取到的数据：反转
     */
    private ArrayList<OriginalAlipayBillDetail> reverseData(ArrayList<OriginalAlipayBillDetail> billDetails){
        Collections.reverse(billDetails);
        return billDetails;
    }

    /**
     * 获取包含信息的web节点
     */
    private AccessibilityNodeInfo getWebViewNodeInfo() {
        AccessibilityNodeInfo webviewNodeInfo = null;
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_ZDXQ_WEB_ID);
        //能拿到内容的view有可能在第三层有可能在第四层
        if (!list.isEmpty()){
            SystemClock.sleep(100);
            webviewNodeInfo = list.get(0);
            while (webviewNodeInfo.getChildCount() < 6 && webviewNodeInfo.getChildCount() > 0){
                SystemClock.sleep(100);
                webviewNodeInfo = webviewNodeInfo.getChild(0);
            }
        }
        return webviewNodeInfo;
    }

    /**
     * 获取当前页面可见的账单条目的个数
     */
    private int getCurrentVisibleItemCount() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (null != nodeInfo){
            List<AccessibilityNodeInfo> zdItemList = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_ZD_ITEM_ID);
            return zdItemList.isEmpty() ? 0 : zdItemList.size();
        }
        return 0;
    }

    /**
     * 点击账单position位置的条目
     * @param position
     */
    private boolean clickZDItem(int position){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (null != nodeInfo){
            List<AccessibilityNodeInfo> zdItemList = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_ZD_ITEM_ID);
            if (!zdItemList.isEmpty() && zdItemList.size() > position){
                return zdItemList.get(position).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        return false;
    }

    /**
     * 通过id 获取控件并实现模拟点击
     *
     * @param childId
     * @return true: 通过控件完成了点击
     *         false: 没有相应的节点
     */
    private boolean clickParentByChaildId(String childId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        switch (childId) {
            case CLASS_ID_TEXT.ZFB_SY_ZD_ID:
                if (nodeInfo != null){
                    //点击支付宝首页面“我的”界面的账单
                    List<AccessibilityNodeInfo> zdList = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_SY_ZD_ID);
                    for (AccessibilityNodeInfo item : zdList) {
                        if (null != item.getText()){
                            String text = item.getText().toString();
                            if (text.equals(CLASS_ID_TEXT.ZFB_SY_ZD_TEXT)){
                                if (!item.isClickable()){
                                    AccessibilityNodeInfo viewGroup = item.getParent();
                                    while (true) {
                                        if (null != viewGroup) {
                                            if (!viewGroup.isClickable()) {
                                                viewGroup = viewGroup.getParent();
                                            } else {
                                                viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                return true;
                                            }
                                        }
                                    }
                                }else {
                                    item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            default:
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(childId);
                    if (!list.isEmpty()) {
                        AccessibilityNodeInfo viewGroup = list.get(0);
                        if (null != viewGroup) {
                            if (!viewGroup.isClickable()) {
                                viewGroup = viewGroup.getParent();
                                while (true) {
                                    if (null != viewGroup) {
                                        if (!viewGroup.isClickable()) {
                                            viewGroup = viewGroup.getParent();
                                        } else {
                                            viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                            return true;
                                        }
                                    }
                                }
                            } else {
                                viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                return true;
                            }
                        }
                    }
                }
                return false;
        }
    }

    /**
     * 通过文本获取控件，并进行模拟点击
     *
     * @param childText
     * @return true: 通过控件完成了点击
     *         false: 没有相应的节点
     */
    public boolean clickParentByChaildText(String childText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(childText);
            if (list.isEmpty()) {
                return false;
            }
            AccessibilityNodeInfo viewGroup = list.get(0);
            if (null != viewGroup) {
                if (!viewGroup.isClickable()) {
                    viewGroup = viewGroup.getParent();
                    while (true) {
                        if (null != viewGroup) {
                            if (!viewGroup.isClickable()) {
                                viewGroup = viewGroup.getParent();
                            } else {
                                viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                return true;
                            }
                        }
                    }
                } else {
                    viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 通过控件id获取文本
     * @param id
     * @return
     */
    public String getTextById(String id) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        String needText = "";
        switch (id) {
            default: {
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(id);
                    for (AccessibilityNodeInfo item : list) {
                        if (null != item.getText()) {
                            needText = item.getText().toString();
                            return needText;
                        }
                    }
                }
            }
        }
        return needText;
    }

    /**
     * 将SP中的数据存放在superInHandBill集合中
     */
    public void sPToList(){
        String zfbClzBill = SPUtils.getString(getApplicationContext(), ContantsUtil.ZFB_CLZ_BILL);
        superInHandBill.clear();
        if (!TextUtils.isEmpty(zfbClzBill)){
            String[] split = zfbClzBill.split(",");
            if (split.length > 0){
                for (String s : split) {
                    superInHandBill.add(s);
                }
            }
        }
        SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_CLZ_BILL, "");
    }

    /**
     * 将superInHandBill集合中的数据存放在SP中
     */
    public void listToSp(){
        if (!superInHandBill.isEmpty()){
            StringBuffer sb = new StringBuffer();
            for (String s : superInHandBill) {
                sb.append(s).append(",");
            }
            SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_CLZ_BILL, sb.substring(0, sb.length()-1).toString());
        }else {
            SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_CLZ_BILL, "");
        }
        superInHandBill.clear();
    }

    /**
     * 中断服务的回调
     */
    @Override
    public void onInterrupt() {
        MyLogger.xLog().d("支付宝无障碍服务中断");
    }

    /**
     * 无障碍服务销毁
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogger.xLog().d("支付宝无障碍服务销毁");

        //注销EventBus
        EventBus.getDefault().unregister(this);
    }
}
