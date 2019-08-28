package com.juanmacuevas.shoottheflakup;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;

public class GameActivity extends Activity {


    private GameThread game;
    private InputController inputController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        GameView gameView = findViewById(R.id.game);

        game = new GameThread(this, gameView, metrics);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        game.inputEvent(event);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        game.setRunning(false);
    }

}

