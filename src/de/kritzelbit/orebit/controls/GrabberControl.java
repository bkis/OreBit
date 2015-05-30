package de.kritzelbit.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Line;


public class GrabberControl extends AbstractControl {
    
    private Spatial s1;
    private Spatial s2;
    private float distance;
    private Line line;
    private float wobble;
    
    
    public GrabberControl(Line line){
        this.line = line;
        this.wobble = 0;
    }
    

    @Override
    protected void controlUpdate(float tpf) {
        //update visuals (ray)
        line.updatePoints(s1.getWorldTranslation(), s2.getWorldTranslation());
        
        //control distance to hooked up object
        Vector3f distanceVector = s1.getWorldTranslation()
                .subtract(s2.getWorldTranslation());
        float offset = distanceVector.length() - distance;
        if (FastMath.abs(offset) > 0){
            s2.getControl(RigidBodyControl.class)
                    .applyCentralForce(distanceVector.mult(10*offset));
        }
        
        //line wobble
        line.setLineWidth(1 + FastMath.cos(wobble/3)*3);
        wobble++;
    }
    
    public void setTargets(Spatial pulling, Spatial pulled){
        this.s1 = pulling;
        this.s2 = pulled;
        this.distance = s1.getWorldTranslation().distance(s2.getWorldTranslation());
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        wobble = 0;
    }
}
