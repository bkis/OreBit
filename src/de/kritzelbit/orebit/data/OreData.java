package de.kritzelbit.orebit.data;



public class OreData {
    
    //default values
    private String planetID;
    private float position = 0;
    private float x = 0;
    private float y = 0;
    private float radius = 1;
    private float mass = 0.5f;
    

    public String getPlanetID() {
        return planetID;
    }

    public void setPlanetID(String planetID) {
        this.planetID = planetID;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
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
