package de.kritzelbit.orebit.controls;

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
        spatial.rotate(0, 0, speed*tpf);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
