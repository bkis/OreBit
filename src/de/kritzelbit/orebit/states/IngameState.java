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
import de.kritzelbit.orebit.controls.MoonControl;
import de.kritzelbit.orebit.controls.ShipCameraControl;
import de.kritzelbit.orebit.data.AsteroidData;
import de.kritzelbit.orebit.data.BaseData;
import de.kritzelbit.orebit.data.CheckpointData;
import de.kritzelbit.orebit.data.MagnetData;
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
import de.kritzelbit.orebit.io.SaveGameData;
import de.kritzelbit.orebit.util.CustomUncaughtExceptionHandler;
import de.kritzelbit.orebit.util.GameObjectBuilder;
import de.kritzelbit.orebit.util.SoundPlayer;
import de.kritzelbit.orebit.util.ValueConverter;
import java.util.HashSet;
import java.util.Set;


public class IngameState extends AbstractAppState implements PhysicsCollisionListener, PhysicsTickListener{
    
    private static final boolean PHYSICS_DEBUG_MODE = true;
    private static final float MIN_CAM_DISTANCE = 80;
    private static final float MAX_LANDING_SPEED = 6;
    private static final float MAX_SHIP_DISTANCE = 300;
    private static final String MISSION_ENDED_INSTRUCTIONS = "Press [SPACE] to return to command center";
    
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
    private SaveGameData sg;
    private GUIController gui;
    private boolean running;
    private float time;
    private float timeLeft;
    private boolean hqGraphics;
    private FilterPostProcessor fpp;
    private AmbientLight ambient;
    private DirectionalLight sun;
    
    //ship limits
    private float shipPower;
    private float shipSpin;
    private float shipBeam;
    private float shipBoost;
    
    public IngameState(GUIController gui, SaveGameData saveGame, MissionData mission, boolean hqGraphics){
        Thread.currentThread().setUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
        this.gui = gui;
        this.sg = saveGame;
        this.mission = mission;
        this.hqGraphics = hqGraphics;
        
        //load GUI
        gui.loadScreen("ingame");
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
        
        //load mission data
        //this.mission = loadMission(sg.getData(SaveGameData.GAME_MISSION));
        
        //init physics
        initPhysics();
        
        //init object builder
        this.gob = new GameObjectBuilder(
                this.app,
                bulletAppState.getPhysicsSpace(),
                rootNode,
                gSources,
                hqGraphics);
        
        //init lights
        initLights();
        
        //init mission
        initMission();
        
        //DEBUG
    }
    
    @Override
    public void update(float tpf) {
        if (running){
            //time
            timeLeft -= tpf*2;
            if (timeLeft <= 0){
                if (mission.getObjectives().size() == 1
                        && mission.getObjectives().get(0).getType().equals("survive")){
                    missionCompleted();
                } else {
                    SoundPlayer.getInstance().play("radioMissionFailedTime");
                    missionFailed("(TIME UP)");
                    resetFlightControls();
                }
                return;
            } else {
                gui.setTimeStatus(timeLeft,time);
            }
            
            //fuel
            gui.setFuelStatus(ship.getFuel(), mission.getMaxFuel());
            if (ship.getFuel() == 0){
                SoundPlayer.getInstance().play("radioMissionFailedFuel");
                missionFailed("(OUT OF FUEL)");
                resetFlightControls();
            }
            
            //speed
            gui.setDisplaySpeed((int)ship.getPhysicsControl().getLinearVelocity().length());
            
            //ship distance
            if (ship.getSpatial().getWorldTranslation().length() > MAX_SHIP_DISTANCE) {
                gSources.remove(ship);
                //ship.destroy(ship.getPhysicsControl().getLinearVelocity().normalize());
                //SoundPlayer.getInstance().play("crash");
                SoundPlayer.getInstance().play("radioMissionFailedLost");
                missionFailed("(LOST CONNECTION TO BASE)");
                resetFlightControls();
            }
        }
    }
    
    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        bulletAppState.setEnabled(isEnabled());
        inputManager.setCursorVisible(!isEnabled());
        for (Node n : rootNode.descendantMatches(Node.class, "moonNode")){
            n.getControl(MoonControl.class).setEnabled(isEnabled());
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        inputManager.clearMappings();
        gSources.clear();
        camNode.detachAllChildren();
        if (fpp != null) app.getViewPort().removeProcessor(fpp);
        camNode.removeControl(CameraControl.class);
        bulletAppState.getPhysicsSpace().removeCollisionListener(this);
        getPhysicsSpace().removeAll(rootNode);
        stateManager.detach(bulletAppState);
        rootNode.detachAllChildren();
        rootNode.removeLight(ambient);
        rootNode.removeLight(sun);
        SoundPlayer.getInstance().stopAllSounds();
        System.out.println("[GAME]\tingame cleanup.");
    }
    
    private void saveGame(){
        GameIO.writeSaveGame(sg);
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
        ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.LightGray);
        rootNode.addLight(ambient); 
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
    }
    
    private void initPhysics(){
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(PHYSICS_DEBUG_MODE);
        getPhysicsSpace().setGravity(Vector3f.ZERO.clone());
        getPhysicsSpace().addCollisionListener(this);
        getPhysicsSpace().addTickListener(this);
        //bulletAppState.setSpeed(GAME_SPEED);
    }
    
    private void initPostProcessors(){
        if (!hqGraphics) return;
        fpp = new FilterPostProcessor(app.getAssetManager());
        fpp.addFilter(new BloomFilter(BloomFilter.GlowMode.Objects));
        app.getViewPort().addProcessor(fpp);
    }
    
    private void initMission(){
        time = mission.getTimeLimit();
        timeLeft = time;
        
        //displays
        displayObjective();
        gui.setDisplayMoney((int)sg.getData(SaveGameData.GAME_MONEY)+"");
        
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
        applyShipLimits();
        initShip();
        
        //magnets
        for (MagnetData m : mission.getMagnets())
            gob.buildMagnet(m, ship.getPhysicsControl());
        
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
        
        if (!mission.getBackgroundMusic().equals("random")){
            SoundPlayer.getInstance().playMusic(mission.getBackgroundMusic());
        } else {
            SoundPlayer.getInstance().playRandomMusic();
        }
    }
    
    private void initShip(){
        //ship object
        ship = gob.buildShip(
                (int)shipPower,
                (int)shipSpin,
                (int)shipBeam,
                mission.getMaxFuel(),
                (int)shipBoost);
        
        //start rotation
        ship.getPhysicsControl().setPhysicsRotation(
                new Quaternion().fromAngleAxis(
                FastMath.DEG_TO_RAD*-90*mission.getStartPosition(),
                Vector3f.UNIT_Z.clone()));
        
        //start position
        Base base = getStartBase();
        float dist = base.getRadius() + ship.getRadius() + 0.2f;
        float posX = (mission.getStartPosition() == 1 ? dist :
                (mission.getStartPosition() == 3 ? -dist : 0));
        float posY = (mission.getStartPosition() == 0 ? dist :
                (mission.getStartPosition() == 2 ? -dist : 0));
        
        ship.getPhysicsControl().setPhysicsLocation(new Vector3f(
                base.getX() + posX,
                base.getY() + posY,
                0));
        ship.getSpatial().addControl(
                new ShipCameraControl(cam, MIN_CAM_DISTANCE));
        rootNode.attachChild(ship.getNode());
    }
    
    private void applyShipLimits(){
        shipPower = sg.getData(SaveGameData.SHIP_THRUST);
        shipSpin = sg.getData(SaveGameData.SHIP_ROTATE);
        shipBeam = sg.getData(SaveGameData.SHIP_GRABBER);
        shipBoost = sg.getData(SaveGameData.SHIP_BOOSTER);
        
        if (mission.getShipMaxPower() > 0 
                && mission.getShipMaxPower() < shipPower){
            shipPower = mission.getShipMaxPower();
        } else { removeShipLimitIndicator("ShipPower"); }
        
        if (mission.getShipMaxSpin() > 0 
                && mission.getShipMaxSpin() < shipSpin){
            shipSpin = mission.getShipMaxSpin();
        } else { removeShipLimitIndicator("ShipSpin"); }
        
        if (mission.getShipMaxBeamLength() > 0 
                && mission.getShipMaxBeamLength() < shipBeam){
            shipBeam = mission.getShipMaxBeamLength();
        } else { removeShipLimitIndicator("ShipBeam"); }
        
        if (mission.getShipMaxBoost() > 0 
                && mission.getShipMaxBoost() < shipBoost){
            shipBoost = mission.getShipMaxBoost();
        } else { removeShipLimitIndicator("ShipBoost"); }
        
        //modify to actual values
        shipPower = ValueConverter.shipPower(shipPower, true);
        shipSpin = ValueConverter.shipSpin(shipSpin, true);
        shipBeam = ValueConverter.shipBeamLength(shipBeam, true);
        shipBoost = ValueConverter.shipBoost(shipBoost, true);
    }
    
    private void removeShipLimitIndicator(String key){
        gui.getElement("indicator"+key).hide();
        gui.getElement("indicator"+key).disable();
    }
    
    private Base getStartBase(){
        for (AbstractGameObject obj : gSources){
            if (obj instanceof Base
                    && ((Base)obj).getName().equals(mission.getStartBase())){
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
        inputManager.addMapping("Thrust",
                new KeyTrigger(KeyInput.KEY_UP),
                new KeyTrigger(KeyInput.KEY_W),
                new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addMapping("Left",
                new KeyTrigger(KeyInput.KEY_LEFT),
                new KeyTrigger(KeyInput.KEY_A),
                new KeyTrigger(KeyInput.KEY_NUMPAD4));
        inputManager.addMapping("Right",
                new KeyTrigger(KeyInput.KEY_RIGHT),
                new KeyTrigger(KeyInput.KEY_D),
                new KeyTrigger(KeyInput.KEY_NUMPAD6));
        inputManager.addMapping("Grabber", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Booster", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("Escape", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(ingameInputListener,
                "Thrust",
                "Left",
                "Right",
                "Grabber",
                "Booster");
        inputManager.addListener(escapeListener, "Escape");
        inputManager.addListener(spaceListener, "Space");
    }
    
    private InputListener ingameInputListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (isEnabled()){
                if (name.equals("Thrust")) {
                    ship.getSpatial().getControl(FlightControl.class).thrust = keyPressed;
                    ship.setThrusterVisuals(keyPressed);
                    if (keyPressed){
                        SoundPlayer.getInstance().play("thrust");
                        if (ship.getSpatial().getControl(FlightControl.class).isBoost()){
                            SoundPlayer.getInstance().play("boostLoop");
                        }
                    } else {
                        SoundPlayer.getInstance().stop("thrust");
                    }
                } 
                if (name.equals("Right")) {
                    ship.getSpatial().getControl(FlightControl.class).right = keyPressed;
                    if (keyPressed)
                        ship.getSpatial().getControl(FlightControl.class).left = false;
                } 
                if (name.equals("Left")) {
                    ship.getSpatial().getControl(FlightControl.class).left = keyPressed;
                    if (keyPressed)
                        ship.getSpatial().getControl(FlightControl.class).right = !keyPressed;
                } 
                if (name.equals("Grabber")) {
                    ship.toggleGrabber(keyPressed);
                } 
                if (name.equals("Booster")) {
                    ship.getSpatial().getControl(FlightControl.class).setBoost(keyPressed);
                    if (keyPressed){
                        SoundPlayer.getInstance().play("boostInit");
                        if (ship.getSpatial().getControl(FlightControl.class).thrust){
                            SoundPlayer.getInstance().play("boostLoop");
                        }
                    } else {
                        SoundPlayer.getInstance().stop("boostLoop");
                    }
                } 
            }
        }
    };
    
    private InputListener escapeListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Escape") && !keyPressed) {
                buttonPauseResume();
            }
        }
    };
    
    private InputListener spaceListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (!running && isEnabled() && name.equals("Space") && keyPressed) {
                app.switchToState(new ShopState(gui, sg));
                SoundPlayer.getInstance().stopAllSounds();
            }
        }
    };
    
    public void collision(PhysicsCollisionEvent event) {
        Boolean isA;
        if ((isA = collisionObjIsA("ship", event)) != null){
            shipCollision(event, isA, collisionObjIsA("base", event) != null);
        }        
        if ((isA = collisionObjIsA("base", event)) != null){
            //ORE COLLECTED?
            Spatial obj = (isA ? event.getNodeB() : event.getNodeA());
            String baseId = (isA ? event.getNodeA() : event.getNodeB()).getUserData("id");
            if (obj != null && obj.getUserData("type").equals("ore"))
                oreCollected(obj, baseId);
        }
    }
    
    private Boolean collisionObjIsA(String objectType, PhysicsCollisionEvent event){
        Spatial a = event.getNodeA();
        Spatial b = event.getNodeB();
        if ((a != null && a.getUserData("type") != null
                && a.getUserData("type").equals(objectType))){
            return true;
        } else if (b != null && b.getUserData("type") != null 
                && b.getUserData("type").equals(objectType)) {
            return false;
        } else {
            return null;
        }
    }
    
    private void shipCollision(PhysicsCollisionEvent event, boolean isA, boolean withBase){
        if (event.getAppliedImpulse() > 0.7f) SoundPlayer.getInstance().play("impact");
        checkForBaseLiftOff();
        
        //if collision with ghost control, don't crash
        if ((isA ? event.getObjectB() : event.getObjectA()) instanceof GhostControl) return;
        
        //get local impact point
        Vector3f local = isA ? event.getLocalPointA() : event.getLocalPointB();
        float impulse = event.getAppliedImpulse() / (withBase ? 2 : 1);
        //landed safely? don't crash.
        if (local.y < 0 - ship.getRadius()*0.9f && impulse < MAX_LANDING_SPEED){
            landedOn(isA ? event.getNodeB() : event.getNodeA());
            return;
        }
        
        //ship crash
        shipCrash(isA, event);
        
        //DEBUG
        System.out.println("[DBG]\tcrash data: impact-y(" + local.y + ") crash-impulse(" + impulse + ")");
        
        if (running){
            SoundPlayer.getInstance().play("radioMissionFailedCrash");
            missionFailed("(SHIP CRASHED)");
        }
        gui.setDisplaySpeed(0);
    }
    
    public float calculateCrashImpulse(Vector3f v1, Vector3f v2){
        return v1.subtract(v2).length();
    }
    
    private void shipCrash(boolean isA, PhysicsCollisionEvent event){
        //crash
        gSources.remove(ship);
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
        //explosions impulse
        ((RigidBodyControl)event.getObjectB()).applyImpulse(dir.mult(1000), dir.negate().normalizeLocal());
        SoundPlayer.getInstance().play("crash");
    }
    
    private void checkForBaseLiftOff(){
        if (ship.getPhysicsControl().getLinearVelocity().length() < 0.1f){
            ship.getSpatial().getControl(FlightControl.class).liftOff();
        }
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
        if (!running) return;
        ObjectiveData o = mission.getObjectives().get(0);
        if (o.getType().equals("land")
                && spatial.getName().equals(o.getTarget())){
            objectiveAchieved();
        }
    }
    
    private void oreCollected(Spatial ore, String baseId){
        //check if objective tyoe is "collect" and base was correct
        if (mission.getObjectives().get(0).getType().equals("collect")
                && mission.getObjectives().get(0).getTarget().equals(baseId)
                && running){
            //remove ore from game
            ore.getControl(RigidBodyControl.class).setEnabled(false);
            ore.removeFromParent();
            objectiveAchieved();
            removeOreFromGSources(ore);
        }
    }
    
    private void objectiveAchieved(){
        mission.getObjectives().remove(0);
        if (mission.getObjectives().isEmpty()){
            missionCompleted();
            return;
        }
        displayObjective();
    }
    
    private void removeOreFromGSources(Spatial spatial){
        for (AbstractGameObject o : gSources) {
            if (o.getSpatial() == spatial) {
                o.setMass(0);
            }
        }
    }
    
    private void displayObjective(){
        if (mission.getObjectives().size() > 0){
            gui.setDisplayLine1("Objective: " + mission.getObjectives().get(0)
                + " (" + (mission.getObjectives().size()-1) + " more)");
            
            //radio sound
            if (mission.getObjectives().get(0).getType().equalsIgnoreCase("LAND")){
                SoundPlayer.getInstance().play("radioObjectiveLand");
            } else if (mission.getObjectives().get(0).getType().equalsIgnoreCase("COLLECT")){
                SoundPlayer.getInstance().play("radioObjectiveCollect");
            } else if (mission.getObjectives().get(0).getType().equalsIgnoreCase("SURVIVE")){
                SoundPlayer.getInstance().play("radioObjectiveSurvive");
            } else if (mission.getObjectives().get(0).getType().equalsIgnoreCase("CHECKPOINT")){
                SoundPlayer.getInstance().play("radioObjectiveCheckpoint");
            }
            
        } else {
            missionCompleted();
        }
    }
    
    private void missionCompleted(){
        gui.setDisplayLine1("MISSION COMPLETE! YOU EARNED " + mission.getReward());
        System.out.println("MISSION COMPLETED!");
        //give mission reward
        sg.setData(SaveGameData.GAME_MONEY,
                sg.getData(SaveGameData.GAME_MONEY) + mission.getReward());
        sg.setData(SaveGameData.GAME_MISSION,
                sg.getData(SaveGameData.GAME_MISSION) + 1);
        System.out.println("SG MISSION: " + sg.getData(SaveGameData.GAME_MISSION));
        SoundPlayer.getInstance().play("radioMissionComplete");
        missionEnded();
        resetFlightControls();
    }
    
    private void missionFailed(String msg){
        gui.setDisplayLine1("MISSION FAILED! " + msg);
        System.out.println("[GAME]\tmission failed. " + msg);
        missionEnded();
    }
    
    private void missionEnded(){
        running = false;
        saveGame();
        gui.setDisplaySpeed(0);
        SoundPlayer.getInstance().stopAllLoops();
        gui.setDisplayLine2(MISSION_ENDED_INSTRUCTIONS);
        //safety cleanup
        bulletAppState.getPhysicsSpace().removeTickListener(this);
        //bulletAppState.getPhysicsSpace().removeCollisionListener(this);
        inputManager.removeListener(ingameInputListener);
    }
    
    private void resetFlightControls(){
        ship.getSpatial().getControl(FlightControl.class).thrust = false;
        ship.getSpatial().getControl(FlightControl.class).setBoost(false);
        ship.setThrusterVisuals(false);
    }
    
    public void buttonPauseResume(){
        setEnabled(!isEnabled());
        if (isEnabled()){
            gui.closePausePopup();
        } else {
            gui.showPausePopup();
        }
        System.out.println("[GAME]\tgame " + (isEnabled() ? "unpaused" : "paused") + ".");
    }
    
    public void buttonPauseMainMenu(){
        app.toMainMenu();
    }
    
}
