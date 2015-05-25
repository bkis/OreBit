package de.kritzelbit.orebit.data;



public class AsteroidData {
    
    //default values
    private float x = 0;
    private float y = 0;
    private float radius = 1;
    private float mass = 1;
    private float initVelX = 10;
    private float initVelY = 10;

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

    public float getInitVelX() {
        return initVelX;
    }

    public void setInitVelX(float initVelX) {
        this.initVelX = initVelX;
    }

    public float getInitVelY() {
        return initVelY;
    }

    public void setInitVelY(float initVelY) {
        this.initVelY = initVelY;
    }

}
