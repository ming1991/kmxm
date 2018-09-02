package com.md.jsyxzs_cn.zym_xs.network.nw_engine;

import android.net.Uri;
import android.util.Log;

import com.md.jsyxzs_cn.zym_xs.utils.ContantsUtil;

import org.apaches.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zym on 2016/12/28.
 */

public class R2GH {

    //1.获取联网公用的地址
    private static Retrofit mRetrofit;


    //尽可能的精简代码，避免只是因为baseURL不同而写很多相同代码的方法
    public static final String BASE_URL1 = "http://777.wb222.net/";
//    http://cs9.tp33.net:8090/toolsappdownload.html?t=1
    public static Retrofit2Services getRetrofit2ServicesInstance(String baseURL){
        if (mRetrofit == null){
            //1.参数位Base地址
            mRetrofit = new Retrofit.Builder().baseUrl(baseURL)
                    .addConverterFactory(StringConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit.create(Retrofit2Services.class);
    }


    public static Retrofit getRetrofitInstance(String baseURL){
        if (mRetrofit == null){
            mRetrofit = new Retrofit.Builder().baseUrl(baseURL)
                    .addConverterFactory(StringConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }
/*
    //2.获取站点地址的连接地址
    public static Retrofit mWebSiteRetrofit;

    public static Retrofit getWebSiteRetrofitInstance(){
        if (mWebSiteRetrofit == null){
            mWebSiteRetrofit = new Retrofit.Builder().baseUrl("http://cs9.tp33.net:8090/")
                    .addConverterFactory(StringConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mWebSiteRetrofit;
    }
*/

    public static Retrofit getNewRetrofit(Uri uri, String requestMethod, String site){
        String baseUrl = uri.toString().split(uri.getPath())[0] + "/";
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient(uri, requestMethod, site))
                .build();
    }

    private static OkHttpClient getOkHttpClient(final Uri uri, final String requestMethod, final String site){
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .header("site", site)
                        .header("Authorization", "Hawk " + getAuthorization(uri, requestMethod))
                        .build();
                return chain.proceed(request);
            }
        }).connectTimeout(3, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取签名的字符串
     */
    private static String getAuthorization(Uri uri, String requestMethod){
        // 请求的host
        String host = uri.getHost();
        String sanitizedHost = (host.indexOf(':') > 0) ? host.substring(0, host.indexOf(':')) : host;
        // 随机字符串
        String nonce = UUID.randomUUID().toString().substring(0, 6);
        // 获取当前时间戳
        long ts = System.currentTimeMillis() / 1000;
        String pathQuery = uri.getPath() + (uri.getQuery() == null ? "" : "?" + uri.getQuery());
        String normalized = "hawk.1.header\n" + ts + "\n" + nonce + "\n" + requestMethod
                + "\n" + pathQuery + "\n" + sanitizedHost + "\n" + (uri.getPort() > 0 ? uri.getPort() : 80) + "\n" + "\n\n";
        String mscStr = hmacSha256(normalized, ContantsUtil.APPKEY);
        String authorization = "id=\"" + ContantsUtil.APPID + "\",ts=\"" + ts + "\",nonce=\"" + nonce + "\",mac=\"" + mscStr + "\"";
        return authorization;
    }

    /**
     * HmacSha256 加密
     */
    private static String hmacSha256(String inputStr, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return Base64.encodeBase64String(sha256_HMAC.doFinal(inputStr.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error");
        }
        return null;
    }

    /**
     * 按照数组顺序替换字符串中的{0},{1}
     */
    public static String stringFormat(String input, Object... arg) {
        if (arg != null) {
            for (int i = 0; i < arg.length; i++) {
                input = input.replace("{" + i + "}", String.valueOf(arg[i]));
            }
        }
        return input;
    }

    /**
     * 组合请求URL
     */
    public static String generateUrl(String path) {
        Random random = new Random();
        String[] apiHost = new String[]{
                "http://cf.bvf11.com/",
                "http://in.bvf11.com/",
                "http://ec.bvf11.com/",
                "http://to.bvf11.com/"};
        String baseUrl = apiHost[random.nextInt(apiHost.length)];
        if(baseUrl.endsWith("/")){
            return baseUrl + path;
        }
        return baseUrl + "/" + path;
    }
}
