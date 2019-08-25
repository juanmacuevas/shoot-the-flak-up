package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.*;
import android.util.DisplayMetrics;

public class FunctionalAircraft implements Renderable {

	private static final int STATUS_FLYING = 0;

	private static final int STATUS_BOOM = 1;

	private static final int STATUS_OVER = 2;

	private static final int AIRCRAFT_RADIUS  = 15;

	private static final float AIRCRAFT_WIDTH  = (float) (127*0.35);

	private static final float AIRCRAFT_HEIGHT  = (float) (134*0.35);


	private static final float TIME_FLYING = 5;

	private static final long TIME_EXPLODING  = 800;
	private final float scale=FuncionalTank.scale;

	//private static final double VERTICAL_ACCELERATION = 14;

	private float lowerPosition;

	private static Bitmap aircraftImg;
	private static Bitmap aircraftDownImg;

	private long time;

	private int status;
	private float posX;
	private float posY;
	private double iniSpeedX;
	private double iniSpeedY;
	private float posX0;
	private float posY0;

	private float drawX;
	private float drawY;

	private int angle;

	private int direction;
	private static float acceleration;

	private Paint paint;

	private long explodingTimer;

	public FunctionalAircraft(DisplayMetrics dm){
		time=0;
		paint = new Paint();
		status = STATUS_FLYING;

		//random values

		direction = (Math.random()<0.5?1:-1);

		posX0 = (float) (dm.widthPixels/2 + (dm.widthPixels*-direction*Math.random()));

		lowerPosition = dm.heightPixels;

		acceleration = (float) (lowerPosition/Math.pow(TIME_FLYING/2,2));

		iniSpeedX = ( dm.widthPixels / TIME_FLYING) ;

		angle = 0;
		iniSpeedY=(TIME_FLYING*acceleration/2)-1/TIME_FLYING;

		//iniSpeedY =  (VERTICAL_ACCELERATION*0.5* Math.pow(TIME_FLYING,2))/(TIME_FLYING);

		//	iniSpeedY= ((VERTICAL_ACCELERATION)* timeLowPosition);
		//	Log.i("aircraft","creacion Vx0: "+iniSpeedX+" Vy0: "+iniSpeedY);




	}

	public void initResources(Resources res){
        if (aircraftDownImg == null) {
            aircraftDownImg = BitmapFactory.decodeResource(res, R.drawable.aircraftdown);
            aircraftDownImg = Bitmap.createScaledBitmap(aircraftDownImg, (int) (AIRCRAFT_WIDTH * scale), (int) (AIRCRAFT_HEIGHT * scale), true);
        }
        if (aircraftImg == null) {
            aircraftImg = BitmapFactory.decodeResource(res, R.drawable.aircraft);
            aircraftImg = Bitmap.createScaledBitmap(aircraftImg, (int) (AIRCRAFT_WIDTH * scale), (int) (AIRCRAFT_HEIGHT * scale), true);
        }



	}
	public void draw(Canvas c) {
		// TODO Auto-generated method stub
		/*	if (status == STATUS_BOOM)
				paint.setColor(Color.YELLOW);
			else paint.setColor(Color.GRAY);
			c.drawCircle(posX, posY, AIRCRAFT_RADIUS * FuncionalTank.scale, paint);*/

		Matrix m = new Matrix();
		m.postTranslate(drawX, drawY);
		m.postRotate(angle,posX,posY);

		if (status == STATUS_FLYING)
			c.drawBitmap(aircraftImg, m, null);
		else if (status == STATUS_BOOM)
			c.drawBitmap(aircraftDownImg, m, null);

	}

	public void update(long elapsedTime) {


		double t2;
		switch (status){
		case STATUS_FLYING:
			time+=elapsedTime;
			t2=(double)(time)/1000;


			posX = (float) (posX0 +  iniSpeedX*t2*direction);
			posY = (float) (iniSpeedY * t2 - (acceleration/2 * Math.pow(t2,2) ));

			angle = (direction>0?180:0)+   (int) (Math.atan((iniSpeedY-acceleration*t2)/iniSpeedX*direction)*180/Math.PI);

			if (posY<0) status=STATUS_OVER;

			break;

		case STATUS_BOOM:
			explodingTimer+=elapsedTime;

			t2=(double)(explodingTimer)/1000;

			posX = (float) (posX0 +  iniSpeedX*t2*direction);
			posY = (float) (posY0 + iniSpeedY * t2 + (7/2 * Math.pow(t2,2) ));

			if (explodingTimer>TIME_EXPLODING)
				status=STATUS_OVER;
			break;
		}

		drawX = (float) (posX-(AIRCRAFT_WIDTH*0.375)* scale);
		drawY = posY-(AIRCRAFT_HEIGHT/2)* scale;
	}



	public boolean isOver() {
		// TODO Auto-generated method stub
		return (status==STATUS_OVER);
	}

	public void setImpact(){

		status = STATUS_BOOM;
		explodingTimer=0;
		posX0=posX;
		posY0=posY;
		iniSpeedY=iniSpeedY-(acceleration*explodingTimer/1000);


	}

	public boolean impactDetected(FunctionalBullet b) {
		boolean impact = false;
		float diffX=Math.abs(posX-b.getPosX());
		float diffY=Math.abs(posY-b.getPosY());
		if (diffX<19 && diffY<19* scale){
			impact = true;
		}
		else impact = false;
		return impact;
	}

	public boolean isFlying(){
		return (status == STATUS_FLYING);
	}

}



