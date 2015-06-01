package de.kritzelbit.orebit.controls;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;



public class FlightControl extends AbstractControl implements PhysicsTickListener{
    
    public boolean thrust,left,right;
    
    private int thruster;
    private boolean boost;
    
    private RigidBodyControl physics;
    
    private Vector3f rotL;
    private Vector3f rotR;
    
    
    public FlightControl(RigidBodyControl physics, int thrust, int spin){
        this.physics = physics;
        this.thruster = thrust;
        this.rotL = new Vector3f(0,0,spin);
        this.rotR = new Vector3f(0,0,-spin);
    }

    
    @Override
    protected void controlUpdate(float tpf) {
        
    }
    
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

    
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        if (thrust){
            Vector3f v = physics.getPhysicsRotation().getRotationColumn(1);
            physics.applyCentralForce(v.mult(thruster*(boost ? 2 : 1)));
        }
        if (left){
            physics.setAngularVelocity(rotL);
        }
        if (right){
            physics.setAngularVelocity(rotR);
        }
        if (!left && !right){
            physics.setAngularVelocity(new Vector3f(0, 0, 0));
        }
        
        //lock rotation on x and y axis
        physics.setAngularVelocity(new Vector3f(0, 0, physics.getAngularVelocity().z));
    }

    
    public void physicsTick(PhysicsSpace space, float tpf) {
        
    }
    
    public void setBoost(boolean enabled){
        boost = enabled;
    }
}
