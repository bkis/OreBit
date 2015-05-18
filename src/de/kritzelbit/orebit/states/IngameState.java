package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import de.kritzelbit.orebit.controls.ShipCameraControl;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import de.kritzelbit.orebit.entities.Asteroid;
import de.kritzelbit.orebit.entities.Planet;
import de.kritzelbit.orebit.entities.Satellite;
import de.kritzelbit.orebit.entities.Ship;
import de.kritzelbit.orebit.io.SaveGame;
import de.kritzelbit.orebit.io.GameIO;
import de.kritzelbit.orebit.util.GameObjectBuilder;
import java.util.HashSet;
import java.util.Set;


public class IngameState extends AbstractAppState {
    
    private static final boolean PHYSICS_DEBUG_MODE = false;
    private static final float GAME_SPEED = 0.5f;
    
    private SimpleApplication app;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private GameObjectBuilder gob;
    private Set<AbstractGameObject> gSources;
    private Camera cam;
    private Node rootNode;
    private Ship ship;
    private float minCamDistance;
    private CameraNode camNode;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //init fields
        this.app = (SimpleApplication) app;
        this.stateManager = stateManager;
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.gSources = new HashSet<AbstractGameObject>();
        this.rootNode = this.app.getRootNode();
        this.minCamDistance = 100;
        
        //init physics
        initPhysics();
        
        //init object builder
        this.gob = new GameObjectBuilder(this.app, bulletAppState.getPhysicsSpace(), gSources);
        
        //init lights
        initLights();
        
        //init test scene
        initTestScene();
        camNode = new CameraNode("camNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.CameraToSpatial);
        rootNode.attachChild(camNode);
    
        //init keys
        initKeys();
        
        //postprocessors, filters
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        app.getViewPort().addProcessor(fpp);
        
        //test background 2
        camNode.attachChild(gob.buildBackgroundQuad(cam));
        
//        //test write savegame
//        SaveGame sg = new SaveGame();
//        sg.setData(SaveGame.GAME_MONEY, 12345);
//        GameIO.writeSaveGame(sg);
//        
//        //test read savegame
//        sg = GameIO.readSaveGame();
//        System.out.println(sg.getData(SaveGame.GAME_MONEY));
    }
    
    @Override
    public void update(float tpf) {

    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
        gSources.clear();
        getPhysicsSpace().removeAll(rootNode);
        stateManager.detach(bulletAppState);
        rootNode.detachAllChildren();
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
        bulletAppState.setDebugEnabled(PHYSICS_DEBUG_MODE);
        getPhysicsSpace().setGravity(Vector3f.ZERO);
        bulletAppState.setSpeed(GAME_SPEED);
    }
    
    private void initTestScene(){
        //test planet 1
        Planet p1 = gob.buildPlanet("p1", 2, 5, ColorRGBA.Green.mult(2));
        p1.setLocation(0, 0);
        rootNode.attachChild(p1.getSpatial());
        gSources.add(p1);
        //test planet 2
        Planet p2 = gob.buildPlanet("p2", 20, 8, ColorRGBA.Orange.mult(2));
        p2.setLocation(30, 20);
        rootNode.attachChild(p2.getSpatial());
        gSources.add(p2);
        //test satellite
        Satellite s1 = gob.buildSatellite("s1", 1, 2, ColorRGBA.White, p1, 4, 2);
        rootNode.attachChild(s1.getSpatial());
        gSources.add(s1);
        //test asteroid
        Asteroid a1 = gob.buildAsteroid("a1", 1, 2, ColorRGBA.Red, -10, -8, 10, -5);
        rootNode.attachChild(a1.getSpatial());
        a1.init(rootNode);
        gSources.add(a1);
        //init ship
        ship = gob.buildShip(100, 100, 20, 2, 20);
        ship.getPhysicsControl().setPhysicsLocation(new Vector3f(-20,30,0));
        ship.getSpatial().addControl(new ShipCameraControl(cam, minCamDistance));
        getPhysicsSpace().addCollisionListener(ship);
        rootNode.attachChild(ship.getNode());
    }
    
    private void initKeys() {
        inputManager.addMapping("Thrust",  new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Grabber",  new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(actionListener,"Left", "Right", "Thrust", "Grabber");
    }
    
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Thrust")) {
                ship.getSpatial().getControl(FlightControl.class).thrust = keyPressed;
            } else if (name.equals("Right")) {
                ship.getSpatial().getControl(FlightControl.class).right = keyPressed;
                ship.getSpatial().getControl(FlightControl.class).stopRot = !keyPressed;
                if (keyPressed)
                    ship.getSpatial().getControl(FlightControl.class).left = false;
            } else if (name.equals("Left")) {
                ship.getSpatial().getControl(FlightControl.class).left = keyPressed;
                ship.getSpatial().getControl(FlightControl.class).stopRot = !keyPressed;
                if (keyPressed)
                    ship.getSpatial().getControl(FlightControl.class).right = false;
            } else if (name.equals("Grabber")) {
                ship.toggleGrabber(keyPressed);
            }
        }
    };

//    private AnalogListener analogListener = new AnalogListener() {
//        public void onAnalog(String name, float value, float tpf) {
//            //TODO
//        }
//    };

}
