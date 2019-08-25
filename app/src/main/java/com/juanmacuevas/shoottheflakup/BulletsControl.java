package com.juanmacuevas.shoottheflakup;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Iterator;

class BulletsControl {
    private final float tankBottom;
    private ArrayList<FunctionalBullet> bullets;

    public BulletsControl(float tankBottom) {
        bullets = new ArrayList<>();
        this.tankBottom = tankBottom;
    }

    public void draw(Canvas canvas) {
        for(Iterator it = bullets.iterator(); it.hasNext();){
            FunctionalBullet b = (FunctionalBullet) it.next();
            if (b!=null)
                b.draw(canvas);
        }

    }

    public void update(long timer) {
        FunctionalBullet b;
        int i = 0;
        while (!bullets.isEmpty() && bullets.size() > i) {
            b = bullets.get(i);
            if (b.isOver())
                bullets.remove(i);
            else {
                b.update(timer);
                i++;
            }
        }
    }

    public void addBullet(int power, float angle, int x, int y) {
        bullets.add(new FunctionalBullet(power, angle,x,y,tankBottom));

    }

    public Iterable<FunctionalBullet> iterable() {
        return bullets;
    }
}
