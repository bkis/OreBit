package de.bkis.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
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
        if (shipGeom.getControl(RigidBodyControl.class) == null) return;
        spatial.setLocalTranslation(
                shipGeom.getWorldTranslation().add(
                shipGeom.getControl(RigidBodyControl.class)
                .getGravity().normalize().mult(3)));
        float strength = shipGeom.getControl(RigidBodyControl.class)
                .getGravity().length()/25;
        ColorRGBA color = new ColorRGBA(strength, 1-strength, 0, 1);
        color.clamp();
        ((Geometry)spatial).getMaterial().setColor("Color", color);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (shipGeom == null || spatial == null) {
            setEnabled(false);
        }
    }

}
