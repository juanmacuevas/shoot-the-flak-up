package com.juanmacuevas.shoottheflakup;

import android.util.DisplayMetrics;

class Utils {

    public static float scale(DisplayMetrics metrics) {
        return ((float)metrics.densityDpi) / 160;
    }
}
