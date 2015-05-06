package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;


public class Satellite extends GameObject {
    
    public Satellite(String name,
            Spatial spatial,
            RigidBodyControl physics) {
        
        super(name, spatial, physics);
    }

}
