package com.juanmacuevas.shoottheflakup

import android.view.MotionEvent
import java.util.concurrent.ArrayBlockingQueue

class InputController(private val game: GameThread) {

    private val INPUT_QUEUE_SIZE = 30
    private val inputObjectPool= ArrayBlockingQueue<InputObject>(INPUT_QUEUE_SIZE)
    private val inputQueue = ArrayBlockingQueue<InputObject>(INPUT_QUEUE_SIZE)
    private val inputQueueMutex = Any()


    init {
        inputObjectPool.addAll((1..INPUT_QUEUE_SIZE).map { InputObject(inputObjectPool) })
//        for (i in 0 until INPUT_QUEUE_SIZE) {
//            inputObjectPool.add(InputObject(inputObjectPool))
//        }
    }

    fun addNewEvent(event: MotionEvent) {

        val hist = event.historySize
        var input: InputObject?
        try {
            if (hist > 0) {
                // add from oldest to newest
                for (i in 0 until hist) {
                    input = null
                    input = inputObjectPool.take()
                    input!!.useEventHistory(event, i)
                    feedInput(input)
                }
            }
            // current last
            input = inputObjectPool.take()
            input!!.useEvent(event)
            feedInput(input)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    @Throws(InterruptedException::class)
    internal fun feedInput(input: InputObject) {
        synchronized(inputQueueMutex) {
            inputQueue.put(input)
        }
    }

    fun processEvents() {

        synchronized(inputQueueMutex) {
            while (!inputQueue.isEmpty()) {
                var input: InputObject? = null
                try {
                    input = inputQueue.take()
                    if (input!!.eventType == InputObject.EVENT_TYPE_TOUCH) {
                        processMotionEvent(input)
                    }
                    input.returnToPool()
                } catch (e: InterruptedException) {
                }

            }
        }
    }

    private fun processMotionEvent(input: InputObject) {

        if (input.action == InputObject.ACTION_TOUCH_DOWN) {
            game.initFiring()
        }
        if (input.action == InputObject.ACTION_TOUCH_DOWN ||
            input.action == InputObject.ACTION_TOUCH_MOVE ||
            input.action == InputObject.ACTION_TOUCH_UP
        ) {
            game.setTarget(input.x, input.y)
        }
        if (input.action == InputObject.ACTION_TOUCH_UP) {
            game.doFire()
        }
    }




}
