package com.juanmacuevas.shoottheflakup

import android.graphics.Canvas

interface Renderable {
    fun draw(c: Canvas)
    fun update(elapsedTime: Long)

}
