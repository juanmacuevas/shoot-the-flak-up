package com.juanmacuevas.shoottheflakup

import androidx.core.util.Pair

internal interface GameEvents {
    fun angleChanged(angle: Float)
    fun aircraftExploded()
    fun shootBullet(angle: Float, i: Int, bulletOrigin: Pair<Int, Int>?)
}
