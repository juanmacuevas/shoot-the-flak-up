package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.*;
import android.util.DisplayMetrics;

public class FuncionalTank implements Renderable{
	/**
	 * Distance between the tank and the bottom of the screen
	 */
	public static final int TANK_BOTTOM_MARGIN = 7;
	/**
	 * Distance between the tank and the left margin of the screen
	 */
	private static final int TANK_LEFT_MARGIN = 0 ;
	/**
	 * vertical size of the tank
	 */
	private static final int TANK_HEIGHT = 55;
	/**
	 * horizontal size of the tank
	 */
	private static final int TANK_WIDTH = 106;
	/**
	 * gun barrel length
	 */
	private static final int GUNBARREL_LENGTH = 100;
	/**
	 * gun barrel width 
	 */
	private static final int GUNBARREL_WIDTH = 23; 

	/**
	 * tankStatus of the tank when inactive
	 */
	private static final int STATUS_IDLE=0;
	/**
	 * tankStatus of the tank while ready to shoot
	 */
	private static final int STATUS_POWERING=1;
	private final GameThread thread;
	private final Bitmap tankImg;
	private final Bitmap gunBarrelImg;

	private int tankStatus;

	private long milisecondsPowering;
	private static final long POWERING_TIMER_LIMIT = 1200;

	private static float ShootOriginX;
	private static float ShootOriginY;

	private float tankLeft;
	private float tankTop;

	private int gunBarrelEndX;
	private int gunBarrelEndY;


	private float angle;

	private int power;

	private Paint paint;
	private int lastBulletPower;

	public static float getScale() {
		return scale;
	}

	public static float scale;
	private DisplayMetrics metrics;

	public FuncionalTank(DisplayMetrics metrics, GameThread thread, Resources res) {
		this.thread = thread;
		tankStatus = STATUS_IDLE;
		power = 0;
		lastBulletPower = 0;
		milisecondsPowering = 0;
		this.metrics = metrics;
		scale = (float) this.metrics.densityDpi / 160;
		tankLeft = TANK_LEFT_MARGIN * scale;
		tankTop = this.metrics.heightPixels - (TANK_HEIGHT + TANK_BOTTOM_MARGIN) * scale;

		ShootOriginX = TANK_LEFT_MARGIN + 60.f * scale;
		ShootOriginY = tankTop + 12 * scale;

		setTarget(this.metrics.widthPixels, 0);

		Bitmap tmp;
		tmp = BitmapFactory.decodeResource(res, R.drawable.tank);
		tankImg = Bitmap.createScaledBitmap(tmp, (int) (TANK_WIDTH * scale), (int) (TANK_HEIGHT * scale), true);
		tmp = BitmapFactory.decodeResource(res, R.drawable.cannon);
		gunBarrelImg = Bitmap.createScaledBitmap(tmp, (int) (GUNBARREL_LENGTH * scale), (int) (GUNBARREL_WIDTH * scale), true);


	}

	/**
	 * Updates the tankStatus of the tank when it's powering
	 * 
	 * 
	 */
	public void update(long elapsedTime){		
		if (tankStatus == STATUS_POWERING){
			milisecondsPowering = milisecondsPowering +elapsedTime;
			if (milisecondsPowering >POWERING_TIMER_LIMIT)
				milisecondsPowering =0;


			power = (int) (((float) milisecondsPowering / POWERING_TIMER_LIMIT) *100);

			//logarithm to improve the powering control  high values
			//power = (int) (50 * Math.log10(power+1)); 
		}
	}

	/**
	 * draw the tank in the canvas surface
	 */
	public void draw(Canvas c) {

		/**	old graphic function
		paint.setStyle(Style.STROKE);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(7*scale);
		c.drawLine(ShootOriginX, ShootOriginY, gunBarrelEndX, gunBarrelEndY, paint);
		 */

		// c.drawBitmap(GameThread.gunBarrelImg, tankLeft, tankTop, null);


		//draw the gun barrel
		Matrix m = new Matrix();
		m.postTranslate(tankLeft+40*scale, tankTop);
		m.postRotate((float) ((-angle)*180 /Math.PI),ShootOriginX,ShootOriginY);		   
		c.drawBitmap(gunBarrelImg, m, null);
		//draw the Tank
		c.drawBitmap(tankImg, tankLeft, tankTop, null);
	}

	/**
	 * Sets the gun barrel to point the specified coordinate
	 * @param x
	 * @param y
	 */
	public void setTarget(int x, int y) {
		float previousAngle=angle;
		if (x<ShootOriginX) x = (int) ShootOriginX;
		if (y>ShootOriginY) y = (int) ShootOriginY;
		if (x==ShootOriginX && y==ShootOriginY) x= metrics.widthPixels;

		angle = Math.abs((float) Math.atan((y-ShootOriginY)/(x-ShootOriginX)));
		//Log.i("angle","x: "+x+" y: "+y+" Angulo: "+Float.toString((float) (angle*180/Math.PI)));

		gunBarrelEndX = (int) (ShootOriginX + (Math.cos(angle) * (GUNBARREL_LENGTH-30) *scale));
		gunBarrelEndY = (int) (ShootOriginY - (Math.sin(angle) * (GUNBARREL_LENGTH-30) *scale));

		int aux = Math.abs( (int) ((previousAngle-angle)*180/Math.PI));
		if (aux>0) //only reproduces the sound when the angle changes at least one degree
			thread.playMovegun();
		//Log.i("angle","gunBarrelEndX: "+gunBarrelEndX+" gunBarrelEndY: "+gunBarrelEndY+" Angulo: "+Float.toString((float) (angle*180/Math.PI)));

	}

	/**
	 * called when the user press the screen. It starts the powering process
	 */
	public void pressFire( ) {
		tankStatus = STATUS_POWERING;
		power = 0;		
		milisecondsPowering = 0;
	}

	/**
	 * called when the user release the finger and the shoot is performed
	 */
	public void releaseFire() {
		thread.shootBullet(angle,60+power*60/100,gunBarrelEndX,gunBarrelEndY);
		lastBulletPower = power;
		tankStatus = STATUS_IDLE;
		power = 0;

	}




	/**
	 *  used to cancel the powering action
	 *  

	public void cancelFire() {
		tankStatus = STATUS_IDLE;
		power = 0;

	}
	 */
	
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
	 * @return the power value of the last shoot
	 * 
	 */
	public int getLastBulletPower() {
		return lastBulletPower;
	}




}
