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
import de.kritzelbit.orebit.entities.Planet;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author boss
 */
public class ForcesControl extends AbstractControl {
    
    private Vector3f gravity;
    private Set<Planet> gravitySources;
    
    public ForcesControl(){
        super();
    }
    
    public ForcesControl(Set<Planet> gravitySources){
        super();
        this.gravitySources = gravitySources;
    }
    
    public void addGravitySource(Planet source){
        gravitySources.add(source);
    }
    
    private void applyForce(){
        calculateGravity();
        spatial.getControl(RigidBodyControl.class).setGravity(gravity);
        //System.out.println(gravity);
    }
    
//    public void applyForce(Vector3f force){
//        spatial.getControl(RigidBodyControl.class).setGravity(force);
//    }
    
    private void correctZAxis(){
        spatial.setLocalTranslation(
                spatial.getLocalTranslation().x,
                spatial.getLocalTranslation().y,
                0);
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
    
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        ForcesControl control = new ForcesControl(gravitySources);
        //TODO: copy parameters to new Control
        return control;
    }
    
    private void calculateGravity(){
        gravity = null;
        for (Planet source : gravitySources){
            float distance = source.getGeometry().getWorldTranslation().distance(spatial.getWorldTranslation());
            Vector3f direction = source.getGeometry().getWorldTranslation().subtract(spatial.getWorldTranslation());
            Vector3f g = direction.divide(FastMath.pow(distance, 2)).mult(source.getMass()*10);
            gravity = (gravity == null ? g : gravity.add(g));
        }
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
