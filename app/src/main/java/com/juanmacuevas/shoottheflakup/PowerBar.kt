package com.juanmacuevas.shoottheflakup

import android.graphics.*
import android.graphics.Paint.Style
import android.util.DisplayMetrics

class PowerBar(dm: DisplayMetrics) : Renderable {


    internal var scale = dm.densityDpi.toFloat() / 160
    private val barPwrLeft = BAR_POWER_LEFT_MARGIN * scale

    private val barPwrTop = dm.heightPixels.toFloat() - BAR_POWER_BOTTOM_MARGIN * scale - BAR_POWER_HEIGHT * scale
    private val barPwrRight = dm.widthPixels - BAR_POWER_RIGHT_MARGIN * scale
    private val barPwrBottom = dm.heightPixels - BAR_POWER_HEIGHT * scale
    internal var gradient= LinearGradient(
        barPwrLeft,
        barPwrTop,
        barPwrRight,
        barPwrTop,
        intArrayOf(Color.GREEN, Color.YELLOW, Color.RED),
        null,
        Shader.TileMode.CLAMP
    )
    internal var paint = Paint()
    private var power: Int = 0

    override fun draw(c: Canvas) {

        val progress = (barPwrLeft + power * (barPwrRight - barPwrLeft) / 100).toInt()
        paint.alpha = 255
        paint.shader = gradient
        paint.style = Style.FILL
        c.drawRoundRect(RectF(barPwrLeft, barPwrTop, progress.toFloat(), barPwrBottom), 4 * scale, 4 * scale, paint)
        paint.shader = null
        //paint the stroke
        paint.style = Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 0f
        c.drawRoundRect(RectF(barPwrLeft, barPwrTop, barPwrRight, barPwrBottom), 4 * scale, 4 * scale, paint)

    }

    override fun update(elapsedTime: Long) {}

    fun setData(data: GameData) {
        power = data.power
    }

    companion object {

        private val BAR_POWER_LEFT_MARGIN = 116
        private val BAR_POWER_RIGHT_MARGIN = 10
        private val BAR_POWER_BOTTOM_MARGIN = 10
        private val BAR_POWER_HEIGHT = 10
    }


}
