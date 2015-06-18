package de.kritzelbit.orebit.io;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.system.JmeSystem;
import de.kritzelbit.orebit.data.MissionData;
import de.kritzelbit.orebit.states.IngameState;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jme3tools.savegame.SaveGame;


public class GameIO {
    
    private static final String SAVEGAME_PATH  = "OreBit";
    private static final String SAVEGAME_FILENAME  = "savegame.sav";
    private static final String CAMPAIGN_FILE_NAME = "campaign";
    private static final String MISSIONS_PATH  = "missions/";
    private static final String MISSIONS_EXTENSION  = ".mis";
    
    
    public static void writeSaveGame(SaveGameData saveGame){
        SaveGame.saveGame(SAVEGAME_PATH, SAVEGAME_FILENAME, saveGame, JmeSystem.StorageFolderType.Internal);
    }
    
    public static SaveGameData readSaveGame(){
        SaveGameData sg = (SaveGameData)SaveGame.loadGame(SAVEGAME_PATH, SAVEGAME_FILENAME,  JmeSystem.StorageFolderType.Internal);
        
        if (sg == null)
            return new SaveGameData();
        else
            return sg;
    }

    private static void registerMissionLocator(String mission, AssetManager assetManager){
        assetManager.registerLocator(MISSIONS_PATH + mission + MISSIONS_EXTENSION, ZipLocator.class);
    }
    
    public static MissionData readMission(String missionTitle, AssetManager assetManager){
        registerMissionLocator(CAMPAIGN_FILE_NAME, assetManager);
        MissionData mission = null;
        
        try {
            JAXBContext context = JAXBContext.newInstance(MissionData.class);
            Unmarshaller um = context.createUnmarshaller();
            mission = (MissionData) um.unmarshal(new StringReader((String)assetManager.loadAsset("mission_" + missionTitle.toLowerCase() + ".xml")));
        } catch (JAXBException ex) {
            Logger.getLogger(IngameState.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return mission;
    }
    
}
