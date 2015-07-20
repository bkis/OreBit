package de.kritzelbit.orebit.controls;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;


public class MagnetControl extends AbstractControl implements PhysicsTickListener {
    
    private RigidBodyControl target;
    private RigidBodyControl physics;
    private float speed;

    public MagnetControl(RigidBodyControl target, float speed) {
        this.target = target;
        this.speed = speed;
    }
    
    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);
        if (physics == null)
            physics = spatial.getControl(RigidBodyControl.class);
    }

    @Override
    protected void controlUpdate(float tpf) {
        correctZAxis();
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        if (target == null){
            setEnabled(false);
            return;
        }
        
        physics.applyCentralForce(
                target.getPhysicsLocation().subtract(
                physics.getPhysicsLocation()).normalizeLocal()
                .mult(speed*5));
    }

    public void physicsTick(PhysicsSpace space, float tpf) {}
    
    private void correctZAxis(){
        physics.setPhysicsLocation(new Vector3f(
                physics.getPhysicsLocation().x,
                physics.getPhysicsLocation().y,
                0));
    }
    
}
