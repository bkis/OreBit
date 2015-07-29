package de.kritzelbit.orebit.controls;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import java.util.Set;


public class ForcesControl extends AbstractControl implements PhysicsTickListener{
    
    private static final float GRAVITY_DAMPING = 1.5f;
    
    private Vector3f gravity;
    private static Set<AbstractGameObject> gSources;
    private RigidBodyControl physics;
    

    public ForcesControl(Set<AbstractGameObject> gravitySources){
        super();
        this.gSources = gravitySources;
    }
    
    private void applyForce(){
        calculateGravity();
        if (gravity != null)
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
    }
    
    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);
        if (physics == null)
            physics = spatial.getControl(RigidBodyControl.class);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    private void calculateGravity(){
        gravity = null;
        for (AbstractGameObject source : gSources){
            if (source.getSpatial() == spatial || source.getMass() == 0) continue;
            float distance = source.getPhysicsControl()
                    .getPhysicsLocation()
                    .distance(spatial.getWorldTranslation())
                    - source.getRadius()*0.9f;
            Vector3f direction = source.getPhysicsControl()
                    .getPhysicsLocation()
                    .subtract(spatial.getWorldTranslation());
            Vector3f g = direction.divide(FastMath.pow(distance, 1.05f)) //actually: distance^2
                    .mult(source.getMass());
            gravity = (gravity == null ? g : gravity.add(g));
        }
    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        if (spatial == null) return;
        correctZAxis();
        applyForce();
    }

    public void physicsTick(PhysicsSpace space, float tpf) {
    }

}
