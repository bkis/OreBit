package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


public class Planet extends AbstractGameObject {
    
    private float radius;

    public Planet(String name, float radius, float mass, Spatial spatial, RigidBodyControl physics) {
        super(name, spatial, physics, radius, mass);
        this.radius = radius;
    }

    @Override
    public void setLocation(float x, float y){
        super.setLocation(x,y);
        spatial.setLocalTranslation(new Vector3f(x ,y ,0));
    }

}
