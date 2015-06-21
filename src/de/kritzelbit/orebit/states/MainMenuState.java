package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.orebit.gui.GUIController;


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
