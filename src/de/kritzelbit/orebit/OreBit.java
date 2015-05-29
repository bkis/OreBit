package de.kritzelbit.orebit;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import de.kritzelbit.orebit.io.GameIO;
import de.kritzelbit.orebit.io.SaveGameContainer;
import de.kritzelbit.orebit.io.XMLLoader;
import de.kritzelbit.orebit.states.IngameState;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.concurrent.Callable;


public class OreBit extends SimpleApplication {
    
    private static int screenWidth;
    private static int screenHeight;
    

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
        settings.setMinResolution(1024, 768);
        settings.setVSync(false);
        settings.setFrameRate(100);
        settings.setFullscreen(false);
        settings.setTitle("OreBit");
        settings.setSettingsDialogImage("Interface/splash.jpg");
        
        app.showSettings = true;
        app.setSettings(settings);
        app.start();
    }
    
    
    @Override
    public void simpleInitApp() {
        //cam settings
        flyCam.setEnabled(false);
        //register custom asset loaders
        assetManager.registerLoader(XMLLoader.class, "xml");
        //in-game state, load mission
        IngameState ingameState = new IngameState(GameIO.readMission("Solaris", getAssetManager()));
        stateManager.attach(ingameState);
    }
    

    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public void setSpeed(float speed){
        this.speed = speed;
    }
    
    public void displayOnScreenMsg(String msg){
        guiNode.detachChildNamed("msg");
        guiFont = assetManager.loadFont("Interface/Fonts/LibSans.fnt");
        BitmapText text = new BitmapText(guiFont, false);
        text.setSize(guiFont.getCharSet().getRenderedSize());
        text.setText(msg);
        text.setLocalTranslation((cam.getWidth()/2)-(text.getLineWidth()/2), cam.getHeight(), 0);
        text.setName("msg");
        guiNode.attachChild(text);
    }
    
    private void writeSaveGame() {
        SaveGameContainer sg = new SaveGameContainer();
        //TODO prepare/fill SaveGameContainer object....
        //sg.setData(SaveGameContainer.GAME_MONEY, 12345);
        GameIO.writeSaveGame(sg);
    }
    
    private SaveGameContainer readSaveGame(){
        return GameIO.readSaveGame();
    }
    
}
