package com.juanmacuevas.shoottheflakup

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.DisplayMetrics

abstract class GraphicComponent(private val res: Resources, metrics: DisplayMetrics) : Renderable {

    protected val scale = metrics.scale()

    protected fun initBitmap(image: Bitmap?, resource: Int, width: Float, height: Float): Bitmap {
        var image = image
        if (image == null) {
            image = BitmapFactory.decodeResource(res, resource)
            image = Bitmap.createScaledBitmap(image!!, (width * scale).toInt(), (height * scale).toInt(), true)
        }
        return image!!

    }

    protected fun matrixTranslateAndMove(
        transX: Float,
        transY: Float,
        angle: Float,
        pivotX: Float,
        pivotY: Float
    ): Matrix {
        val m = Matrix()
        m.postTranslate(transX, transY)
        m.postRotate(angle, pivotX, pivotY)
        return m
    }
}
