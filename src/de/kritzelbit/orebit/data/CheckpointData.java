package de.kritzelbit.orebit.data;

import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={"radius", "colorR", "colorG", "colorB"})
public class CheckpointData {
    
    //default values
    private float radius = 3;
    private float colorR = 0.2f + ((float)Math.random()*0.8f);
    private float colorG = 0.2f + ((float)Math.random()*0.8f);
    private float colorB = 0.2f + ((float)Math.random()*0.8f);

    
    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
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
