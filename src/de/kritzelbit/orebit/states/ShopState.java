package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.orebit.gui.GUIController;
import de.kritzelbit.orebit.io.SaveGameData;


public class ShopState extends AbstractAppState {
    
    private GUIController gui;
    private SaveGameData sg;

    public ShopState(GUIController gui, SaveGameData sg) {
        this.gui = gui;
        this.sg = sg;
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        gui.loadScreen("shop");
        app.getInputManager().setCursorVisible(true);
    }
    
    @Override
    public void update(float tpf) {
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
}
