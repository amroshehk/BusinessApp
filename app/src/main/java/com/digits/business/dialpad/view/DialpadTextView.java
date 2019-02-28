package com.digits.business.dialpad.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
//import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

public class DialpadTextView extends AppCompatTextView {
    private Rect mTextBounds = new Rect();
    private String mTextStr;

    public DialpadTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
       // super.draw(canvas);
        Paint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        canvas.drawText(mTextStr, -mTextBounds.left, -mTextBounds.top, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTextStr = getText().toString();
        getPaint().getTextBounds(mTextStr, 0, mTextStr.length(), mTextBounds);
        int width = resolveSize(mTextBounds.width(), widthMeasureSpec);
        int height = resolveSize(mTextBounds.height(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}