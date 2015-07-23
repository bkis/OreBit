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
        this.speed = 2 + (speed/10);
    }
    
    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);
        if (physics == null){
            physics = spatial.getControl(RigidBodyControl.class);
            float m = 0.07f - (speed/100);
            if (m <= 0) m = 0.01f;
            physics.setMass(m);
        }
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
        
        float v = getDynamicVelocity(
                target.getPhysicsLocation(),
                physics.getPhysicsLocation(),
                physics.getLinearVelocity());
        
        physics.applyCentralForce(
                target.getPhysicsLocation().subtract(
                physics.getPhysicsLocation()).normalizeLocal()
                .mult(speed*v));
    } 

    public void physicsTick(PhysicsSpace space, float tpf) {}
    
    private float getDynamicVelocity(Vector3f testPoint,
            Vector3f objectPosition,
            Vector3f objectVelocity) {
        return testPoint.subtract(objectPosition).dot(objectVelocity) > 0 ? 1 : 5;
    }
    
    private void correctZAxis(){
        physics.setPhysicsLocation(new Vector3f(
                physics.getPhysicsLocation().x,
                physics.getPhysicsLocation().y,
                0));
    }
    
}
