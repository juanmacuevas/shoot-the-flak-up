package com.juanmacuevas.shoottheflakup;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Vibrator;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Iterator;

class AircraftsControl {
    private ArrayList<FunctionalAircraft> aircrafts;
    private long newaircraftimer;
    private DisplayMetrics dm;
    SoundManager soundManager;

    public AircraftsControl(DisplayMetrics dm, SoundManager soundManager, Resources res) {
        this.soundManager = soundManager;
        aircrafts = new ArrayList<>();
        newaircraftimer = 0;
        this.dm = dm;
        new FunctionalAircraft(dm).initResources(res);

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
                    soundManager.playExplode();
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
