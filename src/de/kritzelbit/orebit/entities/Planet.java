package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;


public class Planet extends GameObject{
    
    private float radius;
    private float mass;

    public Planet(String name, float radius, float mass, Spatial spatial, RigidBodyControl physics) {
        super(name, spatial, physics);
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
