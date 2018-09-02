package com.kmjd.android.zxhzm.alipaybill.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kmjd.android.zxhzm.alipaybill.App;
import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.bean.OriginalAlipayBillDetail;
import com.kmjd.android.zxhzm.alipaybill.bean.TheUploadAlipayBillBean;
import com.kmjd.android.zxhzm.alipaybill.bean.event.AliPayNotificationEvent;
import com.kmjd.android.zxhzm.alipaybill.bean.event.BillDetailEvent;
import com.kmjd.android.zxhzm.alipaybill.bean.event.UploadResultEvent;
import com.kmjd.android.zxhzm.alipaybill.network.NetWorkHelper;
import com.kmjd.android.zxhzm.alipaybill.network.NetWorkInterface;
import com.kmjd.android.zxhzm.alipaybill.utils.ContantsUtil;
import com.kmjd.android.zxhzm.alipaybill.utils.MyLogger;
import com.kmjd.android.zxhzm.alipaybill.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AlipayBill_UploadService extends Service {
//    private static final String TAG = "Android-Star";
    private List<OriginalAlipayBillDetail> original_AlipayBillDetailList = null;
    private List<TheUploadAlipayBillBean> processed_Upload_AlipayBillBean_List = null;
    private SimpleDateFormat simpleDateFormat = null;
    //入库失败的数据
    private List<TheUploadAlipayBillBean> inStorage_Failure_AliPayBillBean_List = null;
    //是否有网络响应
    private boolean isResponse = false;

    public AlipayBill_UploadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startNotifi();
        EventBus.getDefault().register(this);
    }

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    private void startNotifi() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //获取一个Notification构造器
        builder = new NotificationCompat.Builder(this.getApplicationContext());
        builder.setContentTitle("service was started") // 设置下拉列表里的标题
                .setContentText("this service for upload aliPay bill") // 设置内容
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.alipay_log))// 设置下拉列表中的图标(大图标)
                .setSmallIcon(R.mipmap.alipay_log) // 设置状态栏内的小图标
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = builder.build(); // 获取构建好的Notification
        //notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        //参数一：唯一的通知标识；参数二：通知消息。
        startForeground(20180511, notification);// 开始前台服务
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        original_AlipayBillDetailList = new ArrayList<>(10);
        processed_Upload_AlipayBillBean_List = new ArrayList<>(10);
        //入库失败的数据
        inStorage_Failure_AliPayBillBean_List = new ArrayList<>(10);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        if (null != mNotificationManager && null != builder) {
            mNotificationManager.notify(20180511, builder.build());
        } else {
            startNotifi();
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
//    private static String originalJsonString = "";
//    @Subscribe(threadMode = ThreadMode.ASYNC)
//    public void onMessageEvent(BillDetailEvent event) {
//        MyLogger.zLog().d("收到消息了");
//        if (event.getmOriginalAlipayBillDetails().isEmpty()) return;
//        //使用之前清空list
//        original_AlipayBillDetailList.clear();
//        //添加原始数据
//        original_AlipayBillDetailList.addAll(event.getmOriginalAlipayBillDetails());
//   /*         originalJsonString = JSON.toJSONString(original_AlipayBillDetailList);
//            Log.d(TAG, "originalJsonString: " + originalJsonString);*/
//        Collections.reverse(original_AlipayBillDetailList);
//        //加工后的Json格式字符串，也是最终上传的内容
//        String resultUploadJsonString = JSON.toJSONString(addInformationForUpload(original_AlipayBillDetailList));
//        String key = NetWorkInterface.md5Key;//加密key
//        String aliPayAccount = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_ALIPAY_ACCOUNT);//账本的支付宝账号
//        String surplus = event.getmOriginalAlipayBillDetails().get(0).getSurplus();//最新余额
//        String current_storage_num = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_STORAGE_NUM);//当前账本数据进入的库
//        //执行提交数据
//        doUploadData(resultUploadJsonString, key, aliPayAccount, surplus, current_storage_num, 0);
//    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessageEvent(BillDetailEvent event) {
        if (event.getmOriginalAlipayBillDetails().isEmpty() && inStorage_Failure_AliPayBillBean_List.isEmpty()) return;
        //使用之前清空list
        original_AlipayBillDetailList.clear();
        //添加原始数据
        original_AlipayBillDetailList.addAll(event.getmOriginalAlipayBillDetails());

        //加工后的Json格式字符串，也是最终上传的内容
        List<TheUploadAlipayBillBean> theUploadAlipayBillBeans = addInformationForUpload(original_AlipayBillDetailList);
        //添加上传失败的数据
        if (!inStorage_Failure_AliPayBillBean_List.isEmpty()){
            theUploadAlipayBillBeans.addAll(inStorage_Failure_AliPayBillBean_List);
            inStorage_Failure_AliPayBillBean_List.clear();
        }
        theUploadAlipayBillBeans = clearUpData(theUploadAlipayBillBeans);
        Collections.reverse(theUploadAlipayBillBeans);
        String resultUploadJsonString = JSON.toJSONString(theUploadAlipayBillBeans);
        String key = NetWorkInterface.md5Key;//加密key
        String aliPayAccount = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_ALIPAY_ACCOUNT);//账本的支付宝账号
        String surplus = theUploadAlipayBillBeans.get(theUploadAlipayBillBeans.size() - 1).getF_surplus();//最新余额
        String current_storage_num = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_STORAGE_NUM);//当前账本数据进入的库
        //执行提交数据
        isResponse = false;
        doUploadData(resultUploadJsonString, key, aliPayAccount, surplus, current_storage_num, 0);
    }


    /**
     * 修改拿到的微信原始数据，增加服务器需要的数据
     *
     * @param original_AlipayBillDetailList
     * @return
     */
    private List<TheUploadAlipayBillBean> addInformationForUpload(List<OriginalAlipayBillDetail> original_AlipayBillDetailList) throws NumberFormatException {
        if (null != processed_Upload_AlipayBillBean_List) processed_Upload_AlipayBillBean_List.clear();
        if (!original_AlipayBillDetailList.isEmpty()) {
            for (OriginalAlipayBillDetail originalAlipayBillDetail : original_AlipayBillDetailList) {
                TheUploadAlipayBillBean detailed = new TheUploadAlipayBillBean();
                /// 跟盘表id
                detailed.setF_id("0");
                /// 充值序号
                detailed.setF_recordid("0");
                /// 银行帐本id 或者 支付宝等级
                detailed.setF_bankid("0");
                /// 当前账本的银行卡号 、支付宝账号、微信账号
                detailed.setF_account(SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_ALIPAY_ACCOUNT));
                /// 帐本名称
                detailed.setF_BankID_zdd(SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_BOOK_NAME));
                /// 管理员
                detailed.setF_Admin("");
                /// 操作时间
                detailed.setF_operateTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
                /// 请求APP次数
                detailed.setPostAppCount("0");
                /// APP返回状态
                detailed.setReturnIndex("0");
                /// 最后请求APP时间
                detailed.setLastTime("");


                /// 收入金额
                if (originalAlipayBillDetail.getType().equals("1")) {
                    detailed.setF_extraction(originalAlipayBillDetail.getAmount().substring(1));
                } else {
                    detailed.setF_extraction("0");
                }
                /// 支出金额
                if (originalAlipayBillDetail.getType().equals("2")) {
                    detailed.setF_income(originalAlipayBillDetail.getAmount().substring(1));
                } else {
                    detailed.setF_income("0");
                }
                /// 余额
                detailed.setF_surplus(originalAlipayBillDetail.getSurplus());
                /// 备注
                detailed.setF_remark(originalAlipayBillDetail.getRemark());
                /// 转账交易时间
                detailed.setF_time(originalAlipayBillDetail.getCreationTime());
                /// 汇款人
                detailed.setF_remitter(originalAlipayBillDetail.getNickName());
                if (!originalAlipayBillDetail.getWithdrawCashTo().equals("")){
                    detailed.setF_remitter(originalAlipayBillDetail.getWithdrawCashTo());
                }
                /// 对方汇款银行 或者 支付宝或者微信绑定的手机号
                detailed.setF_remitting_bank("");
                /// 对方汇款银行帐号 或者 支付宝邮箱
                String reciprocalAccount = originalAlipayBillDetail.getReciprocalAccount();
                detailed.setF_remittance_account(reciprocalAccount.substring(reciprocalAccount.indexOf(" ") + 1));

                /// 处理交易号
                if (originalAlipayBillDetail.getTransactionType().equals("服务费")) {
                    detailed.setF_tradeNum("S" + originalAlipayBillDetail.getOrderNumber());
                } else {
                    detailed.setF_tradeNum((Float.parseFloat(originalAlipayBillDetail.getAmount()) > 0 ? "E" : "I") + originalAlipayBillDetail.getOrderNumber());
                }
                /// 转账类型显示的文本
                detailed.setF_type_str(originalAlipayBillDetail.getTransactionType() + "&" + originalAlipayBillDetail.getTradeState());
                //处理交易状态是回冲的情况
                if (originalAlipayBillDetail.getTradeState().equals(ContantsUtil.JYZT_HC)){
                    detailed.setF_tradeNum("F" + detailed.getF_tradeNum());
                }
                /// 转账类型
                detailed.setF_type(detailed.getF_BankID_zdd().contains("兑银") ? "3" : "2");
                /// 账本类型 0=储值 1=兑银 2=支付宝储值 3=支付宝兑银 4=微信储值 5=微信兑银
                detailed.setF_DetailedType(detailed.getF_BankID_zdd().contains("兑银") ? "3" : "2");

                //支付宝收入
                if (originalAlipayBillDetail.getType().equals("1")) {
                    if (!detailed.getF_type().equals("转账") && !detailed.getF_type().equals("收款")) {
                        detailed.setF_Admin("");
                        detailed.setF_recordid("0");
                        detailed.setF_operateTime(simpleDateFormat.format(Calendar.getInstance().getTime()));
                    }
                } else {
                    switch (originalAlipayBillDetail.getTransactionType()) {
                        case "转账":
                            if (detailed.getF_type().equals("2")) {
                                detailed.setF_recordid("-10");
                            } else if (detailed.getF_type().equals("3")) {
                                detailed.setF_recordid("-9");
                            }
                            break;
                        case "收款":
                            if (detailed.getF_type().equals("2")) {
                                detailed.setF_recordid("-10");
                            } else if (detailed.getF_type().equals("3")) {
                                detailed.setF_recordid("-9");
                            }
                            break;
                        case "服务费":
                            //提现手续费支出
                            detailed.setF_recordid("-7");
                            break;
                        case "提现":
                            //提现
                            detailed.setF_recordid("-6");
                            break;
                        default:
                            //其他支出
                            detailed.setF_recordid("-9");
                            break;
                    }
                    detailed.setF_Admin("AUTO");
                }
                processed_Upload_AlipayBillBean_List.add(detailed);
            }


        }
        return processed_Upload_AlipayBillBean_List;
    }


    public static String md5(String str) {
        String re = null;
        byte encrypt[];
        try {
            byte[] tem = str.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(tem);
            encrypt = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte t : encrypt) {
                String s = Integer.toHexString(t & 0xFF);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                sb.append(s);
            }
            re = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re;
    }

    private HashMap<String, String> parmHashMap = null;

    public void doUploadData(final String resultUploadJsonString, final String key, final String aliPayAccount, final String surplus, final String currentStorageNum, final int elementIndex) {

        String userAgent = md5(key + aliPayAccount + surplus).toUpperCase();
        Retrofit mRetrofit = null;
        String currentUrl = "";
        switch (currentStorageNum) {
            case "test"://测试站
                if (elementIndex >= 0 && elementIndex < NetWorkInterface.STORAGE_BASE_URL_ALIPAY_TEST.length) {
                    mRetrofit = NetWorkHelper.getmRetrofit(NetWorkInterface.STORAGE_BASE_URL_ALIPAY_TEST[elementIndex], userAgent);
                    currentUrl = NetWorkInterface.STORAGE_BASE_URL_ALIPAY_TEST[elementIndex];
                }
                break;
            case "17":
                if (elementIndex >= 0 && elementIndex < NetWorkInterface.STORAGE_BASE_URL_ALIPAY_17.length) {
                    mRetrofit = NetWorkHelper.getmRetrofit(NetWorkInterface.STORAGE_BASE_URL_ALIPAY_17[elementIndex], userAgent);
                    currentUrl = NetWorkInterface.STORAGE_BASE_URL_ALIPAY_17[elementIndex];
                }
                break;
            case "26":
                if (elementIndex >= 0 && elementIndex < NetWorkInterface.STORAGE_BASE_URL_ALIPAY_26.length) {
                    mRetrofit = NetWorkHelper.getmRetrofit(NetWorkInterface.STORAGE_BASE_URL_ALIPAY_26[elementIndex], userAgent);
                    currentUrl = NetWorkInterface.STORAGE_BASE_URL_ALIPAY_26[elementIndex];
                }
                break;
            case "35":
                if (elementIndex >= 0 && elementIndex < NetWorkInterface.STORAGE_BASE_URL_ALIPAY_35.length) {
                    mRetrofit = NetWorkHelper.getmRetrofit(NetWorkInterface.STORAGE_BASE_URL_ALIPAY_35[elementIndex], userAgent);
                    currentUrl = NetWorkInterface.STORAGE_BASE_URL_ALIPAY_35[elementIndex];
                }
                break;
            default:
                break;
        }
        if (null == mRetrofit) {
            //如果所有的链接都是网络原因链接不上导致没有上传成功
            if (!isResponse){
                if (null != inStorage_Failure_AliPayBillBean_List) {inStorage_Failure_AliPayBillBean_List.clear();}
                inStorage_Failure_AliPayBillBean_List = JSON.parseObject(resultUploadJsonString, new TypeReference<List<TheUploadAlipayBillBean>>() {});
                Collections.reverse(inStorage_Failure_AliPayBillBean_List);

                //再次模拟发通知上传
                SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_MESSAGE, true);
                EventBus.getDefault().post(new AliPayNotificationEvent());
            }
            return;
        }
        if (null == parmHashMap) {
            parmHashMap = new HashMap<>(3);
        } else {
            parmHashMap.clear();
        }
        parmHashMap.put("detail", resultUploadJsonString);
        parmHashMap.put("surplus", surplus);
        parmHashMap.put("account", aliPayAccount);
        Call<String> stringCall = mRetrofit.create(NetWorkInterface.class).toUploadAlipayBill(parmHashMap);
        final String finalCurrentUrl = currentUrl;
        stringCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                isResponse = true;
                String body = response.body();

                //通知界面显示上传结果
                if (null != body){
                    switch (body){
                        case "1":
                            Toast.makeText(AlipayBill_UploadService.this, "上传成功", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new UploadResultEvent("成功", finalCurrentUrl));
                            break;
                        case "0":
                            Toast.makeText(AlipayBill_UploadService.this, "不存在指定账号", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new UploadResultEvent("不存在指定账号", finalCurrentUrl));
                            break;
                        case "-1":
                            Toast.makeText(AlipayBill_UploadService.this, "加密签名错误", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new UploadResultEvent("加密签名错误", finalCurrentUrl));
                            break;
                        case "-2":
                            Toast.makeText(AlipayBill_UploadService.this, "账本名称不一致", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new UploadResultEvent("账本名称不一致", finalCurrentUrl));
                            break;
                    }
                }else {
                    Toast.makeText(AlipayBill_UploadService.this, "返回null", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new UploadResultEvent("返回null", finalCurrentUrl));
                }

                if (response.code() != 200 || (null != body && !body.equals("1"))) {
                    doUploadData(resultUploadJsonString, key, aliPayAccount, surplus, currentStorageNum, elementIndex + 1);
                } else if (response.code() == 200 && null != body && body.equals("1")) {
                    //入库成功后再提交一次数据备份
                    count = 0;
                    MyLogger.zLog().d("最新余额->"+surplus+"：JSON->"+resultUploadJsonString);
                    doUploadForBack(aliPayAccount, "最新余额->"+surplus+"：JSON->"+resultUploadJsonString);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(AlipayBill_UploadService.this, "上传失败", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new UploadResultEvent("失败", finalCurrentUrl));
                doUploadData(resultUploadJsonString, key, aliPayAccount, surplus, currentStorageNum, elementIndex + 1);
                MyLogger.zLog().d("上传失败"+"  "+resultUploadJsonString);
            }
        });

    }

    private HashMap<String, String> hashMapField = null;
    private int count = 0;

    private void doUploadForBack(final String account, final String content) {
        ++count;
        if (count > 2) {
            return;
        }
        if (null == hashMapField) {
            hashMapField = new HashMap<>(2);
        } else {
            hashMapField.clear();
        }
        hashMapField.put("account", account);
        hashMapField.put("content", content);
        NetWorkHelper.getmRetrofit(NetWorkInterface.forBack).create(NetWorkInterface.class).toUploadForBack(hashMapField).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String body = response.body();
                if (response.code() != 200 || (body != null && !body.equals("1"))) {
                    doUploadForBack(account, content);
                } else if (response.code() == 200 && body != null && body.equals("1")) {
                    MyLogger.zLog().d("account = " +account+"   content = "+content);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                doUploadForBack(account, content);
                MyLogger.zLog().d("日志备份失败");
            }
        });
    }

    /**
     * 去掉重复的数据
     */
    private List<TheUploadAlipayBillBean> clearUpData(List<TheUploadAlipayBillBean> billDetails) {
        for (int i = 0; i < billDetails.size() - 1; i++) {
            for(int j = billDetails.size() -1; j > i; j--){
                if (billDetails.get(j).getF_tradeNum().equals(billDetails.get(i).getF_tradeNum())){
                    billDetails.remove(j);
                }
            }
        }
        return billDetails;
    }

/*    private static final String rule = "###,###,##0.##";
    public String formatNumber(String parm){
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat(rule);
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        java.math.BigDecimal bigDecimal = new java.math.BigDecimal(parm);
        String result = decimalFormat.format(bigDecimal);
//        Log.d(TAG,"使用 "+rule+" 规则格式化 "+parm+" 的结果是 "+result);
        return result;
    }*/

}
