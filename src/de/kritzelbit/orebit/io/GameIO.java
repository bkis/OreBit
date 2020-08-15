package de.kritzelbit.orebit.io;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.system.JmeSystem;
import de.kritzelbit.orebit.data.MissionData;
import de.kritzelbit.orebit.states.IngameState;
import java.io.File;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jme3tools.savegame.SaveGame;


public class GameIO {
    
    public static final String CAMPAIGN_NAME = "campaign";
    private static final String SAVEGAME_PATH  = "OreBit";
    private static final String SAVEGAME_FILENAME  = "savegame.sav";
    private static final String MISSIONS_PATH  = "missions/";
    private static final String MISSIONS_EXTENSION  = ".zip";
    
    
    public static void writeSaveGame(SaveGameData saveGame){
        System.out.print("[IO]\twriting savegame... ");
        SaveGame.saveGame(SAVEGAME_PATH,
                SAVEGAME_FILENAME,
                saveGame,
                JmeSystem.StorageFolderType.External);
        System.out.println("OK");
    }
    
    public static SaveGameData readSaveGame(){
        SaveGameData sg;
        try {
            sg = (SaveGameData)SaveGame.loadGame(
                SAVEGAME_PATH,
                SAVEGAME_FILENAME,
                JmeSystem.StorageFolderType.External);
        } catch (Exception e){
            System.err.println("[ERROR]\tfound corrupt savegame."
                    + "a fresh one will be created.");
            sg = new SaveGameData();
        }
        
        if (sg == null){
            System.out.println("[IO]\tno savegame found!");
            return null;
        }else{
            System.out.println("[IO]\tloaded savegame: "
                    + (int)sg.getData(SaveGameData.GAME_MISSION));
            return sg;
        }
    }
    
    public static void deleteSaveGame(){
        File dir = JmeSystem.getStorageFolder(JmeSystem.StorageFolderType.External);
        File sav = new File(
                dir.getAbsolutePath()
                + File.separator
                + SAVEGAME_PATH.replace('/', File.separatorChar)
                + File.separator
                + SAVEGAME_FILENAME);
        if (sav.exists() && sav.isFile()){
            sav.delete();
            System.out.println("[IO]\tsavegame deleted.");
        }
    }

    private static void registerMissionLocator(String mission, AssetManager assetManager){
        assetManager.registerLocator(MISSIONS_PATH + mission + MISSIONS_EXTENSION, ZipLocator.class);
    }
    
    public static MissionData readMission(String missionTitle, String campaignTitle, AssetManager assetManager){
        registerMissionLocator(campaignTitle, assetManager);
        MissionData mission = null;
        
        try {
            JAXBContext context = JAXBContext.newInstance(MissionData.class);
            Unmarshaller um = context.createUnmarshaller();
            mission = (MissionData) um.unmarshal(
                    new StringReader((String)assetManager.loadAsset(
                            "mission_" + missionTitle.toLowerCase() + ".xml")));
        } catch (JAXBException ex) {
            Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AssetNotFoundException ex2){
            System.out.println("[IO]\tcouldn't find next mission. game complete.");
        }
        
        return mission;
    }
    
}
