package com.juanmacuevas.shoottheflakup

import android.util.DisplayMetrics


fun DisplayMetrics.scale(): Float {
    return this.densityDpi.toFloat() / 160
}

