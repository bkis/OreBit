package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;


public class Asteroid extends AbstractGameObject {
    
    public Asteroid(String name,
            Spatial spatial,
            RigidBodyControl physics,
            float mass) {
        super(name, spatial, physics, mass);
        spatial.setUserData("grabbable", true);
    }
    
}
