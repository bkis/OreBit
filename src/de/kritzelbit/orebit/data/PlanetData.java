package de.kritzelbit.orebit.data;

import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={"id", "x", "y", "radius", "mass"})
public class PlanetData {
    
    //default values
    private String id = "";
    private float x = 0;
    private float y = 0;
    private float radius = 5;
    private float mass = 5;

    
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

}
