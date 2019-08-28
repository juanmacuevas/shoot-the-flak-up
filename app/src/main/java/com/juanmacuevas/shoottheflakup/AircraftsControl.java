package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Iterator;

class AircraftsControl {
    private GameEvents gameControl;
    private ArrayList<FunctionalAircraft> aircrafts;
    private long newaircraftimer;
    private DisplayMetrics dm;
    private Resources res;

    public AircraftsControl(Resources res,DisplayMetrics dm, GameEvents gameControl) {
        this.gameControl = gameControl;
        aircrafts = new ArrayList<>();
        newaircraftimer = 0;
        this.dm = dm;
        this.res = res;
        new FunctionalAircraft(res,dm);

    }

    public void draw(Canvas canvas) {
        for (Iterator it = aircrafts.iterator(); it.hasNext(); ) {
            FunctionalAircraft a = (FunctionalAircraft) it.next();
            if (a != null)
                a.draw(canvas);
        }
    }

    public void update(long timer, Iterable<FunctionalBullet> bullets) {
        ArrayList<FunctionalAircraft> newList = new ArrayList<>();
        for (FunctionalAircraft a : aircrafts) {
            if (a.isOver()) continue;
            a.update(timer);

            for (FunctionalBullet b : bullets) {
                if (a.isFlying() && b.isFlying() && (a.impactDetected(b))) {
                    gameControl.aircraftExploded();
                    a.setImpact();
                    b.setImpact();
                    newList.add(new FunctionalAircraft( res,dm));


                }
            }
            newList.add(a);

        }
        aircrafts = newList;
        newaircraftimer += timer;
        if (newaircraftimer > 3000) {
            aircrafts.add(new FunctionalAircraft(res,dm));
            newaircraftimer = 0;
        }

    }
}
