package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.ForcesControl;
import de.kritzelbit.orebit.entities.Planet;
import java.util.HashSet;
import java.util.Set;


public class IngameState extends AbstractAppState implements PhysicsCollisionListener {
    
    private SimpleApplication app;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private Camera cam;
    private Node rootNode;
    private Geometry ship;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //init fields
        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.rootNode = this.app.getRootNode();
        
        //init physics
        initPhysics();
        
        //init lights
        initLights();
        
        //cam settings
        cam.setLocation(new Vector3f(0,0,100));
        
        //init test scene
        initTestScene();
    
        //add collision listener
        getPhysicsSpace().addCollisionListener(this);
        
        //init keys
        initKeys();
    }
    
    @Override
    public void update(float tpf) {
//        cam.setLocation(new Vector3f(
//                ship.getLocalTranslation().x,
//                ship.getLocalTranslation().y,
//                100 + ship.));
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
    
    private void initLights(){
        //ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.Gray);
        rootNode.addLight(ambient); 
        //sunlight
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }
    
    private void initPhysics(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);
        getPhysicsSpace().setGravity(Vector3f.ZERO);
    }
    
    private void initTestScene(){
        //test planet 1
        Planet p1 = createPlanet(2, 7, 0, 0, ColorRGBA.Green);
        rootNode.attachChild(p1.getGeometry());
        //test planet 2
        Planet p2 = createPlanet(4, 10, 20, 10, ColorRGBA.Yellow);
        rootNode.attachChild(p2.getGeometry());
        //test asteroid
        Geometry a = createAsteroid("a", 1, 1, -2, -5, ColorRGBA.Red);
        rootNode.attachChild(a);
        Set<Planet> planets = new HashSet<Planet>();
        planets.add(p1);
        planets.add(p2);
        a.addControl(new ForcesControl(planets));
        a.getControl(RigidBodyControl.class).applyImpulse(new Vector3f(9,0,0), Vector3f.UNIT_X);
        //init ship
        initShip(planets);
    }
    
    private void initShip(Set<Planet> planets){
        Box s = new Box(1,1,1);
        ship = new Geometry("ship", s);
        Material planetMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        planetMat.setBoolean("UseMaterialColors",true);
        planetMat.setColor("Diffuse", ColorRGBA.Cyan);
        planetMat.setColor("Ambient", ColorRGBA.Cyan);
        planetMat.setColor("Specular", ColorRGBA.White);
        planetMat.setFloat("Shininess", 9);
        //planetMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/planet.png"));
        //planetMat.setTexture("NormalMap", assetManager.loadTexture("Textures/normal.png"));
        ship.setMaterial(planetMat);
        //physics
        RigidBodyControl planetPhysics = new RigidBodyControl();
        ship.addControl(planetPhysics);
        getPhysicsSpace().add(planetPhysics);
        planetPhysics.setRestitution(0.4f); //bouncyness
        planetPhysics.setFriction(1);
        planetPhysics.setMass(0.5f);
        ship.getControl(RigidBodyControl.class)
                .setPhysicsLocation(new Vector3f(10,10,0));
        ship.addControl(new ForcesControl(planets));
        ship.addControl(new FlightControl(ship));
        rootNode.attachChild(ship);
    }
    
    private Planet createPlanet(float radius, float mass, float x, float y, ColorRGBA color){
        //planet instance
        Planet planet = new Planet("planet", radius, mass);
        //material
        Material planetMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        planetMat.setBoolean("UseMaterialColors",true);
        planetMat.setColor("Diffuse", color);
        planetMat.setColor("Ambient", color);
        planetMat.setColor("Specular", ColorRGBA.White);
        planetMat.setFloat("Shininess", 5);
        //planetMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/planet.png"));
        //planetMat.setTexture("NormalMap", assetManager.loadTexture("Textures/normal.png"));
        planet.getGeometry().setMaterial(planetMat);
        //physics
        RigidBodyControl planetPhysics = new RigidBodyControl();
        planet.getGeometry().addControl(planetPhysics);
        getPhysicsSpace().add(planetPhysics);
        planetPhysics.setRestitution(0.4f); //bouncyness
        planetPhysics.setFriction(1);
        planetPhysics.setMass(0); //static object
        planet.getGeometry().getControl(RigidBodyControl.class)
                .setPhysicsLocation(new Vector3f(x,y,0));
        return planet;
    }
    
    private Geometry createAsteroid(String id, float radius, float mass, float x, float y, ColorRGBA color){
        //planet instance
        Sphere s = new Sphere(32, 32, radius);
//        Box s = new Box(radius, radius, radius);
        Geometry planet = new Geometry(id, s);
        //material
        Material planetMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        planetMat.setBoolean("UseMaterialColors",true);
        planetMat.setColor("Diffuse", color);
        planetMat.setColor("Ambient", color);
        planetMat.setColor("Specular", ColorRGBA.White);
        planetMat.setFloat("Shininess", 0);
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
    
    private void initKeys() {
        inputManager.addMapping("Thrust",  new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_RIGHT));

        inputManager.addListener(actionListener,"Left", "Right", "Thrust");
    }
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Thrust")) {
                ship.getControl(FlightControl.class).thrust = keyPressed;
            }
            if (name.equals("Right")) {
                ship.getControl(FlightControl.class).right = keyPressed;
                ship.getControl(FlightControl.class).stopRot = !keyPressed;
            }
            if (name.equals("Left")) {
                ship.getControl(FlightControl.class).left = keyPressed;
                ship.getControl(FlightControl.class).stopRot = !keyPressed;
            }
        }
    };

    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            //TODO
        }
    };

    public void collision(PhysicsCollisionEvent event) {
//        if (event.getNodeA().getName().equals("a") || event.getNodeB().getName().equals("a"))
//            System.out.println("BOOM");
    }
    
}
