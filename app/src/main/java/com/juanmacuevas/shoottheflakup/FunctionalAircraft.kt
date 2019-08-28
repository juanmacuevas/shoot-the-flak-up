package com.juanmacuevas.shoottheflakup

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.DisplayMetrics

class FunctionalAircraft(res: Resources, metrics: DisplayMetrics) : GraphicComponent(res, metrics) {

    private val STATUS_FLYING = 0
    private val STATUS_BOOM = 1
    private val STATUS_OVER = 2

    private val AIRCRAFT_WIDTH = (127 * 0.35).toFloat()
    private val AIRCRAFT_HEIGHT = (134 * 0.35).toFloat()

    private val TIME_FLYING = 5f
    private val TIME_EXPLODING: Long = 800
    companion object {
        private var aircraftImg: Bitmap? = null
        private var aircraftDownImg: Bitmap? = null
    }

    private var currentImg: Bitmap? = null
    private var timeFlying: Long = 0

    private var status: Int = 0
    private var posX: Float = 0.toFloat()
    private var posY: Float = 0.toFloat()
    private var iniSpeedX: Double = 0.toDouble()
    private var iniSpeedY: Double = 0.toDouble()
    private var initPointX: Float = 0.toFloat()
    private var posY0: Float = 0.toFloat()

    private var drawX: Float = 0.toFloat()
    private var drawY: Float = 0.toFloat()

    private var angle: Int = 0

    private var leftOrRight: Int = 0
    private var acceleration: Float = 0.toFloat()

    private var explodingTimer: Long = 0


    val isOver: Boolean
        get() = status == STATUS_OVER

    val isFlying: Boolean
        get() = status == STATUS_FLYING

    init {

        timeFlying = 0
        angle = 0
        status = STATUS_FLYING
        initValues(metrics)

        Companion.aircraftImg = initBitmap(Companion.aircraftImg, R.drawable.aircraft, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT)
        Companion.aircraftDownImg = initBitmap(Companion.aircraftDownImg, R.drawable.aircraftdown, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT)
        currentImg = aircraftImg
    }

    private fun initValues(metrics: DisplayMetrics) {
        //random values
        leftOrRight = if (Math.random() < 0.5) 1 else -1
        initPointX =
            (metrics.widthPixels / 2 + metrics.widthPixels.toDouble() * (-this.leftOrRight).toDouble() * Math.random()).toFloat()
        val lowerPosition = metrics.heightPixels
        acceleration = (lowerPosition / Math.pow((TIME_FLYING / 2).toDouble(), 2.0)).toFloat()
        iniSpeedX = (metrics.widthPixels / TIME_FLYING).toDouble()
        iniSpeedY = (TIME_FLYING * acceleration / 2 - 1 / TIME_FLYING).toDouble()

    }

    override fun draw(c: Canvas) {

        val m = matrixTranslateAndMove(drawX, drawY, angle.toFloat(), posX, posY)
        c.drawBitmap(currentImg!!, m, null)

    }

    override fun update(elapsedTime: Long) {

        if (status == STATUS_FLYING) {
            handleFlying(elapsedTime)
        } else if (status == STATUS_BOOM) {
            handleExploded(elapsedTime)
        }
        drawX = (posX - AIRCRAFT_WIDTH * 0.375 * scale).toFloat()
        drawY = posY - AIRCRAFT_HEIGHT / 2 * scale
    }


    private fun handleFlying(elapsedTime: Long) {
        timeFlying += elapsedTime
        val timeFlyingSeconds = timeFlying.toDouble() / 1000
        posX = (initPointX + iniSpeedX * timeFlyingSeconds * leftOrRight.toDouble()).toFloat()
        posY = (iniSpeedY * timeFlyingSeconds - acceleration / 2 * Math.pow(timeFlyingSeconds, 2.0)).toFloat()
        angle =
            (if (leftOrRight > 0) 180 else 0) + (Math.atan((iniSpeedY - acceleration * timeFlyingSeconds) / iniSpeedX * leftOrRight) * 180 / Math.PI).toInt()
        if (posY < 0) status = STATUS_OVER

    }

    private fun handleExploded(elapsedTime: Long) {
        explodingTimer += elapsedTime
        val t2 = explodingTimer.toDouble() / 1000
        posX = (initPointX + iniSpeedX * t2 * leftOrRight.toDouble()).toFloat()
        posY = (posY0.toDouble() + iniSpeedY * t2 + 7.0 / 2 * Math.pow(t2, 2.0)).toFloat()
        if (explodingTimer > TIME_EXPLODING)
            status = STATUS_OVER
    }

    fun setImpact() {

        status = STATUS_BOOM
        currentImg = aircraftDownImg
        explodingTimer = 0
        initPointX = posX
        posY0 = posY
        iniSpeedY = iniSpeedY - acceleration * explodingTimer / 1000


    }

    fun impactDetected(b: FunctionalBullet): Boolean {
        var impact = false
        val diffX = Math.abs(posX - b.posX)
        val diffY = Math.abs(posY - b.posY)
        if (diffX < 19 && diffY < 19 * scale) {
            impact = true
        } else
            impact = false
        return impact
    }



}



