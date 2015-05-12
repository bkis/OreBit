package de.kritzelbit.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import java.util.Set;


public class ForcesControl extends AbstractControl {
    
    private Vector3f gravity;
    private Set<AbstractGameObject> gSources;
    

    public ForcesControl(Set<AbstractGameObject> gravitySources){
        super();
        this.gSources = gravitySources;
    }
    
    public void addGravitySource(AbstractGameObject source){
        gSources.add(source);
    }
    
    private void applyForce(){
        calculateGravity();
        spatial.getControl(RigidBodyControl.class).setGravity(gravity);
    }
    
//    public void applyForce(Vector3f force){
//        spatial.getControl(RigidBodyControl.class).setGravity(force);
//    }
    
    private void correctZAxis(){
        spatial.getControl(RigidBodyControl.class).setPhysicsLocation(
                new Vector3f(
                spatial.getControl(RigidBodyControl.class).getPhysicsLocation().x,
                spatial.getControl(RigidBodyControl.class).getPhysicsLocation().y,
                0));
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        applyForce();
        correctZAxis();
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    private void calculateGravity(){
        gravity = null;
        for (AbstractGameObject source : gSources){
            if (source.getSpatial() == spatial) continue;
            float distance = source.getPhysicsControl()
                    .getPhysicsLocation()
                    .distance(spatial.getWorldTranslation())
                    - source.getRadius()*0.8f+0.4f;
            Vector3f direction = source.getPhysicsControl()
                    .getPhysicsLocation()
                    .subtract(spatial.getWorldTranslation());
            Vector3f g = direction.divide(FastMath.pow(distance, 2))
                    .mult(source.getMass()*10);
            gravity = (gravity == null ? g : gravity.add(g));
        }
    }

}
