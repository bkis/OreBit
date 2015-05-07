package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;



public class Ship extends AbstractGameObject {
    
    private float fuel;
    private int maxFuel;
    private int thrust;
    private int spin;
    private Node grabberNode;

    public Ship(String name,
            Spatial spatial,
            RigidBodyControl physics,
            Node grabberNode,
            float mass,
            int fuel,
            int maxFuel,
            int thrust,
            int spin) {
        
        super(name, spatial, physics, mass);
        this.fuel = fuel;
        this.maxFuel = maxFuel;
        this.thrust = thrust;
        this.spin = spin;
        this.grabberNode = grabberNode;
    }

    public boolean reduceFuel(){
        return --fuel > 0;
    }
    
    public boolean fillFuel(){
        return ++fuel >= maxFuel;
    }

    public float getFuel() {
        return fuel;
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public int getThrust() {
        return thrust;
    }

    public int getSpin() {
        return spin;
    }

    public Node getGrabberNode() {
        return grabberNode;
    }

}
