package com.juanmacuevas.shoottheflakup

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.Window

class GameActivity : Activity() {

    private var game: GameThread? = null
    private val inputController: InputController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.main)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val gameView = findViewById<GameView>(R.id.game)

        game = GameThread(this, gameView, metrics)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        game!!.inputEvent(event)
        return true
    }

    override fun onStop() {
        super.onStop()
        game!!.setRunning(false)
    }

}

