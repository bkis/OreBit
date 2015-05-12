package de.kritzelbit.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author boss
 */
public class ShipGravityIndicatorControl extends AbstractControl {
    
    private Geometry shipGeom;
    
    
    public ShipGravityIndicatorControl(Geometry shipGeom){
        this.shipGeom = shipGeom;
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.setLocalTranslation(
                shipGeom.getWorldTranslation().add(
                shipGeom.getControl(RigidBodyControl.class)
                .getGravity().normalize().mult(3)));
        float strength = shipGeom.getControl(RigidBodyControl.class)
                .getGravity().length()/7;
        ColorRGBA color = new ColorRGBA(strength, 1-strength, 0, 1);
        ((Geometry)spatial).getMaterial().setColor("Color", color);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }

}
