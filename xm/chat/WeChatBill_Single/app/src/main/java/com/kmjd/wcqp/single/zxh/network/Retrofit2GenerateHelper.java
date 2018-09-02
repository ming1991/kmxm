package com.kmjd.wcqp.single.zxh.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zym on 2016/12/28.
 */

public class Retrofit2GenerateHelper {

    //	private static String TAG = "Android-Star";
    //1.获取联网公用的地址
    private static Retrofit mRetrofit;

    //尽可能的精简代码，避免只是因为baseURL不同而写很多相同代码的方法
    public static final String BASE_URL1 = "http://wx.zfb88.net/";

    public static final String[] STORAGE_BASE_URL_17_SWITCH = new String[]{

            "http://wx-17.zfb88.net:10017/",
            "http://wx-17.cash33.net:10017/",
//            "http://tw-hk02.tp33.net:10026/"//测试站
    };

    public static final String[] STORAGE_BASE_URL_26_SWITCH = new String[]{
            "http://wx-26.zfb88.net:10026/",
            "http://wx-26.cash33.net:10026/",
//            "http://tw-hk02.tp33.net:10026/"//测试站
    };

    public static final String[] STORAGE_BASE_URL_35_SWITCH = new String[]{
            "http://wx-35.zfb88.net:10035/",
            "http://wx-35.cash33.net:10035/",
//            "http://tw-hk02.tp33.net:10026/"//测试站
    };

    public static final String[] STORAGE_BASE_URL_17 = new String[]{
            "http://bank17-tf.cash33.net/",
            "http://cq11.wf222.net/",
            "http://bank17-cf.zfb88.net/",
            "http://bank17-ec.zfb88.net/",
            "http://bank17-qx.cash33.net/",
//            "http://test.ja333.net/",//测试站点
    };

    public static final String[] STORAGE_BASE_URL_26 = new String[]{

            "http://ju11.wf222.net/",
            "http://bank26-cf.zfb88.net/",
            "http://bank26-tf.cash33.net/",
            "http://bank26-ec.zfb88.net/",
            "http://bank26-qx.cash33.net/",
//            "http://test.ja333.net/",//测试站点
    };
    public static final String[] STORAGE_BASE_URL_35 = new String[]{
            "http://bank35-tf.cash33.net/",
            "http://tx66.wf222.net/",
            "http://bank35-cf.zfb88.net/",
            "http://bank35-ec.zfb88.net/",
            "http://bank35-qx.cash33.net/",
//            "http://test.ja333.net/",//测试站点
    };

    //    http://cs9.tp33.net:8090/toolsappdownload.html?t=1
    public static Retrofit2Services getRetrofit2ServicesInstance(String baseURL) {
        mRetrofit = null;
        //1.参数位Base地址
        mRetrofit = new Retrofit.Builder().baseUrl(baseURL)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).build())
                .build();
        return mRetrofit.create(Retrofit2Services.class);
    }


    public static Retrofit getRetrofitInstance(String baseURL, OkHttpClient okHttpClient) {
        mRetrofit = null;
        mRetrofit = new Retrofit.Builder().baseUrl(baseURL).client(okHttpClient)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return mRetrofit;
    }

    public static OkHttpClient genericClient(final String userAgent) {

        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    Request request = null;

                    @Override
                    public Response intercept(Chain chain) throws IOException {
//		                Log.d(TAG, "intercept: "+userAgent);
                        request = chain.request()
                                .newBuilder()
                                .header("User-Agent", userAgent)
                                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                                .header("Accept-Encoding", "gzip, deflate")//应该是服务器后台已经设置数据gzip压缩算法 ，我们前端就不需要了 否则再压缩数据就是乱码
                                .header("Connection", "keep-alive")
                                .header("Accept", "*/*")
                                .build();
                        return chain.proceed(request);
                    }

                }).connectTimeout(3, TimeUnit.SECONDS)
                .build();
    }


}
