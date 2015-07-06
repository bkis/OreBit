package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.orebit.data.MissionData;
import de.kritzelbit.orebit.gui.GUIController;
import de.kritzelbit.orebit.io.SaveGameData;
import de.lessvoid.nifty.controls.Button;


public class ShopState extends AbstractAppState {
    
    private static final Upgrade[] UPGRADES_THRUST = {
        new Upgrade(20, 0),
        new Upgrade(30, 1000),
        new Upgrade(40, 2000),
        new Upgrade(50, 3500)};
    
    private static final Upgrade[] UPGRADES_ROTATE = {
        new Upgrade(2, 0),
        new Upgrade(3, 1000),
        new Upgrade(4, 2000),
        new Upgrade(5, 3500)};
    
    private GUIController gui;
    private SaveGameData sg;
    private MissionData mission;

    public ShopState(GUIController gui, SaveGameData sg, MissionData mission) {
        this.gui = gui;
        this.sg = sg;
        this.mission = mission;
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        gui.loadScreen("shop");
        app.getInputManager().setCursorVisible(true);
        
        ////init shop gui values
        //mission details
        gui.setLabelTextAndResize("labelMissionTitle", "shop", mission.getTitle());
        gui.setLabelTextAndResize("labelMissionDesc", "shop", mission.getDescription());
        gui.setLabelTextAndResize("labelMissionTime", "shop", mission.getTimeLimit()+ " s");
        gui.setLabelTextAndResize("labelMissionFuel", "shop", mission.getMaxFuel()+"");
        gui.setLabelTextAndResize("labelMissionReward", "shop", mission.getReward()+"");
        gui.setLabelTextAndResize("labelShopPlayerMoney", "shop", (int)sg.getData(SaveGameData.GAME_MONEY)+"");
        //shop
        setupShopButton("buttonShopEngine", "Engine Power", SaveGameData.SHIP_THRUST, UPGRADES_THRUST);
//        gui.setButtonText("buttonShopEngine", "shop",
//                "Engine Power [" + (int)sg.getData(SaveGameData.SHIP_THRUST) + "]"
//                + "\nUpgrade for 3500");
//        gui.setButtonText("buttonShopRotate", "shop",
//                "Ship Spin [" + (int)sg.getData(SaveGameData.SHIP_ROTATE) + "]"
//                + "\nUpgrade for 3500");
    }
    
    private void setupShopButton(String buttonId, String buttonText, String dataId, Upgrade[] upgrades){
        int uLev = getUpgradeLevel(upgrades,(int)sg.getData(dataId));
        int uVal = upgrades[uLev].value;
        int uNex = upgrades.length > uLev+1 ? uLev+1 : -1;
        
        if (uNex == -1){
            gui.setButtonText(buttonId, "shop", buttonText + " ["
                + uVal + "]");
            gui.getControl(buttonId, Button.class).disable();
        } else {
            gui.setButtonText(buttonId, "shop", buttonText + " ["
                + uVal + " -> " + upgrades[uNex].value + "]"
                + "\nUpgrade for " + upgrades[uNex].price);
        }
    }
    
    private int getUpgradeLevel(Upgrade[] upgrades, int value){
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i].value == value) return i;
        }
        return 0;
    }
    
    @Override
    public void update(float tpf) {
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
    private static class Upgrade {
        public int value, price;
        public Upgrade(int value, int price){
            this.value = value;
            this.price = price;
        }
    }
}
