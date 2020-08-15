package de.kritzelbit.orebit.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.kritzelbit.orebit.OreBit;
import de.kritzelbit.orebit.data.MissionData;
import de.kritzelbit.orebit.gui.GUIController;
import de.kritzelbit.orebit.io.GameIO;
import de.kritzelbit.orebit.io.SaveGameData;
import de.kritzelbit.orebit.util.SoundPlayer;


public class ShopState extends AbstractAppState {
    
    private static final int NEW_GAME_COST = 500;
    
    private static final Upgrade[] UPGRADES = {
        new Upgrade(1, 0),
        new Upgrade(2, 150),
        new Upgrade(3, 350),
        new Upgrade(4, 600),
        new Upgrade(5, 900),
        new Upgrade(6, 1250),
        new Upgrade(7, 1650),
        new Upgrade(8, 2100),
        new Upgrade(9, 2600),
        new Upgrade(10, 3200)};
    
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
            SoundPlayer.getInstance().play("radioGameComplete");
            return;
        }
        //game lost?
        if (!checkMoney()){
            gui.loadScreen("end");
            initLoseScreen();
            GameIO.deleteSaveGame();
            SoundPlayer.getInstance().play("radioGameOver");
            return;
        }
        
        gui.loadScreen("shop");
        
        ////init shop gui values
        //mission details
        gui.setLabelText("labelMissionTitle", "shop", mission.getTitle(), false);
        gui.setLabelText("labelMissionDesc", "shop", mission.getDescription(), false);
        gui.setLabelText("labelMissionTime", "shop", mission.getTimeLimit()+ " s", false);
        gui.setLabelText("labelMissionFuel", "shop", mission.getMaxFuel()+"", false);
        gui.setLabelText("labelMissionReward", "shop", mission.getReward()+"", false);
        //shop
        updateShopButtons();
        gui.setButtonText("buttonShopStart", "shop", "Start Mission!\n(Cost: " + NEW_GAME_COST + " for New Ship)");
    }
    
    private void updateShopButtons(){
        gui.setLabelText("labelShopPlayerMoney", "shop", (int)sg.getData(SaveGameData.GAME_MONEY)+"", true);
        setupShopButton("ShopEngine", "Engine Power", SaveGameData.SHIP_THRUST);
        setupShopButton("ShopRotate", "Ship Rotation Speed", SaveGameData.SHIP_ROTATE);
        setupShopButton("ShopGrabber", "Max. Tractor Beam Length", SaveGameData.SHIP_GRABBER);
        setupShopButton("ShopBooster", "Engine Booster", SaveGameData.SHIP_BOOSTER);
        if (sg.getData(SaveGameData.GAME_MONEY) < NEW_GAME_COST) gui.getElement("buttonShopStart").disable();
    }
    
    private void setupShopButton(String id, String buttonText, String dataId){
        int uLev = getUpgradeLevel((int)sg.getData(dataId));
        int uVal = UPGRADES[uLev].value;
        int uNex = UPGRADES.length > uLev+1 ? uLev + 1 : -1;
        
        //set label
        if (uNex == -1){
            gui.setLabelText(
                    "label" + id,
                    "shop",
                    buttonText + ": " + uVal + "\n(MAXIMUM)",
                    false);
        } else {
            gui.setLabelText(
                    "label" + id,
                    "shop",
                    buttonText + "\nUpgrade: " + uVal + " >> " + UPGRADES[uNex].value
                        + "\nCost: " + UPGRADES[uNex].price,
                    false);
        }
        
        //disable if conditions are not met
        if (uNex == -1 || (UPGRADES[uNex].price + NEW_GAME_COST) > sg.getData(SaveGameData.GAME_MONEY)){
            gui.getElement("button"+id).disable();
        }
    }
    
    public void shopButtonClicked(String key){
        if (key.equals("thrust")){
            buyUpgrade(SaveGameData.SHIP_THRUST);
        } else if (key.equals("rotate")){
            buyUpgrade(SaveGameData.SHIP_ROTATE);
        } else if (key.equals("grabber")){
            buyUpgrade(SaveGameData.SHIP_GRABBER);
        } else if (key.equals("booster")){
            buyUpgrade(SaveGameData.SHIP_BOOSTER);
        }
        updateShopButtons();
    }
    
    private void buyUpgrade(String dataId){
        Upgrade uNex = UPGRADES[getUpgradeLevel((int)sg.getData(dataId))+1];
        sg.setData(dataId, uNex.value);
        sg.setData(SaveGameData.GAME_MONEY, sg.getData(SaveGameData.GAME_MONEY)
                - uNex.price);
        GameIO.writeSaveGame(sg);
    }
    
    private int getUpgradeLevel(int value){
        for (int i = 0; i < UPGRADES.length; i++) {
            if (UPGRADES[i].value == value) return i;
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
                    Integer.toString((int)sg.getData(SaveGameData.GAME_MISSION)),
                    GameIO.CAMPAIGN_NAME,
                    app.getAssetManager());
        }
        return mission != null;
    }
    
    private boolean checkMoney(){
        return sg.getData(SaveGameData.GAME_MONEY) >= NEW_GAME_COST;
    }
    
    private void initWinScreen(){
        gui.setLabelText("labelEndGameMsg", "end", "CAMPAIGN COMPLETE! YOU WIN!", false);
        gui.setImage("imageEndGame", "Interface/game-end-win.png");
    }
    
    private void initLoseScreen(){
        gui.setLabelText("labelEndGameMsg", "end", "NO MORE MONEY! GAME OVER!", false);
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
