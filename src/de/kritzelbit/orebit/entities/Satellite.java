package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;


public class Satellite extends AbstractGameObject {
    
    
    public Satellite(String name,
            Spatial spatial,
            RigidBodyControl physics,
            float mass) {
        super(name, spatial, physics, mass);
    }
    
}