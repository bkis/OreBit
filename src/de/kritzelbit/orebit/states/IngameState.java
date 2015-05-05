package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import de.kritzelbit.orebit.controls.ForcesControl;
import de.kritzelbit.orebit.entities.Planet;
import java.util.HashSet;
import java.util.Set;


public class IngameState extends AbstractAppState {
    
    private SimpleApplication app;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private Camera cam;
    private Node rootNode;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //init fields
        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        this.assetManager = app.getAssetManager();
        this.cam = app.getCamera();
        this.rootNode = this.app.getRootNode();
        
        //init physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);
        getPhysicsSpace().setGravity(Vector3f.ZERO);
        
        //cam settings
        cam.setLocation(new Vector3f(0,0,100));
        
        //ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.Gray);
        rootNode.addLight(ambient); 
        
        //sunlight
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        //test planet 1
        Planet p1 = createPlanet("p1", 2, 7, 0, 0, ColorRGBA.Green);
        rootNode.attachChild(p1.getGeometry());
        
        //test planet 2
        Planet p2 = createPlanet("p2", 4, 10, 20, 10, ColorRGBA.Yellow);
        rootNode.attachChild(p2.getGeometry());
        
        //test asteroid
        Geometry a = createAsteroid("a", 1, 1, -2, -5, ColorRGBA.Red);
        rootNode.attachChild(a);
        Set<Planet> planets = new HashSet<Planet>();
        planets.add(p1);
        planets.add(p2);
        a.addControl(new ForcesControl(planets));
        a.getControl(RigidBodyControl.class).applyImpulse(Vector3f.UNIT_X, Vector3f.UNIT_X);
        a.getControl(RigidBodyControl.class).applyImpulse(Vector3f.UNIT_X, Vector3f.UNIT_X);
        a.getControl(RigidBodyControl.class).applyImpulse(Vector3f.UNIT_X, Vector3f.UNIT_X);
        a.getControl(RigidBodyControl.class).applyImpulse(Vector3f.UNIT_X, Vector3f.UNIT_X);
        a.getControl(RigidBodyControl.class).applyImpulse(Vector3f.UNIT_X, Vector3f.UNIT_X);
        a.getControl(RigidBodyControl.class).applyImpulse(Vector3f.UNIT_X, Vector3f.UNIT_X);
        a.getControl(RigidBodyControl.class).applyImpulse(Vector3f.UNIT_X, Vector3f.UNIT_X);
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    private Planet createPlanet(String id, float radius, float mass, float x, float y, ColorRGBA color){
        //planet instance
        Planet planet = new Planet(id, radius, mass);
        //material
        Material planetMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        planetMat.setBoolean("UseMaterialColors",true);
        planetMat.setColor("Diffuse", color);
        planetMat.setColor("Ambient", color);
        planetMat.setColor("Specular", ColorRGBA.White);
        planetMat.setFloat("Shininess", 5);
        planet.getGeometry().setMaterial(planetMat);
        //physics
        RigidBodyControl planetPhysics = new RigidBodyControl();
        planet.getGeometry().addControl(planetPhysics);
        getPhysicsSpace().add(planetPhysics);
        planetPhysics.setRestitution(0.5f); //bouncyness
        planetPhysics.setFriction(1);
        planetPhysics.setMass(0); //static object
        planet.getGeometry().getControl(RigidBodyControl.class)
                .setPhysicsLocation(new Vector3f(x,y,0));
        return planet;
    }
    
    private Geometry createAsteroid(String id, float radius, float mass, float x, float y, ColorRGBA color){
        //planet instance
        Sphere s = new Sphere(32, 32, radius);
        Geometry planet = new Geometry(id, s);
        //material
        Material planetMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        planetMat.setBoolean("UseMaterialColors",true);
        planetMat.setColor("Diffuse", color);
        planetMat.setColor("Ambient", color);
        planetMat.setColor("Specular", ColorRGBA.White);
        planetMat.setFloat("Shininess", 5);
        planet.setMaterial(planetMat);
        //physics
        RigidBodyControl planetPhysics = new RigidBodyControl();
        planet.addControl(planetPhysics);
        getPhysicsSpace().add(planetPhysics);
        planetPhysics.setRestitution(0.5f); //bouncyness
        planetPhysics.setFriction(1);
        planetPhysics.setMass(mass); //dynamic object
        planet.getControl(RigidBodyControl.class)
                .setPhysicsLocation(new Vector3f(x,y,0));
        return planet;
    }
    
}
