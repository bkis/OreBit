package de.kritzelbit.orebit.entities;

import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;


public class Planet {
    
    private float radius;
    private float mass;
    private Geometry geom;

    public Planet(String id, float radius, float mass, float x, float y) {
        this.radius = radius;
        this.mass = mass;
        
        //mesh,geometry
        Sphere s = new Sphere(32, 32, radius);
        geom = new Geometry(id, s);
        geom.setLocalTranslation(x, y, 0);
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
