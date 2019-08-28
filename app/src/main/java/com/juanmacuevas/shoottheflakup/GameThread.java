package com.juanmacuevas.shoottheflakup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import androidx.core.util.Pair;

public class GameThread extends Thread implements GameEvents {

    private final SoundManager soundManager;
    private GameView gameView;

    private final InputController inputController;

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
        inputController = new InputController(this);
    }

    @Override
    public void run() {
        while (readyToDraw) {
            inputController.processEvents();
            long delta = updateCurrentTimeAndCalculateDelta();
            updatePhysics(delta);
            gameView.prepareCanvasAndDraw(this);
            soundManager.update(delta);
        }
        soundManager.pauseMusic();
    }

    private long updateCurrentTimeAndCalculateDelta() {
        long currentTime = System.currentTimeMillis();
        long delta = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        return delta;
    }

    private void updatePhysics(long timer) {
        tank.update(timer);
        bulletsControl.update(timer);
        aircraftsControl.update(timer, bulletsControl.iterable());
        gameData.setPower(tank.getPower());
        powerBar.setData(gameData);
        infoText.setData(gameData);
    }

    public void doDraw(Canvas canvas) {
        landscape.draw(canvas);
        aircraftsControl.draw(canvas);
        bulletsControl.draw(canvas);
        tank.draw(canvas);
        powerBar.draw(canvas);
        infoText.draw(canvas);
    }

    public void setRunning(boolean b) {
        readyToDraw = b;
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

    public void inputEvent(MotionEvent event) {
        inputController.addNewEvent(event);
    }

    public void initFiring() {
        tank.initFiring();
    }

    public void setTarget(int x, int y) {
        tank.setTarget(x,y);
    }

    public void doFire() {
        tank.doFire();
    }
}
