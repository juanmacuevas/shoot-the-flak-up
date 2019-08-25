package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

class Landscape {
    private Bitmap mBackgroundImage;
    public Landscape(Resources res, DisplayMetrics metrics) {
        mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.background);
        mBackgroundImage=Bitmap.createScaledBitmap(
                mBackgroundImage, metrics.widthPixels, metrics.heightPixels, true);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBackgroundImage, 0, 0, null);
    }
}
