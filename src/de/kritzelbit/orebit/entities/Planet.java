package de.kritzelbit.orebit.entities;

import com.jme3.scene.Spatial;


public class Planet extends GameObject{
    
    private float radius;
    private float mass;

    public Planet(String name, int radius, int mass, Spatial spatial) {
        super(name, spatial);
        this.radius = radius;
        this.mass = mass;
    }

    public float getRadius() {
        return radius;
    }

    public float getMass() {
        return mass;
    }
    
}
