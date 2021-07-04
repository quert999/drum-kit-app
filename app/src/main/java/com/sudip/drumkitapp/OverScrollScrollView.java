package com.sudip.drumkitapp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import java.lang.reflect.Field;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class OverScrollScrollView extends HorizontalScrollView {

    public OverScrollScrollView(Context context) {
        super(context);
        initData();
    }

    public OverScrollScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public OverScrollScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public OverScrollScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData();
    }

    private void initData() {
        try {
            Class selfClass = OverScrollScrollView.class.getSuperclass();
            Field field = selfClass.getDeclaredField("mOverscrollDistance");
            field.setAccessible(true);
            int overScrollDistance = 500;
            field.set(this, overScrollDistance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev != null) {
            //解决超过边界松手不回弹得问题
            if (ev.getAction() == (MotionEvent.ACTION_UP | MotionEvent.ACTION_CANCEL)) {
                int xMax = getXMaxScrollRange();
                if (getScrollX() < 0) {
                    scrollTo(0, 0);
                } else if (getScaleX() > xMax) {
                    scrollTo(xMax, 0);
                }
            }
        }
        return super.onTouchEvent(ev);
    }

    private int getXMaxScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0, child.getWidth() - (getWidth() - getPaddingLeft() - getPaddingRight()));
        }
        return scrollRange;
    }
}
