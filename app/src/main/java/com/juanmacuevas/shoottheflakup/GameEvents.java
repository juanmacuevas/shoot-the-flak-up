package com.juanmacuevas.shoottheflakup;

import androidx.core.util.Pair;

interface GameEvents {
    void angleChanged(float angle);
    void aircraftExploded();
    void shootBullet(float angle, int i, Pair<Integer, Integer> bulletOrigin);
}
