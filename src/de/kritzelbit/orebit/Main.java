package de.kritzelbit.orebit;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;


public class Main extends SimpleApplication implements PhysicsCollisionListener{
    
    private List<Geometry> geoms;
    private BulletAppState bulletAppState;


    public static void main(String[] args) {
        Main app = new Main();
        
        //configure settings
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024, 768);
        settings.setMinResolution(1024, 768);
        settings.setVSync(false);
        settings.setFullscreen(false);
        settings.setTitle("Ore Bit");
        
        app.showSettings = false;
        app.setSettings(settings);
        app.start();
    }
    
    
    @Override
    public void simpleInitApp() {
        
        //physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);
        getPhysicsSpace().setGravity(Vector3f.ZERO);
        
        //test cam settings
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(0,0,100));
        
        geoms = new ArrayList<Geometry>();

        //test sphere planet
        Sphere s = new Sphere(32, 32, 1);
        Geometry sphere = new Geometry("Sphere", s);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", ColorRGBA.Blue);
        mat.setColor("Ambient", ColorRGBA.Blue);
        mat.setColor("Specular", ColorRGBA.Blue);
        sphere.setMaterial(mat);
        RigidBodyControl spherePhysics = new RigidBodyControl(0f);
        spherePhysics.setMass(FastMath.pow(s.getRadius()+1,1));
        sphere.addControl(spherePhysics);
        getPhysicsSpace().add(spherePhysics);
        spherePhysics.setRestitution(0.4f);
        rootNode.attachChild(sphere);
        geoms.add(sphere);
        
        //test sphere object
        Sphere o = new Sphere(32, 32, 2);
        Geometry obj = new Geometry("Sphere", o);
        obj.setMaterial(mat);
        obj.move(10, -20, 0);
        RigidBodyControl objPhysics = new RigidBodyControl(0f);
        objPhysics.setMass(FastMath.pow(o.getRadius()+1,1));
        obj.addControl(objPhysics);
        getPhysicsSpace().add(objPhysics);
        objPhysics.setRestitution(0.4f);
        rootNode.attachChild(obj);
        geoms.add(obj);
        
        //test sphere object
        Sphere p = new Sphere(32, 32, 1);
        Geometry pl = new Geometry("Sphere", p);
        pl.setMaterial(mat);
        pl.move(20, -20, 0);
        RigidBodyControl plPhysics = new RigidBodyControl(0f);
        plPhysics.setMass(FastMath.pow(p.getRadius()+1,1));
        pl.addControl(plPhysics);
        getPhysicsSpace().add(plPhysics);
        plPhysics.setRestitution(0.4f);
        rootNode.attachChild(pl);
        geoms.add(pl);
        
        //add controls
        //sphere.addControl(new ForcesControl(new Vector3f(0,-40,0), new HashSet<Geometry>(geoms)));
        //obj.addControl(new ForcesControl(new Vector3f(0,40,0), new HashSet<Geometry>(geoms)));
        //pl.addControl(new ForcesControl(Vector3f.ZERO, new HashSet<Geometry>(geoms)));
        
        //ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient); 
        
        //sunlight
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun); 
    }
    
    private Vector3f calculateGravityFor(Geometry geom){
        Vector3f g = null;
        for (Geometry source : geoms){
            if (source == geom) continue;
            float radius = ((Sphere)source.getMesh()).getRadius();
            float ownRadius = ((Sphere)((Geometry)geom).getMesh()).getRadius();
            float distance = source.getWorldTranslation().distance(geom.getWorldTranslation());
            Vector3f direction = source.getWorldTranslation().subtract(geom.getWorldTranslation());
            if (g == null) g = direction.divide(FastMath.pow(distance, 2)).mult(radius*20).divide(ownRadius*2);
            else g.add(direction.divide(FastMath.pow(distance, 2)).mult(radius*20).divide(ownRadius*2));
        }
        //System.out.println(g);
        return g.mult(10);
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    private Node createPhysicsNode(AssetManager manager, CollisionShape shape, float mass) {
        Node node = new Node("PhysicsNode");
        RigidBodyControl control = new RigidBodyControl(shape, mass);
        node.addControl(control);
        return node;
    }

    @Override
    public void simpleUpdate(float tpf) {
        for (Geometry g : geoms){
            g.getControl(RigidBodyControl.class).setGravity(calculateGravityFor(g));
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void collision(PhysicsCollisionEvent event) {
        //TODO
    }
}
