package de.kritzelbit.orebit.data;

import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={"planetID", "distance", "radius", "mass", "speed", "colorR", "colorG", "colorB"})
public class SatelliteData {
    
    //default values
    private String planetID;
    private float distance = 2;
    private float radius = 1;
    private float mass = 2;
    private float speed = 1;
    private float colorR = 0.2f + ((float)Math.random()*0.8f);
    private float colorG = 0.2f + ((float)Math.random()*0.8f);
    private float colorB = 0.2f + ((float)Math.random()*0.8f);
    

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
