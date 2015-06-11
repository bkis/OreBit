package de.kritzelbit.orebit.gui;

import com.jme3.app.Application;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.kritzelbit.orebit.OreBit;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;


public class GUIController  implements ScreenController {
    
    private Nifty nifty;
    private Screen screen;   
    private final NiftyJmeDisplay niftyDisplay;
    private final OreBit app;
    
    
    public GUIController(Application app){
        this.app = (OreBit) app;
        niftyDisplay = new NiftyJmeDisplay(
            app.getAssetManager(),
            app.getInputManager(),
            app.getAudioRenderer(),
            app.getViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.setIgnoreKeyboardEvents(true);
        app.getGuiViewPort().addProcessor(niftyDisplay);
        
        //set logging level
//        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
//        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
    }
    
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {
        
    }

    public void onEndScreen() {
        
    }
    
    public void loadScreen(String screenKey){
        nifty.fromXml("Interface/gui.xml", screenKey, this);
        this.screen = nifty.getCurrentScreen();
    }
    
    public Label getLabel(String labelId){
        return screen.findNiftyControl(labelId, Label.class);
    }
    
}
