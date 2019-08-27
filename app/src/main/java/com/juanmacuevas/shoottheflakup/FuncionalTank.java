package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import androidx.core.util.Pair;

public class FuncionalTank extends GraphicComponent {
    public static final int TANK_BOTTOM_MARGIN = 7;
    private static final int TANK_LEFT_MARGIN = 0;
    private static final int TANK_HEIGHT = 55;
    private static final int TANK_WIDTH = 106;

    private static final int GUNBARREL_LENGTH = 100;
    private static final int GUNBARREL_WIDTH = 23;

    private static final int STATUS_IDLE = 0;
    private static final int STATUS_POWERING = 1;
    private static final long POWERING_TIMER_LIMIT = 1200;

    private static Bitmap tankImg;
    private static Bitmap gunBarrelImg;

    private final GameEvents thread;
    private final float shootOriginX;
    private final float shootOriginY;
    private final float tankLeft;
    private final float tankTop;

    private int tankStatus;
    private long milisecondsPowering;
    private Pair<Integer, Integer> bulletOrigin;
    private float angle;
    private int power;
    private int lastBulletPower;

    public FuncionalTank(DisplayMetrics metrics, GameThread thread, Resources res) {
        super(res,metrics);
        this.thread = thread;
        tankStatus = STATUS_IDLE;
        power = 0;
        lastBulletPower = 0;
        milisecondsPowering = 0;
        tankLeft = TANK_LEFT_MARGIN * scale;
        tankTop = metrics.heightPixels - (TANK_HEIGHT + TANK_BOTTOM_MARGIN) * scale;
        shootOriginX = TANK_LEFT_MARGIN + 60.f * scale;
        shootOriginY = tankTop + 12 * scale;
        setTarget(metrics.widthPixels, 0);

        tankImg = initBitmap(tankImg, R.drawable.tank, TANK_WIDTH, TANK_HEIGHT);
        gunBarrelImg =initBitmap(gunBarrelImg, R.drawable.cannon, GUNBARREL_LENGTH, GUNBARREL_WIDTH);

    }

    public void update(long elapsedTime) {
        if (tankStatus == STATUS_POWERING) {
            milisecondsPowering = milisecondsPowering + elapsedTime;
            if (milisecondsPowering > POWERING_TIMER_LIMIT)
                milisecondsPowering = 0;

            power = (int) (((float) milisecondsPowering / POWERING_TIMER_LIMIT) * 100);
        }
    }


    public void draw(Canvas c) {
        Matrix m = matrixTranslateAndMove(tankLeft + 40 * scale, tankTop, (float) ((-angle) * 180 / Math.PI), shootOriginX, shootOriginY);
        c.drawBitmap(gunBarrelImg, m, null);
        c.drawBitmap(tankImg, tankLeft, tankTop, null);

    }

    /**
     * Sets the gun barrel to point the specified coordinate
     *
     * @param x
     * @param y
     */
    public void
    setTarget(int x, int y) {

        if (x < shootOriginX) x = (int) shootOriginX;
        if (y > shootOriginY) y = (int) shootOriginY;
        if (x == shootOriginX && y == shootOriginY) x = x+10;

        final float previousAngle = angle;
        angle = calculateAngle(x, y);

        bulletOrigin = calculateBulletOrigin();

        if (angleChanged(previousAngle, angle)) {
            thread.angleChanged(angle);
        }

    }

    private Pair<Integer, Integer> calculateBulletOrigin() {
        return new Pair<>((int) (shootOriginX + (Math.cos(angle) * (GUNBARREL_LENGTH - 30) * scale)),
                (int) (shootOriginY - (Math.sin(angle) * (GUNBARREL_LENGTH - 30) * scale)));
    }

    private float calculateAngle(int x, int y) {
        return Math.abs((float) Math.atan((y - shootOriginY) / (x - shootOriginX)));
    }

    private boolean angleChanged(float before, float after) {
        return 0 < Math.abs((int) ((before - after) * 180 / Math.PI));

    }

    /**
     * called when the user press the screen. It starts the powering process
     */
    public void pressFire() {
        tankStatus = STATUS_POWERING;
        power = 0;
        milisecondsPowering = 0;
    }

    /**
     * called when the user release the finger and the shoot is performed
     */
    public void releaseFire() {
        thread.shootBullet(angle, 60 + power * 60 / 100, bulletOrigin);
        lastBulletPower = power;
        tankStatus = STATUS_IDLE;
        power = 0;

    }

    /**
     * gives information about the power of the tank
     */
    public int getPower() {
        return power;
    }

    /**
     * gives information about the angle of the gun barrel
     */
    public float getAngle() {
        return angle;
    }

    /**
     * used to show the power of the last shoot
     *
     * @return the power value of the last shoot
     */
    public int getLastBulletPower() {
        return lastBulletPower;
    }


}
