package de.kritzelbit.orebit.gui;

import com.jme3.app.Application;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.kritzelbit.orebit.OreBit;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.logging.Level;
import java.util.logging.Logger;


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
        nifty.registerScreenController(this);
        nifty.fromXmlWithoutStartScreen("Interface/gui.xml");
        nifty.setIgnoreKeyboardEvents(true);
        app.getGuiViewPort().addProcessor(niftyDisplay);
        
        //set logging level
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
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
        nifty.gotoScreen(screenKey);
        screen = nifty.getCurrentScreen();
        //nifty.addControls();
        System.out.println("SWITCHED TO SCREEN: " + nifty.getCurrentScreen().getScreenId());
    }
    
    private Label getLabel(String labelId){
        return screen.findNiftyControl(labelId, Label.class);
    }
    
    public Element getElement(String elementId){
        return screen.findElementByName(elementId);
    }
    
    public <T extends NiftyControl> T getControl(String id, Class<T> type){
        return screen.findNiftyControl(id, type);
    }
    
    public void setFuelStatus(float fuel, float maxFuel){
        Element panel = getElement("panelFuelStatus");
        panel.getParent().layoutElements();
        panel.setConstraintWidth(new SizeValue(fuel/maxFuel*100 + "%"));
        getLabel("labelFuel").setText((int)fuel + "");
        setPanelColor("panelFuelStatus", fuel/maxFuel*100);
    }
    
    public void setTimeStatus(float timeLeft, float time){
        Element panel = getElement("panelTimeStatus");
        panel.getParent().layoutElements();
        panel.setConstraintWidth(new SizeValue((timeLeft/time*100) + "%"));
        getLabel("labelTime").setText((int)timeLeft + "");
        setPanelColor("panelTimeStatus", timeLeft/time*100);
    }
    
    private void setPanelColor(String id, float percent){
        getElement(id).getRenderer(PanelRenderer.class)
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
    
    public void setDisplaySpeed(float speed){
        getLabel("labelSpeed").setText((int)speed + "");
        if (speed > 10) speed = 10;
        getLabel("labelSpeed").setColor(new Color(
                1,
                1 - speed/10,
                1 - speed/10,
                1));
    }
    
    public void setDisplayMoney(String msg){
        getLabel("labelMoney").setText(msg);
    }
    
    public void startGame(String cmd){
        app.startGame(cmd);
    }
    
    public void setLabelTextAndResize(String labelId, String parentScreen, String text){
        Element e = nifty.getScreen(parentScreen).findElementByName(labelId);
        e.getRenderer(TextRenderer.class).setText(text);
        e.layoutElements();
        e.setConstraintWidth(new SizeValue(e.getRenderer(TextRenderer.class)
                .getTextWidth()+"px"));
        e.setWidth(e.getRenderer(TextRenderer.class).getTextWidth());
        
        int width = 0;
        for (Element el : e.getParent().getElements())
            if (width < el.getWidth())
                width = el.getWidth();
        
        e.getParent().setWidth(width);
    }
    
    public void setButtonText(String buttonId, String parentScreen, String text){
        Element e = nifty.getScreen(parentScreen).findElementByName(buttonId);
        Element t = e.findElementByName("#text");
        t.getRenderer(TextRenderer.class).setText(text);
    }
    
    public void quitGame(){
        app.stop();
    }
    
    public void toggleQuality(){
        if (app.isHqGraphicsEnabled()){
            app.setHqGraphicsEnabled(false);
            getControl("buttonGraphics", Button.class).setText("Graphics: Low");
        } else {
            app.setHqGraphicsEnabled(true);
            getControl("buttonGraphics", Button.class).setText("Graphics: High");
        }
    }
    
    public void hideElement(String name){
        getElement(name).disable();
    }
}
