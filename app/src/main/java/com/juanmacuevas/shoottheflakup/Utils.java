package com.juanmacuevas.shoottheflakup;

import android.util.DisplayMetrics;

class Utils {

    private Utils(){}

    public static float scale(DisplayMetrics metrics) {
        return ((float)metrics.densityDpi) / 160;
    }
}
