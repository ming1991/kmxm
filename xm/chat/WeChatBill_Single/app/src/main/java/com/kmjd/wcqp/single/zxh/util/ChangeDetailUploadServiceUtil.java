package com.kmjd.wcqp.single.zxh.util;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.kmjd.wcqp.single.zxh.MainApplication;
import com.kmjd.wcqp.single.zxh.model.ChangeDetailBean;
import com.kmjd.wcqp.single.zxh.model.WeChatUploadBean;

import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.core.har.HarRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by androidshuai on 2017/6/26.
 */

public class ChangeDetailUploadServiceUtil {

    private static String TAG = "Android-Star";

    private static List<HarEntry> harEntryList = new CopyOnWriteArrayList<HarEntry>();

    private static List<HarEntry> afterFilterHarEntryList = new CopyOnWriteArrayList<HarEntry>();
    //时间转换器
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);

    private static WeChatUploadBean detailed = null;

    private static List<WeChatUploadBean> weChatUploadBeanList = new CopyOnWriteArrayList<WeChatUploadBean>();

    public static synchronized void clearHarLog(){
        if (MainApplication.isInitProxy){
            MainApplication.proxy.getHar().getLog().clearAllEntries();
        }
    }

    /**
     * 过滤抓取的原始数据,只要微信零钱明细的数据
     * @return
     */
    public static synchronized String getCaptureData(){
        String captureWechatData = null;
        if (MainApplication.isInitProxy) {
            HarLog harLog = null;
            harLog = MainApplication.proxy.getHar().getLog();
            harEntryList.clear();
            harEntryList.addAll(harLog.getEntries());
            harLog.clearAllEntries();
        }

        if (!harEntryList.isEmpty()){
            int harEntryListSize = harEntryList.size();
            afterFilterHarEntryList.clear();
            for (int i = 0; i < harEntryListSize; i++) {
                HarEntry harEntry = harEntryList.get(i);
                HarRequest harRequest = harEntry.getRequest();
                if (null != harRequest){
                    String url = harRequest.getUrl();
                    if (null != url && url.contains("/cgi-bin/mmpayweb-bin/balanceuserrollbatch?exportkey")) {
                        afterFilterHarEntryList.add(harEntry);
                    }
                }

            }
        }


        int afterFilterHarEntryListSize = afterFilterHarEntryList.size();
        if (afterFilterHarEntryListSize > 0) {
            HarEntry lastHarEntry = afterFilterHarEntryList.get(afterFilterHarEntryListSize - 1);
            captureWechatData = lastHarEntry.getResponse().getContent().getText();
        }
        if (null != captureWechatData && !captureWechatData.equals("")) {
            return captureWechatData;
        }
        return captureWechatData;
    }

    /**
     * 格式化抓到的微信零钱明细数据
     * @param content
     * @return
     */
    public static synchronized List<WeChatUploadBean>  formatContent(String content) {
        try {
            if (!TextUtils.isEmpty(content)) {
                String[] firstArray = content.split("allList.push" + " \\(");
                if (firstArray.length > 0) {
                    weChatUploadBeanList.clear();
                    int firstArrayLength = firstArray.length;
                    for (int i = 1; i < firstArrayLength; i++) {
                        //从第1个元素开始，抽取字符串
                        String afterWipeLastBracketString = firstArray[i].substring(0, firstArray[i].indexOf(");"));
                        ChangeDetailBean changeDetailBean = JSON.parseObject(afterWipeLastBracketString, ChangeDetailBean.class);
                        WeChatUploadBean weChatUploadBean = AddInformationForUpload(changeDetailBean);
                        weChatUploadBeanList.add(weChatUploadBean);
                    }
                }
            }
        }catch (NumberFormatException e){
            return null;
        }

        return weChatUploadBeanList;
    }



    /**
     * 修改拿到的微信原始数据，增加服务器需要的数据
     * @param changeDetailBean
     * @return
     */
    private static WeChatUploadBean AddInformationForUpload(ChangeDetailBean changeDetailBean)throws NumberFormatException {
        if (null != changeDetailBean) {
            detailed = new WeChatUploadBean();
            /// 跟盘表id
            detailed.f_id = "0";
            /// 充值序号
            detailed.f_recordid = "0";
            /// 银行帐本id 或者 支付宝等级
            detailed.f_bankid = "0";
            /// 银行卡号 、支付宝账号、微信
            detailed.f_account = SPUtils.getString(MainApplication.applicationContext, ContantsUtil.CURRENT_WECHAT_ACCOUNT);
            /// 收入金额
            if (changeDetailBean.getType().equals("1")) {
                detailed.f_extraction = String.format(Locale.CHINA,"%.2f",Float.parseFloat(changeDetailBean.getPaynum()) / 100F);
            } else {
                detailed.f_extraction = "0";
            }

            /// 支出金额
            if (changeDetailBean.getType().equals("2")) {
                detailed.f_income = String.format(Locale.CHINA,"%.2f",Float.parseFloat(changeDetailBean.getPaynum()) / 100F);
            } else {
                detailed.f_income = "0";
            }

            /// 余额
            detailed.f_surplus = String.format(Locale.CHINA,"%.2f", Float.parseFloat(changeDetailBean.getBalance()) / 100F);

            /// 转账类型
            detailed.f_type = changeDetailBean.getTrans_state_name();
            /// 转账类型显示的文本不入库
            detailed.f_type_str = changeDetailBean.getTrans_state_name();
            /// 备注
            detailed.f_remark = changeDetailBean.getExplain();
            /// 转账交易时间
            detailed.f_time = changeDetailBean.getCreatetime();
            /// 汇款银行 或者 支付宝手机号
            detailed.f_remitting_bank = "";
            /// 汇款人
            detailed.f_remitter = "";
            /// 汇款帐号 或者 支付宝邮箱
            detailed.f_remittance_account = "";
            /// 管理员
            detailed.f_Admin = "";
            /// 帐本名称
            detailed.f_BankID_zdd = "";
            /// 操作时间
            detailed.f_operateTime = simpleDateFormat.format(new Date());
            /// 交易号
            if (detailed.f_type.equals("提现手续费")) {
                detailed.f_tradeNum = "S" + changeDetailBean.getTransid();
            } else {
                detailed.f_tradeNum = (Float.parseFloat(changeDetailBean.getPaynum()) / 100F > 0 ? "E" : "I") + changeDetailBean.getTransid();
            }
            /// 账本类型 0=储值 1=兑银 2=支付宝储值 3=支付宝兑银 4=微信储值 5=微信兑银
            detailed.f_DetailedType = "4";
            /// 请求APP次数
            detailed.PostAppCount = "0";
            /// APP返回状态
            detailed.ReturnIndex = "";
            /// 最后请求APP时间
            detailed.LastTime = "";
            //微信收入
            if (changeDetailBean.getType().equals("1")) {
                if (!detailed.f_type.equals("微信转账") && !detailed.f_type.equals("微信面对面收钱")) {
                    detailed.f_Admin = "";
                    detailed.f_recordid = "0";
                    detailed.f_operateTime = simpleDateFormat.format(new Date());
                }
                detailed.f_extraction = String.format(Locale.CHINA,"%.2f",Float.parseFloat(changeDetailBean.getPaynum()) / 100F);
            } else {
                switch (detailed.f_type) {
                    case "提现":
                        detailed.f_recordid = "-4";
                        break;
                    case "提现手续费":
                        //提现手续费支出
                        detailed.f_recordid = "-10";
                        detailed.f_time = ChangeDetailUploadServiceUtil.addTime(detailed.f_time);
                        break;
                    default:
                        //其他支出
                        detailed.f_recordid = "-9";
                        break;
                }
                detailed.f_Admin = "AUTO";
                detailed.f_operateTime = simpleDateFormat.format(new Date());
                detailed.f_income = String.format(Locale.CHINA,"%.2f",Float.parseFloat(changeDetailBean.getPaynum()) / 100F);
            }

        }
        return detailed;
    }



    /**
     * 将最终要上传的list的数据转化为String
     * @param list
     * @return
     */
    public static synchronized String formatContentListToString(List<WeChatUploadBean> list){
        String uploadJsonString = "";
        StringBuilder sb = new StringBuilder();
        int size = list.size();
        sb.append("[");
        for (int j = 0; j < size; j++) {
            WeChatUploadBean weChatUploadBean = list.get(j);
            if (null == weChatUploadBean){
                continue;
            }
            String jsonString = JSON.toJSONString(weChatUploadBean);
            sb.append(jsonString);
            if (j < size - 1) {
                sb.append(",");
            } else {
                sb.append("]");
                uploadJsonString = sb.toString();
            }
        }
        return uploadJsonString;
    }

//============================================工具方法==================================================================

    private static String addTime(String sourceTime) {
        long longTime = 0;
        try {
            longTime = simpleDateFormat.parse(sourceTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        longTime = longTime + (long) 1000;
        return simpleDateFormat.format(longTime);
    }


    public static String md5(String str){
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

}
