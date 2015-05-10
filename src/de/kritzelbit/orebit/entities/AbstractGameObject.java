
package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


public abstract class AbstractGameObject {
    
    protected Spatial spatial;
    protected String name;
    protected RigidBodyControl physics;
    protected float mass;
    
    public AbstractGameObject(String name,
            Spatial spatial,
            RigidBodyControl physics,
            float mass){
        this.name = name;
        this.spatial = spatial;
        this.physics = physics;
        this.mass = mass;
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
    
    public float getMass(){
        return mass;
    }
    
    public void setMass(float mass){
        this.mass = mass;
    }
    
    public String getName(){
        return name;
    }
    
}
