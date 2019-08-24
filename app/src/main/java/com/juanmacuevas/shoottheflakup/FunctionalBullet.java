package com.juanmacuevas.shoottheflakup;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;



public class FunctionalBullet implements Renderable{

	private static final int STATUS_FLYING = 0;

	private static final int STATUS_BOOM = 1;

	private static final int STATUS_OVER = 2;

	private static final int BULLET_RADIUS  = 4;


	private static final long TIME_EXPLODING  = 100;


	private long time;

	private int status;
	private int power;
	private float angle;
	private float posX0;
	private float posY0;
	private float posX;
	private float posY;
	private float iniSpeedX;
	private float iniSpeedY;

	private Paint paint;

	private long explodingTimer;

	public FunctionalBullet(int power,float angle,int x0,int y0){
		time=0;
		paint = new Paint();
		this.power = power;
		posX0=x0;
		posY0 = y0;
		//if (power<15) power=15;
		iniSpeedY=(float) (Math.sin(angle)*power*1.5) ;
		iniSpeedX=(float) (Math.cos(angle)*power*1.5);

	}

	public void draw(Canvas c) {
		// TODO Auto-generated method stub
		if (status == STATUS_FLYING)
			paint.setColor(Color.BLACK);
		else paint.setColor(Color.RED);
		c.drawCircle(posX, posY, BULLET_RADIUS * FuncionalTank.scale, paint);

	}

	public void update(long elapsedTime) {
		switch (status){		
		case STATUS_FLYING:
			time+=elapsedTime;
			double t2=(double)(time)/100;
			// TODO Auto-generated method stub
			posX=(float) (posX0 + iniSpeedX*t2);
			posY=(float) (posY0 - (iniSpeedY*t2 - (9.8 *Math.pow(t2, 2)/2)));

			if (posY>FuncionalTank.tankBottom) {

				status=STATUS_BOOM;
				posY = FuncionalTank.tankBottom;
				explodingTimer = 0;

			}
			break;

		case STATUS_BOOM:
			explodingTimer+=elapsedTime;
			if (explodingTimer>TIME_EXPLODING)
				status=STATUS_OVER;
			break;
		}
	}

	public boolean isExploding() {
		// TODO Auto-generated method stub
		return (status==STATUS_BOOM);
	}

	public boolean isOver() {
		// TODO Auto-generated method stub
		return (status==STATUS_OVER);
	}

	public void setImpact(){

		status = STATUS_BOOM;
		explodingTimer=0;
	}

	public float getPosX() {
		// TODO Auto-generated method stub
		return posX;
	}

	public float getPosY() {
		// TODO Auto-generated method stub
		return posY;
	}


	public boolean isFlying(){
		return (status == STATUS_FLYING);
	}

}
