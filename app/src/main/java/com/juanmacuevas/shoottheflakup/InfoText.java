package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

public class InfoText extends GraphicComponent{

    private static final int TEXT_INFO_LEFT_MARGIN = 10;
    private static final int TEXT_ANGLE_TOP_MARGIN = 25;
    private static final int TEXT_POWER_TOP_MARGIN = 50;
    private static final int TEXT_COUNTER_TOP_MARGIN = 75;
    private static final int TEXT_SIZE = 20;

    private final Paint paint;
    private int power;
    private int angle;
    private int impacts;

    public InfoText(Resources res, DisplayMetrics metrics) {
        super(res,metrics);
        paint = new Paint();

    }

    public void draw(Canvas c) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);
        paint.setTextSize(TEXT_SIZE*scale);

////        int powerInfo=(tank.getPower()==0?tank.getLastBulletPower():tank.getPower());
//        c.drawText("Angle: "+(int) (tank.getAngle()*180/Math.PI)+"°", TEXT_INFO_LEFT_MARGIN * scale, TEXT_ANGLE_TOP_MARGIN * scale, paint);
//        c.drawText("Power: "+powerInfo, TEXT_INFO_LEFT_MARGIN * scale, TEXT_POWER_TOP_MARGIN * scale, paint);
//        c.drawText("Impacts: "+impactCounter, TEXT_INFO_LEFT_MARGIN * scale, TEXT_COUNTER_TOP_MARGIN * scale, paint);

        c.drawText("Angle: "+angle+"°", TEXT_INFO_LEFT_MARGIN * scale, TEXT_ANGLE_TOP_MARGIN * scale, paint);
        c.drawText("Power: "+power, TEXT_INFO_LEFT_MARGIN * scale, TEXT_POWER_TOP_MARGIN * scale, paint);
        c.drawText("Impacts: "+impacts, TEXT_INFO_LEFT_MARGIN * scale, TEXT_COUNTER_TOP_MARGIN * scale, paint);

    }

    public void update(long elapsedTime) {

    }

    public void setData(GameData data){
        power=data.getPower();
        angle = (int) (data.getAngle()*180/Math.PI) ;
        impacts = data.getImpacts();


    }
}
