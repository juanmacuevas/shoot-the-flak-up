package com.juanmacuevas.shoottheflakup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import androidx.core.util.Pair;

import java.util.concurrent.ArrayBlockingQueue;

public class GameThread extends Thread implements GameEvents {

    private final SoundManager soundManager;
    private GameView gameView;

    private ArrayBlockingQueue<InputObject> inputQueue = new ArrayBlockingQueue<>(30);
    private Object inputQueueMutex = new Object();

    private boolean readyToDraw;

    private PowerBar powerBar;
    private InfoText infoText;
    private FuncionalTank tank;

    private AircraftsControl aircraftsControl;
    private long lastUpdateTime;
    private Vibrator vibrator;
    private BulletsControl bulletsControl;
    private Landscape landscape;

    private final GameData gameData = new GameData();

    public GameThread(Context context, GameView surfaceView, DisplayMetrics metrics) {
        // get handles to some important objects
        readyToDraw = false;
        lastUpdateTime = 0;

        soundManager = new SoundManager(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        Resources res = context.getResources();
        tank = new FuncionalTank(metrics, this, res);
        landscape = new Landscape(res, metrics);
        powerBar = new PowerBar(metrics);
        infoText = new InfoText(res, metrics);

        bulletsControl = new BulletsControl(res, metrics);
        aircraftsControl = new AircraftsControl(res, metrics, this);


        gameView = surfaceView;
        surfaceView.setThread(this);
    }

    /**
     * Resumes from a pause.
     */
    public void unpause() {
        // Move the real time clock up to now
        synchronized (gameView) {
            lastUpdateTime = System.currentTimeMillis() + 100;
        }

    }

    @Override
    public void run() {
        while (readyToDraw) {
            processInput();
            long delta = updateCurrentTimeAndCalculateDelta();
            updatePhysics(delta);
            soundManager.update(delta);
            gameView.doDraw(this);
        }
        pauseMusic();
    }

    private long updateCurrentTimeAndCalculateDelta() {
        long currentTime = System.currentTimeMillis();
        long delta = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        return delta;
    }

    /**
     * Used to signal the thread whether it should be running or not.
     * Passing true allows the thread to run; passing false will shut it
     * down if it's already running. Calling start() after this was most
     * recently called with false will result in an immediate shutdown.
     *
     * @param b true to run, false to shut down
     */
    public void setRunning(boolean b) {
        readyToDraw = b;
    }


    public void doDraw(Canvas canvas) {

        landscape.draw(canvas);
        aircraftsControl.draw(canvas);
        bulletsControl.draw(canvas);
        tank.draw(canvas);
        powerBar.draw(canvas);
        infoText.draw(canvas);

    }

    private void updatePhysics(long timer) {
        tank.update(timer);
        bulletsControl.update(timer);
        aircraftsControl.update(timer, bulletsControl.iterable());

        gameData.setPower(tank.getPower());

        powerBar.setData(gameData);
        infoText.setData(gameData);
    }


    public void feedInput(InputObject input) throws InterruptedException {
        synchronized (inputQueueMutex) {
            inputQueue.put(input);
        }
    }

    private void processInput() {
        synchronized (inputQueueMutex) {
            while (!inputQueue.isEmpty()) {
                InputObject input = null;
                try {
                    input = inputQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (input.eventType == InputObject.EVENT_TYPE_TOUCH) {
                    processMotionEvent(input);
                }
                input.returnToPool();
            }
        }
    }

    private void processMotionEvent(InputObject input) {

        if (input.action == InputObject.ACTION_TOUCH_DOWN) {
            tank.pressFire();
            tank.setTarget(input.x, input.y);

        } else if (input.action == InputObject.ACTION_TOUCH_MOVE) {
            tank.setTarget(input.x, input.y);

        } else if (input.action == InputObject.ACTION_TOUCH_UP) {
            tank.setTarget(input.x, input.y);
            tank.releaseFire();
        }
    }


    private void pauseMusic() {
        soundManager.pauseMusic();
    }

    @Override
    public void shootBullet(float angle, int power, Pair<Integer, Integer> bulletOrigin) {

        bulletsControl.addBullet(power, angle, bulletOrigin);
        soundManager.playShoot();
    }

    @Override
    public void angleChanged(float angle) {
        gameData.setAngle(angle);
        soundManager.playMovegun();
    }

    @Override
    public void aircraftExploded() {
        gameData.setImpacts(gameData.getImpacts() + 1);
        soundManager.playExplode();
        vibrator.vibrate(100);
    }
}
