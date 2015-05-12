package de.kritzelbit.orebit.entities;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import de.kritzelbit.orebit.controls.GrabberControl;



public class Ship extends AbstractGameObject implements PhysicsCollisionListener {
    
    private float fuel;
    private int maxFuel;
    private int thrust;
    private int spin;
    private int grabberLength;
    private Geometry grabber;
    private Node shipNode;
    private PhysicsSpace physicsSpace;
    private boolean grabbing;
    //private Point2PointJoint grabJoint;

    public Ship(String name,
            Spatial spatial,
            RigidBodyControl physics,
            Geometry grabber,
            float mass,
            int fuel,
            int maxFuel,
            int thrust,
            int spin,
            int grabberLength) {
        
        super(name, spatial, physics, mass);
        this.fuel = fuel;
        this.maxFuel = maxFuel;
        this.thrust = thrust;
        this.spin = spin;
        this.grabber = grabber;
        this.grabberLength = grabberLength;
        this.physicsSpace = physics.getPhysicsSpace();
        this.shipNode = new Node();
        this.shipNode.attachChild(spatial);
        //setup grabber visuals
        this.grabber.addControl(new GrabberControl((Line)grabber.getMesh()));
        this.grabber.getControl(GrabberControl.class).setEnabled(false);
    }
    
    public Node getNode(){
        return shipNode;
    }
    
    public void toggleGrabber(){
        if (!grabbing){
            //set up dummy rigid body
            PhysicsRigidBody objPhys = null;
            
            //detect closest physics object
            for (PhysicsRigidBody body : physicsSpace.getRigidBodyList()){
                if (((Geometry)body.getUserObject()).getUserData("grabbable") == null) continue;
                if (objPhys == null
                        || body.getPhysicsLocation().distance(physics.getPhysicsLocation())
                        < objPhys.getPhysicsLocation().distance(physics.getPhysicsLocation())){
                    objPhys = body;
                }
            }
            
            //exit if no grabbable object was found or distance too long
            if (objPhys == null
                    || objPhys.getPhysicsLocation().distance(
                    physics.getPhysicsLocation()) > grabberLength) return;
            
            //set up visual joint representation
            this.grabber.getControl(GrabberControl.class)
                    .setTargets(spatial, (Geometry)objPhys.getUserObject());
            
            //show grabber ray
            shipNode.attachChild(grabber);
            grabbing = true;
        } else {
            //hide grabber ray
            shipNode.detachChild(grabber);
            grabbing = false;
        }
        // enable/disable grabber control
        this.grabber.getControl(GrabberControl.class).setEnabled(grabbing);
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
    
    public void collision(PhysicsCollisionEvent event) {
        if (event.getNodeA() == spatial || event.getNodeB() == spatial){
            destroy();
        }
    }
    
    private void destroy(){
        //TODO
    }
    
}
