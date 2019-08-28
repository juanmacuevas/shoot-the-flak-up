package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

public class FunctionalAircraft extends GraphicComponent  {

	private static final int STATUS_FLYING = 0;
	private static final int STATUS_BOOM = 1;
	private static final int STATUS_OVER = 2;

	private static final float AIRCRAFT_WIDTH  = (float) (127*0.35);
	private static final float AIRCRAFT_HEIGHT  = (float) (134*0.35);

	private static final float TIME_FLYING = 5;
	private static final long TIME_EXPLODING  = 800;

	private static Bitmap aircraftImg;
	private static Bitmap aircraftDownImg;
	private Bitmap currentImg;
	private long timeFlying;

	private int status;
	private float posX;
	private float posY;
	private double iniSpeedX;
	private double iniSpeedY;
	private float initPointX;
	private float posY0;

	private float drawX;
	private float drawY;

	private int angle;

	private int leftOrRight;
	private float acceleration;

	private long explodingTimer;

	public FunctionalAircraft(Resources res, DisplayMetrics metrics) {
		super(res, metrics);

		timeFlying =0;
        angle = 0;
        status = STATUS_FLYING;
        initValues(metrics);

        aircraftImg = initBitmap(aircraftImg, R.drawable.aircraft, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
        aircraftDownImg = initBitmap(aircraftDownImg, R.drawable.aircraftdown, AIRCRAFT_WIDTH, AIRCRAFT_HEIGHT);
		currentImg = aircraftImg;
	}

    private void initValues(DisplayMetrics metrics) {
        //random values
        leftOrRight = (Math.random()<0.5?1:-1);
        initPointX = (float) (metrics.widthPixels/2 + (metrics.widthPixels*-this.leftOrRight *Math.random()));
        int lowerPosition = metrics.heightPixels;
        acceleration = (float) (lowerPosition/Math.pow(TIME_FLYING/2,2));
        iniSpeedX = ( metrics.widthPixels / TIME_FLYING) ;
        iniSpeedY=(TIME_FLYING*acceleration/2)-1/TIME_FLYING;

    }

    public void draw(Canvas c) {

	    Matrix m = matrixTranslateAndMove(drawX,drawY,angle,posX,posY);
		c.drawBitmap(currentImg, m, null);

	}

    public void update(long elapsedTime) {

	    if (status == STATUS_FLYING){
	        handleFlying(elapsedTime);
        }else if (status == STATUS_BOOM) {
            handleExploded(elapsedTime);
        }
		drawX = (float) (posX-(AIRCRAFT_WIDTH*0.375)* scale);
		drawY = posY-(AIRCRAFT_HEIGHT/2)* scale;
	}


    private void handleFlying(long elapsedTime) {
        timeFlying +=elapsedTime;
        double timeFlyingSeconds=(double)(timeFlying)/1000;
        posX = (float) (initPointX +  iniSpeedX*timeFlyingSeconds* leftOrRight);
        posY = (float) (iniSpeedY * timeFlyingSeconds - (acceleration/2 * Math.pow(timeFlyingSeconds,2) ));
        angle = (leftOrRight >0?180:0)+   (int) (Math.atan((iniSpeedY-acceleration*timeFlyingSeconds)/iniSpeedX* leftOrRight)*180/Math.PI);
        if (posY<0) status=STATUS_OVER;

	}
    private void handleExploded(long elapsedTime) {
        explodingTimer+=elapsedTime;
        double  t2=(double)(explodingTimer)/1000;
        posX = (float) (initPointX +  iniSpeedX*t2* leftOrRight);
        posY = (float) (posY0 + iniSpeedY * t2 + (7.0/2 * Math.pow(t2,2) ));
        if (explodingTimer>TIME_EXPLODING)
            status=STATUS_OVER;
    }


    public boolean isOver() {
		return (status==STATUS_OVER);
	}

	public void setImpact(){

		status = STATUS_BOOM;
		currentImg = aircraftDownImg;
		explodingTimer=0;
		initPointX =posX;
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



