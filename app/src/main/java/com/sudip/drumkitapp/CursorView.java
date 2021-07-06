package com.sudip.drumkitapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CursorView extends View {
    private int length;
    private long startTime;
    private Paint paint = new Paint();
    onTimeListener onTimeListener;
    private long lengthExact;

    public CursorView(Context context) {
        super(context);
        initData();
    }

    public CursorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public CursorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(Utils.dp2px(1.5f));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long now = System.currentTimeMillis();
        if (length == 0) return;
        if (onTimeListener != null) {
            onTimeListener.onTime((now - startTime) % lengthExact);
        }
        long offset = (now - startTime) % (length * 100);
        canvas.drawLine(Utils.dp2px(5) + offset / 10f, 0, Utils.dp2px(5) + offset / 10f, getHeight(), paint);

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 10ms per px
        setMeasuredDimension(length * 10 + Utils.dp2px(10), heightSize);
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setLengthExact(long lengthExact) {
        this.lengthExact = lengthExact;
    }

    interface onTimeListener {
        void onTime(long offset);
    }

    public void setOnTimeListener(CursorView.onTimeListener onTimeListener) {
        this.onTimeListener = onTimeListener;
    }
}
