package com.kmjd.jsylc.zxh.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kmjd.jsylc.zxh.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.kmjd.jsylc.zxh.module.card.CardRecListener;
import com.kmjd.jsylc.zxh.module.card.CardRecManager;
import com.kmjd.jsylc.zxh.module.card.entity.BankCardEvent;
import com.kmjd.jsylc.zxh.module.card.entity.CardDetailEntity;
import com.kmjd.jsylc.zxh.module.card.utils.Base64Util;
import com.kmjd.jsylc.zxh.utils.MyLogger;
import com.wintone.bankcard.BankCardAPI;
import com.kmjd.jsylc.zxh.widget.ViewfinderView;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 银行卡识别的相机扫描界面
 */
public class ScanCameraActivity extends AppCompatActivity implements Callback, PreviewCallback{

    private static double NORMAL_CARD_SCALE = 1.58577d;
    private BankCardAPI api;
    private ImageButton back;
    private Bitmap bitmap;
    private Camera camera;
    private int counter = 0;
    private int counterCut = 0;
    private int counterFail = 0;
    private ImageButton flash;
    private int height;
    private ImageView help_word;
    private boolean isFatty = false;
    private boolean isROI = false;
    private boolean isShowBorder = false;
    List<Size> list;
    private Vibrator mVibrator;
    private ViewfinderView myView;

    private int preHeight = 0;
    private int preWidth = 0;
    private RelativeLayout re_c;
    public int srcHeight;
    public int srcWidth;
    public int surfaceHeight;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    public int surfaceWidth;
    private byte[] tackData;
    private Timer time;
    private TimerTask timer;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置屏幕横屏及全屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan_camera);
        setScreenSize(this);
        findView();
    }

    public static void startScanCameraActivity(Context context) {
        Intent intent = new Intent(context, ScanCameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    protected void onRestart() {
        if (this.bitmap != null) {
            this.bitmap.recycle();
            this.bitmap = null;
        }
        this.counterFail = 0;
        super.onRestart();
    }

    protected void onResume() {
        super.onResume();
        this.api = new BankCardAPI();
        this.api.WTInitCardKernal("", 0);
    }

    /**
     * 获取屏幕原始分辨率
     * @param context
     */
    @SuppressLint({"NewApi"})
    private void setScreenSize(Context context) {
        int x;
        int y;
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        if (VERSION.SDK_INT >= 13) {
            Point screenSize = new Point();
            if (VERSION.SDK_INT >= 17) {
                display.getRealSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            } else {
                display.getSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            }
        } else {
            x = display.getWidth();
            y = display.getHeight();
        }
        this.srcWidth = x;
        this.srcHeight = y;
    }

    private void findView() {
        this.surfaceView = findViewById(R.id.surfaceViwe);
        this.re_c = findViewById(R.id.re_c);
        this.help_word = findViewById(R.id.help_word);
        this.back = findViewById(R.id.back_camera);
        this.flash = findViewById(R.id.flash_camera);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        this.width = metric.widthPixels;
        this.height = metric.heightPixels;

        if (this.width * 3 == this.height * 4) {
            this.isFatty = true;
        }

        //为适配各控件尺寸/位置的初始化
        int back_w = (int) (((double) this.width) * 0.066796875d);
        int back_h = back_w;
        LayoutParams layoutParams = new LayoutParams(back_w, back_h);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        int Fheight = this.height;
        if (this.isFatty) {
            Fheight = (int) (((double) this.height) * 0.75d);
        }
        layoutParams.leftMargin = (int) ((((((double) this.width) - ((((double) Fheight) * 0.8d) * 1.585d)) / 2.0d) - ((double) back_h)) / 2.0d);
        layoutParams.bottomMargin = (int) (((double) this.height) * 0.10486111111111111d);
        this.back.setLayoutParams(layoutParams);

        int flash_w = (int) (((double) this.width) * 0.066796875d);
        layoutParams = new LayoutParams(flash_w, flash_w);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        if (this.isFatty) {
            Fheight = (int) (((double) this.height) * 0.75d);
        }
        layoutParams.leftMargin = (int) ((((((double) this.width) - ((((double) Fheight) * 0.8d) * 1.585d)) / 2.0d) - ((double) back_h)) / 2.0d);
        layoutParams.topMargin = (int) (((double) this.height) * 0.10486111111111111d);
        this.flash.setLayoutParams(layoutParams);

        int help_word_w = (int) (((double) this.width) * 0.474609375d);
        int help_word_h = (int) (((double) help_word_w) * 0.05185185185185185d);
        layoutParams = new LayoutParams(help_word_w, help_word_h);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        this.help_word.setLayoutParams(layoutParams);

        //surfaceView的初始化
        this.surfaceHolder = this.surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setType(3);

        //back按钮的点击事件
        this.back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ScanCameraActivity.this.finish();
            }
        });

        //闪光灯的点击事件
        this.flash.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //是否支持闪光灯的判断
                if (!ScanCameraActivity.this.getPackageManager().hasSystemFeature("android.hardware.camera.flash")) {
                    Toast.makeText(ScanCameraActivity.this, ScanCameraActivity.this.getResources().getString(ScanCameraActivity.this.getResources().getIdentifier("toast_flash", "string", ScanCameraActivity.this.getApplication().getPackageName())), Toast.LENGTH_SHORT).show();
                } else if (ScanCameraActivity.this.camera != null) {
                    Parameters parameters = ScanCameraActivity.this.camera.getParameters();
                    if (parameters.getFlashMode().equals("torch")) { //关闭闪光灯
                        parameters.setFlashMode("off");
                        parameters.setExposureCompensation(0);
                    } else { //开启闪光灯
                        parameters.setFlashMode("torch");
                        parameters.setExposureCompensation(-1);
                    }
                    try {
                        ScanCameraActivity.this.camera.setParameters(parameters);
                    } catch (Exception e) {
                        Toast.makeText(ScanCameraActivity.this, ScanCameraActivity.this.getResources().getString(ScanCameraActivity.this.getResources().getIdentifier("toast_flash", "string", ScanCameraActivity.this.getApplication().getPackageName())), Toast.LENGTH_SHORT).show();
                    }
                    ScanCameraActivity.this.camera.startPreview();
                }
            }
        });
    }

    public void surfaceCreated(SurfaceHolder holder) {
        //相机初始化
        if (this.camera == null) {
            try {//打开相机
                this.camera = Camera.open();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_camera), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        try {
            //设置相机的界面
            this.camera.setPreviewDisplay(holder);

            //设置相机2s一次循环聚焦
            this.time = new Timer();
            if (this.timer == null) {
                this.timer = new TimerTask() {
                    public void run() {
                        if (ScanCameraActivity.this.camera != null) {
                            try {
                                //相机聚焦的回调
                                ScanCameraActivity.this.camera.autoFocus(new AutoFocusCallback() {
                                    public void onAutoFocus(boolean success, Camera camera) {
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
            }
            this.time.schedule(this.timer, 500, 2000);
            initCamera(holder);
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (this.camera != null) {
                this.camera.setPreviewCallback(null);
                this.camera.stopPreview();
                this.camera.release();
                this.camera = null;
            }

        } catch (Exception e) {
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                if (this.camera != null) {
                    this.camera.setPreviewCallback(null);
                    this.camera.stopPreview();
                    this.camera.release();
                    this.camera = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 相机参数的初始化
     * @param holder
     */
    @TargetApi(14)
    private void initCamera(SurfaceHolder holder) {
        Parameters parameters = this.camera.getParameters();
        //获取相机的最佳预览尺寸
        getCameraPreParameters(this.camera);
        //初始化银行卡识别区域的尺寸
        if (!this.isROI) {
            int t = this.height / 10;
            int b = this.height - t;
            int l = (this.width - ((int) (((double) (b - t)) * NORMAL_CARD_SCALE))) / 2;
            int r = this.width - l;
            l += 30;
            t += 19;
            r -= 30;
            b -= 19;
            if (this.isFatty) {
                t = this.height / 5;
                b = this.height - t;
                l = (this.width - ((int) (((double) (b - t)) * NORMAL_CARD_SCALE))) / 2;
                r = this.width - l;
            }
            double proportion = ((double) this.width) / ((double) this.preWidth);
            l = (int) (((double) l) / proportion);
            t = (int) (((double) t) / proportion);
            r = (int) (((double) r) / proportion);
            b = (int) (((double) b) / proportion);
            this.api.WTSetROI(new int[]{l, t, r, b}, this.preWidth, this.preHeight);
            this.isROI = true;

            //添加银行卡识别区域的边框
            if (this.isFatty) {
                this.myView = new ViewfinderView(this, this.width, this.height, this.isFatty);
            } else {
                this.myView = new ViewfinderView(this, this.width, this.height);
            }
            this.re_c.addView(this.myView);
        }
        //设置相机的参数
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewSize(this.preWidth, this.preHeight);
        //设置连续自动对焦
        if (parameters.getSupportedFocusModes().contains("continuous-picture")) {
            if (this.time != null) {
                this.time.cancel();
                this.time = null;
            }
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
            parameters.setFocusMode("continuous-picture");
        } else if (parameters.getSupportedFocusModes().contains("auto")) { //设置自动对焦
            parameters.setFocusMode("auto");
        }
        //设置预览界面接口的回调
        this.camera.setPreviewCallback(this);
        this.camera.setParameters(parameters);
        try {
            this.camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //开启相机预览
        this.camera.startPreview();
    }

    /**
     * 预览界面接口的回调
     *
     * @param data 预览界面原始数据
     * @param camera 相机
     */
    public void onPreviewFrame(byte[] data, Camera camera) {
        this.tackData = data;
        Parameters parameters = camera.getParameters();
        int[] isBorders = new int[4];
        this.counter++;

        //设置相机识别区域边框
        if (this.counter == 2) {
            this.counter = 0;
            char[] recogval = new char[30];
            int[] bRotated = new int[1];
            int[] pLineWarp = new int[32000];

            //调用本地银行卡识别功能
            int result = this.api.RecognizeNV21(data, parameters.getPreviewSize().width, parameters.getPreviewSize().height, isBorders, recogval, 30, bRotated, pLineWarp);
            if (isBorders[0] == 1) {
                if (this.myView != null) {
                    this.myView.setLeftLine(1);
                }
            } else if (this.myView != null) {
                this.myView.setLeftLine(0);
            }
            if (isBorders[1] == 1) {
                if (this.myView != null) {
                    this.myView.setTopLine(1);
                }
            } else if (this.myView != null) {
                this.myView.setTopLine(0);
            }
            if (isBorders[2] == 1) {
                if (this.myView != null) {
                    this.myView.setRightLine(1);
                }
            } else if (this.myView != null) {
                this.myView.setRightLine(0);
            }
            if (isBorders[3] == 1) {
                if (this.myView != null) {
                    this.myView.setBottomLine(1);
                }
            } else if (this.myView != null) {
                this.myView.setBottomLine(0);
            }
            if (isBorders[0] != 1 || isBorders[1] != 1 || isBorders[2] != 1 || isBorders[3] != 1) {
                this.counterCut++;
                if (this.counterCut == 5) {
                    this.counterFail = 0;
                    this.counterCut = 0;
                }
            } else if (result == 0) { //本地接口返回数据处理

                //将相机预览数据转换为Bitmap, 并进行压缩处理
                ByteArrayOutputStream baos = null;
                byte[] rawImage = null;
                Camera.Size previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
                YuvImage yuvimage = new YuvImage(
                        data,
                        ImageFormat.NV21,
                        previewSize.width,
                        previewSize.height,
                        null);
                baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 50, baos);// 80--JPG图片的质量[0-100],100最高
                rawImage = baos.toByteArray();
                //将rawImage转换成bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);

                //停止预览功能
                camera.stopPreview();
                this.api.WTUnInitCardKernal();
                this.mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);
                this.mVibrator.vibrate(100);
                camera.setPreviewCallback(null);

                //获取银行卡号
                String bankCardNum = String.valueOf(recogval);
                if (!TextUtils.isEmpty(bankCardNum)) { //本地成功获取
                    bankCardNum = bankCardNum.replace(" ", "").trim();
                    MyLogger.mLog().d(bankCardNum);
//                    BankInfoUtil mInfoUtil = new BankInfoUtil(bankCardNum);
//                    String bankName = mInfoUtil.getBankName();
//                    String cardType = mInfoUtil.getCardType();
//                    Log.d("ming007", "onPreviewFrame:  bankCardNum = " + bankCardNum + " bankName = " + bankName + " cardType = " + cardType);
                    sendMsgToWebFg(bankCardNum);
                }else{ //本地获取失败调用阿里云接口
                    //bitmap转换为Base64
                    String bitmapStr = Base64Util.bitmapToBase64(bitmap);
                    CardRecManager.getInstance().onRecognitionByALIYUN(bitmapStr, new CardRecListener.RecognitionListener() {
                        @Override
                        public void onRecognitionSuccess(CardDetailEntity entity) {
                            String number = entity.getNumber();
                            sendMsgToWebFg(number);
                        }

                        @Override
                        public void onRecognitionError(String error) {
                            String number = "";
                            sendMsgToWebFg(number);
                        }
                    });

                }
            }
        }
    }

    /**
     * 返回银行卡号给WebFragment
     *
     * @param bankCardNum 银行卡号
     */
    private void sendMsgToWebFg(String bankCardNum) {
        BankCardEvent bankCardEvent = new BankCardEvent();
        bankCardEvent.setBankCardNum(bankCardNum);
        EventBus.getDefault().post(bankCardEvent);
        finish();
    }

    protected void onStop() {
        super.onStop();
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        if (this.bitmap != null) {
            this.bitmap.recycle();
            this.bitmap = null;
        }
        try {
            if (this.camera != null) {
                this.camera.setPreviewCallback(null);
                this.camera.stopPreview();
                this.camera.release();
                this.camera = null;
            }
        } catch (Exception e) {
        }
    }

    /**
     * 获取相机的最佳预览尺寸
     * @param camera
     */
    public void getCameraPreParameters(Camera camera) {
        this.isShowBorder = false;
        if ("PLK-TL01H".equals(Build.MODEL)) {
            this.preWidth = 1920;
            this.preHeight = 1080;
        } else if ("MI 3".equals(Build.MODEL)) {
            this.preWidth = 1024;
            this.preHeight = 576;
        } else {
            //获取相机支持的预览尺寸
            this.list = camera.getParameters().getSupportedPreviewSizes();
            float ratioScreen = ((float) this.srcWidth) / ((float) this.srcHeight);
            int i = 0;
            while (i < this.list.size()) {
                if (ratioScreen == ((float) (this.list.get(i)).width) / ((float) (this.list.get(i)).height) && ((this.list.get(i)).width >= 1280 || (this.list.get(i)).height >= 720)) {
                    if (this.preWidth == 0 && this.preHeight == 0) {
                        this.preWidth = (this.list.get(i)).width;
                        this.preHeight = (this.list.get(i)).height;
                    }
                    if ((this.list.get(0)).width > (this.list.get(this.list.size() - 1)).width) {
                        if (this.preWidth > (this.list.get(i)).width || this.preHeight > (this.list.get(i)).height) {
                            this.preWidth = (this.list.get(i)).width;
                            this.preHeight = (this.list.get(i)).height;
                        }
                    } else if ((this.preWidth < (this.list.get(i)).width || this.preHeight < (this.list.get(i)).height) && this.preWidth < 1280 && this.preHeight < 720) {
                        this.preWidth = (this.list.get(i)).width;
                        this.preHeight = (this.list.get(i)).height;
                    }
                }
                i++;
            }
            if (this.preWidth == 0 || this.preHeight == 0) {
                this.isShowBorder = true;
                this.preWidth = (this.list.get(0)).width;
                this.preHeight = (this.list.get(0)).height;
                i = 0;
                while (i < this.list.size()) {
                    if ((this.list.get(0)).width > (this.list.get(this.list.size() - 1)).width) {
                        if ((this.preWidth >= (this.list.get(i)).width || this.preHeight >= (this.list.get(i)).height) && (this.list.get(i)).width >= 1280) {
                            this.preWidth = (this.list.get(i)).width;
                            this.preHeight = (this.list.get(i)).height;
                        }
                    } else if ((this.preWidth <= (this.list.get(i)).width || this.preHeight <= (this.list.get(i)).height) && this.preWidth < 1280 && this.preHeight < 720 && (this.list.get(i)).width >= 1280) {
                        this.preWidth = (this.list.get(i)).width;
                        this.preHeight = (this.list.get(i)).height;
                    }
                    i++;
                }
            }
            if (this.preWidth == 0 || this.preHeight == 0) {
                this.isShowBorder = true;
                if ((this.list.get(0)).width > (this.list.get(this.list.size() - 1)).width) {
                    this.preWidth = (this.list.get(0)).width;
                    this.preHeight = (this.list.get(0)).height;
                } else {
                    this.preWidth = (this.list.get(this.list.size() - 1)).width;
                    this.preHeight = (this.list.get(this.list.size() - 1)).height;
                }
            }
            if (!this.isShowBorder) {
                this.surfaceWidth = this.srcWidth;
                this.surfaceHeight = this.srcHeight;
            } else if (ratioScreen > ((float) this.preWidth) / ((float) this.preHeight)) {
                this.surfaceWidth = (int) ((((float) this.preWidth) / ((float) this.preHeight)) * ((float) this.srcHeight));
                this.surfaceHeight = this.srcHeight;
            } else {
                this.surfaceWidth = this.srcWidth;
                this.surfaceHeight = (int) ((((float) this.preHeight) / ((float) this.preWidth)) * ((float) this.srcHeight));
            }
        }
    }

    @TargetApi(19)
    public void hiddenVirtualButtons(View mDecorView) {
        if (VERSION.SDK_INT >= 19) {
            mDecorView.setSystemUiVisibility(3334);
        }
    }

}
