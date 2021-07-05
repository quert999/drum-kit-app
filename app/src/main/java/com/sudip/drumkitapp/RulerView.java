package com.sudip.drumkitapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RulerView extends View {
    private int mHeight;
    private int rulerLength = 0;   //Unit: 100 ms
    Paint paint = new Paint();
    Paint paintLineLarge = new Paint();
    Paint paintLineMedium = new Paint();
    Paint paintLineSmall = new Paint();

    public RulerView(Context context) {
        super(context);
        initData();
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        List<Paint> paints = new ArrayList<>();
        paints.add(paint);
        paints.add(paintLineLarge);
        paints.add(paintLineMedium);
        paints.add(paintLineSmall);
        for (Paint paintTmp : paints) {
            paintTmp.setAntiAlias(true);
            paintTmp.setStyle(Paint.Style.FILL);
            paintTmp.setStrokeCap(Paint.Cap.ROUND);
        }
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(Utils.dp2px( 1));
        paintLineLarge.setColor(Color.WHITE);
        paintLineLarge.setStrokeWidth(Utils.dp2px( 1.5f));
        paintLineMedium.setColor(0xffeeeeee);
        paintLineMedium.setStrokeWidth(Utils.dp2px( 1.1f));
        paintLineSmall.setColor(0xffcccccc);
        paintLineSmall.setStrokeWidth(Utils.dp2px( 1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < rulerLength; i++) {
            float x = Utils.dp2px( 5) + i * 10;
            float yStartLong = (mHeight - Utils.dp2px( 5)) / 5f + Utils.dp2px( 5);
            float yStartMedium = (mHeight - Utils.dp2px( 5)) * 2 / 5f + Utils.dp2px( 5);
            float yStartMin = (mHeight - Utils.dp2px( 5)) * 3 / 5f + Utils.dp2px( 5);
            float yEnd = (mHeight - Utils.dp2px( 5)) * 4 / 5f + Utils.dp2px( 5);
            ;
            if (i % 8 == 0) {
                canvas.drawLine(x, yStartLong, x, yEnd, paintLineLarge);
                canvas.drawText((i / 8 + 1) + "", x + Utils.dp2px( 1), yStartLong - Utils.dp2px( 3), paint);
            } else if (i % 2 == 0) {
                canvas.drawLine(x, yStartMedium, x, yEnd, paintLineMedium);
            } else {
                canvas.drawLine(x, yStartMin, x, yEnd, paintLineSmall);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        System.out.println("控件高度：" + heightSize);
        mHeight = heightSize;
        // 10ms per px
        setMeasuredDimension(rulerLength * 10 + Utils.dp2px( 10), heightSize);
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public int getRulerLength() {
        return rulerLength;
    }

    public void setRulerLength(int rulerLength) {
        this.rulerLength = rulerLength;
    }
}
