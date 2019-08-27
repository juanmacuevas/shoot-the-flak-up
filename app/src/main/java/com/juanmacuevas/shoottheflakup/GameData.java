package com.juanmacuevas.shoottheflakup;

class GameData {
    private int impacts;
    private float angle;
    private int power;

    public int getPower() {
        return power;
    }

    public float getAngle() {
        return angle;
    }

    public int getImpacts() {
        return impacts;
    }

    public void setImpacts(int impacts) {
        this.impacts=impacts;
    }

    public void setAngle(float angle) {
        this.angle=angle;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
