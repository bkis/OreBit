package de.kritzelbit.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;



public class ThrusterVisualsControl extends AbstractControl {
    
    private Geometry shipGeom;
    
    public ThrusterVisualsControl(Geometry shipGeom){
        this.shipGeom = shipGeom;
    }

    @Override
    protected void controlUpdate(float tpf) {
        ((ParticleEmitter)spatial).getParticleInfluencer().setInitialVelocity(
                shipGeom.getWorldRotation().getRotationColumn(0).negate().mult(20));
        spatial.setLocalTranslation(
                shipGeom.getWorldTranslation());
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}