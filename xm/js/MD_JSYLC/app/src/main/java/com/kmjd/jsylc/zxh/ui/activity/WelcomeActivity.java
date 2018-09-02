package com.kmjd.jsylc.zxh.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.listener.CallListener;
import com.kmjd.jsylc.zxh.mvp.model.bean.GameVideoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.WebsiteInformation;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.network.VelocityManager;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.MobileHeightUtil;
import com.kmjd.jsylc.zxh.utils.MyLogger;
import com.kmjd.jsylc.zxh.utils.NetWorkStateUtils;
import com.kmjd.jsylc.zxh.utils.PingTestSpeedUtil;
import com.kmjd.jsylc.zxh.utils.RootUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WelcomeActivity extends AppCompatActivity {

    /**
     * 测速任务
     */
    private Disposable mTestSpeedSubscribe;
    private VelocityManager getVelocityManager;
    public static List<String> DOMAIN = Arrays.asList(AllAPI.DOMAIN_NAME_CN_REAL);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            //如果你就放在launcher Activity中话，这里可以直接return了
            finish();
            return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
        }
        setContentView(R.layout.activity_welcome);
        getVelocityManager = VelocityManager.getInstance();
        //1.检测手机有没有被root
        if (RootUtil.isDeviceRooted()){
            showRootOrProxyXunXiDialog(this, getString(R.string.dialogTitle), getResources().getString(R.string.current_using_root));
            return;
        }

        //2.检测手机有没有使用网络代理
        if (NetWorkStateUtils.isWifiProxy(this)) {
            showRootOrProxyXunXiDialog(this, getString(R.string.dialogTitle), getResources().getString(R.string.current_using_proxy));
            return;
        }

        //根据手机的分辨率设置头部状态栏和首页底部按键的高度
        initTopAndBottomHeight();

        requestPermissions();
    }

    /**
     * 弹出程序不可root或程序不可使用网络代理的提示框
     */
    public void showRootOrProxyXunXiDialog(Context context, String title, final String message){
        final Dialog messageDialog = new Dialog(context, R.style.MessageDialog);
        //这里警告避免传递null作为视图根,但这里只能传null
        View dialogView = LayoutInflater.from(context).inflate(R.layout.public_dialog3, null);
        messageDialog.setContentView(dialogView);
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.setCancelable(false);//返回键无效
        View.OnClickListener onClickListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        messageDialog.dismiss();

                        if (getString(R.string.current_using_proxy).equals(message)){//如果设置了用户代理
                            //去wifi设置界面
                            Intent intent = new Intent();
                            ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                            intent.setComponent(componentName);
                            startActivity(intent);
                        }

                        finish();
                        break;
                }
            }
        };
        ((TextView) dialogView.findViewById(R.id.tv_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.tv_message)).setText(Html.fromHtml(message));
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        messageDialog.show();
    }

    /**
     * 自定义Toolbar的高度要求：
     * 1.默认时52dp-> 45
     * 2.当屏幕的宽度大于400dp时，设置为70dp-> 55
     * 首页面底部RadioButton的高度
     * 1.默认为60dp
     * 2.当屏幕的宽度大于400dp时，设置为75dp
     */
    private void initTopAndBottomHeight() {
        // 获取屏幕的宽高
        int screenWidth = MobileHeightUtil.getScreenWidth(this);
        int screenWidthDp = DpPxUtil.getDpByPx(screenWidth);
        int toolbarHeight = 45;//(dp)
        int radioGroupHeight = 60;//(dp)
        int person = 44;//(dp)
        int home_up=85;
        int home_down=47;
        if (screenWidthDp >= 400 && screenWidthDp < 768) {
            toolbarHeight = 55;
            radioGroupHeight = 75;
            person = 46;
            home_up=111;
            home_down=65;
        } else if (screenWidthDp >= 768) {
            toolbarHeight = 90;
            radioGroupHeight = 105;
            person = 80;
            home_up=154;
            home_down=137;
        }
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString(), toolbarHeight);
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.RADIOBUTTOM_HEIGHT.toString(), radioGroupHeight);
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.PERSONHEIGHT.toString(), person);
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOMEUP.toString(), home_up);
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOMEDOWN.toString(), home_down);
    }

    /**
     * 测试域名最快的存放在SP中
     */
    private void testSpeed(final List<String> domainList, final Boolean memberLoginState) {
        mTestSpeedSubscribe = Observable.combineLatest(new Iterable<Observable<WebsiteInformation>>() {
            @android.support.annotation.NonNull
            @Override
            public Iterator<Observable<WebsiteInformation>> iterator() {
                ArrayList<Observable<WebsiteInformation>> observableArrayList = new ArrayList<>();
                for (String domian : domainList) {
                    observableArrayList.add(getSpeedObservable(domian));
                }
                return observableArrayList.iterator();
            }
        }, new Function<Object[], String>() {
            @Override
            public String apply(Object[] websiteInformations) throws Exception {
                float fastSpeed = 0;
                String fastWebsite = "";
                for (Object object : websiteInformations) {
                    WebsiteInformation websiteInformation = (WebsiteInformation) object;
                    if (websiteInformation.getSpeed() >= fastSpeed) {
                        fastSpeed = websiteInformation.getSpeed();
                        fastWebsite = websiteInformation.getWebsite();
                    }
                }
                return fastWebsite;
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String fastWebsite) throws Exception {
                        //测速决定使用速度最快的域名
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.FAST_DOMAIN.toString(), fastWebsite);
                        enterActivity(memberLoginState);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (TextUtils.isEmpty(SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.FAST_DOMAIN.toString()))){
                            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.FAST_DOMAIN.toString(), "http://test26.tz111.net/");
                        }
                        enterActivity(memberLoginState);
                    }
                });
    }

    /**
     * 对具体的某个域名或ip地址进行测速
     */
    private Observable<WebsiteInformation> getSpeedObservable(String netAddress){
        return Observable.just(netAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .flatMap(new Function<String, ObservableSource<WebsiteInformation>>() {
                    @Override
                    public ObservableSource<WebsiteInformation> apply(String netAddress) throws Exception {
                        String ip = netAddress.replace("http://", "").replace("https://", "").replace("www.", "").replace("/","");
                        if (ip.contains(":")){
                            ip = ip.split(":")[0];
                        }
                        //-c 3是指ping的次数是3,-w 5是以秒为单位的超时时间
                        Process p = Runtime.getRuntime().exec("ping -c " + 1 + " -w 1 " + ip);

                        //读取ping的内容
                        InputStream inputStream = p.getInputStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String content;
                        while ((content = in.readLine()) != null){
                            content = content + "\r\n";
                            stringBuilder.append(content);
                        }
                        //关闭流
                        in.close();
                        //分析结果
                        String pingResult = stringBuilder.toString();
                        MyLogger.zLog().d("测速结果："+pingResult);
                        int pbyte = PingTestSpeedUtil.pingResultToByte(pingResult);
                        if (pbyte == -1){
                            return Observable.just(new WebsiteInformation(netAddress, 0));
                        }
                        List<String> pingList = PingTestSpeedUtil.pingResultToSpeed(pingResult);

                        Float[] floats = new Float[pingList.size()];
                        for (int i = 0; i < pingList.size(); i++) {
                            Float ms = Float.valueOf(pingList.get(i));
                            floats[i] = ms;
                        }
                        //获取平均速度
                        float v = (pbyte / PingTestSpeedUtil.getAverageSpeed(floats)) * 1000 / 1024;
                        return Observable.just(new WebsiteInformation(netAddress, v));
                    }
                });
    }

    private void enterActivity(Boolean memberLoginState) {
        if (memberLoginState) {
            //已经成功登录,去主页
            HomeActivity.goHomeActivity(WelcomeActivity.this, null, 0);
            WelcomeActivity.this.finish();
        } else {
            //没有登录,去登录页
            LoginActivity.startLoginActivity(WelcomeActivity.this);
            WelcomeActivity.this.finish();
        }
    }

    /**
     * <p>requestPermissions</p>
     * @Description  申请权限
     */
    private void requestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        509);
            } else {
                //toParseXml();
                //测速
                boolean memberLoginState = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
                testSpeed(DOMAIN, memberLoginState);
            }
        }else{
            //toParseXml();
            boolean memberLoginState = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
            testSpeed(DOMAIN, memberLoginState);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== 509) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //toParseXml();
            }
            boolean memberLoginState = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
            testSpeed(DOMAIN, memberLoginState);
        }
    }

    private void toParseXml() {
        getVelocityManager.parseGameXML(new CallListener.OnVelocityListener() {
            @Override
            public void onVelocitySuccess(List<String> urls) {

            }

            @Override
            public void onAnalyzeSuccess(GameVideoBean gameVideoBean) {

            }

            @Override
            public void onVelocityFailed(int type, String error) {
                String result = "";
                switch (type) {
                    case VelocityManager.TYPE_ANALYZE:
                        result = getResources().getString(R.string.parse_error);
                        break;
                    case VelocityManager.TYPE_VELOCITY:
                        result = getResources().getString(R.string.velocity_error);
                        break;
                   case VelocityManager.TYPE_NOT_PERMISSION:
                       result=error;
                       break;

                }
                MyLogger.hLog().d(getResources().getString(R.string.error_type)+ result+"\n"+error);
            }
        });
    }
    /**
     * 屏蔽物理返回按钮
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mTestSpeedSubscribe && !mTestSpeedSubscribe.isDisposed()){
            mTestSpeedSubscribe.dispose();
            mTestSpeedSubscribe = null;
        }
    }
}
