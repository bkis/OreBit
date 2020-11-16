package de.bkis.orebit.controls;

import com.jme3.bullet.control.GhostControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;



public class CheckpointDissolveControl extends AbstractControl{

    @Override
    protected void controlUpdate(float tpf) {
        spatial.scale(0.8f);
        if (spatial.getLocalScale().z <= 0.5f){
            spatial.removeFromParent();
            spatial.getControl(GhostControl.class).setEnabled(false);
            spatial.removeControl(this);
            setEnabled(false);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
