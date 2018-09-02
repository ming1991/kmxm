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
 * 通过抓取“余额”和“账单”来获取支付宝的数据：
 *      先抓取余额明细里的余额，通过流水号和账单里的订单号进行匹配从而获取到需要的数据上传
 */

public class NewAlipayBillAccessibilityService extends AccessibilityService {

    //间隔时间
    private static final int waitTime = 2000;
    //支付宝账单界面可见未读的账单条目数
    private int currentVisibileUnReadZdItemCount = 0;
    //是首页面进入账单界面还是账单详情回退到账单界面(true:首页面进入账单 false:账单详情回退到账单)
    private boolean syToZdOrZdxqBackToZd = true;
    //是否是滚动后的账单
    private boolean rollToZd = false;//一般情况都是false
    //是否是余额到的首页面(默认为false)
    private boolean yeToSy = false;
    //是否所有新的数据已经读取
    private boolean isAllNewRead = false;
    //是否是自动化点击进入的账单页面(自动化点击进入的账单赋值true，自动化出账单赋值false),如果进入账单和账单详情是false代表是手动点击进入的
    private boolean isAutoToZd = false;

    //TODO 新增一些标志位
    //是首页面进入余额界面还是余额明细界面回退到余额界面(true: 首页面进入余额 false: 余额明细回退到余额)
    private boolean syToYeOrYemxBackToYe = true;
    //是余额界面进入余额明细界面还是收支详情界面回退到余额明细界面(true: 余额界面进入余额明细 false: 收支详情界面回退到余额明细界面)
    private boolean yeToYemxOrSzxqBackToYemx = true;
    //余额明细列表的最新余额
    private String yemxNewestBalance;
    //是否是自动化点击进入的余额明细页面(自动化点击进入的余额明细赋值true，自动化出余额明细赋值false),如果进入余额明细和收支详情是false代表是手动点击进入的
    private boolean isAutoToYemx = false;
    //是否是自动化点击进入的余额(自动化点击进入的余额赋值true,自动化出余额赋值false),如果进入余额为false代表是手动点击的
    private boolean isAutoToYe = false;
    //支付宝余额明细当前可见未读的余额条目数
    private int currentVisibileUnReadYeItemCount = 0;
    //缓存抓取的最新数据
    private ArrayList<OriginalAlipayBillDetail> mNewOriginalAlipayBillDetails = new ArrayList<>();
    //由于在eventbus事件传递过程中清理mNewOriginalAlipayBillDetails集合，会导致接收的集合为空，创建一个临时集合来做数据传递
    private ArrayList<OriginalAlipayBillDetail> mTempOriginalAlipayBillDetails = new ArrayList<>();
    //最新的一条收支明细是否已经读取
    private boolean isReadSzxqNewest = false;
    //是否所有新的收支明细都已经读取
    private boolean isAllNewSzxqRead = false;
    //是否是滚动后的余额明细
    private boolean rollToYemx = false;//默认一般情况下为false
    //是否遗漏最新数据(进入余额明细读取完成到将要进入账单是否有新的账单产生：true有 false没有)
    private boolean isMissData = false;
    //缓存遗漏的最新数据
    private ArrayList<OriginalAlipayBillDetail> mMissOriginalAlipayBillDetails = new ArrayList<>();
    //是从余额进入余额明细还是多任务界面返回到余额明细(true: 余额进入余额明细 false: 多任务回退到余额明细)
    private boolean yeToYemxOrRecentsBackToYemx = true;
    //是从余额明细进入多任务栏还是多任务栏返回带余额明细(true: 余额明细进入多任务栏 false: 多任务栏回退到余额明细)
    private boolean yemxToRecentsOrRecentsBackToYemx = false;
    //第一次进入余额明细一屏剩余不读取的条目数+1(默认为1)
    private int unNeedReadCount = 1;
    //缓存之前读取过的收支详情的流水号和收支详情的类型(lsh + type)
    private ArrayList<String> oldNewestLshTypeList = new ArrayList<>(10);
    //缓存最新读取的收支详情的流水号和收支详情的类型(lsh + type)
    private ArrayList<String> newNewestLshTypeList = new ArrayList<>(5);

    //保存上一次窗口变化所在的页面
    private int superWindow = -1;
    private static final int ZFB_SY_WINDOW = 0;//支付宝首页面
    private static final int ZFB_ZD_WINDOW = 1;//支付宝账单页面
    private static final int ZFB_ZDQX_WINDOW = 2;//支付宝账单详情页面
    private static final int ZFB_YE_WINDOW = 3;//支付宝余额页面

    //TODO 添加的窗口变化
    private static final int ZFB_YEMX_WINDOW = 4;//支付宝余额详情界面
    private static final int ZFB_SZXQ_WINDOW = 5;//支付宝收支详情界面
    private static final int SYSTEMUI_RECENTS = 6;//系统多任务栏的界面

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
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_YE_CLASS, CLASS_ID_TEXT.ZFB_YE_MX_TEXT));
                        break;


                    //TODO 添加的界面的处理
                    case CLASS_ID_TEXT.ZFB_YEMX_CLASS://支付宝余额明细界面
                        if (superWindow == ZFB_YEMX_WINDOW){
                            return;
                        }else {
                            superWindow = ZFB_YEMX_WINDOW;
                        }
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_YEMX_CLASS, CLASS_ID_TEXT.ZFB_YEMX_ITEM_ID));
                        break;
                    case CLASS_ID_TEXT.ZFB_SZXQ_CLASS://支付宝收支详情界面
                        if (superWindow == ZFB_SZXQ_WINDOW){
                            return;
                        }else {
                            superWindow = ZFB_SZXQ_WINDOW;
                        }
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_SZXQ_CLASS, CLASS_ID_TEXT.ZFB_SZXQ_LSH_ID));
                        break;
                    case CLASS_ID_TEXT.SYSTEMUI_RECENTS://系统多任务栏的界面
                        if (superWindow == SYSTEMUI_RECENTS){
                            return;
                        }else {
                            superWindow = SYSTEMUI_RECENTS;
                        }
                        excute(new BillRunnable(CLASS_ID_TEXT.SYSTEMUI_RECENTS, ""));
                        break;



                    case CLASS_ID_TEXT.NOTIFICATION_CLASS://小米MIUI通知提示栏,解决小米手机停在首页面的问题
                    case CLASS_ID_TEXT.PROGRESSDIALOG_CLASS://余额明细界面ProgressDialog屏蔽
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
                    /**
                     * 余额明细已经抓取完成回到支付宝首页：
                            获取到支付宝首页的余额
                            判断支付宝首页的余额和刚刚抓取的余额明细的最新的余额是否一致
                                a.余额不一致：代表在进入余额明细到回到首页的过程中还有新的数据产生，
                                    重新回到余额明细抓取遗漏的新的数据
                                b.余额一致判断抓取的余额明细是否为空
                                    1)数据为空：停在首页
                                    2)数据不为空：点击进入账单
                     */
                    //获取余额
                    String totalBalance = getBalance();
                    MyLogger.xLog().e("totalBance: " + totalBalance + " yemxNewestBalance: " + yemxNewestBalance);

                    //比较余额是否一致：如果是一致的但是有进有出(暂不考虑，因为自动化点击的时候基本没有支出的情况)
                    if (!totalBalance.equals(yemxNewestBalance)){
                        //余额不一致，代表有新的数据产生，需要重新回到余额界面到余额明细抓取新的数据
                        //点击余额
                        MyLogger.xLog().d("余额不一致, 点击余额");
                        //首页面到余额
                        syToYeOrYemxBackToYe = true;//从首页点击到余额
                        isMissData = true;//是由于有数据遗漏才进入的余额，进入余额之后的逻辑按照遗漏数据逻辑处理
                        isAutoToYe = true;//是自动化点击进入的余额
                        clickParentByChaildText(CLASS_ID_TEXT.ZFB_SY_YE_TEXT);//通过“余额”文本点击进入余额页面
                        return;
                    }

                    //FIXME 通过填充余额明细来判断数据是否读取包括提现回冲的情况，不再需要“处理中”的缓存表
                    SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_MESSAGE, false);//将SP中是否有新的支付宝消息的标志置为false,下一步将进入到账单，之后有新的支付宝消息视为下一次的数据
                    //初始化标志
                    syToZdOrZdxqBackToZd = true;
                    rollToZd = false;
                    isAllNewRead = false;
                    yeToSy = false;

                    //FIXME 判断余额明细集合是否为空，如果为空代表没有新的账单产生，不需要进入账单列表进行读取
                    if (mNewOriginalAlipayBillDetails.isEmpty()){
                        //没有新的数据产生将停在首页面
                        isAutoToZd = false;
                        //TODO Eventbus发送通知
                        MyLogger.xLog().e("没有新的账单发送空消息");
                        //发送空的消息，是因为处理由于网络原因，数据上传时候，通过发送空的数据，使数据再次上传
                        EventBus.getDefault().post(new BillDetailEvent(mNewOriginalAlipayBillDetails));//不管数据是不是空都发送通知上传
                    }else {
                        isAutoToZd = true;

                        //对数据做去重处理(处理在余额明细中由于人为或网络网络原因导致多次进入余额明细抓取数据导致的数据重复抓取的问题)
                        mNewOriginalAlipayBillDetails = clearUpData(mNewOriginalAlipayBillDetails);

                        //点击账单
                        MyLogger.xLog().d("点击账单");
                        clickParentByChaildText(CLASS_ID_TEXT.ZFB_SY_ZD_TEXT);
                    }
                }else {//其他页面到的首页面(这是有新的支付宝通知来之后第一次进入余额抓取数据)
                    //点击余额
                    MyLogger.xLog().d("点击余额");
                    //首页面到余额
                    syToYeOrYemxBackToYe = true;//首页面点击进入余额界面
                    isAutoToYe = true;//自动化点击进入余额
                    //如果有余额列表集合不为空，说明上次的数据并没有完全填充并发送(处理由于人为原因导致的数据没有填充发送，支付宝来通知直接抓取下一次数据从而导致的数据遗漏的问题的，当做数据遗漏处理，两次数据一次提交)
                    if (!mNewOriginalAlipayBillDetails.isEmpty()){
                        isMissData = true;
                    }
                    clickParentByChaildText(CLASS_ID_TEXT.ZFB_SY_YE_TEXT);
                }
            }else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.ZFB_ZD_CLASS) && idOrText.equals(CLASS_ID_TEXT.ZFB_ZD_ITEM_ID)){
                //如果不是自动化操作进入的账单，不做任何逻辑处理
                if (!isAutoToZd){return;}

                SystemClock.sleep(waitTime);
                //获取当前页面账单条目的个数
                int currentVisibleItemCount = getCurrentVisibleZdItemCount();
                MyLogger.xLog().d("currentVisibleItemCount: " + currentVisibleItemCount);
                if (currentVisibleItemCount == 0){
                    currentVisibileUnReadZdItemCount = 0;
                    isAutoToZd = false;
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    return;
                }

                if (syToZdOrZdxqBackToZd){
                    //首页面进入到账单页面(当前可见的未读条目个数 = 当前页面可见的条目个数)
                    currentVisibileUnReadZdItemCount = currentVisibleItemCount;
                    //点击第一个条目
                    if (!clickZDItem(currentVisibleItemCount - currentVisibileUnReadZdItemCount)){
                        currentVisibileUnReadZdItemCount = 0;
                        isAutoToZd = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                }else if (rollToZd){
                    //账单页面滚动后到账单
                    currentVisibileUnReadZdItemCount = currentVisibleItemCount - 1;
                    //点击第二个条目
                    if (!clickZDItem(currentVisibleItemCount - currentVisibileUnReadZdItemCount)){
                        currentVisibileUnReadZdItemCount = 0;
                        isAutoToZd = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    rollToZd = false;
                }else {

                    //FIXME  通过填充余额明细来判断数据是否读取包括提现回冲的情况，不再需要“处理中”检查的缓存表是否为空来判断
                    //账单详情页面返回到账单页面
                    if (isAllNewRead){
                        MyLogger.xLog().d("原始的数据：" + mNewOriginalAlipayBillDetails);
                        //如果所有新的数据都已经读取
                        //TODO Eventbus发送通知
                        mTempOriginalAlipayBillDetails.clear();
                        mTempOriginalAlipayBillDetails.addAll(mNewOriginalAlipayBillDetails);
                        EventBus.getDefault().post(new BillDetailEvent(mTempOriginalAlipayBillDetails));//不管数据是不是空都发送通知上传
                        if (!mTempOriginalAlipayBillDetails.isEmpty()){//数据不为空才发送显示
                            EventBus.getDefault().post(new ShowBillDetailEvent(mTempOriginalAlipayBillDetails));
                        }
                        //数据在填充完成已经发送之后清理
                        mNewOriginalAlipayBillDetails.clear();

                        //返回
                        SystemClock.sleep(waitTime);
                        currentVisibileUnReadZdItemCount = 0;
                        isAutoToZd = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        return;
                    }

                    //当前界面没有可见的未读消息但是还有未读消息，滚动到下一屏的位置
                    if (currentVisibileUnReadZdItemCount == 0 && !isAllNewRead){
                        RollBill();
                        rollToZd = true;
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_ZD_CLASS, CLASS_ID_TEXT.ZFB_ZD_ITEM_ID));
                        return;
                    }

                    //点击最近的未读的条目
                    if (!clickZDItem(currentVisibleItemCount - currentVisibileUnReadZdItemCount)){
                        currentVisibileUnReadZdItemCount = 0;
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
                    currentVisibileUnReadZdItemCount--;
                }

                //返回到账单页面
                SystemClock.sleep(waitTime);
                syToZdOrZdxqBackToZd = false;
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.ZFB_YE_CLASS) && idOrText.equals(CLASS_ID_TEXT.ZFB_YE_MX_TEXT)){
                if (isAutoToYe){//自动化操作进入的余额

                    //判断是怎样进入的余额
                    if (syToYeOrYemxBackToYe){//首页面进入的余额
                        //点击进入余额明细
                        SystemClock.sleep(waitTime);
                        yeToYemxOrSzxqBackToYemx = true;
                        yeToYemxOrRecentsBackToYemx = true;
                        isAutoToYemx = true;
                        isAllNewSzxqRead = false;
                        isReadSzxqNewest = false;
                        //清空newNewestLshTypeList
                        newNewestLshTypeList.clear();
                        //sp中(lsh + type)数据转oldNewestLshTypeList集合
                        spToList();
                        //清理遗漏数据的集合
                        if (isMissData){
                            mMissOriginalAlipayBillDetails.clear();
                        }
                        clickParentByChaildText(CLASS_ID_TEXT.ZFB_YE_MX_TEXT);
                    }else {//余额明细回到余额
                        //点击返回到首页面
                        SystemClock.sleep(waitTime / 2);
                        yeToSy = true;//余额界面返回到首页
                        isAutoToYe = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                }else {
                    /**
                     * 非自动化操作进入的余额:
                     *      1.提现进入的余额，需手动点击返回到支付宝首页
                     *      2.其他触发页面变化的操作，需手动返回到支付宝首页
                     */
                }
            }else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.ZFB_YEMX_CLASS) && idOrText.equals(CLASS_ID_TEXT.ZFB_YEMX_ITEM_ID)){//余额明细列表界面
                //如果不是自动化操作进入余额明细，不做任何逻辑操作
                if (!isAutoToYemx){return;}

                /**
                 * 由于小米手机在从余额到余额明细会出现ProgressDialog,但是ProgressDialog消失后没有出现className的改变
                        导致余额明细界面获取不到根节点，逻辑代码无法执行。
                    解决方法：1.现从余额界面到余额明细界面之后不直接执行列表点击，而是马上进入我的客服页面之后马上回到余额明细界面再
                                开始点击列表(方法不可用，还是获取不到节点)
                            2.从余额到余额明细界面之后不直接执行点击，而是马上进入多任务界面，之后再次点击多任务键返回到余额明细界面再
                                开始点击列表
                 */
                if (yeToYemxOrRecentsBackToYemx){
                    SystemClock.sleep(waitTime * 2);
                    //判断是否可以获取到根节点
                    if (null == getRootInActiveWindow()){
                        //进入系统的多任务栏
                        yemxToRecentsOrRecentsBackToYemx = true;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                        return;
                    }else {
                        yeToYemxOrRecentsBackToYemx = false;
                    }
                }

                SystemClock.sleep(waitTime / 2);
                //获取当前界面可见的余额条目的数量
                int currentVisibleYeItemCount = getCurrentVisibleYeItemCount();
                MyLogger.xLog().e("currentVisibleYeItemCount: " + currentVisibleYeItemCount);
                if (currentVisibleYeItemCount == 0){
                    currentVisibileUnReadYeItemCount = 0;
                    isAutoToYemx = false;
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    return;
                }

                //判断是怎么进入的余额明细
                if (yeToYemxOrSzxqBackToYemx){//余额进入余额明细
                    //计算unNeedReadCount
                    if (currentVisibleYeItemCount > 5){
                        unNeedReadCount = currentVisibleYeItemCount - 5 + 1;
                    }
                    //余额界面进入到余额明细界面
                    currentVisibileUnReadYeItemCount = currentVisibleYeItemCount;
                    //点击第一个余额条目
                    if (!clickYeItem(currentVisibleYeItemCount - currentVisibileUnReadYeItemCount)){
                        currentVisibileUnReadYeItemCount = 0;
                        isAutoToYemx = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                }else if (rollToYemx){
                    //余额明细滚动后的余额明细
                    currentVisibileUnReadYeItemCount = currentVisibleYeItemCount;
                    if (!clickYeItem(currentVisibleYeItemCount - currentVisibileUnReadYeItemCount)){
                        currentVisibileUnReadYeItemCount = 0;
                        isAutoToYemx = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                    rollToYemx = false;
                }else {//收支详情回退到余额明细
                    //判断新的数据是否已经读取完成
                    if (isAllNewSzxqRead){
                        //新的余额数据都已经读取，返回到余额界面
                        currentVisibileUnReadYeItemCount = 0;
                        isAutoToYemx = false;
                        syToYeOrYemxBackToYe = false;
                        if (isMissData){
                            //遗漏的数据列表中的顺序是在之前抓取的数据的前面，所以要将数据加入到集合的前面
                            mNewOriginalAlipayBillDetails.addAll(0, mMissOriginalAlipayBillDetails);
                            isMissData = false;
                        }
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        MyLogger.xLog().e("mNewOriginalAlipayBillDetails: " + mNewOriginalAlipayBillDetails);
                        return;
                    }

                    //当前界面没有可见消息但是还有未读消息,滚动到下一屏(数据滚动之后，中间重复的数据较多，对数据需做去重处理)
                    if (currentVisibileUnReadYeItemCount == 0 && !isAllNewSzxqRead){
                        RollYemx();
                        rollToYemx = true;
                        excute(new BillRunnable(CLASS_ID_TEXT.ZFB_YEMX_CLASS, CLASS_ID_TEXT.ZFB_YEMX_ITEM_ID));
                        return;
                    }

                    //点击最近的未读的余额明细条目
                    if (!clickYeItem(currentVisibleYeItemCount - currentVisibileUnReadYeItemCount)){
                        currentVisibileUnReadYeItemCount = 0;
                        isAutoToYemx = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    }
                }
            }else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.ZFB_SZXQ_CLASS) && idOrText.equals(CLASS_ID_TEXT.ZFB_SZXQ_LSH_ID)){//支付宝收支详情界面
                //如果不是自动化操作进入的收支明细，不做任何逻辑处理
                if (!isAutoToYemx){return;}

                //获取收支明细的信息
                SystemClock.sleep(waitTime);
                if (getBalanceDetail()){//成功获取到收支详情页面的信息
                    currentVisibileUnReadYeItemCount--;
                }

                //返回到余额明细界面
                SystemClock.sleep(waitTime / 2);
                yeToYemxOrSzxqBackToYemx = false;
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.SYSTEMUI_RECENTS)){//系统多任务栏的界面
                //如果不是自动化操作进入不做任何逻辑处理
                if (!isAutoToYemx){return;}

                //如果是余额明细进入的
                if (yemxToRecentsOrRecentsBackToYemx){
                    SystemClock.sleep(waitTime / 2);
                    //回到余额明细
                    yeToYemxOrRecentsBackToYemx = false;
                    yemxToRecentsOrRecentsBackToYemx = false;
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
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
    private String getBalance() {
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
                return balance.replace(",", "");
            }
        }
        return "";
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
     * 向下滚动余额明细
     */
    private void RollYemx(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> listNodes = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_YEMX_LISTVIEW_ID);
        if (!listNodes.isEmpty()){
            AccessibilityNodeInfo listNode = listNodes.get(0);
            listNode.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }
    }

    /**
     *获取收支详情里面的信息：
     *      流水号：账单详情里面的订单号
     *      类型：当出现提现和提现手续费时作为判断
     *      余额
     */
    private boolean getBalanceDetail(){
        OriginalAlipayBillDetail originalAlipayBillDetail = new OriginalAlipayBillDetail();
        //1.获取流水号
        String lsh = getTextById(CLASS_ID_TEXT.ZFB_SZXQ_LSH_ID);
        originalAlipayBillDetail.setOrderNumber(lsh);
        //2.获取收支详情类型(因为类型为收费（提现手续费）和退费(提现手续费退回)的流水号无法和账单详情的流水号进行匹配，所以不加入集合)
        String type = getTextById(CLASS_ID_TEXT.ZFB_SZXQ_TYPE_ID);
        if (!TextUtils.isEmpty(type)){
            if (ContantsUtil.SZXQ_SF.equals(type) || ContantsUtil.SZXQ_TF.equals(type)){
                originalAlipayBillDetail.setOrderNumber("");
            }else {
                originalAlipayBillDetail.setSzxqType(type);
            }
        }
        //3.获取余额
        String balance = getTextById(CLASS_ID_TEXT.ZFB_SZXQ_YE_ID);
        if (!TextUtils.isEmpty(balance)){
            balance = balance.replace("元", "").replace(",", "");
        }
        originalAlipayBillDetail.setSurplus(balance);


        if (!TextUtils.isEmpty(lsh) && !TextUtils.isEmpty(type) && !TextUtils.isEmpty(balance)){
            //是最新的第一条收支详情数据
            if (!isReadSzxqNewest){
                isReadSzxqNewest = true;
                //记录抓取的最新的余额
                yemxNewestBalance = balance;
            }

            //该流水号和收支详情类型是否之前读取过
            if (oldNewestLshTypeList.contains(lsh + type)){
                //如果这条收支明细包含判断是否是集合中的第一个
                int index = oldNewestLshTypeList.indexOf(lsh + type);
                if (0 == index){//是第一个代表正常跳转
                    //之前读取过,代表所有新的数据都已读取完
                    isAllNewSzxqRead = true;
                    oldNewestLshTypeList.addAll(0, newNewestLshTypeList);
                    //oldNewestLshTypeList最多只保留10个元素
                    while (oldNewestLshTypeList.size() > 10){
                        oldNewestLshTypeList.remove(10);
                    }
                    //将oldNewestLshTypeList转为sp保留
                    listToSp();
                    //这条数据之前读取过不需要添加到集合
                    originalAlipayBillDetail.setOrderNumber("");
                }else {//不是第一个代表有人为的滑动，前面有数据遗漏
                    //重新进入余额明细抓取数据
                    yemxNewestBalance = "-1.00";
                    isAllNewSzxqRead = true;
                    //将oldNewestLshTypeList转为sp保留
                    listToSp();
                    //这条数据之前读取过不需要添加到集合
                    originalAlipayBillDetail.setOrderNumber("");
                }
            }else {
                //之前未读取是新条目
                newNewestLshTypeList.add(lsh + type);

                //如果是第一次进入app,最多读取5条数据
                if (currentVisibileUnReadYeItemCount == unNeedReadCount && oldNewestLshTypeList.isEmpty()){
                    isAllNewSzxqRead = true;
                    oldNewestLshTypeList.addAll(0, newNewestLshTypeList);
                    //将oldNewestLshTypeList转为sp保留
                    listToSp();
                }
            }

            //加入到集合
            if (!TextUtils.isEmpty(originalAlipayBillDetail.getOrderNumber())){
                if (isMissData){
                    //因为数据遗漏进入的余额明细，抓取的数据放在遗漏数据集合
                    mMissOriginalAlipayBillDetails.add(originalAlipayBillDetail);
                }else {
                    //正常进入余额明细抓取数据
                    mNewOriginalAlipayBillDetails.add(originalAlipayBillDetail);
                }
            }

            MyLogger.xLog().e("lsh: " + lsh + " type: " + type + " balance: " + balance);
            return true;
        }

        return false;


//        //如果是最新的第一条收支详情数据，将其流水号和收支详情的类型存放在SP中
//        if (!isReadSzxqNewest && !TextUtils.isEmpty(lsh) && !TextUtils.isEmpty(type)){
//            SPUtils.put(getApplicationContext(), ContantsUtil.NEW_NEWEST_LSH, lsh + type);
//            isReadSzxqNewest = true;
//            //记录抓取的最新的余额
//            yemxNewestBalance = balance;
//        }
//
//        //如果当前的流水号和之前保存的最新的流水号是一致的
//        if (!TextUtils.isEmpty(lsh) && !TextUtils.isEmpty(type) && (lsh + type).equals(SPUtils.getString(getApplicationContext(), ContantsUtil.OLD_NEWEST_LSH))){
//            isAllNewSzxqRead = true;
//            //将最新的第一条流水号存放在久的最新的流水号里
//            SPUtils.put(getApplicationContext(), ContantsUtil.OLD_NEWEST_LSH,
//                    SPUtils.getString(getApplicationContext(), ContantsUtil.NEW_NEWEST_LSH));
//            //这条数据之前读取过不需要添加到集合
//            originalAlipayBillDetail.setOrderNumber("");
//        }
//
//        //如果是第一次进入的话，SP里的旧的最新的流水号数据为空，所以第一次进入账单只读取一屏的数据
//        if (currentVisibileUnReadYeItemCount == unNeedReadCount &&
//                TextUtils.isEmpty(SPUtils.getString(getApplicationContext(), ContantsUtil.OLD_NEWEST_LSH))){
//            //当读取的为余额明细一屏的最后一个，且是第一次进入余额明细
//            isAllNewSzxqRead = true;
//            //将最新的第一条流水号存放在久的最新的流水号里
//            SPUtils.put(getApplicationContext(), ContantsUtil.OLD_NEWEST_LSH,
//                    SPUtils.getString(getApplicationContext(), ContantsUtil.NEW_NEWEST_LSH));
//        }
//
//        //加入到集合
//        if (!TextUtils.isEmpty(originalAlipayBillDetail.getOrderNumber())){
//            if (isMissData){
//                //因为数据遗漏进入的余额明细，抓取的数据放在遗漏数据集合
//                mMissOriginalAlipayBillDetails.add(originalAlipayBillDetail);
//            }else {
//                //正常进入余额明细抓取数据
//                mNewOriginalAlipayBillDetails.add(originalAlipayBillDetail);
//            }
//        }
//
//        MyLogger.xLog().e("lsh: " + lsh + " type: " + type + " balance: " + balance);
//        return !TextUtils.isEmpty(lsh);
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
        //FIXME 最新的账单是否都已经读取完成是通过余额明细的数据是否填充完毕决定的

        //获取到能拿到数据信息的节点
        AccessibilityNodeInfo webviewNodeInfo = getWebViewNodeInfo();

        //先创建一个对象，填充数据，最后匹配余额明细集合找到订单号和流水号一致的账单进行填充
        OriginalAlipayBillDetail originalAlipayBillDetail = new OriginalAlipayBillDetail();
        int childCount = webviewNodeInfo.getChildCount();

        //服务费
        String serviceCharge = "";

        //先确定并拿到金额
        for (int i = 1; i < childCount; i++) {
            String text = webviewNodeInfo.getChild(i).getContentDescription().toString();
            if (!TextUtils.isEmpty(text) && text.length() > 3){
                //通过正则判断该节点是否是金额
                String regex = "^((-|\\+)?[0-9]{1,3}(,[0-9]{3})*)(.[0-9]{2})$";
                Pattern pattern = Pattern.compile(regex);
                boolean matches = pattern.matcher(text).matches();
                if (matches){
                    //当前这个节点是金额
                    originalAlipayBillDetail.setAmount(text.replace(",",""));
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
                case CLASS_ID_TEXT.ZFB_ZDXQ_DDH_TEXT://订单号
                    originalAlipayBillDetail.setOrderNumber(value);
                    //开始进行订单号和流水号匹配
                    if (!TextUtils.isEmpty(value)){
                        //遍历余额明细抓取的集合
                        for (int index = 0; index < mNewOriginalAlipayBillDetails.size(); index++) {
                            OriginalAlipayBillDetail alipayBillDetail = mNewOriginalAlipayBillDetails.get(index);
                            if (!TextUtils.isEmpty(alipayBillDetail.getAmount())){
                                //该笔数据已经填充过不再处理
                                continue;
                            }
                            if (value.equals(alipayBillDetail.getOrderNumber())){
                                //判断收支明细的类型
                                if (ContantsUtil.SZXQ_TX.equals(alipayBillDetail.getSzxqType())){
                                    alipayBillDetail.setSzxqType("");
                                    //该笔账单对应的有提现和提现手续费2笔数据
                                    //1.修改金额的正负号
                                    //将金额和服务费的符号改为“-”
                                    if (!TextUtils.isEmpty(originalAlipayBillDetail.getAmount()) && originalAlipayBillDetail.getAmount().length() > 1){
                                        originalAlipayBillDetail.setAmount("-" + originalAlipayBillDetail.getAmount().substring(1));
                                    }
                                    if (!TextUtils.isEmpty(serviceCharge) && serviceCharge.length() > 1){
                                        serviceCharge = "-" + serviceCharge.substring(1);
                                    }
                                    //2.改交易状态为交易成功
                                    originalAlipayBillDetail.setTradeState(ContantsUtil.JYZT_JYCG);
                                    //设置是支出类型
                                    originalAlipayBillDetail.setType("2");
                                    //拷贝提现数据
                                    fillBillData(originalAlipayBillDetail, alipayBillDetail);
                                    if (!TextUtils.isEmpty(serviceCharge)){//该笔提现没有服务费产生
                                        //3.计算服务费的余额(提现收支详情的余额-服务费)
                                        String balance = String.valueOf(new BigDecimal(alipayBillDetail.getSurplus()).subtract(new BigDecimal(serviceCharge.substring(1))).doubleValue());
                                        //4.生成服务费
                                        OriginalAlipayBillDetail serviceChargeBillDetail = copyOriginalAlipayBillDetail(alipayBillDetail);
                                        serviceChargeBillDetail.setAmount(serviceCharge);
                                        serviceChargeBillDetail.setSurplus(balance);
                                        serviceChargeBillDetail.setTransactionType(ContantsUtil.JYLX_FWF);
                                        mNewOriginalAlipayBillDetails.add(index, serviceChargeBillDetail);
                                    }
                                }else if (ContantsUtil.SZXQ_TXSBTH.equals(alipayBillDetail.getSzxqType())){
                                    alipayBillDetail.setSzxqType("");
                                    //该笔账单对应的有提现失败和提现失败手续费回退2笔数据
                                    //1.修改金额的正负号
                                    //将金额和服务费的符号改为“+”
                                    if (!TextUtils.isEmpty(originalAlipayBillDetail.getAmount()) && originalAlipayBillDetail.getAmount().length() > 1){
                                        originalAlipayBillDetail.setAmount("+" + originalAlipayBillDetail.getAmount().substring(1));
                                    }
                                    if (!TextUtils.isEmpty(serviceCharge) && serviceCharge.length() > 1){
                                        serviceCharge = "+" + serviceCharge.substring(1);
                                    }
                                    //2.交易状态改为回冲
                                    originalAlipayBillDetail.setTradeState(ContantsUtil.JYZT_HC);
                                    //设置是收入类型
                                    originalAlipayBillDetail.setType("1");
                                    //拷贝提现失败数据
                                    fillBillData(originalAlipayBillDetail, alipayBillDetail);
                                    if (!TextUtils.isEmpty(serviceCharge)){//该笔提现失败回退账单没有服务费
                                        //3.计算服务费的余额
                                        String balance = String.valueOf(new BigDecimal(alipayBillDetail.getSurplus()).add(new BigDecimal(serviceCharge.substring(1))).doubleValue());
                                        //4.生成服务费
                                        OriginalAlipayBillDetail serviceChargeBillDetail = copyOriginalAlipayBillDetail(alipayBillDetail);
                                        serviceChargeBillDetail.setAmount(serviceCharge);
                                        serviceChargeBillDetail.setSurplus(balance);
                                        serviceChargeBillDetail.setTransactionType(ContantsUtil.JYLX_FWF);
                                        mNewOriginalAlipayBillDetails.add(index, serviceChargeBillDetail);
                                    }
                                }else {
                                    //设置是支出还是收入类型
                                    if (originalAlipayBillDetail.getAmount().startsWith("+")){
                                        originalAlipayBillDetail.setType("1");
                                    }else if (originalAlipayBillDetail.getAmount().startsWith("-")){
                                        originalAlipayBillDetail.setType("2");
                                    }
                                    //流水号和订单号一致拷贝数据
                                    fillBillData(originalAlipayBillDetail, alipayBillDetail);
                                }
                            }
                        }
                    }

                    boolean isAll = true;
                    //如果明细列表集合所有的数据都有金额代表数据都已经填充完毕即所有的数据都已经读取
                    for (OriginalAlipayBillDetail newOriginalAlipayBillDetail : mNewOriginalAlipayBillDetails) {
                        if (TextUtils.isEmpty(newOriginalAlipayBillDetail.getAmount())){
                            isAll = false;
                            break;
                        }
                    }
                    if (isAll){
                        isAllNewRead = true;
                    }
                    break;
            }
        }
        return !TextUtils.isEmpty(originalAlipayBillDetail.getAmount());
    }

    /**
     * 从账单详情的数据填充到余额明细抓取的集合数据中:
     *      1.流水号和订单号一致不用拷贝
     *      2.余额账单明细没有不用可拷贝
     */
    private void fillBillData(OriginalAlipayBillDetail zdxq, OriginalAlipayBillDetail szmx){
        szmx.setNickName(zdxq.getNickName());
        szmx.setAmount(zdxq.getAmount());
        szmx.setType(zdxq.getType());
        szmx.setTradeState(zdxq.getTradeState());
        szmx.setActionDescription(zdxq.getActionDescription());
        szmx.setRemark(zdxq.getRemark());
        szmx.setTransactionType(zdxq.getTransactionType());
        szmx.setBillClassification(zdxq.getBillClassification());
        szmx.setCreationTime(zdxq.getCreationTime());
        szmx.setExpenditureMethod(zdxq.getExpenditureMethod());
        szmx.setInComeMethod(zdxq.getInComeMethod());
        szmx.setReciprocalAccount(zdxq.getReciprocalAccount());
        szmx.setWithdrawCashTo(zdxq.getWithdrawCashTo());
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
     *      流水号(订单号)、收支详情类型、余额 均一致视为同一笔数据
     */
    private ArrayList<OriginalAlipayBillDetail> clearUpData(ArrayList<OriginalAlipayBillDetail> billDetails) {
        for (int i = 0; i < billDetails.size() - 1; i++) {
            for(int j = billDetails.size() -1; j > i; j--){
                if (billDetails.get(j).getOrderNumber().equals(billDetails.get(i).getOrderNumber())
                        && billDetails.get(j).getSzxqType().equals(billDetails.get(i).getSzxqType())
                        && billDetails.get(j).getSurplus().equals(billDetails.get(i).getSurplus())){
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
    private int getCurrentVisibleZdItemCount() {
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
     * 获取当前页面可见的余额条目的个数
     */
    private int getCurrentVisibleYeItemCount() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (null != nodeInfo){
            List<AccessibilityNodeInfo> yeItemList = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_YEMX_ITEM_ID);
            return yeItemList.isEmpty() ? 0 : yeItemList.size();
        }
        return 0;
    }

    /**
     * 点击余额position位置的条目
     * @param position
     */
    private boolean clickYeItem(int position){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (null != nodeInfo){
            List<AccessibilityNodeInfo> yeItemList = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ZFB_YEMX_ITEM_ID);
            if (!yeItemList.isEmpty() && yeItemList.size() > position){
                return yeItemList.get(position).performAction(AccessibilityNodeInfo.ACTION_CLICK);
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
     * 将SP中的数据存放在oldNewestLshTypeList集合中
     */
    public void spToList(){
        String lshTypeList = SPUtils.getString(getApplicationContext(), ContantsUtil.LSH_TYPE_LIST);
        oldNewestLshTypeList.clear();
        if (!TextUtils.isEmpty(lshTypeList)){
            String[] split = lshTypeList.split(",");
            if (split.length > 0){
                for (String s : split) {
                    oldNewestLshTypeList.add(s);
                }
            }
        }
        SPUtils.put(getApplicationContext(), ContantsUtil.LSH_TYPE_LIST, "");
    }

    /**
     * 将oldNewestLshTypeList集合中的数据存放在SP中
     */
    public void listToSp(){
        if (!oldNewestLshTypeList.isEmpty()){
            StringBuffer sb = new StringBuffer();
            for (String s : oldNewestLshTypeList) {
                sb.append(s).append(",");
            }
            SPUtils.put(getApplicationContext(), ContantsUtil.LSH_TYPE_LIST, sb.substring(0, sb.length()-1).toString());
        }else {
            SPUtils.put(getApplicationContext(), ContantsUtil.LSH_TYPE_LIST, "");
        }
        oldNewestLshTypeList.clear();
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
