package com.md.jsyxzs_cn.zym_xs.custom_views.spinner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.utils.CommonUtils;

import java.util.List;

/**
 * @author angelo.marchesin
 */
@SuppressWarnings("unused")
public class NiceSpinner extends TextView {

    private static final int MAX_LEVEL = 10000;
    private static final int DEFAULT_ELEVATION = 16;
    private static final String INSTANCE_STATE = "instance_state";
    private static final String SELECTED_INDEX = "selected_index";
    private static final String IS_POPUP_SHOWING = "is_popup_showing";

    private int mSelectedIndex;
    private PopupWindow mPopup;
    private ListView mListView;
    private TextView mTextView;
    private NiceSpinnerBaseAdapter mAdapter;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;

    @SuppressWarnings("ConstantConditions")
    public NiceSpinner(Context context) {
        super(context);
        init(context, null);
    }

    public NiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NiceSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /*@Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(SELECTED_INDEX, mSelectedIndex);

        if (mPopup != null) {
            bundle.putBoolean(IS_POPUP_SHOWING, mPopup.isShowing());
            dismissDropDown();
        }

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable savedState) {
        if (savedState instanceof Bundle) {
            Bundle bundle = (Bundle) savedState;

            mSelectedIndex = bundle.getInt(SELECTED_INDEX);

            if (mAdapter != null) {
                setText(mAdapter.getItemInDataset(mSelectedIndex).toString());
                mAdapter.notifyItemSelected(mSelectedIndex);
            }

            if (bundle.getBoolean(IS_POPUP_SHOWING)) {
                if (mPopup != null) {
                    // Post the show request into the looper to avoid bad token exception
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showDropDown();
                        }
                    });
                }
            }

            savedState = bundle.getParcelable(INSTANCE_STATE);
        }

        super.onRestoreInstanceState(savedState);
    }*/

    private void init(Context context, AttributeSet attrs) {
        Resources resources = getResources();
        int defaultPadding = resources.getDimensionPixelSize(R.dimen.common_dp);

        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(defaultPadding, defaultPadding, defaultPadding,
                defaultPadding);
        setTextColor(getResources().getColor(R.color.mainAy_rb_textColor_checked));
        setClickable(true);
//        setBackgroundResource(R.drawable.selector);

        mListView = new ListView(context);
        // Set the spinner's id into the listview to make it pretend to be the right parent in
        // onItemClick
        mListView.setId(getId());

        //设置取消分割线
        //mListView.setDivider(null);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= mSelectedIndex && position < mAdapter.getCount()) {
                    position++;
                }

                // Need to set selected index before calling listeners or getSelectedIndex() can be
                // reported incorrectly due to race conditions.
                mSelectedIndex = position;
                isChange = true;

                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(parent, view, position, id);
                }

                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(parent, view, position, id);
                }

                if (isChange){
                    mAdapter.notifyItemSelected(position);
                    setText(mAdapter.getItemInDataset(position).toString());
                }
                dismissDropDown();
            }
        });

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        mTextView = new TextView(getContext());
        mTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDropDown();
            }
        });
        mTextView.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout listLinearLayout = new LinearLayout(getContext());
        listLinearLayout.setOrientation(LinearLayout.VERTICAL);
        listLinearLayout.addView(mListView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listLinearLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.website_choose_background));

        linearLayout.addView(mTextView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(listLinearLayout,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setBackgroundColor(Color.TRANSPARENT);

        mPopup = new PopupWindow(context);
        mPopup.setContentView(linearLayout);
        mPopup.setOutsideTouchable(true);
        mPopup.setFocusable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopup.setElevation(DEFAULT_ELEVATION);
            mPopup.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.transparent_drawable));
        } else {
            mPopup.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.transparent_drawable));
        }

        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (null != mOnArrowAnimateListener){
                    mOnArrowAnimateListener.animateArrow(false);
                }
            }
        });
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    public void setSelectedIndex(int position) {
        if (mAdapter != null) {
            if (position >= 0 && position <= mAdapter.getCount()) {
                mAdapter.notifyItemSelected(position);
                mSelectedIndex = position;
                setText(mAdapter.getItemInDataset(position).toString());
            } else {
                throw new IllegalArgumentException("Position must be lower than adapter count!");
            }
        }
    }

    public void addOnItemClickListener(@NonNull AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemSelectedListener(@NonNull AdapterView.OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public <T> void attachDataSource(@NonNull List<T> dataset) {
        mAdapter = new NiceSpinnerAdapter<>(getContext(), dataset);
        setAdapterInternal(mAdapter);
    }

    public void setAdapter(@NonNull ListAdapter adapter) {
        mAdapter = new NiceSpinnerAdapterWrapper(getContext(), adapter);
        setAdapterInternal(mAdapter);
    }

    private void setAdapterInternal(@NonNull NiceSpinnerBaseAdapter adapter) {
        mListView.setAdapter(adapter);
        /**
         * 解决切换用户类型数组越界的BUG，每次默认选中第一个
         */
        mSelectedIndex = 0;
        setText(adapter.getItemInDataset(mSelectedIndex).toString());
    }

    //获取用户输入的控件属性的大小
    public int getResult(int measureSpec){
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int result=0;
        switch (mode) {//模式有3种
            case MeasureSpec.AT_MOST://至多模式   wrap_content,父控件的大小,768；我们希望的包裹内容是和控件的大小和图片的大小一致
            case MeasureSpec.UNSPECIFIED://未指定模式:一般不用当成wrap_content来理解
                float pxFromDp = CommonUtils.pxFromDp(getContext(), 40.0f);
                result= (int) pxFromDp;//默认大小为40dp
                break;
            case MeasureSpec.EXACTLY://精确模式   1:match_parent,768   父控件的大小   2:300具体的数，
                result=size;
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthResult = getResult(widthMeasureSpec);
        int heightResult = getResult(heightMeasureSpec);
        mTextView.setWidth(widthResult);
        mTextView.setHeight(heightResult);

        mPopup.setWidth(MeasureSpec.getSize(widthMeasureSpec));
        mPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!mPopup.isShowing()) {
                if (null != mOnArrowAnimateListener && mOnArrowAnimateListener.isCanExpand()){
                    showDropDown();
                }
            } else {
                dismissDropDown();
            }
        }

        return super.onTouchEvent(event);
    }

    public void dismissDropDown() {
        if (null != mOnArrowAnimateListener){
            mOnArrowAnimateListener.animateArrow(false);
        }
        mPopup.dismiss();
    }

    public void showDropDown() {
        if (null != mOnArrowAnimateListener){
            mOnArrowAnimateListener.animateArrow(true);
        }
//        mPopup.showAsDropDown(this);
        int[] location = new int[2];
        getLocationOnScreen(location);
        mPopup.showAtLocation(this, Gravity.TOP | Gravity.START, location[0], location[1]);
    }

    private boolean isChange;
    public void setIsChanage(boolean isChange) {
        this.isChange = isChange;
    }

    private OnArrowAnimateListener mOnArrowAnimateListener;
    public interface OnArrowAnimateListener{
        void animateArrow(boolean shouldRotateUp);
        boolean isCanExpand();
    }
    public void setOnArrowAnimateListener(OnArrowAnimateListener onArrowAnimateListener){
        mOnArrowAnimateListener = onArrowAnimateListener;
    }
}
