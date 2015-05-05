package de.kritzelbit.orebit.entities;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;


public class Planet {
    
    private float radius;
    private float mass;
    private Geometry geom;

    public Planet(String id, float radius, float mass) {
        this.radius = radius;
        this.mass = mass;
        
        //mesh,geometry
        Sphere s = new Sphere(32, 32, radius);
        geom = new Geometry(id, s);
    }

    public float getRadius() {
        return radius;
    }

    public float getMass() {
        return mass;
    }
    
    public Geometry getGeometry(){
        return geom;
    }
    
}
