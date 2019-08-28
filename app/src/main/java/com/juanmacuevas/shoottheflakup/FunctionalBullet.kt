package com.juanmacuevas.shoottheflakup

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.DisplayMetrics
import androidx.core.util.Pair


class FunctionalBullet(
    res: Resources,
    power: Int,
    angle: Float,
    bulletOrigin: Pair<Int, Int>?,
    metrics: DisplayMetrics
) : GraphicComponent(res, metrics) {

    companion object {
        private val STATUS_FLYING = 0
        private val STATUS_BOOM = 1
        private val STATUS_OVER = 2
        private val BULLET_RADIUS = 4
        private val TIME_EXPLODING: Long = 100
    }

    private var time: Long = 0

    private var status: Int = 0
    private val posX0: Float
    private val posY0: Float
    var posX: Float = 0.toFloat()
        private set
    var posY: Float = 0.toFloat()
        private set
    private val iniSpeedX: Float
    private val iniSpeedY: Float
    private val tankBottom: Float
    private val paint: Paint

    private var explodingTimer: Long = 0

    val isExploding: Boolean
        get() = status == STATUS_BOOM

    val isOver: Boolean
        get() = status == STATUS_OVER


    val isFlying: Boolean
        get() = status == STATUS_FLYING


    init {
        time = 0
        paint = Paint()
        posX0 = bulletOrigin?.first!!.toFloat()
        posY0 = bulletOrigin?.second!!.toFloat()
        iniSpeedY = (Math.sin(angle.toDouble()) * power.toDouble() * 1.5).toFloat()
        iniSpeedX = (Math.cos(angle.toDouble()) * power.toDouble() * 1.5).toFloat()


        this.tankBottom = metrics.heightPixels - FuncionalTank.TANK_BOTTOM_MARGIN * metrics.scale()

    }

    override fun draw(c: Canvas) {
        if (status == STATUS_FLYING)
            paint.color = Color.BLACK
        else
            paint.color = Color.RED
        c.drawCircle(posX, posY, BULLET_RADIUS * scale, paint)

    }

    override fun update(elapsedTime: Long) {
        when (status) {
            STATUS_FLYING -> {
                time += elapsedTime
                val t2 = time.toDouble() / 100
                posX = (posX0 + iniSpeedX * t2).toFloat()
                posY = (posY0 - (iniSpeedY * t2 - 9.8 * Math.pow(t2, 2.0) / 2)).toFloat()

                if (posY > tankBottom) {

                    status = STATUS_BOOM
                    posY = tankBottom
                    explodingTimer = 0

                }
            }

            STATUS_BOOM -> {
                explodingTimer += elapsedTime
                if (explodingTimer > TIME_EXPLODING)
                    status = STATUS_OVER
            }
        }
    }

    fun setImpact() {

        status = STATUS_BOOM
        explodingTimer = 0
    }



}
