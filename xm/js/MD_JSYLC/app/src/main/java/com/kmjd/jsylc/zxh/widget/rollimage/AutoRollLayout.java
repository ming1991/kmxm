package com.kmjd.jsylc.zxh.widget.rollimage;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.mvp.model.bean.RollBean;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.utils.SPUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *  登录页轮播图
 */
public class AutoRollLayout extends FrameLayout {

    private Context mContext;
    private ViewPager mArl_viewPager;
    private LinearLayout mArl_ll_points;
    private List<RollBean.ImgsBean> mRollItems;

    private Handler mHandler=new Handler();

    public AutoRollLayout(Context context) {
        this(context, null);
    }

    public AutoRollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoRollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context) {
        mContext = context;
        View.inflate(context, R.layout.arl_lunbo_layout,this);
        mArl_viewPager = findViewById(R.id.arl_ViewPager);
        mArl_ll_points = findViewById(R.id.arl_ll_points);
    }

    //2、设计方法，让外界把数据传进来
    public void setRollItems(List<RollBean.ImgsBean> rollItems){
        this.mRollItems=rollItems;
        //1、给ViewPager设置适配器，显示图片
        mArl_viewPager.setAdapter(mPagerAdapter);
        //2、根据不同的图片，显示标题  给ViewPager设置监听，修改标题
        mArl_viewPager.addOnPageChangeListener(mOnPageChangeListener);
        //3、初始化点
        initPoints();

        //4.通过反射来修改 ViewPager的mScroller属性,解决滑动时不平缓的问题
        try {
            Class clazz=Class.forName("android.support.v4.view.ViewPager");
            Field f=clazz.getDeclaredField("mScroller");
            FixedSpeedScroller fixedSpeedScroller=new FixedSpeedScroller(mContext,new LinearOutSlowInInterpolator());
            fixedSpeedScroller.setmDuration(500);
            f.setAccessible(true);
            f.set(mArl_viewPager,fixedSpeedScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mArl_viewPager.setCurrentItem(mRollItems.size()*2);

        //开始轮播
        setAutoRoll(true);
    }

    //初始化点
    private void initPoints() {
        //清空所有的点
        mArl_ll_points.removeAllViews();
        if (mRollItems==null){
            return;
        }
        if (mRollItems.size() == 1){
            //只有一张图的时候，不显示白点
            return;
        }
        for (int i = 0; i < mRollItems.size(); i++) {
            //创建点
            View view=new View(getContext());
            //dp===》px
            int pxFrom10Dp= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(pxFrom10Dp,pxFrom10Dp);
            //右边距
            params.rightMargin=pxFrom10Dp;
            params.leftMargin=pxFrom10Dp;
            params.topMargin=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,getResources().getDisplayMetrics());
            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.arl_point_selector);
            //添加到线性布局里面
            mArl_ll_points.addView(view);
        }
    }
    //设置自动轮播
    public void setAutoRoll(boolean isAuto){
        if (isAuto && null != mRollItems && mRollItems.size() > 1){
            //自动轮播
            mHandler.removeCallbacks(goToNextPageRunnable);
            mHandler.postDelayed(goToNextPageRunnable,2000);
        }else{
            //停止轮播
            mHandler.removeCallbacks(goToNextPageRunnable);
        }
    }

    private Runnable goToNextPageRunnable=new Runnable() {
        @Override
        public void run() {
            //获取当前条目
            int currentItem = mArl_viewPager.getCurrentItem();
            mArl_viewPager.setCurrentItem(currentItem+1,true);
            mHandler.postDelayed(this,2000);
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        //选中的时候调用
        @Override
        public void onPageSelected(int position) {
            //改变指示器的状态
            for (int i = 0; i < mArl_ll_points.getChildCount(); i++) {
                if (i == position % mRollItems.size()){
                    mArl_ll_points.getChildAt(i).setEnabled(false);//未选中
                }else{
                    mArl_ll_points.getChildAt(i).setEnabled(true);//选中
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state){
                case ViewPager.SCROLL_STATE_IDLE://滚动状态闲置
                    setAutoRoll(true);//继续轮播
                    break;
                case ViewPager.SCROLL_STATE_DRAGGING://滚动状态拖
                    setAutoRoll(false);//停止轮博
                    break;
                case ViewPager.SCROLL_STATE_SETTLING://滚动状态解决
                    break;
            }
        }
    };

    private PagerAdapter mPagerAdapter=new PagerAdapter() {

        private List<ImageView> cacheImage=new ArrayList<>();
        @Override
        public int getCount() {
            int count = 1;
            if (null != mRollItems && mRollItems.size() > 1){
                count = Integer.MAX_VALUE;
            }
            return count;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (cacheImage.size()==0){
                ImageView imageView=new ImageView(container.getContext());
                //设置参数
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                imageView.setLayoutParams(params);
                //没有的话就默认添加，后面会移除出来
                cacheImage.add(imageView);
            }
            //使用完了之后就从集合里面移除出来
            ImageView imageView = cacheImage.remove(0);
            //跟当前条目的uri显示图片  使用Picasso
            RollBean.ImgsBean rollItem = mRollItems.get(position % mRollItems.size());
            //设置点击监听
            imageView.setTag(R.id.auto_toll_image_first_tag,position % mRollItems.size());
            imageView.setOnClickListener(mOnClickListener);
            //显示
            Glide.with(container.getContext())
                    .load(getImgNetWorkAddress(rollItem.getImg()))
                    .into(imageView);
            //添加到容器
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //保存消失的ImageView
            cacheImage.add((ImageView) object);
            container.removeView((ImageView) object);
        }
    };

    /**
     * 处理网络图片地址
     */
    private String getImgNetWorkAddress(String originalAddress){
        String imgNetWorkAddress = originalAddress.replace("https", "http");
        if (!imgNetWorkAddress.contains("http")){
            imgNetWorkAddress = SPUtils.getString(MainApplication.applicationContext,SPUtils.SP_KEY.FAST_DOMAIN.toString()) + imgNetWorkAddress;
        }
        return imgNetWorkAddress;
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null != view){
                Object tag = view.getTag(R.id.auto_toll_image_first_tag);
                if (tag instanceof Integer){
                    int position = (int) tag;
                    if (null != mOnRollLayoutOnClickListener){
                        mOnRollLayoutOnClickListener.onClick(mRollItems.get(position));
                    }
                }
            }
        }
    };

    private OnRollLayoutOnClickListener mOnRollLayoutOnClickListener;

    public interface OnRollLayoutOnClickListener{
        void onClick(RollBean.ImgsBean rollItem);
    }

    public void setOnRollLayoutOnClickListener(OnRollLayoutOnClickListener onRollLayoutOnClickListener){
        mOnRollLayoutOnClickListener = onRollLayoutOnClickListener;
    }
}
