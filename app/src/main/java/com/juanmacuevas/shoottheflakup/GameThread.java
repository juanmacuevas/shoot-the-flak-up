package com.juanmacuevas.shoottheflakup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;


public class GameThread extends Thread {

	public static Bitmap mBackgroundImage;
	public static Bitmap tankImg;
	public static Bitmap gunBarrelImg;
	public static Bitmap aircraftImg;
	public static Bitmap aircraftDownImg;

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

    public GameThread(Context context, GameView surfaceView, DisplayMetrics metrics) {
        // get handles to some important objects
        readyToDraw = false;
        mSurfaceHolder = surfaceView.getHolder();
        lastUpdateTime = 0;

        bulletsControl = new BulletsControl();
        aircraftsControl = new AircraftsControl(metrics);
		loadBitmaps(context.getResources());

		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		SoundManager.loadSound(context);

		surfaceView.setThread(this);
		setDisplayMetrics(metrics);

	}

	private static void loadBitmaps(Resources res) {
		// we don't need to transform it and it's faster to draw this way
		mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);

		tankImg = BitmapFactory.decodeResource(res,R.drawable.tank);
		gunBarrelImg = BitmapFactory.decodeResource(res,R.drawable.cannon);

		aircraftImg= BitmapFactory.decodeResource(res,R.drawable.aircraft);
		aircraftDownImg= BitmapFactory.decodeResource(res,R.drawable.aircraftdown);
	}

	/**
	 * Starts the game
	 */
	public void doStart() {
		synchronized (mSurfaceHolder) {
		}
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
					long delta = (long) (currentTime - lastUpdateTime);
					lastUpdateTime = currentTime;
					processInput();
					updatePhysics(delta);
					SoundManager.update(delta);
					if (c!=null) {
                        doDraw(c);
                    }
				}
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

		canvas.drawBitmap(mBackgroundImage, 0, 0, null);
        aircraftsControl.draw(canvas);
        bulletsControl.draw(canvas);
		tank.draw(canvas);
		hud.draw(canvas);

	}

	private void updatePhysics(long timer) {

	    tank.update(timer);
	    bulletsControl.update(timer);
        aircraftsControl.update(timer,bulletsControl.iterable(),hud,vibrator);

        //hud.update(timer);

    }


	public void feedInput(InputObject input) {
		synchronized(inputQueueMutex) {
			try {
				inputQueue.put(input);
			} catch (InterruptedException e) {
				//Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	private void processInput() {
		synchronized(inputQueueMutex) {
			ArrayBlockingQueue<InputObject> inputQueue = this.inputQueue;
			while (!inputQueue.isEmpty()) {
				try {
					InputObject input = inputQueue.take();

					if (input.eventType == InputObject.EVENT_TYPE_TOUCH) {
						processMotionEvent(input);
					}

					/*	else if (input.eventType == InputObject.EVENT_TYPE_KEY) {
    					processKeyEvent(input);
    				} else 
					 */

					input.returnToPool();
				} catch (InterruptedException e) {
					//Log.e(TAG, e.getMessage(), e);
				}
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



	private void setDisplayMetrics(DisplayMetrics displayMetrics) {

		mBackgroundImage=mBackgroundImage.createScaledBitmap(
				mBackgroundImage, displayMetrics.widthPixels, displayMetrics.heightPixels, true);
		hud = new HUD(displayMetrics);
		tank = new FuncionalTank(displayMetrics,this);
		hud.register(tank);


	}

	public void shootBullet(float angle, int power,int x, int y) {

        bulletsControl.addBullet(power, angle,x,y);
		SoundManager.playShoot();
	}



}
