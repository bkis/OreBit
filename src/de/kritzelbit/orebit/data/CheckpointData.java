package de.kritzelbit.orebit.data;

import javax.xml.bind.annotation.XmlType;


public class CheckpointData {
    
    //default values
    private float x = 0;
    private float y = 0;
    private float radius = 3;
    private int angle = 0;
    private float colorR = 0.5f + ((float)Math.random()*0.5f);
    private float colorG = 0.5f + ((float)Math.random()*0.5f);
    private float colorB = 0.5f + ((float)Math.random()*0.5f);

    
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
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
