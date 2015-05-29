package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import de.kritzelbit.orebit.OreBit;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.ShipCameraControl;
import de.kritzelbit.orebit.data.AsteroidData;
import de.kritzelbit.orebit.data.BaseData;
import de.kritzelbit.orebit.data.MissionData;
import de.kritzelbit.orebit.data.OreData;
import de.kritzelbit.orebit.data.PlanetData;
import de.kritzelbit.orebit.data.SatelliteData;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import de.kritzelbit.orebit.entities.Asteroid;
import de.kritzelbit.orebit.entities.Base;
import de.kritzelbit.orebit.entities.Ore;
import de.kritzelbit.orebit.entities.Planet;
import de.kritzelbit.orebit.entities.Satellite;
import de.kritzelbit.orebit.entities.Ship;
import de.kritzelbit.orebit.io.GameIO;
import de.kritzelbit.orebit.util.GameObjectBuilder;
import java.util.HashSet;
import java.util.Set;


public class IngameState extends AbstractAppState implements PhysicsCollisionListener {
    
    private static final boolean PHYSICS_DEBUG_MODE = false;
    private static final float GAME_SPEED = 0.5f;
    
    private OreBit app;
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
        this.app = (OreBit) app;
        this.stateManager = stateManager;
        this.inputManager = app.getInputManager();
        this.cam = app.getCamera();
        this.gSources = new HashSet<AbstractGameObject>();
        this.rootNode = this.app.getRootNode();
        this.minCamDistance = 100;
        this.app.setSpeed(GAME_SPEED);
        
        //init physics
        initPhysics();
        
        //init object builder
        this.gob = new GameObjectBuilder(this.app, bulletAppState.getPhysicsSpace(), gSources);
        
        //init lights
        initLights();
        
        //init test scene
        initMission(GameIO.readMission("Solaris", app.getAssetManager()));
    }
    
    @Override
    public void update(float tpf) {
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        gSources.clear();
        getPhysicsSpace().removeAll(rootNode);
        stateManager.detach(bulletAppState);
        rootNode.detachAllChildren();
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    private void initShipCam(){
        camNode = new CameraNode("camNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.CameraToSpatial);
        camNode.attachChild(gob.buildBackgroundQuad(cam)); //init background and attach to cam node
        rootNode.attachChild(camNode);
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
        getPhysicsSpace().addCollisionListener(this);
        //bulletAppState.setSpeed(GAME_SPEED);
    }
    
    private void initPostProcessors(){
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        app.getViewPort().addProcessor(fpp);
    }
    
    public void initMission(MissionData mission){
        for (BaseData b : mission.getBases()) initBase(b);
        for (PlanetData p : mission.getPlanets()) initPlanet(p);
        for (AsteroidData a : mission.getAsteroids()) initAsteroid(a);
        for (SatelliteData s : mission.getSatellites()) initSatellite(s);
        for (OreData o : mission.getOres()) initOre(o);
        
        
//        //test planet 1
//        Planet p1 = gob.buildPlanet("p1", 2, 5, ColorRGBA.Green.mult(2));
//        p1.setLocation(0, 0);
//        rootNode.attachChild(p1.getSpatial());
//        gSources.add(p1);
//        //test planet 2
//        Planet p2 = gob.buildPlanet("p2", 20, 8, ColorRGBA.Orange.mult(2));
//        p2.setLocation(30, 20);
//        rootNode.attachChild(p2.getSpatial());
//        gSources.add(p2);
//        //test satellite
//        Satellite s1 = gob.buildSatellite("s1", 1, 2, ColorRGBA.White, p1, 4, 2);
//        rootNode.attachChild(s1.getSpatial());
//        gSources.add(s1);
//        //test asteroid
//        Asteroid a1 = gob.buildAsteroid("a1", 1, 2, ColorRGBA.Red, -10, -8, 10, -5);
//        rootNode.attachChild(a1.getSpatial());
//        a1.init(rootNode);
//        gSources.add(a1);
        //init ship
        ship = gob.buildShip(100, 100, 20, 3, 20);
        ship.getPhysicsControl().setPhysicsLocation(new Vector3f(-20,30,0));
        ship.getSpatial().addControl(new ShipCameraControl(cam, minCamDistance));
        rootNode.attachChild(ship.getNode());
        
        //mission init aftermath
        initShipCam();
        initKeys();
        initPostProcessors();
    }
    
    private void initBase(BaseData b){
        Base base = gob.buildBase(b);
        base.setLocation(b.getX(), b.getY());
        rootNode.attachChild(base.getSpatial());
        gSources.add(base);
    }
    
    private void initOre(OreData o){
        Ore ore = gob.buildOre(o);
        //ore.setLocation(o.getX(), o.getY());
        rootNode.attachChild(ore.getSpatial());
        gSources.add(ore);
    }
    
    private void initPlanet(PlanetData p){
        Planet planet = gob.buildPlanet(p);
        planet.setLocation(p.getX(), p.getY());
        rootNode.attachChild(planet.getSpatial());
        gSources.add(planet);
    }
    
    private void initAsteroid(AsteroidData a){
        Asteroid asteroid = gob.buildAsteroid(a);
        asteroid.setLocation(a.getX(), a.getY());
        rootNode.attachChild(asteroid.getSpatial());
        gSources.add(asteroid);
    }
    
    private void initSatellite(SatelliteData s){
        Satellite satellite = gob.buildSatellite(s);
        rootNode.attachChild(satellite.getSpatial());
        gSources.add(satellite);
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
                ship.setThrusterVisuals(keyPressed);
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

    public void collision(PhysicsCollisionEvent event) {
        Boolean isA;
        if ((isA = collisionObjIsA("ship", event)) != null){
            shipCollision(event, isA);
        } else if ((isA = collisionObjIsA("base", event)) != null){
            //TODO
        }
    }
    
    private Boolean collisionObjIsA(String objectType, PhysicsCollisionEvent event){
        String a = event.getNodeA().getUserData("type");
        String b = event.getNodeB().getUserData("type");
        if ((a != null && a.equals(objectType))){
            return true;
        } else if (b != null && b.equals(objectType)) {
            return false;
        } else {
            return null;
        }
    }
    
    private void shipCollision(PhysicsCollisionEvent event, boolean isA){
        //get local impact point
        Vector3f local = isA ? event.getLocalPointA() : event.getLocalPointB();
        //if impact is on bottom side and slower than X, don't crash
        if (local.y < 0 - ship.getSpatial().getLocalScale().y/1.9f
                && ship.getPhysicsControl().getLinearVelocity().length() < 7) return;
        
        //crash
        if (isA){
            ship.destroy(event.getPositionWorldOnB()
                    .subtract(event.getPositionWorldOnA()).normalizeLocal());
        } else {
            ship.destroy(event.getPositionWorldOnA()
                    .subtract(event.getPositionWorldOnB()).normalizeLocal());
        }
    }

}
