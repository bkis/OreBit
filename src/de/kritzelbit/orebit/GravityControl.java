package de.kritzelbit.orebit;

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
import java.util.List;

/**
 *
 * @author boss
 */
public class GravityControl extends AbstractControl {
    
    private Vector3f velocity;
    
    public GravityControl(Vector3f initialVelocity){
        velocity = initialVelocity;
    }

    public void applyForce(List<Geometry> sources){
        for (Geometry geom : sources){
            if (geom == spatial) continue;
            float distance = geom.getWorldTranslation().distance(spatial.getWorldTranslation());
            float force = ((Sphere)geom.getMesh()).getRadius();
            Vector3f direction = geom.getWorldTranslation().subtract(spatial.getWorldTranslation());
            velocity = velocity.add(direction.divide(FastMath.pow(distance, 2)).mult(force*10));
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.move(velocity.mult(tpf/10));
        
        //correct z-Axis
        spatial.setLocalTranslation(
                spatial.getLocalTranslation().x,
                spatial.getLocalTranslation().y,
                0);
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }
    
    public Control cloneForSpatial(Spatial spatial) {
        GravityControl control = new GravityControl(velocity);
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
