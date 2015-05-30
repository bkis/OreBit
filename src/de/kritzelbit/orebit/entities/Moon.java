package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;


public class Moon extends AbstractGameObject {
    
    
    public Moon(String name,
            Spatial spatial,
            RigidBodyControl physics,
            float radius,
            float mass) {
        super(name, spatial, physics, radius, mass);
        spatial.setUserData("type", "moon");
    }
    
}
