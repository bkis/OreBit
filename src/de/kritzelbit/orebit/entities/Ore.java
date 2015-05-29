package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;



public class Ore extends AbstractGameObject {

    public Ore(String name, Spatial spatial, RigidBodyControl physics, float size, float mass) {
        super(name, spatial, physics, size, mass);
        spatial.setUserData("grabbable", true);
        spatial.setUserData("type", "ore");
    }
    
    
}
