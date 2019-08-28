package com.juanmacuevas.shoottheflakup

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.DisplayMetrics

internal class Landscape(res: Resources, metrics: DisplayMetrics) {
    private var mBackgroundImage: Bitmap? = null

    init {
        mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.background)
        mBackgroundImage = Bitmap.createScaledBitmap(
            mBackgroundImage!!, metrics.widthPixels, metrics.heightPixels, true
        )
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(mBackgroundImage!!, 0f, 0f, null)
    }
}
