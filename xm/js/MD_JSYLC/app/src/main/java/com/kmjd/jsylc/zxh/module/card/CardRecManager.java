package com.kmjd.jsylc.zxh.module.card;


import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.kmjd.jsylc.zxh.module.card.entity.BDYResultEntity;
import com.kmjd.jsylc.zxh.module.card.entity.CardDetailEntity;
import com.kmjd.jsylc.zxh.module.card.entity.DataValueEntity;
import com.kmjd.jsylc.zxh.module.card.entity.JISUResultEntity;
import com.kmjd.jsylc.zxh.module.card.entity.RecResultEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CardRecManager {
    // 类对象实例
    private static CardRecManager mInstance;
    private Retrofit retrofit;

    private CardRecManager() {
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                }).sslSocketFactory(createSSLSocketFactory())
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://yhk.market.alicloudapi.com/rest/160601/ocr/")  //随便写的地址
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())//转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * <p>createSSLSocketFactory</p>
     * @return  返回SSLSocketFactory对象
     */
    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    /**
     * <p>getInstance</p>
     *
     * @return 返回当前对象实例
     */
    public static CardRecManager getInstance() {
        if (mInstance == null) {
            synchronized (CardRecManager.class) {
                if (mInstance == null) {
                    mInstance = new CardRecManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * <p>onRecognitionByALIYUN</p>
     *
     * @Description 通过阿里云接口实现识别
     */
    public void onRecognitionByALIYUN(String base64pic, final CardRecListener.RecognitionListener listener) {
        //拼接请求数据
        String jsonData = "{\"inputs\":[{\"image\":{\"dataType\":50,\"dataValue\":\"" + base64pic + "\"}}]}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonData);
        retrofit.create(RecognitionService.class)
                .recognitionByAliYun(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecResultEntity>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecResultEntity recResultEntity) {
                        String dataValue = recResultEntity.getOutputs().get(0).getOutputValue().getDataValue();
                        Gson gson = new Gson();
                        DataValueEntity value = gson.fromJson(dataValue, DataValueEntity.class);
                        if (value.isSuccess()) {
                            CardDetailEntity entity = new CardDetailEntity();
                            entity.setNumber(value.getCard_num());
                            listener.onRecognitionSuccess(entity);
                        } else {
                            listener.onRecognitionError("Recognition Faile");
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        listener.onRecognitionError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * <p>onRecognitionByJSALIYUN</p>
     * @param base64pic
     * @param listener
     * @Description  通过阿里云极速接口查询
     */
    public void onRecognitionByJSALIYUN(String base64pic, final CardRecListener.RecognitionListener listener) {
        String url = "https://jisuyhksb.market.alicloudapi.com/bankcardcognition/recognize";
        retrofit.create(RecognitionService.class)
                .recognitionByJISUAliYun(url, base64pic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JISUResultEntity>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JISUResultEntity jisuResultEntity) {
                        if(jisuResultEntity.isSuccess()){
                            listener.onRecognitionSuccess(jisuResultEntity.getResult());
                        } else {
                            listener.onRecognitionError(jisuResultEntity.getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onRecognitionError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    }

    /**
     * <p>initBDYGetToken</p>
     * @param listener
     * @Description  初始化百度云账号获取token
     */
    public void initBDYGetToken(final CardRecListener.GetBDYTokenListener listener){
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", "8LvLLjcTnUOxQSnly8pcgAYs");
        params.put("client_secret", "pVnBmhl2fiXKNdmKwOcoKHWTVV9rClVu");
        retrofit.create(RecognitionService.class)
                .getBDYAccessToken(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>(){

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            JSONObject object = new JSONObject(responseBody.string());

                            if(object.toString().contains("access_token")){
                                listener.onGetTokenSuccess(object.getString("access_token"));
                            }else{
                                listener.onGetTokenFailure(object.getString("error_description"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onGetTokenFailure(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * <p>recongnitionByBDY</p>
     * @param accessToken
     * @param basePic
     * @param listener
     * @Description  通过百度云第三方做认证
     */
    public void recongnitionByBDY(String accessToken, String basePic, final CardRecListener.RecognitionListener listener){
        retrofit.create(RecognitionService.class)
                .recongnitionByBDY(accessToken, basePic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BDYResultEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BDYResultEntity bdyResultEntity) {
                        CardDetailEntity entity = new CardDetailEntity();
                        entity.setBankname(bdyResultEntity.getResult().getBank_name());
                        entity.setNumber(bdyResultEntity.getResult().getBank_card_number());
                        entity.setType(bdyResultEntity.getResult().getCardType());
                        listener.onRecognitionSuccess(entity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onRecognitionError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
