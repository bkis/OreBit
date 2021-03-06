package de.bkis.orebit.gui;

import com.jme3.app.Application;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.bkis.orebit.OreBit;
import de.bkis.orebit.states.IngameState;
import de.bkis.orebit.states.MainMenuState;
import de.bkis.orebit.states.ShopState;
import de.bkis.orebit.util.SoundPlayer;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GUIController implements ScreenController {
    
    private Nifty nifty;
    private Screen screen;   
    private final NiftyJmeDisplay niftyDisplay;
    private final OreBit app;
    private Element popupPause;
    
    
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
        nifty.fromXml("Interface/gui.xml", screenKey);
        screen = nifty.getScreen(screenKey);
        System.out.println("[GUI]\tswitched to screen: " + screen.getScreenId());
    }
    
    public Label getLabel(String labelId){
        return screen.findNiftyControl(labelId, Label.class);
    }
    
    public Element getElement(String elementId){
        return screen.findElementByName(elementId);
    }
    
    public String getScreenId(){
        return nifty.getCurrentScreen().getScreenId();
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
    
    public void startMission(){
        ShopState shopState = app.getStateManager().getState(ShopState.class);
        shopState.startMission();
    }
    
    public void shopButtonClicked(String key){
        ShopState shopState = app.getStateManager().getState(ShopState.class);
        shopState.shopButtonClicked(key);
    }
    
    public void showInstructions(){
        loadScreen("instructions");
    }
    
    public void instructionsBack(){
        loadScreen("start");
        MainMenuState mms = app.getStateManager().getState(MainMenuState.class);
        mms.checkForSaveGame(app.getAssetManager());
    }
    
    public void buttonPauseResume(){
        app.getStateManager().getState(IngameState.class).buttonPauseResume();
    }
    
    public void buttonPauseMainMenu(){
        app.getStateManager().getState(IngameState.class).buttonPauseMainMenu();
    }
    
    public void buttonBackClicked(){
        app.toMainMenu();
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
    
    public void toggleSound(){
        if (!SoundPlayer.getInstance().isMuted()){
            SoundPlayer.getInstance().setMuted(true);
            getControl("buttonSound", Button.class).setText("Sound: Off");
            nifty.getSoundSystem().setSoundVolume(0);
            nifty.getSoundSystem().setMusicVolume(0);
        } else {
            SoundPlayer.getInstance().setMuted(false);
            getControl("buttonSound", Button.class).setText("Sound: On");
            nifty.getSoundSystem().setSoundVolume(1);
            nifty.getSoundSystem().setMusicVolume(1);
        }
    }
    
    public void setLabelText(
            String labelId,
            String parentScreen,
            String text,
            boolean resizeLabelToFitTextWidth){
        Element e = nifty.getScreen(parentScreen).findElementById(labelId);
        TextRenderer tr = e.getRenderer(TextRenderer.class);
        tr.setText(text);
        e.layoutElements();
        
        if (resizeLabelToFitTextWidth){
            e.setConstraintWidth(new SizeValue(tr.getTextWidth()+"px"));
        }
    }
    
    public void setButtonText(String buttonId, String parentScreen, String text){
        Element e = nifty.getScreen(parentScreen).findElementById(buttonId);
        Element t = e.findElementById("#text");
        t.getRenderer(TextRenderer.class).setText(text);
    }
    
    public void hideElement(String name){
        getElement(name).disable();
    }
    
    public void showPausePopup(){
        //ingame pause popup
        if (popupPause == null) popupPause = nifty.createPopup("popupPause");
        nifty.showPopup(screen, popupPause.getId(), null);
    }
    
    public void closePausePopup(){
        nifty.closePopup(popupPause.getId());
    }
    
    public void setImage(String imageId, String imagePath){
        Element e = screen.findElementById(imageId);
        setImage(e, imagePath);
    }
    
    private void setImage(Element e, String imagePath){
        NiftyImage img = nifty.getRenderEngine().createImage(screen, imagePath, false);
        e.getRenderer(ImageRenderer.class).setImage(img);
    }
}
