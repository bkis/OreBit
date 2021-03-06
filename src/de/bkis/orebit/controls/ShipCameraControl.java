package de.bkis.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;


public class ShipCameraControl extends AbstractControl {
    
    private float minCamDistance;
    private Vector3f velocity;
    private Camera cam;

    public ShipCameraControl(Camera cam, float minCamDistance) {
        this.minCamDistance = minCamDistance;
        this.cam = cam;
        this.velocity = Vector3f.ZERO.clone();
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        velocity = velocity.interpolateLocal(velocity,
                spatial.getControl(RigidBodyControl.class).getLinearVelocity(),
                0.005f);
        cam.setLocation(new Vector3f(
                spatial.getLocalTranslation().x - velocity.x/2,
                spatial.getLocalTranslation().y - velocity.y/2,
                minCamDistance + velocity.length()));
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
