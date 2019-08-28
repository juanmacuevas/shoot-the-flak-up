package com.juanmacuevas.shoottheflakup;

import android.view.MotionEvent;

import java.util.concurrent.ArrayBlockingQueue;

public class InputController {
    public static final int INPUT_QUEUE_SIZE = 30;
    public final ArrayBlockingQueue<InputObject> inputObjectPool = new ArrayBlockingQueue<>(INPUT_QUEUE_SIZE);
    private final GameThread game;


    private ArrayBlockingQueue<InputObject> inputQueue = new ArrayBlockingQueue<>(30);
    private Object inputQueueMutex = new Object();


    public InputController(GameThread game) {
        this.game = game;
        createInputObjectPool();
    }

    private void createInputObjectPool() {
        for (int i = 0; i < INPUT_QUEUE_SIZE; i++) {
            inputObjectPool.add(new InputObject(inputObjectPool));
        }
    }

    public void addNewEvent(MotionEvent event) {

        // we only care about down actions in this game.
        // history first
        int hist = event.getHistorySize();
        InputObject input;
        try {
            if (hist > 0) {
                // add from oldest to newest
                for (int i = 0; i < hist; i++) {
                    input = null;
                    input = inputObjectPool.take();
                    input.useEventHistory(event, i);
                    feedInput(input);
                }
            }
            // current last
            input = inputObjectPool.take();
            input.useEvent(event);
            feedInput(input);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void feedInput(InputObject input) throws InterruptedException {
        synchronized (inputQueueMutex) {
            inputQueue.put(input);
        }
    }

    public void processEvents() {

        synchronized (inputQueueMutex) {
            while (!inputQueue.isEmpty()) {
                InputObject input = null;
                try {
                    input = inputQueue.take();
                    if (input.eventType == InputObject.EVENT_TYPE_TOUCH) {
                        processMotionEvent(input);
                    }
                    input.returnToPool();
                } catch (InterruptedException e) {
                }

            }
        }
    }

    private void processMotionEvent(InputObject input) {

        if (input.action == InputObject.ACTION_TOUCH_DOWN) {
            game.initFiring();
        }
        if (input.action == InputObject.ACTION_TOUCH_DOWN ||
                input.action == InputObject.ACTION_TOUCH_MOVE ||
                input.action == InputObject.ACTION_TOUCH_UP) {
            game.setTarget(input.x, input.y);
        }
        if (input.action == InputObject.ACTION_TOUCH_UP) {
            game.doFire();
        }
    }

}
