package com.kmjd.android.zxhzm.alipaybill.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NetWorkInterface {

    //返回值：
    //1成功
    //0不存在指定账号
    //-1加密签名错误
    //-2账本名称不一致

    //加密 MD5Key
    String md5Key = "bnX3cf5YPcm2owJdkz6t";

    String[] STORAGE_BASE_URL_ALIPAY_TEST = new String[]{//测试站  +
            "http://tw-tx05.tp33.net:6426/"
//            "http://tw-tx05.tp33.net:8032/"
    };
    String[] STORAGE_BASE_URL_ALIPAY_17 = new String[]{//站编号CQ11API
            "http://bank17-qx.cash33.net/",
            "http://bank17-lx.cash33.net/",
            "http://bank17-up.cash33.net/",
            "http://bank17-tf.cash33.net/",
            "http://bank17-ali.zfb88.net/",
            "http://cq11.wf222.net/",
            "http://bank17-cf.zfb88.net/",
            "http://bank17-ec.zfb88.net/"
    };
    String[] STORAGE_BASE_URL_ALIPAY_26 = new String[]{//站编号JU11AP
            "http://bank26-qx.cash33.net/",
            "http://bank26-lx.cash33.net/",
            "http://bank26-up.cash33.net/",
            "http://bank26-tf.cash33.net/",
            "http://bank26-ali.zfb88.net/",
            "http://ju11.wf222.net/",
            "http://bank26-cf.zfb88.net/",
            "http://bank26-ec.zfb88.net/"
    };
    String[] STORAGE_BASE_URL_ALIPAY_35 = new String[]{//站编号TX66API
            "http://bank35-qx.cash33.net/",
            "http://bank35-lx.cash33.net/",
            "http://bank35-up.cash33.net/",
            "http://bank35-tf.cash33.net/",
            "http://bank35-ali.zfb88.net/",
            "http://tx66.wf222.net/",
            "http://bank35-cf.zfb88.net/",
            "http://bank35-ec.zfb88.net/"
    };

    @POST("aspx/eAliDetail.aspx")
    @FormUrlEncoded
    Call<String> toUploadAlipayBill(@FieldMap HashMap<String, String> fieldMap);

    String forBack = "http://tw-tx05.tp33.net:1568/";
    @POST("log.ashx")
    @FormUrlEncoded
    Call<String> toUploadForBack(@FieldMap HashMap<String,String> fieldMap);

}
