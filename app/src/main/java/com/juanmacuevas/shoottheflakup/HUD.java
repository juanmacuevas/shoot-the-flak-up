package com.juanmacuevas.shoottheflakup;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;

public class HUD implements Renderable {

	private static final int BAR_POWER_LEFT_MARGIN = 116;
	private static final int BAR_POWER_RIGHT_MARGIN = 10;
	private static final int BAR_POWER_BOTTOM_MARGIN = 10;
	private static final int BAR_POWER_HEIGHT = 10;
	private static final int TEXT_INFO_LEFT_MARGIN = 10;
	private static final int TEXT_ANGLE_TOP_MARGIN = 25;
	private static final int TEXT_POWER_TOP_MARGIN = 50;
	private static final int TEXT_COUNTER_TOP_MARGIN = 75;
	private static final int TEXT_SIZE = 20;

	private float barPwrLeft;
	private float barPwrTop;
	private float barPwrRight;
	private float barPwrBottom;


	private int impactCounter;
	LinearGradient gradient;
	float scale;
	Paint paint;
	private FuncionalTank tank;

	public HUD(DisplayMetrics dm){
		paint = new Paint();

		scale = (float) dm.densityDpi/160;

		barPwrLeft = BAR_POWER_LEFT_MARGIN * scale;
		barPwrTop = dm.heightPixels - (BAR_POWER_BOTTOM_MARGIN * scale) - (BAR_POWER_HEIGHT * scale);
		barPwrRight = dm.widthPixels - (BAR_POWER_RIGHT_MARGIN * scale);
		barPwrBottom = dm.heightPixels - (BAR_POWER_HEIGHT * scale);

		impactCounter =0;
		gradient= new LinearGradient (barPwrLeft, barPwrTop, barPwrRight, barPwrTop, new int[]{Color.GREEN,Color.YELLOW, Color.RED},null, Shader.TileMode.CLAMP);

	}

	public void draw(Canvas c){

		int progress = (int) (barPwrLeft + tank.getPower()* (barPwrRight-barPwrLeft) /100);

		paint.setAlpha(255);
		paint.setShader(gradient);
		paint.setStyle(Style.FILL);
		c.drawRoundRect(new RectF(barPwrLeft, barPwrTop, progress, barPwrBottom), 4*scale, 4*scale, paint);
		paint.setShader(null);
		//paint the stroke
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(0);
		c.drawRoundRect(new RectF(barPwrLeft, barPwrTop, barPwrRight, barPwrBottom), 4*scale, 4*scale, paint);

		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(0);
		paint.setAntiAlias(true);
		paint.setTextSize(TEXT_SIZE*scale);
		int powerInfo=(tank.getPower()==0?tank.getLastBulletPower():tank.getPower());

		c.drawText("Angle: "+(int) (tank.getAngle()*180/Math.PI)+"°", TEXT_INFO_LEFT_MARGIN * scale, TEXT_ANGLE_TOP_MARGIN * scale, paint);
		c.drawText("Power: "+powerInfo, TEXT_INFO_LEFT_MARGIN * scale, TEXT_POWER_TOP_MARGIN * scale, paint);
		c.drawText("Impacts: "+impactCounter, TEXT_INFO_LEFT_MARGIN * scale, TEXT_COUNTER_TOP_MARGIN * scale, paint);

	}

	public void update(long elapsedTime) {
	}

	public void register(FuncionalTank tank) {
		this.tank = tank;
	}

	public void addImpact() {
		impactCounter++;

	}


}
