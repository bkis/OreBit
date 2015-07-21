package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.orebit.OreBit;
import de.kritzelbit.orebit.data.MissionData;
import de.kritzelbit.orebit.gui.GUIController;
import de.kritzelbit.orebit.io.GameIO;
import de.kritzelbit.orebit.io.SaveGameData;


public class ShopState extends AbstractAppState {
    
    private static final int NEW_GAME_COST = 500;
    
    private static final Upgrade[] UPGRADES_THRUST = {
        new Upgrade(20, 0),
        new Upgrade(21, 100),
        new Upgrade(22, 200),
        new Upgrade(23, 300),
        new Upgrade(24, 400),
        new Upgrade(25, 500),
        new Upgrade(26, 600),
        new Upgrade(27, 700),
        new Upgrade(28, 800),
        new Upgrade(29, 900),
        new Upgrade(30, 1000),
        new Upgrade(31, 1100),
        new Upgrade(32, 1200),
        new Upgrade(33, 1300),
        new Upgrade(34, 1400),
        new Upgrade(35, 1500)};
    
    private static final Upgrade[] UPGRADES_ROTATE = {
        new Upgrade(2, 0),
        new Upgrade(3, 200),
        new Upgrade(4, 400),
        new Upgrade(5, 600),
        new Upgrade(6, 800),
        new Upgrade(7, 1000),
        new Upgrade(8, 1200),
        new Upgrade(9, 1400)};
    
    private static final Upgrade[] UPGRADES_GRABBER = {
        new Upgrade(10, 0),
        new Upgrade(15, 300),
        new Upgrade(20, 600),
        new Upgrade(25, 900),
        new Upgrade(30, 1200),
        new Upgrade(35, 1500),
        new Upgrade(40, 1800)};
    
    private static final Upgrade[] UPGRADES_BOOSTER = {
        new Upgrade(1, 0),
        new Upgrade(2, 100),
        new Upgrade(3, 200),
        new Upgrade(4, 300),
        new Upgrade(5, 400),
        new Upgrade(6, 500),
        new Upgrade(7, 600),
        new Upgrade(8, 700),
        new Upgrade(9, 800),
        new Upgrade(10, 900),
        new Upgrade(11, 1000),
        new Upgrade(12, 1100),
        new Upgrade(13, 1200),
        new Upgrade(14, 1300),
        new Upgrade(15, 1400)};
    
    private GUIController gui;
    private SaveGameData sg;
    private MissionData mission;
    private OreBit app;

    public ShopState(GUIController gui, SaveGameData sg, MissionData mission) {
        this.gui = gui;
        this.sg = sg;
        this.mission = mission;
    }
    
    public ShopState(GUIController gui, SaveGameData sg){
        this.gui = gui;
        this.sg = sg;
    }
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (OreBit) app;
        app.getInputManager().setCursorVisible(true);
        
        //game completed?
        if (!checkMission()){
            gui.loadScreen("end");
            initWinScreen();
            GameIO.deleteSaveGame();
            return;
        }
        //game lost?
        if (!checkMoney()){
            gui.loadScreen("end");
            initLoseScreen();
            GameIO.deleteSaveGame();
            return;
        }
        
        gui.loadScreen("shop");
        
        ////init shop gui values
        //mission details
        gui.setLabelTextAndResize("labelMissionTitle", "shop", mission.getTitle(), false);
        gui.setLabelTextAndResize("labelMissionDesc", "shop", mission.getDescription(), true);
        gui.setLabelTextAndResize("labelMissionTime", "shop", mission.getTimeLimit()+ " s", false);
        gui.setLabelTextAndResize("labelMissionFuel", "shop", mission.getMaxFuel()+"", false);
        gui.setLabelTextAndResize("labelMissionReward", "shop", mission.getReward()+"", false);
        //shop
        updateShopButtons();
        gui.setButtonText("buttonShopStart", "shop", "Start Mission!\n(Cost: " + NEW_GAME_COST + " for New Ship)");
    }
    
    private void updateShopButtons(){
        gui.setLabelTextAndResize("labelShopPlayerMoney", "shop", (int)sg.getData(SaveGameData.GAME_MONEY)+"", false);
        setupShopButton("ShopEngine", "Engine Power", SaveGameData.SHIP_THRUST, UPGRADES_THRUST);
        setupShopButton("ShopRotate", "Ship Rotation Speed", SaveGameData.SHIP_ROTATE, UPGRADES_ROTATE);
        setupShopButton("ShopGrabber", "Max. Tractor Beam Length", SaveGameData.SHIP_GRABBER, UPGRADES_GRABBER);
        setupShopButton("ShopBooster", "Engine Booster", SaveGameData.SHIP_BOOSTER, UPGRADES_BOOSTER);
        if (sg.getData(SaveGameData.GAME_MONEY) < NEW_GAME_COST) gui.getElement("buttonShopStart").disable();
    }
    
    private void setupShopButton(String id, String buttonText, String dataId, Upgrade[] upgrades){
        int uLev = getUpgradeLevel(upgrades,(int)sg.getData(dataId));
        int uVal = upgrades[uLev].value;
        int uNex = upgrades.length > uLev+1 ? uLev + 1 : -1;
        
        //set label
        if (uNex == -1){
            gui.setLabelText("label"+id, "shop", buttonText + ": " + uVal + "\n(MAXIMUM)");
        } else {
            gui.setLabelText("label"+id, "shop", buttonText + "\nUpgrade: " + uVal
                + " >> " + upgrades[uNex].value
                + "\nCost: " + upgrades[uNex].price);
        }
        
        //disable if conditions are not met
        if (uNex == -1 || (upgrades[uNex].price + NEW_GAME_COST) > sg.getData(SaveGameData.GAME_MONEY)){
            gui.getElement("button"+id).disable();
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
        GameIO.writeSaveGame(sg);
    }
    
    private int getUpgradeLevel(Upgrade[] upgrades, int value){
        for (int i = 0; i < upgrades.length; i++) {
            if (upgrades[i].value == value) return i;
        }
        return 0;
    }
    
    public void startMission(){
        sg.setData(SaveGameData.GAME_MONEY, sg.getData(SaveGameData.GAME_MONEY)
                - NEW_GAME_COST);
        GameIO.writeSaveGame(sg);
        app.startMission(mission);
    }
    
    @Override
    public void update(float tpf) {
        
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
    private boolean checkMission(){
        if (mission == null){
            mission = GameIO.readMission(
                    (int)sg.getData(SaveGameData.GAME_MISSION)+"",
                    GameIO.CAMPAIGN_NAME,
                    app.getAssetManager());
        }
        return mission != null;
    }
    
    private boolean checkMoney(){
        return sg.getData(SaveGameData.GAME_MONEY) >= NEW_GAME_COST;
    }
    
    private void initWinScreen(){
        gui.setLabelTextAndResize("labelEndGameMsg", "end", "CAMPAIGN COMPLETE! YOU WIN!", false);
        gui.setImage("imageEndGame", "Interface/game-end-win.png");
    }
    
    private void initLoseScreen(){
        gui.setLabelTextAndResize("labelEndGameMsg", "end", "NO MORE MONEY! GAME OVER!", false);
        gui.setImage("imageEndGame", "Interface/game-end-lose.png");
    }
    
    private static class Upgrade {
        public int value, price;
        public Upgrade(int value, int price){
            this.value = value;
            this.price = price;
        }
    }
}
