package de.bkis.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Spatial;



public class Base extends AbstractGameObject {
    
    public static final float BASE_MASS = 2;
    public static final float BASE_SIZE = 5;
    

    public Base(String name, Spatial spatial, RigidBodyControl physics) {
        super(name, spatial, physics, BASE_SIZE, BASE_MASS);
        spatial.setUserData("type", "base");
        spatial.setUserData("id", name);
    }
    
}
