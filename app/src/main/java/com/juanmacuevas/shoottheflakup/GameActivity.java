package com.juanmacuevas.shoottheflakup;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;

import java.util.concurrent.ArrayBlockingQueue;

public class GameActivity extends Activity {

    public static final int INPUT_QUEUE_SIZE = 30;
    public final ArrayBlockingQueue<InputObject> inputObjectPool = new ArrayBlockingQueue<>(INPUT_QUEUE_SIZE);

    private GameThread mGameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        GameView gameSurfaceView = findViewById(R.id.game);

        mGameThread = new GameThread(this, gameSurfaceView, metrics);

        createInputObjectPool();
    }


    private void createInputObjectPool() {
        for (int i = 0; i < INPUT_QUEUE_SIZE; i++) {
            inputObjectPool.add(new InputObject(inputObjectPool));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            processEventWithPool(event,inputObjectPool,mGameThread);
            // don't allow more than 60 motion events per second
            Thread.sleep(16);
        } catch (InterruptedException e) {
            Log.d("GameActivity",e.getMessage());
        }
        return true;
    }

    private void processEventWithPool(MotionEvent event, ArrayBlockingQueue<InputObject> inputObjectPool, GameThread mGameThread) throws InterruptedException {
        // we only care about down actions in this game.
        // history first
        int hist = event.getHistorySize();
        if (hist > 0) {
            // add from oldest to newest
            for (int i = 0; i < hist; i++) {
                InputObject input = inputObjectPool.take();
                input.useEventHistory(event, i);
                mGameThread.feedInput(input);
            }
        }
        // current last
        InputObject input = inputObjectPool.take();
        input.useEvent(event);
        mGameThread.feedInput(input);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGameThread.setRunning(false);
    }

}

