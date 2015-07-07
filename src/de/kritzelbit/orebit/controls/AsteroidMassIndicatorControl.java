package de.kritzelbit.orebit.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;


public class AsteroidMassIndicatorControl extends AbstractControl {
    
    private Spatial toFollow;
    
    public AsteroidMassIndicatorControl(Spatial toFollow){
        this.toFollow = toFollow;
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.setLocalTranslation(toFollow.getWorldTranslation());
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
