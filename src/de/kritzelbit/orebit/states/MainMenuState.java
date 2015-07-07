package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.orebit.gui.GUIController;
import de.kritzelbit.orebit.io.GameIO;
import de.kritzelbit.orebit.io.SaveGameData;
import de.lessvoid.nifty.elements.render.TextRenderer;


public class MainMenuState extends AbstractAppState {
    
    private GUIController gui;

    public MainMenuState(GUIController gui) {
        this.gui = gui;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        gui.loadScreen("start");
        app.getInputManager().setCursorVisible(true);
        
        //check for savegame
        checkForSaveGame();
    }
    
    public void checkForSaveGame(){
        SaveGameData sg = GameIO.readSaveGame();
        if (sg == null) {
            gui.hideElement("buttonContinueGame");
        } else {
            gui.getElement("labelButtonContinueGame").getRenderer(TextRenderer.class)
                    .setText("> mission " + (int)sg.getData(SaveGameData.GAME_MISSION));
            gui.getElement("labelButtonNewGame").getRenderer(TextRenderer.class)
                    .setText("> overwrites existing savegame!");
        }
    }
    
    @Override
    public void update(float tpf) {
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
    }
}
