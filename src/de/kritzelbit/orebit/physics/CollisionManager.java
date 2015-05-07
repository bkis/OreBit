package de.kritzelbit.orebit.physics;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;


public class CollisionManager implements PhysicsCollisionListener {
    
    public CollisionManager(){
        //TODO
    }

    public void collision(PhysicsCollisionEvent event) {
        System.out.println(event.getNodeA().getName() + " - " + event.getNodeB().getName());
    }
    
}
