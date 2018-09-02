package com.kmjd.jsylc.zxh.utils;


import android.content.Context;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.kmjd.jsylc.zxh.R;

public class KeyboardNum implements View.OnTouchListener {
    private Context mContext;
    private KeyboardView mKeyboardView;
    private Keyboard key;
    private EditText ed;
    private View mView;
    private boolean isM2W;

    public KeyboardNum(Context context, View view, EditText editText, Boolean M2W) {
        this.isM2W = M2W;
        this.mContext = context;
        mKeyboardView = view.findViewById(R.id.keyboard_view);
        this.ed = editText;
        mKeyboardView = view.findViewById(R.id.keyboard_view);
        mView = view.findViewById(R.id.view_sp);
        ed.setInputType(InputType.TYPE_NULL);//不然会弹出系统键盘
        ed.setOnTouchListener(this);
        ed.addTextChangedListener(textWatcherListener);
        key = new Keyboard(context, R.xml.key_board);
        mKeyboardView.setKeyboard(key);
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(listener);
    }

    public void setM2W(boolean m2W) {
        isM2W = m2W;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isM2W) {
                    if (mKeyboardView.getVisibility() == View.INVISIBLE || mKeyboardView.getVisibility() == View.GONE) {
                        mKeyboardView.setVisibility(View.VISIBLE);
                        mView.setVisibility(View.GONE);
                    } else {
                        mKeyboardView.setVisibility(View.GONE);
                        mView.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
        return false;
    }

    /**
     * 额度互转输入框的内容监听
     */

    private TextWatcher textWatcherListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (isM2W) {
                ed.setTextSize(22.0f);
            } else {
                ed.setTextSize(15.0f);
            }
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)) {//获取焦点没有输入内容时依然给出提示语
                ed.setHint(mContext.getResources().getString(R.string.qqcp_edittext_hint));
                ed.setTypeface(Typeface.DEFAULT);
                ed.setTextSize(15.0f);
            } else {
                ed.setTypeface(Typeface.DEFAULT_BOLD);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();
            int len = s.toString().length();
            if (len == 1 && text.equals("0")) {
                s.clear();
            }
        }
    };
    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            switch (primaryCode) {
                case Keyboard.KEYCODE_CANCEL:
                    if (isM2W) {
                        mKeyboardView.setVisibility(View.GONE);
                        mView.setVisibility(View.VISIBLE);
                    }
                    break;
                case Keyboard.KEYCODE_DELETE:
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                    break;
                default:
                    editable.insert(start, Character.toString((char) primaryCode));
                    break;
            }
        }
    };


}
