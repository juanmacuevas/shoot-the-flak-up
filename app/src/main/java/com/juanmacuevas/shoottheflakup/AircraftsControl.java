package com.juanmacuevas.shoottheflakup;

import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Iterator;

class AircraftsControl {
    private ArrayList<FunctionalAircraft> aircrafts;
    private long newaircraftimer;
    private DisplayMetrics dm;

    public AircraftsControl(DisplayMetrics dm) {
        aircrafts = new ArrayList<FunctionalAircraft>();
        newaircraftimer = 0;
        this.dm = dm;

    }

    public void draw(Canvas canvas) {
        for (Iterator it = aircrafts.iterator(); it.hasNext(); ) {
            FunctionalAircraft a = (FunctionalAircraft) it.next();
            if (a != null)
                a.draw(canvas);
        }
    }

    public void update(long timer, Iterable<FunctionalBullet> bullets, HUD
            hud, Vibrator vibrator) {
        ArrayList<FunctionalAircraft> newList = new ArrayList<>();
        for (FunctionalAircraft a : aircrafts) {
            if (a.isOver()) continue;
            a.update(timer);

            for (FunctionalBullet b : bullets) {
                if (a.isFlying() && b.isFlying() && (a.impactDetected(b))) {

                    hud.addImpact();
                    a.setImpact();
                    b.setImpact();
                    newList.add(new FunctionalAircraft(dm));

                    // Vibrate for 300 milliseconds
                    vibrator.vibrate(100);

                }
            }
            newList.add(a);

        }
        aircrafts = newList;
        newaircraftimer += timer;
        if (newaircraftimer > 3000) {
            aircrafts.add(new FunctionalAircraft(dm));
            newaircraftimer = 0;
        }

    }
}
