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
    
    private int thruster;
    
    private RigidBodyControl physics;
    
    private Vector3f rotL;
    private Vector3f rotR;
    
    
    public FlightControl(Spatial spatial, int thrust, int spin){
        physics = spatial.getControl(RigidBodyControl.class);
        this.thruster = thrust;
        this.rotL = new Vector3f(0,0,spin);
        this.rotR = new Vector3f(0,0,-spin);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (thrust){
            Vector3f v = physics.getPhysicsRotation().getRotationColumn(0);
            physics.applyCentralForce(v.mult(thruster));
        }
        if (left){
            physics.setAngularVelocity(rotL);
        }
        if (right){
            physics.setAngularVelocity(rotR);
        }
        if (stopRot && !left && !right){
            physics.setAngularVelocity(Vector3f.ZERO);
        }
        
        //lock rotation on x and y axis
        Vector3f angV = physics.getAngularVelocity();
        physics.setAngularVelocity(new Vector3f(0 ,0 ,angV.z));
        
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
