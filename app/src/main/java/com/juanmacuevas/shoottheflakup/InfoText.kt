package com.juanmacuevas.shoottheflakup

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.DisplayMetrics

class InfoText(res: Resources, metrics: DisplayMetrics) : GraphicComponent(res, metrics) {

    private val paint = Paint()
    private var power: Int = 0
    private var angle: Int = 0
    private var impacts: Int = 0

    override fun draw(c: Canvas) {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0f
        paint.isAntiAlias = true
        paint.textSize = TEXT_SIZE * scale

        c.drawText("Angle: $angle°", TEXT_INFO_LEFT_MARGIN * scale, TEXT_ANGLE_TOP_MARGIN * scale, paint)
        c.drawText("Power: $power", TEXT_INFO_LEFT_MARGIN * scale, TEXT_POWER_TOP_MARGIN * scale, paint)
        c.drawText("Impacts: $impacts", TEXT_INFO_LEFT_MARGIN * scale, TEXT_COUNTER_TOP_MARGIN * scale, paint)

    }

    override fun update(elapsedTime: Long) {

    }

    fun setData(data: GameData) {
        power = data.power
        angle = (data.angle * 180 / Math.PI).toInt()
        impacts = data.impacts


    }

    companion object {

        private val TEXT_INFO_LEFT_MARGIN = 10
        private val TEXT_ANGLE_TOP_MARGIN = 25
        private val TEXT_POWER_TOP_MARGIN = 50
        private val TEXT_COUNTER_TOP_MARGIN = 75
        private val TEXT_SIZE = 20
    }
}
