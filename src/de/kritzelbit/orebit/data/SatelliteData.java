package de.kritzelbit.orebit.data;



public class SatelliteData {
    
    //default values
    private String planetID;
    private float distance = 2;
    private float radius = 1;
    private float mass = 2;
    private float speed = 1;
    

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

}
