package de.kritzelbit.orebit.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Line;


public class GrabberVisualControl extends AbstractControl {
    
    private Spatial s1;
    private Spatial s2;
    private Line line;
    
    
    public GrabberVisualControl(Line line){
        this.line = line;
    }
    

    @Override
    protected void controlUpdate(float tpf) {
        line.updatePoints(s1.getWorldTranslation(), s2.getWorldTranslation());
    }
    
    public void setTargets(Spatial s1, Spatial s2){
        this.s1 = s1;
        this.s2 = s2;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
   
}
