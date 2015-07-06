package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.orebit.OreBit;
import de.kritzelbit.orebit.data.MissionData;
import de.kritzelbit.orebit.gui.GUIController;
import de.kritzelbit.orebit.io.SaveGameData;
import de.lessvoid.nifty.controls.Button;


public class ShopState extends AbstractAppState {
    
    private static final Upgrade[] UPGRADES_THRUST = {
        new Upgrade(20, 0),
        new Upgrade(25, 3000),
        new Upgrade(30, 6000),
        new Upgrade(35, 9000)};
    
    private static final Upgrade[] UPGRADES_ROTATE = {
        new Upgrade(2, 0),
        new Upgrade(4, 2500),
        new Upgrade(6, 5000),
        new Upgrade(8, 7500)};
    
    private static final Upgrade[] UPGRADES_GRABBER = {
        new Upgrade(10, 0),
        new Upgrade(20, 1000),
        new Upgrade(30, 2000),
        new Upgrade(40, 3500)};
    
    private static final Upgrade[] UPGRADES_BOOSTER = {
        new Upgrade(1, 0),
        new Upgrade(5, 2000),
        new Upgrade(10, 4500),
        new Upgrade(15, 7500)};
    
    private GUIController gui;
    private SaveGameData sg;
    private MissionData mission;
    private OreBit app;

    public ShopState(GUIController gui, SaveGameData sg, MissionData mission) {
        this.gui = gui;
        this.sg = sg;
        this.mission = mission;
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (OreBit) app;
        
        gui.loadScreen("shop");
        app.getInputManager().setCursorVisible(true);
        
        ////init shop gui values
        //mission details
        gui.setLabelTextAndResize("labelMissionTitle", "shop", mission.getTitle());
        gui.setLabelTextAndResize("labelMissionDesc", "shop", mission.getDescription());
        gui.setLabelTextAndResize("labelMissionTime", "shop", mission.getTimeLimit()+ " s");
        gui.setLabelTextAndResize("labelMissionFuel", "shop", mission.getMaxFuel()+"");
        gui.setLabelTextAndResize("labelMissionReward", "shop", mission.getReward()+"");
        //shop
        updateShopButtons();
//        gui.setButtonText("buttonShopEngine", "shop",
//                "Engine Power [" + (int)sg.getData(SaveGameData.SHIP_THRUST) + "]"
//                + "\nUpgrade for 3500");
//        gui.setButtonText("buttonShopRotate", "shop",
//                "Ship Spin [" + (int)sg.getData(SaveGameData.SHIP_ROTATE) + "]"
//                + "\nUpgrade for 3500");
    }
    
    private void updateShopButtons(){
        setupShopButton("buttonShopEngine", "Engine Power", SaveGameData.SHIP_THRUST, UPGRADES_THRUST);
        setupShopButton("buttonShopRotate", "Ship Spin Speed", SaveGameData.SHIP_ROTATE, UPGRADES_ROTATE);
        setupShopButton("buttonShopGrabber", "Tractor Beam Length", SaveGameData.SHIP_GRABBER, UPGRADES_GRABBER);
        setupShopButton("buttonShopBooster", "Engine Booster", SaveGameData.SHIP_BOOSTER, UPGRADES_BOOSTER);
        gui.setLabelTextAndResize("labelShopPlayerMoney", "shop", (int)sg.getData(SaveGameData.GAME_MONEY)+"");
    }
    
    private void setupShopButton(String buttonId, String buttonText, String dataId, Upgrade[] upgrades){
        int uLev = getUpgradeLevel(upgrades,(int)sg.getData(dataId));
        int uVal = upgrades[uLev].value;
        int uNex = upgrades.length > uLev+1 ? uLev+1 : -1;
        
        if (uNex == -1 || upgrades[uNex].price > sg.getData(SaveGameData.GAME_MONEY)){
            gui.setButtonText(buttonId, "shop", buttonText + " ["
                + uVal + "]");
            gui.getControl(buttonId, Button.class).disable();
        } else {
            gui.setButtonText(buttonId, "shop", buttonText + ": " + uVal
                + "\nUpgrade to " + upgrades[uNex].value
                + " for " + upgrades[uNex].price);
        }
    }
    
    public void shopButtonClicked(String key){
        if (key.equals("thrust")){
            buyUpgrade(UPGRADES_THRUST, SaveGameData.SHIP_THRUST);
        } else if (key.equals("rotate")){
            buyUpgrade(UPGRADES_ROTATE, SaveGameData.SHIP_ROTATE);
        } else if (key.equals("grabber")){
            buyUpgrade(UPGRADES_GRABBER, SaveGameData.SHIP_GRABBER);
        } else if (key.equals("booster")){
            buyUpgrade(UPGRADES_BOOSTER, SaveGameData.SHIP_BOOSTER);
        }
        updateShopButtons();
    }
    
    private void buyUpgrade(Upgrade[] upgrades, String dataId){
        Upgrade uNex = upgrades[getUpgradeLevel(upgrades, (int)sg.getData(dataId))+1];
        sg.setData(dataId, uNex.value);
        sg.setData(SaveGameData.GAME_MONEY, sg.getData(SaveGameData.GAME_MONEY)
                - uNex.price);
    }
    
    private int getUpgradeLevel(Upgrade[] upgrades, int value){
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i].value == value) return i;
        }
        return 0;
    }
    
    public void startMission(){
        app.startMission(mission);
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
