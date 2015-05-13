package de.kritzelbit.orebit;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import de.kritzelbit.orebit.states.IngameState;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


public class Main extends SimpleApplication {
    

    public static void main(String[] args) {
        Main app = new Main();
        
        //get local screen resolution
//        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//        int width = gd.getDisplayMode().getWidth();
//        int height = gd.getDisplayMode().getHeight();
        
        //configure settings
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024, 768);
        settings.setMinResolution(1024, 768);
        settings.setVSync(false);
        settings.setFrequency(60);
        settings.setFullscreen(false);
        settings.setTitle("Ore Bit");
        
        app.showSettings = true;
        app.setSettings(settings);
        app.start();
    }
    
    
    @Override
    public void simpleInitApp() {
        
        //cam settings
        flyCam.setEnabled(false);
        
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
    
}
