package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;


public class Planet extends AbstractGameObject {
    
    private float radius;

    public Planet(String name, float radius, float mass, Spatial spatial, RigidBodyControl physics) {
        super(name, spatial, physics, mass);
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

}
