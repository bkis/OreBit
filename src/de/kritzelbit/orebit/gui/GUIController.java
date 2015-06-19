package de.kritzelbit.orebit.gui;

import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.kritzelbit.orebit.OreBit;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;


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
    
    private Label getLabel(String labelId){
        return screen.findNiftyControl(labelId, Label.class);
    }
    
    private Element getPanel(String panelId){
        return screen.findElementByName(panelId);
    }
    
    public void setFuelStatus(float fuel, float maxFuel){
        Element panel = getPanel("panelFuelStatus");
        panel.getParent().layoutElements();
        panel.setConstraintWidth(new SizeValue(fuel/maxFuel*100 + "%"));
        getLabel("labelFuel").setText((int)fuel + "");
        setPanelColor("panelFuelStatus", fuel/10);
    }
    
    public void setTimeStatus(float timeLeft, float time){
        Element panel = getPanel("panelTimeStatus");
        panel.getParent().layoutElements();
        panel.setConstraintWidth(new SizeValue((timeLeft/time*100) + "%"));
        getLabel("labelTime").setText((int)timeLeft + "");
        setPanelColor("panelTimeStatus", timeLeft/time*100);
    }
    
    private void setPanelColor(String id, float percent){
        getPanel(id).getRenderer(PanelRenderer.class)
                .setBackgroundColor(new Color(
                1,
                percent/100,
                percent/100,
                0.3f));
    }
    
    public void setDisplayLine1(String msg){
        getLabel("labelLine1").setText(msg);
    }
    
    public void setDisplayLine2(String msg){
        getLabel("labelLine2").setText(msg);
    }
    
}
