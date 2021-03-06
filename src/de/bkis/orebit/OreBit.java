package de.bkis.orebit;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import de.bkis.orebit.data.MissionData;
import de.bkis.orebit.gui.GUIController;
import de.bkis.orebit.io.GameIO;
import de.bkis.orebit.io.SaveGameData;
import de.bkis.orebit.io.XMLLoader;
import de.bkis.orebit.states.IngameState;
import de.bkis.orebit.states.MainMenuState;
import de.bkis.orebit.states.ShopState;
import de.bkis.orebit.util.SoundPlayer;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class OreBit extends SimpleApplication {

    private static int screenWidth;
    private static int screenHeight;

    private GUIController gui;
    private SaveGameData sg;
    private AppState currentState;
    private boolean hqGraphics;

    public static void main(String[] args) {
        OreBit app = new OreBit();
        
        //get local screen resolution
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();

        //configure settings
        AppSettings settings = new AppSettings(true);
        //settings.setResolution(screenWidth, screenHeight);
        settings.setResolution(1024, 768);
        settings.setMinResolution(800, 600);
        settings.setVSync(false);
        settings.setFrameRate(200);
        settings.setFullscreen(false);
        settings.setTitle("OreBit");
        settings.setSettingsDialogImage("Interface/splash.jpg");

        app.showSettings = true;
        app.setSettings(settings);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        hqGraphics = true;
        //cam settings
        flyCam.setEnabled(false);
        //init sound player
        SoundPlayer.init(assetManager);
        stateManager.attach(SoundPlayer.getInstance());
        //disable default inputs
        inputManager.clearMappings();
        //register custom asset loaders
        assetManager.registerLoader(XMLLoader.class, "xml");
        //init GUI controller
        gui = new GUIController(this);
        //start main menu state
        switchToState(new MainMenuState(gui));
    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public boolean isHqGraphicsEnabled(){
        return hqGraphics;
    }
    
    public void setHqGraphicsEnabled(boolean enabled){
        this.hqGraphics = enabled;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void startGame(String cmd) {
        if (cmd.equals("new")) {
            sg = new SaveGameData();
        } else if (cmd.equals("continue")) {
            sg = GameIO.readSaveGame();
        }
        MissionData mission = GameIO.readMission(
                Integer.toString((int)sg.getData(SaveGameData.GAME_MISSION)),
                "campaign", assetManager);
        switchToState(new ShopState(gui, sg, mission));
    }
    
    public void startMission(MissionData mission){
        switchToState(new IngameState(gui, sg, mission, hqGraphics));
    }

    public void switchToState(AppState state) {
        if (currentState != null && stateManager.hasState(currentState)) {
            stateManager.detach(currentState);
        }
        stateManager.attach(state);
        currentState = state;
    }
    
    public void toMainMenu(){
        MainMenuState mms = new MainMenuState(gui);
        switchToState(mms);
        mms.checkForSaveGame(assetManager);
    }
    
    
//    public void displayOnScreenMsg(String msg){
//        enqueue(new OnScreenMessage(msg));
//    }
//    
//    private class OnScreenMessage implements Callable<Boolean>{
//        private String msg;
//        public OnScreenMessage(String msg){
//            this.msg = msg;
//        }
//        public Boolean call() throws Exception {
//            guiNode.detachAllChildren();
//            guiFont = assetManager.loadFont("Interface/Fonts/LibSans.fnt");
//            BitmapText text = new BitmapText(guiFont, false);
//            text.setSize(guiFont.getCharSet().getRenderedSize());
//            text.setText(msg);
//            text.setLocalTranslation((cam.getWidth()/2)-(text.getLineWidth()/2), cam.getHeight(), 0);
//            text.setName("msg");
//            guiNode.attachChild(text);
//            return true;
//        }
//    }
}
