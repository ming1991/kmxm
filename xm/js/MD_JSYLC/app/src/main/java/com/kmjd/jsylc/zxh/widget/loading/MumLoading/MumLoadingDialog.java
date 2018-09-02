
package com.kmjd.jsylc.zxh.widget.loading.MumLoading;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.R;


public class MumLoadingDialog {


    public enum Style {
        SPIN_INDETERMINATE
    }


    private ProgressDialog mProgressDialog;
    private float mDimAmount;
    private int mWindowColor;
    private float mCornerRadius;
    private Context mContext;

    private int mAnimateSpeed;

    private int mMaxProgress;
    private boolean mIsAutoDismiss;

    public MumLoadingDialog(Context context) {
        mContext = context;
        mProgressDialog = new ProgressDialog(context);
        Window window = mProgressDialog.getWindow();
        if (null != window){
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        mDimAmount = 0;

        //Dialog的背景色
        mWindowColor = context.getResources().getColor(android.R.color.transparent);
        mAnimateSpeed = 1;
        mCornerRadius = 10;
        mIsAutoDismiss = true;

        show(Style.SPIN_INDETERMINATE);
    }


    public static MumLoadingDialog create(Context context) {
        return new MumLoadingDialog(context);
    }


    public static MumLoadingDialog create(Context context, Style style) {
        return new MumLoadingDialog(context).show(style);
    }


    public MumLoadingDialog show(Style style) {
        View view = null;
        switch (style) {
            case SPIN_INDETERMINATE:
                view = new SpinView(mContext);
                break;

        }
        mProgressDialog.setView(view);
        return this;
    }


    public MumLoadingDialog setDimAmount(float dimAmount) {
        if (dimAmount >= 0 && dimAmount <= 1) {
            mDimAmount = dimAmount;
        }
        return this;
    }


    public MumLoadingDialog setSize(int width, int height) {
        mProgressDialog.setSize(width, height);
        return this;
    }


    public MumLoadingDialog setWindowColor(int color) {
        mWindowColor = color;
        return this;
    }


    public MumLoadingDialog setCornerRadius(float radius) {
        mCornerRadius = radius;
        return this;
    }


    public MumLoadingDialog setAnimationSpeed(int scale) {
        mAnimateSpeed = scale;
        return this;
    }


    public MumLoadingDialog setLabel(String label) {
        mProgressDialog.setLabel(label);
        return this;
    }


    public MumLoadingDialog setDetailsLabel(String detailsLabel) {
        mProgressDialog.setDetailsLabel(detailsLabel);
        return this;
    }


    public MumLoadingDialog setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        return this;
    }


    public void setProgress(int progress) {
        mProgressDialog.setProgress(progress);
    }


    public MumLoadingDialog setCustomView(View view) {
        if (view != null) {
            mProgressDialog.setView(view);
        } else {
            throw new RuntimeException("Custom view must not be null!");
        }
        return this;
    }


    public MumLoadingDialog setCancellable(boolean isCancellable) {
        mProgressDialog.setCancelable(isCancellable);
        return this;
    }


    public MumLoadingDialog setAutoDismiss(boolean isAutoDismiss) {
        mIsAutoDismiss = isAutoDismiss;
        return this;
    }

    public MumLoadingDialog show() {
        if (!isShowing()) {
            mProgressDialog.show();
        }
        return this;
    }

    public boolean isShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    public void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private class ProgressDialog extends Dialog {

        private Determinate mDeterminateView;
        private Indeterminate mIndeterminateView;
        private View mView;
		private TextView mLabelText;
        private TextView mDetailsText;
        private String mLabel;
        private String mDetailsLabel;
        private FrameLayout mCustomViewContainer;
        private BackgroundLayout mBackgroundLayout;
        private int mWidth, mHeight;
		
        public ProgressDialog(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.kprogresshud_hud);

            Window window = getWindow();
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.dimAmount = mDimAmount;
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);

            setCanceledOnTouchOutside(false);

            initViews();
        }

        private void initViews() {
            mBackgroundLayout = (BackgroundLayout) findViewById(R.id.background);
            mBackgroundLayout.setBaseColor(mWindowColor);
            mBackgroundLayout.setCornerRadius(mCornerRadius);
            if (mWidth != 0) {
                updateBackgroundSize();
            }

            mCustomViewContainer = (FrameLayout) findViewById(R.id.container);
            addViewToFrame(mView);

            if (mDeterminateView != null) {
                mDeterminateView.setMax(mMaxProgress);
            }
            if (mIndeterminateView != null) {
                mIndeterminateView.setAnimationSpeed(mAnimateSpeed);
            }

            mLabelText = (TextView) findViewById(R.id.label);
            if (mLabel != null) {
                mLabelText.setText(mLabel);
                mLabelText.setVisibility(View.VISIBLE);
            } else {
                mLabelText.setVisibility(View.GONE);
            }
            mDetailsText = (TextView) findViewById(R.id.details_label);
            if (mDetailsLabel != null) {
                mDetailsText.setText(mDetailsLabel);
                mDetailsText.setVisibility(View.VISIBLE);
            } else {
                mDetailsText.setVisibility(View.GONE);
            }
        }

        private void addViewToFrame(View view) {
            if (view == null) return;
            int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wrapParam, wrapParam);
            mCustomViewContainer.addView(view, params);
        }

        private void updateBackgroundSize() {
            ViewGroup.LayoutParams params = mBackgroundLayout.getLayoutParams();
            params.width = Helper.dpToPixel(mWidth, getContext());
            params.height = Helper.dpToPixel(mHeight, getContext());
            mBackgroundLayout.setLayoutParams(params);
        }

        public void setProgress(int progress) {
            if (mDeterminateView != null) {
                mDeterminateView.setProgress(progress);
                if (mIsAutoDismiss && progress >= mMaxProgress) {
                    dismiss();
                }
            }
        }

        public void setView(View view) {
            if (view != null) {
                if (view instanceof Determinate) {
                    mDeterminateView = (Determinate) view;
                }
                if (view instanceof Indeterminate) {
                    mIndeterminateView = (Indeterminate) view;
                }
                mView = view;
                if (isShowing()) {
                    mCustomViewContainer.removeAllViews();
                    addViewToFrame(view);
                }
            }
        }

        public void setLabel(String label) {
            mLabel = label;
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText.setText(label);
                    mLabelText.setVisibility(View.VISIBLE);
                } else {
                    mLabelText.setVisibility(View.GONE);
                }
            }
        }

        public void setDetailsLabel(String detailsLabel) {
            mDetailsLabel = detailsLabel;
            if (mDetailsText != null) {
                if (detailsLabel != null) {
                    mDetailsText.setText(detailsLabel);
                    mDetailsText.setVisibility(View.VISIBLE);
                } else {
                    mDetailsText.setVisibility(View.GONE);
                }
            }
        }

        public void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
            if (mBackgroundLayout != null) {
                updateBackgroundSize();
            }
        }
    }
}
