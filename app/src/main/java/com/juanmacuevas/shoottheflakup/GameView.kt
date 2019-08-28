package com.juanmacuevas.shoottheflakup

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    var thread: GameThread? = null
    private var hasActiveHolder = false

    init {
        val holder = holder
        holder.addCallback(this)
        isFocusable = true
        isFocusableInTouchMode = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread!!.setRunning(true)
        thread!!.start()
        hasActiveHolder = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        hasActiveHolder = false
        thread?.setRunning(false)
    }


    fun prepareCanvasAndDraw(gameThread: GameThread) {
        val c = holder.lockCanvas()
        if (c != null) {
            gameThread.doDraw(c)
        }
        if (hasActiveHolder) {
            holder.unlockCanvasAndPost(c)
        }
    }
}



