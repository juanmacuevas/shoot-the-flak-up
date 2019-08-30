package com.juanmacuevas.shoottheflakup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics


fun DisplayMetrics.scale(): Float {
    return this.densityDpi.toFloat() / 160
}


