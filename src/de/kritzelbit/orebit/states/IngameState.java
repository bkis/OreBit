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
        bulletAppState.setDebugEnabled(true);
        getPhysicsSpace().setGravity(Vector3f.ZERO);
        
        //cam settings
        cam.setLocation(new Vector3f(0,0,100));
        
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
    
    private Geometry createPlanet(String id, float radius, float mass, ColorRGBA color){
        //mesh,geometry
        Sphere s = new Sphere(32, 32, radius);
        Geometry planet = new Geometry(id, s);
        //material
        Material planetMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        planetMat.setColor("Diffuse", color);
        planetMat.setColor("Ambient", color);
        planetMat.setColor("Specular", color);
        planet.setMaterial(planetMat);
        //physics
        RigidBodyControl planetPhysics = new RigidBodyControl(0f);
        planetPhysics.setRestitution(0.4f); //bouncyness
        planetPhysics.setMass(0); //static object
        planet.addControl(planetPhysics);
        getPhysicsSpace().add(planetPhysics);
        planet.getControl(RigidBodyControl.class)
                .setPhysicsLocation(planet.getLocalTranslation());
        return planet;
    }
    
}
