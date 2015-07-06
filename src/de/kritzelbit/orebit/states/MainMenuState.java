package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.orebit.gui.GUIController;
import de.kritzelbit.orebit.io.GameIO;
import de.kritzelbit.orebit.io.SaveGameData;
import de.lessvoid.nifty.controls.Label;


public class MainMenuState extends AbstractAppState {
    
    private GUIController gui;

    public MainMenuState(GUIController gui) {
        this.gui = gui;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        gui.loadScreen("start");
        //gui.loadScreen("shop");
        app.getInputManager().setCursorVisible(true);
        
        //check for savegame
        SaveGameData sg = GameIO.readSaveGame();
        if (sg == null) {
            gui.hideElement("buttonContinueGame");
        } else {
            Label labelContinue = gui.getControl("labelButtonContinueGame", Label.class);
            labelContinue.setText("> mission " + (int)sg.getData(SaveGameData.GAME_MISSION));
            Label labelNew = gui.getControl("labelButtonNewGame", Label.class);
            labelNew.setText("> overwrites existing savegame!");
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
