package com.juanmacuevas.shoottheflakup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import com.juanmacuevas.shoottheflakup.R;


public class GameThread extends Thread {



	/** Handle to the surface manager object we interact with */
	private SurfaceHolder mSurfaceHolder;

	public DisplayMetrics metrics = new DisplayMetrics();

	/** The drawable to use as the background of the animation canvas */
	private static Bitmap mBackgroundImage;

	/** The drawable to use as the tank */
	public static Bitmap tankImg;	

	/** The drawable to use as the gunbarrel */
	public static Bitmap gunBarrelImg;

	/** The drawable to use as the aircraft */
	public static Bitmap aircraftImg;	

	/** The drawable to use as the aircraft Down */
	public static Bitmap aircraftDownImg;	

	/** Message handler used by thread to interact with TextView */
	private Handler mHandler;

	private Context mContext;

	private ArrayBlockingQueue<InputObject> inputQueue = new ArrayBlockingQueue<InputObject>(30);    
	private Object inputQueueMutex = new Object();

	private long newaircraftimer;


	/**
	 * end of test area
	 */

	/** Indicate whether the surface has been created & is ready to draw */
	public static boolean mRun = false;

	private HUD hud;
	private FuncionalTank tank;
	private static ArrayList<FunctionalBullet> bullets; 
	private static ArrayList<FunctionalAircraft> aircrafts;
	private long lastUpdateTime;
	public static Resources res;
	private Vibrator vibrator;
	public static DisplayMetrics displayMetrics;

	public GameThread(SurfaceHolder surfaceHolder, Context context,
			Handler handler) {
		// get handles to some important objects
		mSurfaceHolder = surfaceHolder;
		mContext = context;
		lastUpdateTime = 0;
		Resources res = context.getResources();
		bullets = new ArrayList<FunctionalBullet>();
		aircrafts = new ArrayList<FunctionalAircraft>();
		newaircraftimer=0;

		// we don't need to transform it and it's faster to draw this way
		mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);

		tankImg = BitmapFactory.decodeResource(res,R.drawable.tank);
		gunBarrelImg = BitmapFactory.decodeResource(res,R.drawable.cannon);

		aircraftImg= BitmapFactory.decodeResource(res,R.drawable.aircraft);
		aircraftDownImg= BitmapFactory.decodeResource(res,R.drawable.aircraftdown);
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		SoundManager.loadSound(mContext);

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


		while (mRun) {
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
					doDraw(c);
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
		mRun = b;
	}




	/**
	 * Draws the tank, bullets, planes, and background to the provided
	 * Canvas.
	 */
	private void doDraw(Canvas canvas) {

		//draw some background 


		canvas.drawBitmap(mBackgroundImage, 0, 0, null);
		/*	Paint mPaint = new Paint();
    	mPaint.setColor(0xFF94B0FF);    	
    	canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);
		 */
		//draw the aircrafts

		for(Iterator it = aircrafts.iterator();it.hasNext();){
			FunctionalAircraft a = (FunctionalAircraft) it.next();
			if (a!=null)
				a.draw(canvas);  	
		}


		//draw the bullets

		for(Iterator it = bullets.iterator();it.hasNext();){
			FunctionalBullet b = (FunctionalBullet) it.next();
			if (b!=null)
				b.draw(canvas);  	
		}


		//Draw the tank
		tank.draw(canvas); 

		//Hud draw
		hud.draw(canvas);

	}

	/**
	 * Figures the lander state (x, y, fuel, ...) based on the passage of
	 * realtime. Does not invalidate(). Called at the start of draw().
	 * Detects the end-of-game and sets the UI to the next state.
	 */
	private void updatePhysics(long timer) {
		timer=(long) (timer); //adjust the overall speed
		//	Log.i("update", "fisicacaaa");

		tank.update(timer);

		FunctionalBullet b ;
		int i=0;
		while (!bullets.isEmpty() && bullets.size()>i){
			b = bullets.get(i);
			if (b.isOver())
				bullets.remove(i);
			else {
				b.update(timer);
				i++;
			}

		}

		FunctionalAircraft a ;
		i=0;
		while (!aircrafts.isEmpty() && aircrafts.size()>i){
			a = aircrafts.get(i);
			if (a.isOver())
				aircrafts.remove(i);
			else {
				a.update(timer);
				////
				int j=0;
				while (j<bullets.size()){
					b = bullets.get(j);
					if (a.isFlying() && b.isFlying())
						if(a.impactDetected(b)){
							hud.addImpact();
							a.setImpact();
							b.setImpact();
							aircrafts.add(new FunctionalAircraft());

							// Vibrate for 300 milliseconds
							vibrator.vibrate(100);

						}    					
					j++;
				}
				i++;
			}    		
		}   	
		newaircraftimer+=timer;
		if (newaircraftimer>3000){
			aircrafts.add(new FunctionalAircraft());
			newaircraftimer=0;
		}
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

		/**
		 * whole screen fire
		 */
		if( input.action==InputObject.ACTION_TOUCH_DOWN){

			tank.pressFire();
			tank.setTarget(input.x, input.y);

		}	else if( input.action==InputObject.ACTION_TOUCH_MOVE)//{

			tank.setTarget(input.x, input.y);
		//	bullets.add(new FunctionalBullet(50, (float) (Math.PI/2), input.x, input.y));
		//}

		else if( input.action==InputObject.ACTION_TOUCH_UP){

			tank.setTarget(input.x, input.y);
			tank.releaseFire();
		}




		/**
		 * with fire button

    	if( input.action==InputObject.ACTION_TOUCH_DOWN)
    		if (hud.insideFireBtn(input.x,input.y))
    			tank.pressFire();
    		else tank.setTarget(input.x, input.y);

    	if( input.action==InputObject.ACTION_TOUCH_MOVE)
    		if (!hud.insideFireBtn(input.x,input.y)){
    			tank.cancelFire();
    			tank.setTarget(input.x, input.y);
    		}

    	if( input.action==InputObject.ACTION_TOUCH_UP)
    		if (hud.insideFireBtn(input.x,input.y))
    			tank.releaseFire();
    		else tank.setTarget(input.x, input.y);

		 */



	}





	public void pause() {
		// TODO Auto-generated method stub


	}
	public void setDisplayMetrics(DisplayMetrics m) {
		// TODO Auto-generated method stub
		displayMetrics = m;
		mBackgroundImage=mBackgroundImage.createScaledBitmap(
				mBackgroundImage, displayMetrics.widthPixels, displayMetrics.heightPixels, true);
		hud = new HUD(m);
		tank = new FuncionalTank(m);
		hud.register(tank);


	}
	public static void shootBullet(float angle, int power,int x, int y) {

		bullets.add(new FunctionalBullet(power, angle,x,y));

		SoundManager.playShoot();



	}



}
