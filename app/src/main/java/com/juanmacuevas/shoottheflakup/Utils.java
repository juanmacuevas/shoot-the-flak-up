package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

class Utils {

    private Utils(){}

    public static float scale(DisplayMetrics metrics) {
        return ((float)metrics.densityDpi) / 160;
    }

    public static Bitmap loadBitmap(Resources res, int imageResource, int w, int h, float scale) {
        Bitmap tmp = BitmapFactory.decodeResource(res, imageResource);
        return Bitmap.createScaledBitmap(tmp, (int) (w * scale), (int) (h * scale), true);
    }
}
