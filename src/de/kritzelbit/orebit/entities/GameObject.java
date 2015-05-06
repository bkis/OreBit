
package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


public abstract class GameObject {
    
    protected Spatial spatial;
    protected String name;
    protected RigidBodyControl physics;
    
    public GameObject(String name, Spatial spatial, RigidBodyControl physics){
        this.name = name;
        this.spatial = spatial;
        this.physics = physics;
    }
    
    public Spatial getSpatial(){
        return spatial;
    }
    
    public RigidBodyControl getPhysicsControl(){
        return physics;
    }
    
    public float getX(){
        return physics.getPhysicsLocation().x;
    }
    
    public float getY(){
        return physics.getPhysicsLocation().y;
    }
    
    public void setLocation(float x, float y){
        physics.setPhysicsLocation(new Vector3f(x ,y ,0));
    }
    
}
