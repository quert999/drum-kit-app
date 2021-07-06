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
    private int timeLength = 0;   //Unit: 100 ms
    private final List<SingleRecordBean> drumPosition = new ArrayList<>();
    private int positionScale = 10; // 10ms per px

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
        for (SingleRecordBean singleRecordBean : drumPosition) {
            long singleTime = singleRecordBean.recordTime;
            float ruleTime = singleTime / 10f; // 10ms per px
            int poolId = singleRecordBean.poolId;
            canvas.drawLine(ruleTime + Utils.dp2px(5), (mHeight - 20f) / 6 * poolId, ruleTime + Utils.dp2px(6.5f), (mHeight - 20f) / 6 * poolId, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        System.out.println("控件高度：" + heightSize);
        int width = timeLength * positionScale + Utils.dp2px(10);
        mHeight = heightSize;
        setMeasuredDimension(width, heightSize);
    }

    public int getPositionScale() {
        return positionScale;
    }

    public void setPositionScale(int positionScale) {
        this.positionScale = positionScale;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }

    public List<SingleRecordBean> getDrumPosition() {
        return drumPosition;
    }

    public void setDrumPosition(List<SingleRecordBean> drumPosition) {
        this.drumPosition.clear();
        this.drumPosition.addAll(drumPosition);
    }

    long lastTime;
    List<SingleRecordBean> playedList = new ArrayList<>();
    public void onTime(long offset){
        if (Math.abs(offset - lastTime) > timeLength*100/2){
            playedList.clear();
        }
        lastTime = offset;
        for (SingleRecordBean singleRecordBean : drumPosition) {
            if (Math.abs(singleRecordBean.recordTime - offset) < 20 && !playedList.contains(singleRecordBean)){
                SoundPoolUtil.playByPoolId(singleRecordBean.poolId);
                playedList.add(singleRecordBean);
            }
        }
    }
}
