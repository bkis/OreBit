/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kritzelbit.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;



public class FlightControl extends AbstractControl {
    
    public boolean thrust,left,right,stopRot;
    
    private RigidBodyControl physics;
    
    private static final Vector3f rotL = new Vector3f(0,0,2);
    private static final Vector3f rotR = new Vector3f(0,0,-2);
    
    private float lockRotX;
    private float lockRotY;
    
    
    public FlightControl(Spatial spatial){
        physics = spatial.getControl(RigidBodyControl.class);
        lockRotX = physics.getPhysicsRotation().getX();
        lockRotY = physics.getPhysicsRotation().getY();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (thrust){
            Vector3f v = physics.getPhysicsRotation().getRotationColumn(0);
            physics.applyCentralForce(v.mult(10));
        }
        if (left){
            physics.applyTorque(rotL);
        }
        if (right){
            physics.applyTorque(rotR);
        }
        if (stopRot){
            physics.setPhysicsRotation(physics.getPhysicsRotation());
        }
        
        //lock rotation on x and y axis
//        physics.setPhysicsRotation(
//                new Quaternion(
//                    lockRotX,
//                    lockRotY,
//                    physics.getPhysicsRotation().getZ(),
//                    0));
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
