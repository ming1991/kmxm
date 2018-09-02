package com.kmjd.wcqp.single.zxh.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;


import com.kmjd.wcqp.single.zxh.MainApplication;
import com.kmjd.wcqp.single.zxh.model.CLASS_ID_TEXT;
import com.kmjd.wcqp.single.zxh.model.rxbusBean.RxBusEvent;
import com.kmjd.wcqp.single.zxh.util.ChangeDetailUploadServiceUtil;
import com.kmjd.wcqp.single.zxh.util.CheckWhetherInstalledAnApplication;
import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.RxBus;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewWeChatAccessibilityService extends AccessibilityService {


    private static boolean go = false;
    private static boolean jszz = false;//接受转账
    private static boolean acceptFriendRequest = false;
    private static boolean clickMe = true;
    private static boolean clickMe_QB = false;
    private ExecutorService executorService;
    private static final int waitTime = 2000;
    private String TAG = "Android-Star";
    private boolean temp = false;

    /**
     * 当启动服务的时候就会被调用
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        initConfig();
    }


    /**
     * 监听窗口变化的回调
     */
    private final String classNameTemp[] = {null};

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        int eventType = event.getEventType();
        switch (eventType) {
            //当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                classNameTemp[0] = className;
                if (go) {
                    switch (className) {
                        case CLASS_ID_TEXT.CLASS_SY_WX: //微信首页面
                            //通过判断点击"微信”通讯录“或者”我“
                            excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_SY_WX, CLASS_ID_TEXT.ID_TXL_AND_ME));
                            break;
                        case CLASS_ID_TEXT.CLASS_WDQB: //“我的钱包”页面
                            //点击“零钱”的控件
                            excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_WDQB, CLASS_ID_TEXT.ID_LQ));
                            break;
                        case CLASS_ID_TEXT.CLASS_LQ:  //“零钱”页面
                            //点击”零钱明细“
                            excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_LQ, CLASS_ID_TEXT.ID_LQMX));
                            go = false;
                            break;

                    }

                    if (acceptFriendRequest) {
                        switch (className) {
                            case CLASS_ID_TEXT.CLASS_XDPY:  //”新的朋友“界面
                                //删除所有已经添加的记录
                                excute(new OperateAcceptFriendRunnable(className, CLASS_ID_TEXT.TEXT_YTJ));
                                break;
                            case CLASS_ID_TEXT.CLASS_PYYZ: //"朋友验证"界面
                                //点击“完成”
                                excute(new OperateAcceptFriendRunnable(className, CLASS_ID_TEXT.ID_WC));
                                break;
                            case CLASS_ID_TEXT.CLASS_XXZL: //“详细资料"界面
                                //点击返回
                                excute(new OperateAcceptFriendRunnable(CLASS_ID_TEXT.CLASS_XXZL, CLASS_ID_TEXT.ID_ALL_BACK));
                                break;
                        }
                    } else if (jszz) {
                        switch (className) {
                            case CLASS_ID_TEXT.CLASS_TALLK_ITEM: {//首页聊天页面对话框类名
                                excute(new ReceiveTransfersRunnable(CLASS_ID_TEXT.CLASS_TALLK_ITEM, CLASS_ID_TEXT.TEXT_DELETE_CHAT));
                            }
                            break;
                            case CLASS_ID_TEXT.CLASS_TALLK_ITEM_DELETE: {//是否删除该聊天类名
                                excute(new ReceiveTransfersRunnable(CLASS_ID_TEXT.CLASS_TALLK_ITEM_DELETE, CLASS_ID_TEXT.ID_DELETE));
                            }
                            break;
                            case CLASS_ID_TEXT.CLASS_WILL_RECEIVE_TRANSFER: {//转账确认收款页面
                                excute(new ReceiveTransfersRunnable(CLASS_ID_TEXT.CLASS_WILL_RECEIVE_TRANSFER, ""));
                            }
                            break;
                            case CLASS_ID_TEXT.CLASS_TENCENT_NEWS: {
                                excute(new ReceiveTransfersRunnable(CLASS_ID_TEXT.CLASS_TENCENT_NEWS, CLASS_ID_TEXT.ID_TENCENT_NEWS_BACKS));
                            }
                            break;
                        }
                    } else {
                        switch (className) {
                            //异常停留在添加朋友界面
                            case CLASS_ID_TEXT.CLASS_XDPY://异常情况卡在了“添加朋友”界面,且acceptFriendRequest为false,再次点击退出
                                excute(new OperateAcceptFriendRunnable(className, CLASS_ID_TEXT.ID_ALL_BACK));
                                break;
                        }
                    }
                } else {
                    switch (className) {
                        case CLASS_ID_TEXT.CLASS_LQMX:  //”零钱明细“页面
                            excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_LQMX, CLASS_ID_TEXT.ID_ALL_BACK));
                            break;
                        case CLASS_ID_TEXT.CLASS_LQ: //”零钱“页面
                            //退出”零钱“
                            excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_LQ, CLASS_ID_TEXT.ID_ALL_BACK));
                            break;
                        case CLASS_ID_TEXT.CLASS_WDQB: //”我的钱包“页面
                            //退出我的钱包
                            excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_WDQB, CLASS_ID_TEXT.ID_ALL_BACK));
                            go = true;

                            jszz = false;
                            acceptFriendRequest = false;
                            clickMe = true;

                            clickMe_QB = false;
                            break;
                        //异常情况
                        case CLASS_ID_TEXT.CLASS_SY_WX://模拟点击突然跳转到微信的首页面 异常为：go = false:
                            excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_SY_WX, CLASS_ID_TEXT.E_MNDJ_SKWXSYM));
                            break;
                    }
                }

                if (className.equals(CLASS_ID_TEXT.E_CLASS_YHJD)) {//"用户较多"异常对话框
                    clickParentByChaildText(CLASS_ID_TEXT.E_TEXT_YHJD);
                }
                break;

        }


    }


    /*#################################################零钱明细################################################*/

    private class ChangeDetailRunnable implements Runnable {
        private String classNameOrFunctionName;
        private String idOrText;
//        private static final int waitTime = 2000;

        ChangeDetailRunnable(String classNameOrFunctionName, String idOrText) {
            this.classNameOrFunctionName = classNameOrFunctionName;
            this.idOrText = idOrText;
        }

        @Override
        public synchronized void run() {
            Log.d(TAG, "ChangeDetailRunnable->classNameOrFunctionName：" + classNameOrFunctionName + "->idOrText：" + idOrText);
            SystemClock.sleep(waitTime);
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_SY_WX) && idOrText.equals(CLASS_ID_TEXT.ID_TXL_AND_ME)) {
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> windowFlag = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_CHAT_WINDOW_WECHAT_ACCOUNT_BACKUP);
                    if (windowFlag.isEmpty()) {//如果不是聊天界面就正常执行
                        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_TXL_AND_ME);
                        /*if (list.isEmpty()) {
                            SystemClock.sleep(waitTime);
                            openWeChat_HomePage();
                            return;
                        }*/
                        if (!list.isEmpty()){
                            for (AccessibilityNodeInfo item : list) {
                                if (null != item.getText()) {
                                    String text = item.getText().toString();
                                    if (jszz && text.equals(CLASS_ID_TEXT.TEXT_WECHAT)) {
                                        begin(list, CLASS_ID_TEXT.TEXT_WECHAT);
                                        break;
                                    } else if (acceptFriendRequest && text.equals(CLASS_ID_TEXT.TEXT_TXL)) {
                                        begin(list, CLASS_ID_TEXT.TEXT_TXL);
                                        break;
                                    } else if (clickMe && text.equals(CLASS_ID_TEXT.TEXT_ME)) {//如果text为“我”
                                        begin(list, CLASS_ID_TEXT.TEXT_ME);
                                        String currentWeChatCount = SPUtils.getString(MainApplication.applicationContext, ContantsUtil.CURRENT_WECHAT_ACCOUNT);
                                        if (null != currentWeChatCount && !currentWeChatCount.isEmpty()) {
                                            if (clickMe_QB) {
                                                clickParentByChaildId(CLASS_ID_TEXT.ID_QB);
                                            } else {
                                                boolean s = ChangeDetailUploadService.doCheckSwitch(currentWeChatCount, 0, 5000);
                                                temp = s;
                                                Log.d(TAG, "储值器开关状态: " + s);
                                                if (s && classNameTemp[0] != null && classNameTemp[0].equals(CLASS_ID_TEXT.CLASS_SY_WX)) {
//                                                begin(list, CLASS_ID_TEXT.TEXT_WECHAT);

                                                    //FIXME 判断是否有新的消息
                                                    /**
                                                     * 原来的需求：点击维信
                                                     * 尝试修改：
                                                     *      检查是否有新的消息
                                                     *          1.如果有点“微信”
                                                     *          2.如果没有就直接点击钱包
                                                     */
                                                    if (unReadCount() > 0) {
                                                        begin(list, CLASS_ID_TEXT.TEXT_WECHAT);
                                                    } else {
                                                        clickParentByChaildId(CLASS_ID_TEXT.ID_QB);
                                                    }
                                                } else {//每5秒请求返回false的情况
//                                                clickMe = false;
//                                                jszz = false;
//                                                acceptFriendRequest = true;
//                                                begin(list, CLASS_ID_TEXT.TEXT_TXL);

                                                    //FIXME 判断是否有新的朋友添加
                                                    /**
                                                     * 原来的需求：
                                                     *      点击通讯录，查找我的朋友，处理我的朋友界面的逻辑
                                                     * 尝试修改：如果返回为false
                                                     *       检查是否有新的好友添加
                                                     *          1.如果有就去添加
                                                     *          2.没有继续在“我”检查返回值
                                                     */
                                                    if (unAddCount() > 0) {
                                                        clickMe = false;
                                                        jszz = false;
                                                        acceptFriendRequest = true;
                                                        begin(list, CLASS_ID_TEXT.TEXT_TXL);
                                                    } else {
                                                        //没有新的朋友添加
                                                        excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_SY_WX, CLASS_ID_TEXT.ID_TXL_AND_ME));
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                    }
                    } else {
                        excute(new ReceiveTransfersRunnable("receiveTransfers()", ""));
                    }
                }
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_WDQB) && idOrText.equals(CLASS_ID_TEXT.ID_LQ)) {
                //获取“我的钱包”页面的“零钱”
                SystemClock.sleep(waitTime / 2);
                //获取零钱存入SP
                SPUtils.put(getApplicationContext(), ContantsUtil.CURRENT_WECHAT_ACCOUNT_BALANCE, getTextById(CLASS_ID_TEXT.ID_LQ_RMB));
                SystemClock.sleep(waitTime / 2);
                //TODO 修改为通过零钱的文本点击
                clickParentByChaildText(CLASS_ID_TEXT.TEXT_LQ);
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_LQ) && idOrText.equals(CLASS_ID_TEXT.ID_LQMX)) {
                //点击“零钱”页面的”零钱明细“
                SystemClock.sleep(waitTime);
                clickParentByChaildId(idOrText);
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_LQMX) && idOrText.equals(CLASS_ID_TEXT.ID_ALL_BACK)) {
                //“返回”的id
                //到达零钱明细界面4秒后发送通知提交数据
                SystemClock.sleep(4000);
                getTextAll("com.tencent.mm:id/ay3");
                //TODO RxBus发送通知
                RxBus.getInstance().post(RxBusEvent.RXBUSEVENT_NOTICE_UPLOAD);
                //退出"零钱明细"
                int sleepTime = SPUtils.getInt(getApplicationContext(), ContantsUtil.REFRESH_RATE) * 1000 >= 16 * 1000 ?
                        SPUtils.getInt(getApplicationContext(), ContantsUtil.REFRESH_RATE) * 1000 : 16 * 1000;
                SystemClock.sleep(sleepTime);
                clickParentByChaildId(idOrText);
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_LQ) && idOrText.equals(CLASS_ID_TEXT.ID_ALL_BACK)) {
                clickParentByChaildId(idOrText);
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_WDQB) && idOrText.equals(CLASS_ID_TEXT.ID_ALL_BACK)) {
                clickParentByChaildId(idOrText);
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_SY_WX) && idOrText.equals(CLASS_ID_TEXT.E_MNDJ_SKWXSYM)) {
                //模拟点击异常出现在了微信的首页面：直接切换到原生微信
                SystemClock.sleep(waitTime);
                openWeChat_HomePage();
            }

        }
    }

    private void getTextAll(String id) {
        List<AccessibilityNodeInfo> nodeInfosByViewId = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(id);
        if (!nodeInfosByViewId.isEmpty()){
            Log.d(TAG, "getTextAll: "+nodeInfosByViewId.get(0).
                    getChild(0).getChild(0).getChild(0).getChild(0).getContentDescription().toString()); ;
        }
    }
    private class OperateAcceptFriendRunnable implements Runnable {
        private String classNameOrFunctionName;
        private String idOrText;
//        private static final int waitTime = 3000;

        OperateAcceptFriendRunnable(String classNameOrFunctionName, String idOrText) {
            this.classNameOrFunctionName = classNameOrFunctionName;
            this.idOrText = idOrText;
        }

        @Override
        public synchronized void run() {
            Log.d(TAG, "OperateAcceptFriendRunnable->classNameOrFunctionName：" + classNameOrFunctionName + "->idOrText：" + idOrText);
            if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_XDPY) && idOrText.equals(CLASS_ID_TEXT.TEXT_YTJ)) {
                while (true) {
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    if (null != nodeInfo) {
                        List<AccessibilityNodeInfo> alreadyAcceptList = nodeInfo.findAccessibilityNodeInfosByText(idOrText);
                        if (alreadyAcceptList.size() == 0) {
                            SystemClock.sleep(waitTime / 2);
                            break;
                        } else {
                            AccessibilityNodeInfo viewGroup = null;
                            AccessibilityNodeInfo item = alreadyAcceptList.get(0);
                            if (!item.isClickable()) {
                                viewGroup = item.getParent();
                            }
                            while (true) {
                                if (null != viewGroup) {
                                    if (!viewGroup.isClickable()) {
                                        viewGroup = viewGroup.getParent();
                                    } else {
                                        SystemClock.sleep(waitTime / 2);
                                        viewGroup.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                                        SystemClock.sleep(waitTime / 2);
                                        clickParentByChaildText(CLASS_ID_TEXT.TEXT_SC);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> acceptList = nodeInfo.findAccessibilityNodeInfosByText(CLASS_ID_TEXT.TEXT_JS);
                    //没有申请就返回
                    if (acceptList.size() == 0) {
                        acceptFriendRequest = false;
                        jszz = false;
                        clickMe = true;
                        clickMe_QB = false;
                        SystemClock.sleep(waitTime);
                        clickParentByChaildId(CLASS_ID_TEXT.ID_ALL_BACK);
                       /* //通过判断点击"微信”通讯录“或者”我“
                        excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_SY_WX, CLASS_ID_TEXT.ID_TXL_AND_ME));*/
                    } else {
                        //有申请就添加第一个
                        SystemClock.sleep(waitTime);
                        acceptList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }

                }

                ////"朋友验证"界面，点击“完成”
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_PYYZ) && idOrText.equals(CLASS_ID_TEXT.ID_WC)) {
                SystemClock.sleep(waitTime);
                clickParentByChaildId(idOrText);
                //”详细资料“界面，点击返回
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_XXZL) && idOrText.equals(CLASS_ID_TEXT.ID_ALL_BACK)) {
                SystemClock.sleep(waitTime);
                clickParentByChaildId(idOrText);
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_XDPY) && idOrText.equals(CLASS_ID_TEXT.ID_ALL_BACK)) {
                //异常卡住在了添加朋友界面，再次点击返回
                SystemClock.sleep(waitTime);
                clickParentByChaildId(idOrText);
            }

        }
    }


    private class ReceiveTransfersRunnable implements Runnable {
        private String classNameOrFunctionName;
        private String idOrText;
        private static final int waitTime = 2000;

        public ReceiveTransfersRunnable(String classNameOrFunctionName, String idOrText) {
            this.classNameOrFunctionName = classNameOrFunctionName;
            this.idOrText = idOrText;
        }


        @Override
        public synchronized void run() {
            Log.d(TAG, "ReceiveTransfersRunnable->classNameOrFunctionName：" + classNameOrFunctionName + "->idOrText：" + idOrText);
            if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_TALLK_ITEM) && idOrText.equals(CLASS_ID_TEXT.TEXT_DELETE_CHAT)) {
                SystemClock.sleep(waitTime);
                clickParentByChaildText(CLASS_ID_TEXT.TEXT_DELETE_CHAT);
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_TALLK_ITEM_DELETE) && idOrText.equals(CLASS_ID_TEXT.ID_DELETE)) {
                SystemClock.sleep(waitTime);
                if (clickParentByChaildId(CLASS_ID_TEXT.ID_DELETE)) {
                    checkAllTallkList();//如果删除成功就在此检测
                }
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_WILL_RECEIVE_TRANSFER) && idOrText.equals("")) {
                SystemClock.sleep(waitTime);
                String flag = getTextById(CLASS_ID_TEXT.ID_WILL_RECEIVE_OR_RECEIVED);//获取是待确认收款还是已经收取
                switch (flag) {
                    case CLASS_ID_TEXT.TEXT_NEED_SURE: {//如果是待确认收款，就点击”确认收款“
                        clickParentByChaildId(CLASS_ID_TEXT.ID_SURE_RECEIVE);
                    }
                    break;
                    case CLASS_ID_TEXT.TEXT_RECEIVED: {//如果是已经收钱，就点击返回
                        clickParentByChaildId(CLASS_ID_TEXT.ID_RECEIVED);
                    }
                    break;
                }
            } else if (classNameOrFunctionName.equals("checkAllTallkList()") && idOrText.equals("")) {
                SystemClock.sleep(waitTime);
                checkAllTallkList();
            } else if (classNameOrFunctionName.equals("receiveTransfers()") && idOrText.equals("")) {
                SystemClock.sleep(waitTime);
                receiveTransfers();
            } else if (classNameOrFunctionName.equals(CLASS_ID_TEXT.CLASS_TENCENT_NEWS) && idOrText.equals(CLASS_ID_TEXT.ID_TENCENT_NEWS_BACKS)) {
                clickParentByChaildId(CLASS_ID_TEXT.ID_TENCENT_NEWS_BACKS);
            }
        }
    }
    /*#################################################接收转账################################################*/


    public void openWeChat_HomePage() {
        //初始化字段配置
        initConfig();
        boolean isInstalled = CheckWhetherInstalledAnApplication.isWeiXinClientAvilible(MainApplication.applicationContext);
        if (isInstalled) {
            Intent intent = null;
            ComponentName componentName = null;
            intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            componentName = new ComponentName(CLASS_ID_TEXT.PACKAGE_NAME_WX, CLASS_ID_TEXT.CLASS_SY_WX);
            intent.setComponent(componentName);
            startActivity(intent);
        }

    }

    /**
     * 对于运行中无障碍服务卡住，直接重置配置
     */
    private void initConfig() {
        go = true;
        jszz = false;
        acceptFriendRequest = false;
        clickMe = true;
        clickMe_QB = false;
        classNameTemp[0] = null;
        if (null == executorService) {
            executorService = Executors.newCachedThreadPool();
        }
        //将当前微信账号置空
//        SPUtils.put(MainApplication.applicationContext, ContantsUtil.CURRENT_WECHAT_ACCOUNT, "");
        //置空当前零钱
//        SPUtils.put(MainApplication.applicationContext, ContantsUtil.CURRENT_WECHAT_ACCOUNT_BALANCE, "");
        //TODO 清空抓取的数据
        ChangeDetailUploadServiceUtil.clearHarLog();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean clickParentByChaildId(String childId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        switch (childId) {
            case CLASS_ID_TEXT.ID_QB:
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_QB);

                    for (AccessibilityNodeInfo item : list) {

                        if (null != item.getText()) {
                            String text = item.getText().toString();

                            if (text.equals(CLASS_ID_TEXT.TEXT_QB)) {

                                if (!item.isClickable()) {
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
                                } else {
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
                                            return true;//TODO 如果点击了就退出该方法
                                        }
                                    }
                                }
                            } else {
                                viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                return true;//TODO 如果点击了就退出该方法
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
     * false: 没有相应的节点
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
                                return true;//TODO 如果点击了就退出该方法
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
     * 通过控件ID获取文本
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getTextById(String id) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        String needText = "";
        switch (id) {
            case CLASS_ID_TEXT.ID_WXH: {
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_WXH);
                    for (AccessibilityNodeInfo item : list) {
                        if (null != item.getText()) {
                            String text = item.getText().toString();
                            if (text.contains("微信号：")) {
                                needText = text;
                                return needText;
                            }
                        }

                    }
                }
                return needText;
            }
            case CLASS_ID_TEXT.ID_LQ_RMB: {
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_LQ_RMB);
                    for (AccessibilityNodeInfo item : list) {
                        if (null != item.getText()) {
                            String text = item.getText().toString();
                            if (text.contains("¥")) {
                                needText = text;
                                return needText;
                            }
                        }

                    }
                }
                return needText;
            }
            case CLASS_ID_TEXT.ID_WILL_RECEIVE_OR_RECEIVED: {
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_WILL_RECEIVE_OR_RECEIVED);
                    for (AccessibilityNodeInfo item : list) {
                        if (null != item.getText()) {
                            needText = item.getText().toString();
                            return needText;
                        }

                    }
                }
                return needText;
            }

        }

        return needText;
    }

    /**
     * 中断服务的回调
     */
    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        initConfig();

        super.onDestroy();
    }

    /**
     * 检查所有的聊天：
     * 原来的需求：是否有聊天的条目
     * 1.还有聊天的条目：获取第一个条目
     * 1.第一个条目是未读条目，点击进去，查看是否有转账记录
     * 2.第一个条目时已读条目，长按删除
     * 2.没有聊天的条目了：修改标志，点击"微信”通讯录“或者”我“
     * <p>
     * 为了解决华硕手机不能删除聊天记录的问题：
     * 修改的需求为：是否有新的未读聊天条目
     * 1.有新的未读聊天条目，点击进入，查看是否有转账记录
     * 2.没有新的未读聊天条目：判断总的未读消息的数目是否大于0
     * 1.总的未读消息的数量大于0：双击微信手机自动滑动到新消息的位置
     * 2.总的未读消息也是0，真的没有新的消息了：单击微信，保证第一条消息出现在头部，修改标志，点击"微信”通讯录“或者”我“
     */
    public void checkAllTallkList() {
        SystemClock.sleep(waitTime / 2);
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> redDotList = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_NEW_MSG_FLAG);
        if (!redDotList.isEmpty()) {
            Log.d(TAG, "当前页面还有新消息，当前界面新消息有： " + redDotList.size());
            AccessibilityNodeInfo redDot = redDotList.get(0);
            //有新消息进入聊天页面
            if (!redDot.isClickable()) {
                redDot = redDot.getParent();
                while (true) {
                    if (null != redDot) {
                        if (!redDot.isClickable()) {
                            redDot = redDot.getParent();
                        } else {
                            redDot.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            //检查是否有转账记录
                            excute(new ReceiveTransfersRunnable("receiveTransfers()", ""));
                            break;
                        }
                    }
                }
            } else {
                redDot.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                excute(new ReceiveTransfersRunnable("receiveTransfers()", ""));
            }
        } else {//当前界面没有新消息
            Log.d(TAG, "当前页面没有新消息了");
            //判断当前界面的有没有不可见的消息，判读总的未读消息的数量是不是大于0
            if (unReadCount() > 0) {
                Log.d(TAG, "总的未读消息的数量大于0，双击微信到未读消息的位置");
                clickWX(2);
                checkAllTallkList();
            } else {
                Log.d(TAG, "真的是所有的消息都已经读了  单击微信");
                clickWX(1);
                //通过判断点击"微信”通讯录“或者”我“
                if (unReadCount() > 0) {//判断消息读完准备跳转而没有跳转的时候又有新消息来的特殊情况
                    excute(new ReceiveTransfersRunnable("checkAllTallkList()", ""));
                } else {
                    jszz = false;//关闭接受转账
                    acceptFriendRequest = false;
                    clickMe_QB = true;
                    excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_SY_WX, CLASS_ID_TEXT.ID_TXL_AND_ME));
                }

            }
        }
    }

    /**
     * 未添加的好友数量
     */
    private int unAddCount() {
        int unAddCount = 0;
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> wxtxlList = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.WXWDXXS_ID);
        //遍历所有的节点看有没有时通讯录的
        if (!wxtxlList.isEmpty()) {
            for (AccessibilityNodeInfo wxtxlNodeInfo : wxtxlList) {
                //判断这个节点是不是通讯录
                Rect unAddRect = new Rect();
                wxtxlNodeInfo.getBoundsInScreen(unAddRect);

                Rect topRect = new Rect();
                List<AccessibilityNodeInfo> topNodeInfos = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.WX_TOP_TOOLBAR_ID);
                if (!topNodeInfos.isEmpty()) {
                    AccessibilityNodeInfo topNodeInfo = topNodeInfos.get(0);
                    //获取头部节点右坐标的位置
                    topNodeInfo.getBoundsInScreen(topRect);
                }

                if (unAddRect.right > (topRect.right / 4) && unAddRect.right < (topRect.right / 2)) {
                    //该节点对应的是“通迅录”
                    String count = wxtxlNodeInfo.getText().toString();
                    unAddCount = Integer.valueOf(count);
                    break;
                }
            }
        }
        Log.d(TAG, "unAddCount： " + unAddCount);
        return unAddCount;
    }

    /**
     * 检查总的未读消息还有几条
     */
    private int unReadCount() {
        int unReadCount = 0;
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> wxwdxxsList = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.WXWDXXS_ID);
        if (!wxwdxxsList.isEmpty()) {
            //拿到第一个节点
            AccessibilityNodeInfo wxxxsNodeInfo = wxwdxxsList.get(0);
            //判断这个节点的对应的是不是微信
            Rect unReadRect = new Rect();
            wxxxsNodeInfo.getBoundsInScreen(unReadRect);

            Rect topRect = new Rect();
            List<AccessibilityNodeInfo> topNodeInfos = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.WX_TOP_TOOLBAR_ID);
            if (!topNodeInfos.isEmpty()) {
                AccessibilityNodeInfo topNodeInfo = topNodeInfos.get(0);
                //获取头部节点右坐标的位置
                topNodeInfo.getBoundsInScreen(topRect);
            }

            if (unReadRect.right < (topRect.right / 4)) {
                //该节点对应的是"微信"
                String count = wxxxsNodeInfo.getText().toString();
                unReadCount = Integer.valueOf(count);
            }
        }
        Log.d(TAG, "unReadCount： " + unReadCount);
        return unReadCount;
    }

    /**
     * 点击微信RadioButton的微信
     */
    private void clickWX(int count) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_TXL_AND_ME);
        if (!list.isEmpty()) {
            for (AccessibilityNodeInfo item : list) {
                if (null != item.getText() && CLASS_ID_TEXT.TEXT_WECHAT.equals(item.getText().toString())) {
                    AccessibilityNodeInfo viewGroup = null;
                    if (!item.isClickable()) {
                        viewGroup = item.getParent();
                    }
                    while (true) {
                        if (null != viewGroup) {
                            if (!viewGroup.isClickable()) {
                                viewGroup = viewGroup.getParent();
                            } else {
                                SystemClock.sleep(2000);
                                for (int i = 0; i < count; i++) {
                                    viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * 点开聊天条目到达某个微信好友的的聊天界面
     */
    public synchronized void receiveTransfers() {
        boolean haveNoReceivedTransfer = false;
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (null != nodeInfo) {
            //获取转账
            List<AccessibilityNodeInfo> windowFlag = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_CHAT_WINDOW_WECHAT_ACCOUNT_BACKUP);
            if (!windowFlag.isEmpty()) {
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(CLASS_ID_TEXT.ID_TRANSFER_BACKUP);
                String transfersBackup = "";
                if (!list.isEmpty()) {//无论是否收账，说明有转账记录
                    for (AccessibilityNodeInfo item : list) {
                        transfersBackup = item.getText().toString();
                        if ((!transfersBackup.contains(CLASS_ID_TEXT.TEXT_HAVA_RECEIVED)) && (!transfersBackup.equals(CLASS_ID_TEXT.TEXT_HAVA_RECEIVED_M))) {//只要不是这其中一个，那么就是未收款的转账
                            haveNoReceivedTransfer = true;
                            //对未收款的转账执行收款
                            if (!item.isClickable()) {
                                AccessibilityNodeInfo viewGroup = item.getParent();
                                while (true) {
                                    if (null != viewGroup) {
                                        if (!viewGroup.isClickable()) {
                                            viewGroup = viewGroup.getParent();
                                        } else {
                                            viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                            break;
                                        }
                                    }
                                }
                            } else {
                                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                break;
                            }
                        }
                    }
                    if (!haveNoReceivedTransfer) {//说明无转账记录
                        SystemClock.sleep(waitTime);
                        clickParentByChaildId(CLASS_ID_TEXT.ID_CHAT_BACK);//返回
                        //上一部操作是从聊天界面返回到首页，需要再次检测聊天条目
                        excute(new ReceiveTransfersRunnable("checkAllTallkList()", ""));
                    }
                } else {//说明聊天内容中无转账记录
                    SystemClock.sleep(waitTime);
                    clickParentByChaildId(CLASS_ID_TEXT.ID_CHAT_BACK);//返回
                    //上一部操作是从聊天界面返回到首页，需要再次检测聊天条目
                    excute(new ReceiveTransfersRunnable("checkAllTallkList()", ""));
                }
            } else {
                //FIXME 华硕手机如果不是最新的消息的话，点击新消息点不进去，必须有新消息触发，直接跳过接受转账到其他操作
                Log.d(TAG, "华硕手机如果不是最新的消息的话，点击新消息点不进去，必须有新消息触发，直接跳过接受转账到其他操作");
                clickWX(1);
                jszz = false;//关闭接受转账
                acceptFriendRequest = false;
                clickMe_QB = true;
                //通过判断点击"微信”通讯录“或者”我“
                excute(new ChangeDetailRunnable(CLASS_ID_TEXT.CLASS_SY_WX, CLASS_ID_TEXT.ID_TXL_AND_ME));
            }
        }
    }

    private Future resultFuture = null;
    private Runnable mRunnable = null;

    private synchronized void excute(Runnable targetRunnable) {

        if (null == executorService) {
            executorService = Executors.newCachedThreadPool();
        }

        if (null == mRunnable) {
            mRunnable = targetRunnable;
            resultFuture = executorService.submit(mRunnable);
        } else if (!resultFuture.isDone()) {
            if (resultFuture.cancel(true)) {
                Log.d(TAG, "excute: 可以取消");
                mRunnable = null;
                mRunnable = targetRunnable;
                resultFuture = executorService.submit(mRunnable);
            }
        } else {
            mRunnable = null;
            mRunnable = targetRunnable;
            resultFuture = executorService.submit(mRunnable);
        }
    }


    public void begin(List<AccessibilityNodeInfo> list, String textToClick) {
        if (list.isEmpty()) {
            return;
        }
        for (AccessibilityNodeInfo item : list) {
            if (null != item.getText()) {
                String text = item.getText().toString();
                if (text.equals(textToClick)) {
                    AccessibilityNodeInfo viewGroup = null;
                    if (!item.isClickable()) {
                        viewGroup = item.getParent();
                    }
                    while (true) {
                        if (null != viewGroup) {
                            if (!viewGroup.isClickable()) {
                                viewGroup = viewGroup.getParent();
                            } else {
                                //一段时间后点击首页微信图标
                                SystemClock.sleep(waitTime);
                                viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                switch (textToClick) {
                                    case CLASS_ID_TEXT.TEXT_WECHAT:
                                        SystemClock.sleep(waitTime);
                                        //一段时间后检测微信首页消息界面
                                        jszz = true;
                                        excute(new ReceiveTransfersRunnable("checkAllTallkList()", ""));
                                        break;
                                    case CLASS_ID_TEXT.TEXT_TXL:
                                        SystemClock.sleep(waitTime);
                                        acceptFriendRequest = true;
//                                        //TODO 通过“新的朋友” 或 “朋友推荐”点开添加朋友
//                                        if (!clickParentByChaildText(CLASS_ID_TEXT.TEXT_XDPY)) {//在页面没有"新的朋友"节点的情况下，再查找“朋友推荐”
//                                            clickParentByChaildText(CLASS_ID_TEXT.TEXT_PYTJ);
//                                        }


                                        //FIXME 当滑动到了通讯录页面滑动到了下面，再单击一下通讯录
                                        //TODO 通过“新的朋友” 或 “朋友推荐”点开添加朋友
                                        if (!clickParentByChaildText(CLASS_ID_TEXT.TEXT_XDPY)) {//在页面没有"新的朋友"节点的情况下，再查找“朋友推荐”
                                            if (!clickParentByChaildText(CLASS_ID_TEXT.TEXT_PYTJ)) {
                                                SystemClock.sleep(waitTime / 2);
                                                viewGroup.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                                //单击之后再查找点击
                                                SystemClock.sleep(waitTime / 2);
                                                if (!clickParentByChaildText(CLASS_ID_TEXT.TEXT_XDPY)) {
                                                    clickParentByChaildText(CLASS_ID_TEXT.TEXT_PYTJ);
                                                }
                                            }
                                        }

                                        break;
                                    case CLASS_ID_TEXT.TEXT_ME:
                                        SystemClock.sleep(waitTime);
                                        //获取微信号
                                        String[] index = getTextById(CLASS_ID_TEXT.ID_WXH).split("：");
                                        if (index.length == 2) {
                                            String currentWeChatCount = index[1];
                                            //将当前微信账号存入SP中
                                            SPUtils.put(getApplicationContext(), ContantsUtil.CURRENT_WECHAT_ACCOUNT, currentWeChatCount);
                                        }
                                        break;
                                }
                                break;
                            }
                        }
                    }
                    break;
                }


            }
        }

    }

}
