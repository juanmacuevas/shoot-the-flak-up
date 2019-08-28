package com.juanmacuevas.shoottheflakup

import android.content.Context
import android.graphics.Canvas
import android.os.Vibrator
import android.util.DisplayMetrics
import android.view.MotionEvent
import androidx.core.util.Pair

class GameThread(context: Context, private val gameView: GameView, metrics: DisplayMetrics) : Thread(), GameEvents {

    private val soundManager = SoundManager(context)
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private val gameData = GameData()

    private val powerBar = PowerBar(metrics)
    private val infoText = InfoText(context.resources, metrics)
    private val tank = FuncionalTank(metrics, this, context.resources)
    private val aircraftsControl= AircraftsControl(context.resources, metrics, this)
    private val bulletsControl = BulletsControl(context.resources, metrics)

    private val landscape = Landscape(context.resources, metrics)
    private val inputController: InputController = InputController(this)

    private var readyToDraw: Boolean = false
    private var lastUpdateTime = 0L

    init {
        gameView.setThread(this)
    }

    override fun run() {
        while (readyToDraw) {
            inputController.processEvents()
            val delta = updateCurrentTimeAndCalculateDelta()
            updatePhysics(delta)
            gameView.prepareCanvasAndDraw(this)
            soundManager.update(delta)
        }
        soundManager.pauseMusic()
    }

    private fun updateCurrentTimeAndCalculateDelta(): Long {
        val currentTime = System.currentTimeMillis()
        val delta = currentTime - lastUpdateTime
        lastUpdateTime = currentTime
        return delta
    }

    private fun updatePhysics(timer: Long) {
        tank.update(timer)
        bulletsControl.update(timer)
        aircraftsControl.update(timer, bulletsControl.iterable())
        gameData.power = tank.power
        powerBar.setData(gameData)
        infoText.setData(gameData)
    }

    fun doDraw(canvas: Canvas) {
        landscape.draw(canvas)
        aircraftsControl.draw(canvas)
        bulletsControl.draw(canvas)
        tank.draw(canvas)
        powerBar.draw(canvas)
        infoText.draw(canvas)
    }

    fun setRunning(b: Boolean) {
        readyToDraw = b
    }

    override fun shootBullet(angle: Float, power: Int, bulletOrigin: Pair<Int, Int>) {
        bulletsControl.addBullet(power, angle, bulletOrigin)
        soundManager.playShoot()
    }

    override fun angleChanged(angle: Float) {
        gameData.angle = angle
        soundManager.playMovegun()
    }

    override fun aircraftExploded() {
        gameData.impacts = gameData.impacts + 1
        soundManager.playExplode()
        vibrator.vibrate(100)
    }

    fun inputEvent(event: MotionEvent) {
        inputController.addNewEvent(event)
    }

    fun initFiring() {
        tank.initFiring()
    }

    fun setTarget(x: Int, y: Int) {
        tank.setTarget(x, y)
    }

    fun doFire() {
        tank.doFire()
    }
}
