package com.sudip.drumkitapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrumBeatView extends View {
    Paint paint = new Paint();
    private Integer mHeight;
    private List<Integer> drumPosition;
    private List<List<Integer>> singleDrumPosition;
    private int positionScale = 5;

    public DrumBeatView(Context context) {
        super(context);
        initData();
    }

    public DrumBeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public DrumBeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        drumPosition = new ArrayList<>();
        singleDrumPosition = new ArrayList<>();
        drumPosition.add(50);
        List<Integer> list = new ArrayList<>();
        list.add(50);
        singleDrumPosition.add(list);


        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(Utils.dp2px(4));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.SQUARE);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xff2196F3);
        for (int i = 0; i < drumPosition.size(); i++) {
            for (Integer integer : singleDrumPosition.get(i)) {
                canvas.drawLine(integer + 10, (mHeight - 20f) / 100 * integer, integer + 10 + Utils.dp2px(1.5f), (mHeight - 20f) / 100 * integer, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        System.out.println("控件高度：" + heightSize);
        int width;
        if (drumPosition.size() == 0) {
            width = 0;
        } else {
            width = drumPosition.get(drumPosition.size() - 1) * positionScale + 20;
        }
        mHeight = heightSize;
        setMeasuredDimension(width, heightSize);
    }

    public int getPositionScale() {
        return positionScale;
    }

    public void setPositionScale(int positionScale) {
        this.positionScale = positionScale;
    }
}
