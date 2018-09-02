package com.kmjd.jsylc.zxh.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.mvp.contact.LoginContact;
import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.TimeRestrictBean;
import com.kmjd.jsylc.zxh.mvp.model.event.LoginFailureEvent;
import com.kmjd.jsylc.zxh.mvp.model.event.LoginWebFailureEvent;
import com.kmjd.jsylc.zxh.mvp.presenter.LoginPresenter;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.ui.fragment.LoginFragment;
import com.kmjd.jsylc.zxh.ui.fragment.LoginWebFragment;
import com.kmjd.jsylc.zxh.utils.ActivityUtils;
import com.kmjd.jsylc.zxh.utils.MyLogger;
import com.kmjd.jsylc.zxh.utils.NetWorkStateUtils;
import com.kmjd.jsylc.zxh.utils.RootUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.utils.TAG;
import com.kmjd.jsylc.zxh.utils.TimeFormatUtil;
import com.kmjd.jsylc.zxh.utils.upload_photo.GetPathFromUri4kitkat;
import com.kmjd.jsylc.zxh.utils.upload_photo.PhotoUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;

public class LoginActivity extends BaseActivity implements LoginContact.View{

    private LoginPresenter mLoginPresenter;

    private FragmentManager fragmentManager = getSupportFragmentManager();

    private WeakReference<LoginFragment> loginFragmentWeakReference;
    private WeakReference<LoginWebFragment> loginWebFragmentWeakReference;

    //选取图片的工具类
    public PhotoUtil mPhotoUtil = null;

    //测速所得最快的BaseUrl
    private String FAST_BASE_URL = null;
    public String getBASE_URL() {
        return FAST_BASE_URL;
    }

    /**
     * 跳转到该页面
     */
    public static void startLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    }

    /**
     * 弹出程序不可root或程序不可使用网络代理的提示框
     */
    public void showRootOrProxyXunXiDialog(Context context, String title, final String message){
        final Dialog messageDialog = new Dialog(context, R.style.MessageDialog);
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

    private void init() {
        mLoginPresenter = new LoginPresenter(this);
        switchLoginFragment();
        mPhotoUtil = new PhotoUtil(this);
        FAST_BASE_URL = SPUtils.getString(MainApplication.applicationContext,SPUtils.SP_KEY.FAST_DOMAIN.toString());
    }

    /**
     *切换到登录页面
     */
    public void switchLoginFragment(){
        /*if (null != loginFragmentWeakReference){
            loginFragmentWeakReference.clear();
            loginFragmentWeakReference = null;
        }
        loginFragmentWeakReference = new WeakReference<>(LoginFragment.newInstance());*/

        //为空才创建被回收了才创建
        if (null == loginFragmentWeakReference){
            loginFragmentWeakReference = new WeakReference<>(LoginFragment.newInstance());
        }else if (null != loginFragmentWeakReference && null == loginFragmentWeakReference.get()){
            loginFragmentWeakReference.clear();
            loginFragmentWeakReference = null;
            loginFragmentWeakReference = new WeakReference<>(LoginFragment.newInstance());
        }

        ActivityUtils.addFragmentToActivity(fragmentManager, loginFragmentWeakReference.get(), R.id.login_container, LoginFragment.LOGINFRAGMENT_KEY);
    }

    /**
     * 切换LoginWebFragment
     */
    public void switchLoginWebFragment(String url, String tag) {
        TimeRestrictBean timeRestrictBean = new TimeRestrictBean("", url, true, "");
        if ((FAST_BASE_URL + AllAPI.TLQ_URL).equals(url) && SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_BBS.toString()) == 1){//讨论区
            //当前时间
            long mfycTime = System.currentTimeMillis();
            //上一次进入的时间
            long superMfycTime = SPUtils.getLong(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_TLQ_SUPER_TIME.toString(), 0);
            if (mfycTime - superMfycTime > 10000) {
                //时间大于10秒，正常进入
                //将新的时间存在SP中
                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_TLQ_SUPER_TIME.toString(), mfycTime);
            } else {
                //时间小于10秒，进去倒计时
                timeRestrictBean.setTitle(getString(R.string.login_footer_discuss));
                timeRestrictBean.setIsable(false);
                timeRestrictBean.setSuperTime(TimeFormatUtil.getSuperTime(superMfycTime));
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString(LoginWebFragment.LOADING_TAG, tag);
        bundle.putParcelable(LoginWebFragment.KEY_LOGINWEBFRAGMENT, timeRestrictBean);

        if (null != loginWebFragmentWeakReference){
            loginWebFragmentWeakReference.clear();
            loginWebFragmentWeakReference = null;
        }
        loginWebFragmentWeakReference = new WeakReference<>(LoginWebFragment.newInstance(bundle));
        ActivityUtils.addFragmentToActivity(
                fragmentManager,
                loginWebFragmentWeakReference.get(),
                R.id.login_container,
                tag);
    }

    @Override
    public void setPresenter(LoginContact.Presenter presenter) {

    }

    /**
     * 登陆成功
     * @param memberInformation
     * @param condition 0正常登录 1登录后进入逛逛 2登录后进入补充资料的界面
     */
    @Override
    public void loginSuccess(LoginBean.DataBean memberInformation, int condition) {
        HomeActivity.goHomeActivity(this, memberInformation, condition);
        finish();
    }

    /**
     * 登录失败
     * @param verify
     * @param failureMessage
     * @param condition 0正常登录 1登录后进入逛逛 2登录后进入补充资料的界面
     */
    @Override
    public void loginFailure(String verify, String failureMessage, int condition) {
        switch (condition){
            case 0:
                EventBus.getDefault().post(new LoginFailureEvent(verify, failureMessage));
                break;
            case 1:
            case 2:
                EventBus.getDefault().post(new LoginWebFailureEvent());
                showLoginFailureDialog(getString(R.string.dialog_title_xinxi), getString(R.string.dialog_notification_login_failure_message));
                break;
        }
    }

    /**
     * 刚注册登录失败
     */
    public void showLoginFailureDialog(String title, String message) {
        final Dialog messageDialog = new Dialog(this, R.style.MessageDialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.public_dialog3, null);
        messageDialog.setContentView(dialogView);
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.setCancelable(false);//返回键无效
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        messageDialog.dismiss();
                        //到登录界面登录
                        switchLoginFragment();
                        break;
                }
            }
        };
        ((TextView) dialogView.findViewById(R.id.tv_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.tv_message)).setText(message);
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        messageDialog.show();
    }

    /**
     * 登录
     *  accountId 用户名
     *  password 密码
     *  condition 登录的情况
     */
    public void login(String accountId, String password, int condition) {
        mLoginPresenter.loginEnter(accountId, password, condition);
    }

    public Fragment getCurrentFragment() {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.login_container);
        return currentFragment;
    }

    public static final int REQUEST_CODE_PERMISSION_LOGIN = 0x2;

    /**
     * 从相册选取图片，检查SD卡权限和摄像头权限
     */
    public  boolean checkPermission() {
        boolean haveNoPrmission_WRITE_EXTERNAL_STORAGE = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean haveNoPrmission_CAMERA = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        return haveNoPrmission_CAMERA && haveNoPrmission_WRITE_EXTERNAL_STORAGE;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOGIN://下载文件
                Resources resources = getResources();
                String cancel = resources.getString(R.string.cancel);
                String sure = resources.getString(R.string.sure);
                String getPermissions = resources.getString(R.string.getPermissions);
                String permissionsMsg1 = resources.getString(R.string.permissionsMsg1);
                String permissionsMsg2 = resources.getString(R.string.permissionsMsg2);
                final String noPermissions = resources.getString(R.string.noPermissions);
                for (int i = 0; i < grantResults.length; i++) {
                    boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if (isTip) {//表明用户没有彻底禁止弹出权限请求
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setCancelable(false);
                            builder.setTitle(getPermissions);
                            builder.setMessage(permissionsMsg1);
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                //   whichButton，是哪一个按钮被触发
                                //   其值如下：
                                //   Dialog.BUTTON_POSITIVE     确认
                                //   Dialog.BUTTON_NEUTRAL      取消
                                //   Dialog.BUTTON_NEGATIVE     忽略
                                @Override
                                public void onClick(DialogInterface dialogInterface, int whichButton) {
                                    switch (whichButton) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialogInterface.dismiss();
                                            //再次清求
                                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                                                    REQUEST_CODE_PERMISSION_LOGIN);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            dialogInterface.dismiss();
                                            Toast.makeText(LoginActivity.this, noPermissions, Toast.LENGTH_SHORT).show();
                                            break;

                                    }
                                }
                            };
                            builder.setPositiveButton(sure, onClickListener);
                            builder.setNegativeButton(cancel, onClickListener);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        } else {//表明用户已经彻底禁止弹出权限请求

                            //这里一般会提示用户进入权限设置界面
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setCancelable(false);
                            builder.setTitle(getPermissions);
                            builder.setMessage(permissionsMsg2);
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                //   whichButton，是哪一个按钮被触发
                                //   其值如下：
                                //   Dialog.BUTTON_POSITIVE     确认
                                //   Dialog.BUTTON_NEUTRAL      取消
                                //   Dialog.BUTTON_NEGATIVE     忽略
                                @Override
                                public void onClick(DialogInterface dialogInterface, int whichButton) {
                                    switch (whichButton) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialogInterface.dismiss();
                                            //打开设置界面
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivityForResult(intent, REQUEST_CODE_PERMISSION_LOGIN);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            dialogInterface.dismiss();
                                            Toast.makeText(LoginActivity.this, noPermissions, Toast.LENGTH_SHORT).show();
                                            break;

                                    }
                                }
                            };
                            builder.setPositiveButton(sure, onClickListener);
                            builder.setNegativeButton(cancel, onClickListener);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        return;
                    }
                }

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         /*
         * 处理页面返回或取消选择结果
         */
        Resources resources = getResources();
        String havePermissions = resources.getString(R.string.havePermissions);
        String noSetPermissions = resources.getString(R.string.noSetPermissions);
        switch (requestCode) {
            case PhotoUtil.REQUEST_FILE_PICKER:

                pickPhotoResult(resultCode, data);
                break;
            case PhotoUtil.REQUEST_CODE_PICK_PHOTO:

                pickPhotoResult(resultCode, data);
                break;
            case PhotoUtil.REQUEST_CODE_TAKE_PHOTO:

                takePhotoResult(resultCode);
                break;
            case PhotoUtil.REQUEST_CODE_PREVIEW_PHOTO:

                mPhotoUtil.cancelFilePathCallback(PhotoUtil.photoPath);
                break;
            case REQUEST_CODE_PERMISSION_LOGIN:
                boolean requestPermissionsResult = checkPermission();
                if (requestPermissionsResult){
                    Toast.makeText(this,havePermissions , Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, noSetPermissions, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }



    private void pickPhotoResult(int resultCode, Intent data) {
        if (PhotoUtil.mFilePathCallback != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                PhotoUtil.mFilePathCallback.onReceiveValue(new Uri[]{uri});
                /*
                 * 将路径赋值给常量photoFile4，记录第一张上传照片路径
                 */
                PhotoUtil.photoPath = path;

                Log.d(TAG.d, "onActivityResult: " + path);
            } else {
                /*
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PhotoUtil.mFilePathCallback.onReceiveValue(null);
                PhotoUtil.mFilePathCallback = null;
            }
            /*
             * 针对API 19之前的版本
             */
        } else if (PhotoUtil.mFilePathCallback4 != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                PhotoUtil.mFilePathCallback4.onReceiveValue(uri);
                /*
                 * 将路径赋值给常量photoFile
                 */
                Log.d(com.kmjd.jsylc.zxh.utils.TAG.d, "onActivityResult: " + path);
            } else {
                /*
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PhotoUtil.mFilePathCallback4.onReceiveValue(null);
                PhotoUtil.mFilePathCallback4 = null;
            }
        }
    }

    private void takePhotoResult(int resultCode) {
        if (PhotoUtil.mFilePathCallback != null) {
            if (resultCode == RESULT_OK) {
                String path = PhotoUtil.photoPath;
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                PhotoUtil.mFilePathCallback.onReceiveValue(new Uri[]{uri});

                Log.d(com.kmjd.jsylc.zxh.utils.TAG.d, "onActivityResult: " + path);
            } else {
                /*
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PhotoUtil.mFilePathCallback.onReceiveValue(null);
                PhotoUtil.mFilePathCallback = null;
            }
            /*
             * 针对API 19之前的版本
             */
        } else if (PhotoUtil.mFilePathCallback4 != null) {
            if (resultCode == RESULT_OK) {
                String path = PhotoUtil.photoPath;
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                PhotoUtil.mFilePathCallback4.onReceiveValue(uri);
            } else {
                /*
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PhotoUtil.mFilePathCallback4.onReceiveValue(null);
                PhotoUtil.mFilePathCallback4 = null;
            }
        }
    }

    private long mExitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (getCurrentFragment() instanceof LoginFragment) {
                if (loginFragmentWeakReference.get().onKeyDown(keyCode, event)){
                    return true;
                }else {
                    if ((System.currentTimeMillis() - mExitTime) > 2000) {
                        Toast.makeText(this, getResources().getString(R.string.quit_app), Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();
                    } else {
                        finish();
                    }
                    return true;
                }
            }else if (getCurrentFragment() instanceof LoginWebFragment){
                if (loginWebFragmentWeakReference.get().onKeyDown(keyCode, event)){
                    return true;
                }else {
                    switchLoginFragment();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (null != mLoginPresenter){
            mLoginPresenter.onDestory();
        }
        super.onDestroy();
    }


}
