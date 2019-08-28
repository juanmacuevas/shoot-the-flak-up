package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

abstract class GraphicComponent implements Renderable {

    private final Resources res;
    protected final float scale;

    public GraphicComponent(Resources res, DisplayMetrics metrics) {
        this.res = res;
        scale = Utils.scale(metrics);
    }


    protected Bitmap initBitmap(Bitmap image, int resource, float width, float height) {
        if (image == null) {
            image = BitmapFactory.decodeResource(res, resource);
            image = Bitmap.createScaledBitmap(image, (int) (width * scale), (int) (height * scale), true);
        }
        return image;

    }

    protected Matrix matrixTranslateAndMove(float transX, float transY, float angle, float pivotX, float pivotY) {
        Matrix m = new Matrix();
        m.postTranslate(transX, transY);
        m.postRotate(angle, pivotX, pivotY);
        return m;
    }
}
