package de.bkis.orebit.controls;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import de.bkis.orebit.entities.Ship;



public class FlightControl extends AbstractControl implements PhysicsTickListener{
    
    public boolean thrust,left,right;
    private boolean boost;
    
    private float fuel;
    
    private int thruster;
    private float booster;
    
    private RigidBodyControl physics;
    
    private Vector3f rotL;
    private Vector3f rotR;
    
    private Ship ship;
    
    
    public FlightControl(Ship ship, RigidBodyControl physics, int thrust, int spin, int fuel, int booster){
        this.ship = ship;
        this.physics = physics;
        this.thruster = thrust;
        this.booster = 1 + (float)((float)booster/10);
        this.fuel = fuel;
        this.rotL = new Vector3f(0,0,spin);
        this.rotR = new Vector3f(0,0,-spin);
        physics.setAngularFactor(0);
    }

    @Override
    protected void controlUpdate(float tpf) {
        
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        if (thrust && reduceFuel(tpf*40*(boost ? booster : 1))){
            Vector3f v = physics.getPhysicsRotation().getRotationColumn(1);
            physics.applyCentralForce(v.mult(tpf*60*thruster*(boost ? booster : 1)));
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
        ship.setBoost(enabled ? booster : 1);
    }
    
    public boolean isBoost(){
        return boost;
    }

    private boolean reduceFuel(float amount){
        if (fuel >= amount){
            fuel -= amount;
            return true;
        } else {
            fuel = 0;
            ship.setThrusterVisuals(false);
            return false;
        }
    }
    
    public float getFuel() {
        return fuel;
    }
    
    public void liftOff(){
        if (!thrust) return;
        physics.setPhysicsLocation(physics.getPhysicsLocation().add(
                physics.getPhysicsRotation().getRotationColumn(1).divide(10)));
        System.out.println("[PHY]\tbase lift off");
    }
    
}
