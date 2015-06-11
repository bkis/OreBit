package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.Camera;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.texture.Texture;
import de.kritzelbit.orebit.OreBit;
import de.kritzelbit.orebit.controls.CheckpointDissolveControl;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.ShipCameraControl;
import de.kritzelbit.orebit.data.AsteroidData;
import de.kritzelbit.orebit.data.BaseData;
import de.kritzelbit.orebit.data.CheckpointData;
import de.kritzelbit.orebit.data.MissionData;
import de.kritzelbit.orebit.data.OreData;
import de.kritzelbit.orebit.data.PlanetData;
import de.kritzelbit.orebit.data.MoonData;
import de.kritzelbit.orebit.data.ObjectiveData;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import de.kritzelbit.orebit.entities.Base;
import de.kritzelbit.orebit.entities.Ship;
import de.kritzelbit.orebit.gui.GUIController;
import de.kritzelbit.orebit.io.GameIO;
import de.kritzelbit.orebit.io.SaveGameContainer;
import de.kritzelbit.orebit.util.GameObjectBuilder;
import java.util.HashSet;
import java.util.Set;


public class IngameState extends AbstractAppState implements PhysicsCollisionListener, PhysicsTickListener{
    
    private static final boolean PHYSICS_DEBUG_MODE = false;
    private static final float MIN_CAM_DISTANCE = 80;
    
    private OreBit app;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private GameObjectBuilder gob;
    private Set<AbstractGameObject> gSources;
    private Set<PhysicsGhostObject> checkpoints;
    private Camera cam;
    private Node rootNode;
    private Ship ship;
    private CameraNode camNode;
    private MissionData mission;
    private SaveGameContainer sg;
    private GUIController gui;
    private boolean running;
    
    
    public IngameState(GUIController gui, MissionData mission){
        this.gui = gui;
        this.mission = mission;
    }
    
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
        
        //init physics
        initPhysics();
        
        //init object builder
        this.gob = new GameObjectBuilder(this.app, bulletAppState.getPhysicsSpace(), rootNode, gSources);
        
        //init lights
        initLights();
        
        //load savegame
        sg = GameIO.readSaveGame();
        
        //init mission
        initMission();
        
        //init GUI
        gui.loadScreen("ingame");
        
        //DEBUG
    }
    
    @Override
    public void update(float tpf) {
        if (running){
            gui.getLabel("labelFuel").setText((int)ship.getFuel() + "");
        }
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
    
    private void initShipCam(Texture bgTex){
        camNode = new CameraNode("camNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.CameraToSpatial);
        camNode.attachChild(gob.buildBackgroundQuad(cam, bgTex)); //init background and attach to cam node
        rootNode.attachChild(camNode);
    }
    
    private void initLights(){
        //ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.LightGray);
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
        getPhysicsSpace().addTickListener(this);
        //bulletAppState.setSpeed(GAME_SPEED);
    }
    
    private void initPostProcessors(){
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        app.getViewPort().addProcessor(fpp);
    }
    
    private void initMission(){
        app.displayOnScreenMsg("MISSION: " + mission.getTitle());
        
        //game speed
        this.app.setSpeed(mission.getGameSpeed());
        
        //mission objects
        for (BaseData b : mission.getBases()) gob.buildBase(b);
        for (PlanetData p : mission.getPlanets()) gob.buildPlanet(p);
        for (AsteroidData a : mission.getAsteroids()) gob.buildAsteroid(a);
        for (MoonData s : mission.getMoons()) gob.buildMoon(s);
        for (OreData o : mission.getOres()) gob.buildOre(o);
        for (CheckpointData c : mission.getCheckpoints()) gob.buildCheckpoint(c);
        
        //get set of ghost controls (for checkpoints)
        this.checkpoints = new HashSet<PhysicsGhostObject>(
                bulletAppState.getPhysicsSpace().getGhostObjectList());
        
        //init ship
        initShip();
        
        //background texture
        Texture bgTex = null;
        if (!mission.getBackgroundImage().equals("random"))
            bgTex = app.getAssetManager().loadTexture(mission.getBackgroundImage());
        if (bgTex == null)
            bgTex = app.getAssetManager().loadTexture("Textures/Backgrounds/space.jpg");
        
        //mission init aftermath
        initShipCam(bgTex);
        initKeys();
        initPostProcessors();
        inputManager.setCursorVisible(false);
        running = true;
    }
    
    private void initShip(){
        //ship object
        ship = gob.buildShip(
                (int)sg.getData(SaveGameContainer.SHIP_THRUST),
                (int)sg.getData(SaveGameContainer.SHIP_ROTATE),
                (int)sg.getData(SaveGameContainer.SHIP_GRABBER));
        
        //start rotation
        ship.getPhysicsControl().setPhysicsRotation(
                new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*-90*mission.getStartPosition(), Vector3f.UNIT_Z));
        
        //start position
        Base base = getStartBase();
        float dist = base.getRadius() + ship.getRadius() + 1;
        float posX = (mission.getStartPosition() == 1 ? dist :
                (mission.getStartPosition() == 3 ? -dist : 0));
        float posY = (mission.getStartPosition() == 0 ? dist :
                (mission.getStartPosition() == 2 ? -dist : 0));
        
        ship.getPhysicsControl().setPhysicsLocation(new Vector3f(
                base.getX() + posX,
                base.getY() + posY,
                0));
        ship.getSpatial().addControl(new ShipCameraControl(cam, MIN_CAM_DISTANCE));
        rootNode.attachChild(ship.getNode());
    }
    
    private Base getStartBase(){
        for (AbstractGameObject obj : gSources){
            if (obj instanceof Base && ((Base)obj).getName().equals(mission.getStartBase())){
                return (Base) obj;
            }
        }
        for (AbstractGameObject obj : gSources){
            if (obj instanceof Base){
                return (Base) obj;
            }
        }
        System.out.println("ERROR: BASE NOT FOUND.");
        return null;
    }
    
    private void initKeys() {
        inputManager.addMapping("Thrust", new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Grabber", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Booster", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("Debug", new KeyTrigger(KeyInput.KEY_F1));

        inputManager.addListener(ingameInputListener, "Thrust", "Left", "Right", "Grabber", "Booster", "Debug");
    }
    
    private InputListener ingameInputListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            //System.out.println(name + " - " + keyPressed);
            if (name.equals("Thrust")) {
                ship.getSpatial().getControl(FlightControl.class).thrust = keyPressed;
                ship.setThrusterVisuals(keyPressed);
            } 
            if (name.equals("Right")) {
                ship.getSpatial().getControl(FlightControl.class).right = keyPressed;
                if (keyPressed)
                    ship.getSpatial().getControl(FlightControl.class).left = false;
            } 
            if (name.equals("Left")) {
                ship.getSpatial().getControl(FlightControl.class).left = keyPressed;
                if (keyPressed)
                    ship.getSpatial().getControl(FlightControl.class).right = false;
            } 
            if (name.equals("Grabber")) {
                ship.toggleGrabber(keyPressed);
            } 
            if (name.equals("Booster")) {
                ship.getSpatial().getControl(FlightControl.class).setBoost(keyPressed);
                ship.setBoost(keyPressed);
            } 
            if (name.equals("Debug")) {
                //DEBUG output
                app.start();
            }
        }
    };
    
//    private class OreBitInputListener implements ActionListener, AnalogListener {
//        public void onAction(String name, boolean isPressed, float tpf) {
//            
//        }
//        public void onAnalog(String name, float value, float tpf) {
//            
//        }
//    }

//    private AnalogListener analogListener = new AnalogListener() {
//        public void onAnalog(String name, float value, float tpf) {
//            //TODO
//        }
//    };

    public void collision(PhysicsCollisionEvent event) {
        Boolean isA;
        if ((isA = collisionObjIsA("ship", event)) != null){
            //SHIP COLLISION?
            shipCollision(event, isA);
        } else if ((isA = collisionObjIsA("base", event)) != null){
            //ORE COLLECTED?
            Spatial obj = (isA ? event.getNodeB() : event.getNodeA());
            if (obj.getUserData("type").equals("ore"))
                oreCollected(obj);
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
        //if collision with ghost control, don't crash
        if ((isA ? event.getObjectB() : event.getObjectA()) instanceof GhostControl) return;
        
        //get local impact point
        Vector3f local = isA ? event.getLocalPointA() : event.getLocalPointB();
        float impulse = event.getAppliedImpulse();
        //landed safely? don't crash.
        if (local.y < 0 - ship.getRadius()*0.95f && impulse < 5){
            landedOn(isA ? event.getNodeB() : event.getNodeA());
            return;
        }
        
        //DEBUG
        System.out.println("[CRASH DEBUG DATA] impact-y: " + local.y + " / crash impulse: " + impulse);
        
        //crash
        Vector3f dir;
        if (isA){
            dir = event.getNodeA().getWorldTranslation()
                    .subtract(event.getNodeB().getWorldTranslation()).normalizeLocal();
            ship.destroy(dir);
        } else {
            dir = event.getNodeB().getWorldTranslation()
                    .subtract(event.getNodeA().getWorldTranslation()).normalizeLocal();
            ship.destroy(dir);
        }
        //TODO explosions impulse
        ((RigidBodyControl)event.getObjectB()).applyImpulse(dir.mult(1000), dir.negate().normalizeLocal());
        missionFailed();
    }

    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        
    }

    public void physicsTick(PhysicsSpace space, float tpf) {
        //check for checkpoint collision
        if (checkpoints.size() > 0
                && mission.getObjectives().get(0).getType().equals("checkpoint")){
            for (PhysicsGhostObject g : checkpoints){
                if (g instanceof GhostControl){
                    for (PhysicsCollisionObject p : g.getOverlappingObjects()){
                        if (p.getUserObject() == ship.getSpatial()){
                            ((Spatial)g.getUserObject()).addControl(new CheckpointDissolveControl());
                            checkpoints.remove(g);
                            objectiveAchieved();
                            return;
                        }
                    }
                }
            }
        }
    }
    
    private void landedOn(Spatial spatial){
        ObjectiveData o = mission.getObjectives().get(0);
        if (o.getType().equals("land")
                && spatial.getName().equals(o.getData1())){
            objectiveAchieved();
        }
    }
    
    private void oreCollected(Spatial ore){
        if (mission.getObjectives().get(0).getType().equals("collect")){
            ore.getControl(RigidBodyControl.class).setEnabled(false);
            ore.removeFromParent();
            objectiveAchieved();
        }
    }
    
    private void objectiveAchieved(){
        System.out.println("OBJECTIVE ACHIEVED: " + mission.getObjectives().get(0).getMessage());
        app.displayOnScreenMsg("OBJECTIVE ACHIEVED: " + mission.getObjectives().get(0).getMessage());
        mission.getObjectives().remove(0);
        //TODO
        
        if (mission.getObjectives().isEmpty()){
            missionCompleted();
        }
    }
    
    private void missionCompleted(){
        missionEnded();
        System.out.println("MISSION COMPLETED");
        app.displayOnScreenMsg("MISSION COMPLETED");
    }
    
    private void missionFailed(){
        missionEnded();
        System.out.println("MISSION FAILED");
        app.displayOnScreenMsg("MISSION FAILED");
    }
    
    private void missionEnded(){
        //safety cleanup
        running = false;
        bulletAppState.getPhysicsSpace().removeTickListener(this);
        bulletAppState.getPhysicsSpace().removeCollisionListener(this);
        inputManager.removeListener(ingameInputListener);
    }
    
//    private void removeFromRoot(Spatial spatial){
//        app.enqueue(new RemovalTask(spatial));
//    }
//    
//    private class RemovalTask implements Callable<Boolean>{
//        private Spatial spatial;
//        public RemovalTask(Spatial spatial){
//            this.spatial = spatial;
//        }
//        public Boolean call() throws Exception {
//            rootNode.detachChild(spatial);
//            return true;
//        }
//    }

}
