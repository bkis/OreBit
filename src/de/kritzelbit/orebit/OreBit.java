package de.kritzelbit.orebit;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import de.kritzelbit.orebit.io.XMLLoader;
import de.kritzelbit.orebit.states.IngameState;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


public class OreBit extends SimpleApplication {
    

    public static void main(String[] args) {
        OreBit app = new OreBit();
        
        //get local screen resolution
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        
        //configure settings
        AppSettings settings = new AppSettings(true);
        //settings.setResolution(width, height);
        settings.setResolution(1024, 768);
        settings.setMinResolution(1024, 768);
        settings.setVSync(false);
        settings.setFrameRate(60);
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
        
        //game state
        stateManager.attach(new IngameState());
        
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
    
}
