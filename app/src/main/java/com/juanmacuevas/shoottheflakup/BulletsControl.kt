package com.juanmacuevas.shoottheflakup

import android.content.res.Resources
import android.graphics.Canvas
import android.util.DisplayMetrics
import androidx.core.util.Pair

import java.util.ArrayList

internal class BulletsControl(private val res: Resources, private val metrics: DisplayMetrics) {
    private val bullets = ArrayList<FunctionalBullet>()

    fun draw(canvas: Canvas) {
        val it = bullets.iterator()
        while (it.hasNext()) {
            val b = it.next()
            b.draw(canvas)
        }

    }

    fun update(timer: Long) {
        var b: FunctionalBullet
        var i = 0
        while (!bullets.isEmpty() && bullets.size > i) {
            b = bullets[i]
            if (b.isOver)
                bullets.removeAt(i)
            else {
                b.update(timer)
                i++
            }
        }
    }

    fun addBullet(power: Int, angle: Float, bulletOrigin: Pair<Int, Int>?) {
        bullets.add(FunctionalBullet(res, power, angle, bulletOrigin, metrics))

    }

    fun iterable(): Iterable<FunctionalBullet> {
        return bullets
    }
}
