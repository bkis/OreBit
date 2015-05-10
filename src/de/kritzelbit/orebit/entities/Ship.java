package de.kritzelbit.orebit.entities;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.joints.Point2PointJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;



public class Ship extends AbstractGameObject implements PhysicsCollisionListener {
    
    private float fuel;
    private int maxFuel;
    private int thrust;
    private int spin;
    private int grabberLength;
    private Geometry grabber;
    private PhysicsSpace physicsSpace;
    private Point2PointJoint grabJoint;

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
    }
    
    public void toggleGrabber(boolean activate){
        if (activate && grabJoint == null){
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
            Vector3f connection = physics.getPhysicsLocation().subtract(objPhys.getPhysicsLocation());
            grabJoint = new Point2PointJoint(physics,
                    objPhys,
                    new Vector3f(0, 0, 0),
                    new Vector3f(connection.x, connection.y, 0));
            physicsSpace.add(grabJoint);
        } else {
            if (physicsSpace.getJointList().contains(grabJoint)){
                physicsSpace.remove(grabJoint);
                grabJoint.destroy();
                grabJoint = null;
            }
        }
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
    
    public void grab(Spatial target){
        //TODO
        HingeJoint joint = new HingeJoint(spatial.getControl(RigidBodyControl.class), // A
                     target.getControl(RigidBodyControl.class), // B
                     new Vector3f(0, -1, 0),  // pivot point local to A
                     new Vector3f(0, 4, 0),    // pivot point local to B 
                     Vector3f.UNIT_Z,           // DoF Axis of A (Z axis)
                     Vector3f.UNIT_Z);          // DoF Axis of B (Z axis)
        physicsSpace.add(joint);
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
