package com.juanmacuevas.shoottheflakup;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.ArrayBlockingQueue;

public class GameThread extends Thread {

	private final SoundManager soundManager;

	private SurfaceHolder mSurfaceHolder;

	private ArrayBlockingQueue<InputObject> inputQueue = new ArrayBlockingQueue<>(30);
	private Object inputQueueMutex = new Object();

    private boolean readyToDraw;

	private HUD hud;
	private FuncionalTank tank;

	private AircraftsControl aircraftsControl;
	private long lastUpdateTime;
	private Vibrator vibrator;
    private BulletsControl bulletsControl;
	private Landscape landscape;

	public GameThread(Context context, GameView surfaceView, DisplayMetrics metrics) {
        // get handles to some important objects
        readyToDraw = false;
        lastUpdateTime = 0;

        mSurfaceHolder = surfaceView.getHolder();
		surfaceView.setThread(this);

		soundManager = new SoundManager(context);
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

		tank = new FuncionalTank(metrics,this,context.getResources());
		landscape = new Landscape(context.getResources(),metrics);
		hud = new HUD(metrics);
		float tankBottom = metrics.heightPixels - (FuncionalTank.TANK_BOTTOM_MARGIN * FuncionalTank.scale);
		bulletsControl = new BulletsControl(tankBottom);
		aircraftsControl = new AircraftsControl(metrics,soundManager,context.getResources());
		hud.register(tank);


	}


	/**
	 * Resumes from a pause.
	 */
	public void unpause() {
		// Move the real time clock up to now
		synchronized (mSurfaceHolder) {
			lastUpdateTime = System.currentTimeMillis() + 100;
		}

	}



	@Override
	public void run() {
		while (readyToDraw) {
			Canvas c = null;
			try {
				c = mSurfaceHolder.lockCanvas(null);
				synchronized (mSurfaceHolder) {
					long currentTime = System.currentTimeMillis();
					long delta = currentTime - lastUpdateTime;
					lastUpdateTime = currentTime;
					processInput();
					updatePhysics(delta);
					soundManager.update(delta);
					if (c!=null) {
                        doDraw(c);
                    }
				}
			} catch (InterruptedException e) {
				Log.d("GameThread",e.getMessage());
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					mSurfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
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




	/**
	 * Draws the tank, bullets, planes, and background to the provided
	 * Canvas.
	 */
	private void doDraw(Canvas canvas) {

		landscape.draw(canvas);
        aircraftsControl.draw(canvas);
        bulletsControl.draw(canvas);
		tank.draw(canvas);
		hud.draw(canvas);

	}

	private void updatePhysics(long timer) {
	    tank.update(timer);
	    bulletsControl.update(timer);
        aircraftsControl.update(timer,bulletsControl.iterable(),hud,vibrator);
    }


	public void feedInput(InputObject input) throws InterruptedException {
		synchronized(inputQueueMutex) {
				inputQueue.put(input);
		}
	}

	private void processInput() throws InterruptedException {
		synchronized (inputQueueMutex) {
			while (!inputQueue.isEmpty()) {
				InputObject input = inputQueue.take();

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

	public void shootBullet(float angle, int power,int x, int y) {

        bulletsControl.addBullet(power, angle,x,y);
		soundManager.playShoot();
	}


	public void pauseMusic() {
		soundManager.pauseMusic();
	}

	public void playMovegun() {
		soundManager.playMovegun();
	}
}
