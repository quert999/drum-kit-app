package com.sudip.drumkitapp;

import android.content.res.Resources;

class Utils {
    static int dp2px(float dpVal) {
        return (int) (0.5f + dpVal * Resources.getSystem().getDisplayMetrics().density);
    }
}
