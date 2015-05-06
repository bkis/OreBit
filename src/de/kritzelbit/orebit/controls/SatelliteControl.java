package de.kritzelbit.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;


public class SatelliteControl extends AbstractControl {
    
    private float speed;

    public SatelliteControl(float speed) {
        this.speed = speed;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        correctZAxis();
        spatial.rotate(0, 0, speed*tpf);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    private void correctZAxis(){
        spatial.getControl(RigidBodyControl.class).setPhysicsLocation(
                new Vector3f(
                spatial.getControl(RigidBodyControl.class).getPhysicsLocation().x,
                spatial.getControl(RigidBodyControl.class).getPhysicsLocation().y,
                0));
    }
    
}
