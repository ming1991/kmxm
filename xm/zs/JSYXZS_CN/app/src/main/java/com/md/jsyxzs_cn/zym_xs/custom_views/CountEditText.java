package com.md.jsyxzs_cn.zym_xs.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.widget.EditText;

import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.utils.DpPxUtil;

/**
 * Created by KMJD on 2017/2/11.
 */

public class CountEditText extends EditText {


    //最多输入字数
    private int maxLength = 300;
    public CountEditText(Context context) {
        super(context);
        init();
    }



    public CountEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        this.setPadding(DpPxUtil.getPxByDp(8),DpPxUtil.getPxByDp(8),DpPxUtil.getPxByDp(8),DpPxUtil.getPxByDp(25));
    }

    private int textCount;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String contents = textCount+"/"+maxLength;
        //设置最多输入的字数
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.little_background_color));
        Rect rect = new Rect(3,getHeight()-DpPxUtil.getPxByDp(24),getWidth()-DpPxUtil.getPxByDp(2), getHeight()-DpPxUtil.getPxByDp(3));
        canvas.drawRect(rect,paint);
        Paint countPaint = new Paint();
        countPaint.getTextBounds(contents,0,contents.length(),rect);
        countPaint.setTextSize(DpPxUtil.getPxByDp(14));
        countPaint.setColor(getResources().getColor(R.color.shallow_text_color));
        canvas.drawText(contents,getWidth()-DpPxUtil.getPxByDp(60), getHeight()-DpPxUtil.getPxByDp(8),countPaint);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        textCount = this.length();
        invalidate();
    }
}
