package com.kmjd.wcqp.single.zxh.customwidget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.util.CheckWhetherInstalledAnApplication;
import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

import static com.kmjd.wcqp.single.zxh.R.id.rl_install_certificate;


/**
 * Created by androidshuai on 2017/7/17.
 */

public class Rote3DView extends ViewGroup{
    private Context mContext;
    private int mWidth;
    private Scroller mScroller;
    private Camera mCamera;
    private Matrix mMatrix;
    // 旋转的角度，可以进行修改来观察效果
    private float angle = 90;
    private TextView mIvIsInstallWechat;
    private ImageView mIvOpenWechat;

    public Rote3DView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mScroller = new Scroller(context);
        mCamera = new Camera();
        mMatrix = new Matrix();
        initScreens();
    }

    public void initScreens(){
        LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        View welcome_guide1 = LayoutInflater.from(mContext).inflate(R.layout.welcome_guide1, null);
        initWelcomeGuide1(welcome_guide1);
        View welcome_guide2 = LayoutInflater.from(mContext).inflate(R.layout.welcome_guide2, null);
        initWelcomeGuide2(welcome_guide2);
        View welcome_guide3 = LayoutInflater.from(mContext).inflate(R.layout.welcome_guide3, null);
        initWelcomeGuide3(welcome_guide3);
        View welcome_guide4 = LayoutInflater.from(mContext).inflate(R.layout.welcome_guide4, null);
        initWelcomeGuide4(welcome_guide4);
        View welcome_guide5 = LayoutInflater.from(mContext).inflate(R.layout.welcome_guide5, null);
        initWelcomeGuide5(welcome_guide5);

        this.addView(welcome_guide1, 0, p);
        this.addView(welcome_guide2, 1, p);
        this.addView(welcome_guide3, 2, p);
        this.addView(welcome_guide4, 3, p);
        this.addView(welcome_guide5, 4, p);
    }

    public void refreshView(){
        //判断手机是否已经安装了微信客户端
        boolean weiXinClientAvilible = CheckWhetherInstalledAnApplication.isWeiXinClientAvilible(mContext);
        mIvIsInstallWechat.setText(weiXinClientAvilible ? R.string.welcome_guide1_open_wechat : R.string.welcome_guide1_no_wechat);
        mIvOpenWechat.setVisibility(weiXinClientAvilible ? View.VISIBLE : View.GONE);
    }

    private OnRote3DViewClickListener mOnRote3DViewClickListener;

    //点击事件通过接口回调调用
    public interface OnRote3DViewClickListener{
        void openWechat();
        void installCert();
        void setSystemAgency();
        void setNoBarrierService();
        void enterWelComeActivity();
        void openGuide3Photoview(View view);
        void openGuide4Photoview(View view);
    }

    public void setOnRote3DViewClickListener(OnRote3DViewClickListener onRote3DViewClickListener){
        this.mOnRote3DViewClickListener = onRote3DViewClickListener;
    }


    private void initWelcomeGuide1(View welcome_guide1) {
        //判断手机是否已经安装了微信客户端
        boolean weiXinClientAvilible = CheckWhetherInstalledAnApplication.isWeiXinClientAvilible(mContext);

        mIvIsInstallWechat = (TextView) welcome_guide1.findViewById(R.id.tv_isInstall_wechat);
        mIvOpenWechat = (ImageView) welcome_guide1.findViewById(R.id.iv_open_wechat);
        RelativeLayout rl_open_wechat = (RelativeLayout) welcome_guide1.findViewById(R.id.rl_open_wechat);
        RelativeLayout next1 = (RelativeLayout) welcome_guide1.findViewById(R.id.next1);

        mIvIsInstallWechat.setText(weiXinClientAvilible ? R.string.welcome_guide1_open_wechat : R.string.welcome_guide1_no_wechat);
        mIvOpenWechat.setVisibility(weiXinClientAvilible ? View.VISIBLE : View.GONE);

        rl_open_wechat.setOnClickListener(mOnClickListener);
        next1.setOnClickListener(mOnClickListener);
    }

    private void initWelcomeGuide2(View welcome_guide2) {
        RelativeLayout next2 = (RelativeLayout) welcome_guide2.findViewById(R.id.next2);
        RelativeLayout rl_install_certificate = (RelativeLayout) welcome_guide2.findViewById(R.id.rl_install_certificate);

        rl_install_certificate.setOnClickListener(mOnClickListener);
        next2.setOnClickListener(mOnClickListener);
    }

    private void initWelcomeGuide3(View welcome_guide3) {
        RelativeLayout next3 = (RelativeLayout) welcome_guide3.findViewById(R.id.next3);
        RelativeLayout rl_system_agency = (RelativeLayout) welcome_guide3.findViewById(R.id.rl_system_agency);
        ImageView photoView = (ImageView) welcome_guide3.findViewById(R.id.photoview3);

        photoView.setOnClickListener(mOnClickListener);
        rl_system_agency.setOnClickListener(mOnClickListener);
        next3.setOnClickListener(mOnClickListener);
    }

    private void initWelcomeGuide4(View welcome_guide4) {
        RelativeLayout next4 = (RelativeLayout) welcome_guide4.findViewById(R.id.next4);
        RelativeLayout rl_nobarrier_service = (RelativeLayout) welcome_guide4.findViewById(R.id.rl_nobarrier_service);
        ImageView photoView = (ImageView) welcome_guide4.findViewById(R.id.photoview4);

        photoView.setOnClickListener(mOnClickListener);
        rl_nobarrier_service.setOnClickListener(mOnClickListener);
        next4.setOnClickListener(mOnClickListener);
    }

    private void initWelcomeGuide5(View welcome_guide5) {
        RelativeLayout next5 = (RelativeLayout) welcome_guide5.findViewById(R.id.next5);
        next5.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.rl_open_wechat:
                    if (null != mOnRote3DViewClickListener){
                        mOnRote3DViewClickListener.openWechat();
                    }
                    break;
                case R.id.next1:
                    // 下一步
                    boolean weiXinClientAvilible = CheckWhetherInstalledAnApplication.isWeiXinClientAvilible(mContext);
                    if (!weiXinClientAvilible){
                        Toast.makeText(mContext, R.string.welcome_guide1_no_wechat, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    snapToScreen(1);
                    break;
                case rl_install_certificate:
                    if (null != mOnRote3DViewClickListener){
                        mOnRote3DViewClickListener.installCert();
                    }
                    break;
                case R.id.next2:
                    boolean isInstallCert = SPUtils.getBoolean(mContext, ContantsUtil.ISINSTALLCERT, false);
                    if (!isInstallCert) {
                        Toast.makeText(mContext, "还没有安装成功安装CA证书", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    snapToScreen(2);
                    break;
                case R.id.rl_system_agency:
                    if (null != mOnRote3DViewClickListener){
                        mOnRote3DViewClickListener.setSystemAgency();
                    }
                    break;
                case R.id.photoview3:
                    if (null != mOnRote3DViewClickListener){
                        mOnRote3DViewClickListener.openGuide3Photoview(view);
                    }
                    break;
                case R.id.next3:
                    snapToScreen(3);
                    break;
                case R.id.rl_nobarrier_service:
                    if (null != mOnRote3DViewClickListener){
                        mOnRote3DViewClickListener.setNoBarrierService();
                    }
                    break;
                case R.id.photoview4:
                    if (null != mOnRote3DViewClickListener){
                        mOnRote3DViewClickListener.openGuide4Photoview(view);
                    }
                    break;
                case R.id.next4:
                    snapToScreen(4);
                    break;
                case R.id.next5:
                    if (null != mOnRote3DViewClickListener){
                        mOnRote3DViewClickListener.enterWelComeActivity();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;
        final int childCount = getChildCount();
        for(int i = 0; i< childCount; i++){
            final View childView = getChildAt(i);
            if(childView.getVisibility() != View.GONE){
                final int childWidth = childView.getMeasuredWidth();
                childView.layout(childLeft, 0, childLeft + childWidth, childView.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if(widthMode != MeasureSpec.EXACTLY){
            throw new IllegalStateException("仅支持精确尺寸");
        }
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(heightMode != MeasureSpec.EXACTLY){
            throw new IllegalStateException("仅支持精确尺寸");
        }
        final int count = getChildCount();
        for(int i = 0; i < count; i++){
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public void snapToScreen(int whichScreen){
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        setMWidth();
        int scrollX = getScrollX();
        int startWidth = whichScreen * mWidth;
        if(scrollX != startWidth){
            int delta = 0;
            int startX = 0;

            startX = scrollX;
            delta = startWidth - scrollX;

            mScroller.startScroll(startX, 0, delta, 0, 1200/*Math.abs(delta) * 2*/);
            invalidate();
        }
    }

    private void setMWidth(){
        if(mWidth == 0){
            mWidth = getWidth();
        }
    }

    /**
     * 当进行View滑动时，会导致当前的View无效，该函数的作用是对View进行重新绘制 调用drawScreen函数
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        final long drawingTime = getDrawingTime();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            drawScreen(canvas, i, drawingTime);
        }
    }

    public void drawScreen(Canvas canvas, int screen, long drawingTime) {
        // 得到当前子View的宽度
        final int width = getWidth();
        final int scrollWidth = screen * width;
        final int scrollX = this.getScrollX();
        // 偏移量不足的时
        if (scrollWidth > scrollX + width || scrollWidth + width < scrollX) {
            return;
        }
        final View child = getChildAt(screen);
        final int faceIndex = screen;
        final float currentDegree = getScrollX() * (angle / getMeasuredWidth());
        final float faceDegree = currentDegree - faceIndex * angle;
        if (faceDegree > 90 || faceDegree < -90) {
            return;
        }
        final float centerX = (scrollWidth < scrollX) ? scrollWidth + width
                : scrollWidth;
        final float centerY = getHeight() / 2;
        final Camera camera = mCamera;
        final Matrix matrix = mMatrix;
        canvas.save();
        camera.save();
        camera.rotateY(-faceDegree);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
        canvas.concat(matrix);
        drawChild(canvas, child, drawingTime);
        canvas.restore();
    }
}
