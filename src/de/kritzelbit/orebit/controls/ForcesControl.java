package de.kritzelbit.orebit.controls;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author boss
 */
public class ForcesControl extends AbstractControl {
    
    private Vector3f velocity;
    private Set<Geometry> gravitySources;
    
    public ForcesControl(){
        super();
        this.velocity = Vector3f.ZERO;
    }
    
    public ForcesControl(Vector3f initialVelocity){
        super();
        this.velocity = initialVelocity;
    }
    
    public ForcesControl(Vector3f initialVelocity, Set<Geometry> gravitySources){
        super();
        this.velocity = initialVelocity;
        this.gravitySources = gravitySources;
    }
    
    public void addGravitySource(Geometry source){
        gravitySources.add(source);
    }
    
    private void applyForces(float tpf){
        for (Geometry source : gravitySources){
            if (source == spatial) continue;
            float radius = ((Sphere)source.getMesh()).getRadius();
            float ownRadius = ((Sphere)((Geometry)spatial).getMesh()).getRadius();
            float distance = source.getWorldTranslation().distance(spatial.getWorldTranslation());
            Vector3f direction = source.getWorldTranslation().subtract(spatial.getWorldTranslation());
            applyForce(direction.divide(FastMath.pow(distance, 2)).mult(radius*10).divide(ownRadius*2));
        }
        spatial.move(velocity.mult(tpf));
        spatial.getControl(RigidBodyControl.class).setPhysicsLocation(spatial.getLocalTranslation());
    }
    
    public void applyForce(Vector3f force){
        velocity = velocity.add(force);
    }
    
    private void correctZAxis(){
        spatial.setLocalTranslation(
                spatial.getLocalTranslation().x,
                spatial.getLocalTranslation().y,
                0);
    }

    @Override
    protected void controlUpdate(float tpf) {
        applyForces(tpf);
        correctZAxis();
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        ForcesControl control = new ForcesControl(velocity);
        //TODO: copy parameters to new Control
        return control;
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}
