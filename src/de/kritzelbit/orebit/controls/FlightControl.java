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
    
    private static final Vector3f rotL = new Vector3f(0,0,3);
    private static final Vector3f rotR = new Vector3f(0,0,-3);
    
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
            physics.applyCentralForce(v.mult(8));
        }
        if (left){
            physics.setAngularVelocity(rotL);
        }
        if (right){
            physics.setAngularVelocity(rotR);
        }
        if (stopRot){
            physics.setAngularVelocity(Vector3f.ZERO);
        }
        
        //lock rotation on x and y axis
        Vector3f angV = physics.getAngularVelocity();
        physics.setAngularVelocity(new Vector3f(0 ,0 ,angV.z));
        
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
