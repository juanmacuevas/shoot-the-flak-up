package com.juanmacuevas.shoottheflakup

import java.util.concurrent.ArrayBlockingQueue

import android.view.KeyEvent
import android.view.MotionEvent

class InputObject(internal var pool: ArrayBlockingQueue<InputObject>) {
    internal var eventType: Byte = 0
    internal var time: Long = 0
    internal var action: Int = 0
    internal var keyCode: Int = 0
    internal var x: Int = 0
    internal var y: Int = 0

    fun useEvent(event: KeyEvent) {
        eventType = EVENT_TYPE_KEY
        val a = event.action
        when (a) {
            KeyEvent.ACTION_DOWN -> action = ACTION_KEY_DOWN
            KeyEvent.ACTION_UP -> action = ACTION_KEY_UP
            else -> action = 0
        }
        time = event.eventTime
        keyCode = event.keyCode
    }

    fun useEvent(event: MotionEvent) {
        eventType = EVENT_TYPE_TOUCH
        val a = event.action
        when (a) {
            MotionEvent.ACTION_DOWN -> action = ACTION_TOUCH_DOWN
            MotionEvent.ACTION_MOVE -> action = ACTION_TOUCH_MOVE
            MotionEvent.ACTION_UP -> action = ACTION_TOUCH_UP
            else -> action = 0
        }
        time = event.eventTime
        x = event.x.toInt()
        y = event.y.toInt()
    }

    fun useEventHistory(event: MotionEvent, historyItem: Int) {
        eventType = EVENT_TYPE_TOUCH
        action = ACTION_TOUCH_MOVE
        time = event.getHistoricalEventTime(historyItem)
        x = event.getHistoricalX(historyItem).toInt()
        y = event.getHistoricalY(historyItem).toInt()
    }

    fun returnToPool() {
        pool.add(this)
    }

    companion object {
        internal val EVENT_TYPE_KEY: Byte = 1
        internal val EVENT_TYPE_TOUCH: Byte = 2
        internal val ACTION_KEY_DOWN = 1
        internal val ACTION_KEY_UP = 2
        internal val ACTION_TOUCH_DOWN = 3
        internal val ACTION_TOUCH_MOVE = 4
        internal val ACTION_TOUCH_UP = 5
    }
}