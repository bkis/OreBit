/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kritzelbit.orebit.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;


public class AsteroidIndicatorControl extends AbstractControl {
    
    private Spatial toFollow;
    
    public AsteroidIndicatorControl(Spatial toFollow){
        this.toFollow = toFollow;
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.setLocalTranslation(toFollow.getWorldTranslation());
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
