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
    private final Line line;
    private float wobble;
    
    
    public GrabberControl(final Line line){
        this.line = line;
        this.wobble = 1;
    }
    

    @Override
    protected void controlUpdate(float tpf) {
        //update visuals (ray)
        line.updatePoints(s1.getWorldTranslation(), s2.getWorldTranslation());
        
        //control distance to hooked up object
        Vector3f distanceVector = s1.getWorldTranslation()
                .subtract(s2.getWorldTranslation());
        float offset = distanceVector.length() - distance;
        Vector3f force = distanceVector.mult(100*offset);
        if (FastMath.abs(offset) > 0){
            s2.getControl(RigidBodyControl.class)
                    .applyCentralForce(force);
            s1.getControl(RigidBodyControl.class)
                    .applyCentralForce(force.negate());
        }
        
        //line wobble (method deprecated, but alternative doesn't work)
        line.setLineWidth(Math.max(1, 1 + (FastMath.cos(wobble/3)*3)));
        wobble += 2;
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
        wobble = 1;
    }
}
