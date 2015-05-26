package de.kritzelbit.orebit.data;

import de.kritzelbit.orebit.util.RandomName;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={"id", "x", "y", "radius", "mass", "colorR", "colorG", "colorB"})
public class PlanetData {
    
    //default values
    private String id = RandomName.getRndName();
    private float x = 0;
    private float y = 0;
    private float radius = 5;
    private float mass = 5;
    private float colorR = 0.2f + ((float)Math.random()*0.8f);
    private float colorG = 0.2f + ((float)Math.random()*0.8f);
    private float colorB = 0.2f + ((float)Math.random()*0.8f);

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
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
