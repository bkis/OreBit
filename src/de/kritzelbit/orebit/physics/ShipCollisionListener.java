package de.kritzelbit.orebit.physics;

import com.jme3.scene.Spatial;
import de.kritzelbit.orebit.entities.Ship;



public class ShipCollisionListener extends AbstractCollisionListener{
    
    Ship ship;

    public ShipCollisionListener(Ship ship) {
        super(ship.getSpatial().getName());
        this.ship = ship;
    }
    
    @Override
    public void collisionResult(Spatial targetSpatial, Spatial otherSpatial) {
        
    }
    
}
