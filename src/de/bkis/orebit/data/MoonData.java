package de.bkis.orebit.data;

import de.bkis.orebit.util.RandomValues;


public class MoonData {
    
    //default values
    private String planetID;
    private float distance = 2;
    private float radius = 1;
    private float mass = 2;
    private float speed = 1;
    private float colorR = RandomValues.getRndFloat(0.2f, 1);
    private float colorG = RandomValues.getRndFloat(0.2f, 1);
    private float colorB = RandomValues.getRndFloat(0.2f, 1);
    

    public String getPlanetID() {
        return planetID;
    }

    public void setPlanetID(String planetID) {
        this.planetID = planetID;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getColorR() {
        return colorR;
    }

    public void setColorR(float colorR) {
        this.colorR = colorR;
    }

    public float getColorG() {
        return colorG;
    }

    public void setColorG(float colorG) {
        this.colorG = colorG;
    }

    public float getColorB() {
        return colorB;
    }

    public void setColorB(float colorB) {
        this.colorB = colorB;
    }

}
