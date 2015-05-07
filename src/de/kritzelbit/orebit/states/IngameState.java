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
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.ForcesControl;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import de.kritzelbit.orebit.entities.Asteroid;
import de.kritzelbit.orebit.entities.Planet;
import de.kritzelbit.orebit.entities.Satellite;
import de.kritzelbit.orebit.util.GameObjectBuilder;
import java.util.HashSet;
import java.util.Set;


public class IngameState extends AbstractAppState implements PhysicsCollisionListener {
    
    private SimpleApplication app;
    private AppStateManager stateManager;
    private AssetManager assetManager;
    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private GameObjectBuilder gob;
    private Set<AbstractGameObject> gSources;
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
        this.gSources = new HashSet<AbstractGameObject>();
        this.rootNode = this.app.getRootNode();
        
        //init physics
        initPhysics();
        
        //init object builder
        this.gob = new GameObjectBuilder(this.app, bulletAppState.getPhysicsSpace(), gSources);
        
        //init lights
        initLights();
        
        //cam settings
        CameraNode camNode = new CameraNode("camNode", cam);
        cam.setLocation(new Vector3f(0,0,100));
//        cam.setLocation(new Vector3f(-10.108947f, 2.6281173f, 22.48542f));
//        cam.setRotation(new Quaternion(0.0015916151f, 0.9320993f, -0.004096228f, 0.3621763f));
        
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
//                cam.getLocation().z));
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
        bulletAppState.setDebugEnabled(true);
        getPhysicsSpace().setGravity(Vector3f.ZERO);
    }
    
    private void initTestScene(){
        //test planet 1
        Planet p1 = gob.buildPlanet("p1", 2, 7, ColorRGBA.Green);
        p1.setLocation(0, 0);
        rootNode.attachChild(p1.getSpatial());
        gSources.add(p1);
        //test planet 2
        Planet p2 = gob.buildPlanet("p2", 4, 10, ColorRGBA.Yellow);
        p2.setLocation(20, 10);
        rootNode.attachChild(p2.getSpatial());
        gSources.add(p2);
        //test satellite
        Satellite s1 = gob.buildSatellite("s1", 1, 5, ColorRGBA.White, p1, 4, 2);
        rootNode.attachChild(s1.getSpatial());
        gSources.add(s1);
        //test asteroid
        Asteroid a1 = gob.buildAsteroid("a1", 1, 1, ColorRGBA.Red, -10, -8, 10, 0);
        rootNode.attachChild(a1.getSpatial());
        gSources.add(a1);
        //init ship
        initShip(gSources);
    }
    
    private void initShip(Set<AbstractGameObject> gSources){
        Box s = new Box(1,1,1);
        ship = new Geometry("ship", s);
        Material shipMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        shipMat.setBoolean("UseMaterialColors",true);
        shipMat.setColor("Diffuse", ColorRGBA.Cyan);
        shipMat.setColor("Ambient", ColorRGBA.Cyan);
        shipMat.setColor("Specular", ColorRGBA.White);
        shipMat.setFloat("Shininess", 9);
        //shipMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/ship.png"));
        //shipMat.setTexture("NormalMap", assetManager.loadTexture("Textures/normal.png"));
        ship.setMaterial(shipMat);
        rootNode.attachChild(ship);
        //physics
        RigidBodyControl shipPhysics = new RigidBodyControl();
        ship.addControl(shipPhysics);
        getPhysicsSpace().add(shipPhysics);
        shipPhysics.setRestitution(0); //bouncyness
        shipPhysics.setFriction(1);
        shipPhysics.setMass(1);
        ship.getControl(RigidBodyControl.class)
                .setPhysicsLocation(new Vector3f(10,10,0));
        ship.addControl(new ForcesControl(gSources));
        ship.addControl(new FlightControl(ship));
        
        Geometry rod = gob.buildRod();
        Node rodNode = new Node("rodNode");
        rodNode.attachChild(rod);
        rod.rotate(FastMath.DEG_TO_RAD*90, 0, 0);
        rootNode.attachChild(rodNode);
        RigidBodyControl rodPhysics = new RigidBodyControl();
        rodNode.addControl(rodPhysics);
        getPhysicsSpace().add(rodPhysics);
        rodPhysics.setRestitution(0); //bouncyness
        rodPhysics.setFriction(1);
        rodPhysics.setMass(0.1f);
        rodPhysics.setPhysicsRotation(new Quaternion()
                .fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0)));
        
        rodNode.getControl(RigidBodyControl.class)
                .setPhysicsLocation(new Vector3f(10,6.5f,0));
        rodNode.addControl(new ForcesControl(gSources));
        
        HingeJoint joint = new HingeJoint(ship.getControl(RigidBodyControl.class), // A
                     rodNode.getControl(RigidBodyControl.class), // B
                     new Vector3f(0, 0, 0),  // pivot point local to A
                     new Vector3f(0, 2.5f, 0),    // pivot point local to B 
                     Vector3f.UNIT_Z,           // DoF Axis of A (Z axis)
                     Vector3f.UNIT_Z);          // DoF Axis of B (Z axis)
        getPhysicsSpace().add(joint);
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
