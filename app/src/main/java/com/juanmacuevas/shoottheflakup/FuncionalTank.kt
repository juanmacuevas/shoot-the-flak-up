package com.juanmacuevas.shoottheflakup

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import androidx.core.util.Pair

class FuncionalTank(metrics: DisplayMetrics, thread: GameThread, res: Resources) : GraphicComponent(res, metrics) {

    private val thread: GameEvents
    private val shootOriginX: Float
    private val shootOriginY: Float
    private val tankLeft: Float
    private val tankTop: Float

    private var tankStatus: Int = 0
    private var milisecondsPowering: Long = 0
    private var bulletOrigin: Pair<Int, Int>? = null
    val TANK_BOTTOM_MARGIN = 7
    val TANK_LEFT_MARGIN = 0
    val TANK_HEIGHT = 55
    val TANK_WIDTH = 106

    val GUNBARREL_LENGTH = 100
    val GUNBARREL_WIDTH = 23

    val STATUS_IDLE = 0
    val STATUS_POWERING = 1
    val POWERING_TIMER_LIMIT: Long = 1200

    companion object {
        val TANK_BOTTOM_MARGIN = 7
        var tankImg: Bitmap? = null
        var gunBarrelImg: Bitmap? = null
    }

    private var angle: Float = 0.toFloat()
    var power: Int = 0
    private var lastBulletPower: Int = 0

    init {
        this.thread = thread
        tankStatus = STATUS_IDLE
        power = 0
        lastBulletPower = 0
        milisecondsPowering = 0
        tankLeft = TANK_LEFT_MARGIN * scale
        tankTop = metrics.heightPixels - (TANK_HEIGHT + TANK_BOTTOM_MARGIN) * scale
        shootOriginX = TANK_LEFT_MARGIN + 60f * scale
        shootOriginY = tankTop + 12 * scale
        setTarget(metrics.widthPixels, 0)

        Companion.tankImg = initBitmap(Companion.tankImg,R.drawable.tank, TANK_WIDTH.toFloat(), TANK_HEIGHT.toFloat())
        Companion.gunBarrelImg = initBitmap(Companion.gunBarrelImg, R.drawable.cannon, GUNBARREL_LENGTH.toFloat(), GUNBARREL_WIDTH.toFloat())
    }

    override fun update(elapsedTime: Long) {
        if (tankStatus == STATUS_POWERING) {
            milisecondsPowering = milisecondsPowering + elapsedTime
            if (milisecondsPowering > POWERING_TIMER_LIMIT)
                milisecondsPowering = 0

            power = (milisecondsPowering.toFloat() / POWERING_TIMER_LIMIT * 100).toInt()
        }
    }


    override fun draw(c: Canvas) {
        val m = matrixTranslateAndMove(
            tankLeft + 40 * scale,
            tankTop,
            (-angle * 180 / Math.PI).toFloat(),
            shootOriginX,
            shootOriginY
        )
        gunBarrelImg?.let { c.drawBitmap(it, m, null) }
        tankImg?.let { c.drawBitmap(it, tankLeft, tankTop, null) }

    }

    /**
     * Sets the gun barrel to point the specified coordinate
     *
     * @param x
     * @param y
     */
    fun setTarget(x: Int, y: Int) {
        var x = x
        var y = y
        if (x < shootOriginX) x = shootOriginX.toInt()
        if (y > shootOriginY) y = shootOriginY.toInt()
        if (x.toFloat() == shootOriginX && y.toFloat() == shootOriginY) x = x + 10

        val previousAngle = angle
        angle = calculateAngle(x, y)

        bulletOrigin = calculateBulletOrigin()

        if (angleChanged(previousAngle, angle)) {
            thread.angleChanged(angle)
        }

    }

    private fun calculateBulletOrigin(): Pair<Int, Int> {
        return Pair(
            (shootOriginX + Math.cos(angle.toDouble()) * (GUNBARREL_LENGTH - 30).toDouble() * scale.toDouble()).toInt(),
            (shootOriginY - Math.sin(angle.toDouble()) * (GUNBARREL_LENGTH - 30).toDouble() * scale.toDouble()).toInt()
        )
    }

    private fun calculateAngle(x: Int, y: Int): Float {
        return Math.abs(Math.atan(((y - shootOriginY) / (x - shootOriginX)).toDouble()).toFloat())
    }

    private fun angleChanged(before: Float, after: Float): Boolean {
        return 0 < Math.abs(((before - after) * 180 / Math.PI).toInt())

    }

    /**
     * called when the user press the screen. It starts the powering addNewEvent
     */
    fun initFiring() {
        tankStatus = STATUS_POWERING
        power = 0
        milisecondsPowering = 0
    }

    /**
     * called when the user release the finger and the shoot is performed
     */
    fun doFire() {
        thread.shootBullet(angle, 60 + power * 60 / 100, bulletOrigin)
        lastBulletPower = power
        tankStatus = STATUS_IDLE
        power = 0

    }




}
