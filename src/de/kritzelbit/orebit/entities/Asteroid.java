package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public class Asteroid extends AbstractGameObject {
    
    private Geometry massIndicator;
    
    public Asteroid(String name,
            Spatial spatial,
            Geometry massIndicator,
            RigidBodyControl physics,
            float radius,
            float mass) {
        super(name, spatial, physics, radius, mass);
        this.massIndicator = massIndicator;
        spatial.setUserData("grabbable", true);
        spatial.setUserData("type", "asteroid");
    }
    
    public void init(Node attachTo){
        attachTo.attachChild(massIndicator);
    }
    
}
