package de.kritzelbit.orebit.physics;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.scene.Spatial;



public abstract class AbstractCollisionListener implements PhysicsCollisionListener{
    
    private String targetName;
    
    public AbstractCollisionListener(String targetName){
        this.targetName = targetName;
    }
    
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA().getName().equals(targetName)) {
            collisionResult(event.getNodeA(), event.getNodeB());
        } else if (event.getNodeB().getName().equals(targetName)) {
            collisionResult(event.getNodeB(), event.getNodeA());
        }
    }
    
    public abstract void collisionResult(Spatial targetSpatial, Spatial otherSpatial);
    
}
