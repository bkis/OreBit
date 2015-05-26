package de.kritzelbit.orebit.io;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.system.JmeSystem;
import jme3tools.savegame.SaveGame;


public class GameIO {
    
    private static final String SAVEGAME_PATH  = "OreBit";
    private static final String SAVEGAME_FILENAME  = "savegame.sav";
    private static final String MISSIONS_PATH  = "missions/";
    private static final String MISSIONS_EXTENSION  = ".mis";
    
    
    public static void writeSaveGame(SaveGameContainer saveGame){
        SaveGame.saveGame(SAVEGAME_PATH, SAVEGAME_FILENAME, saveGame, JmeSystem.StorageFolderType.Internal);
    }
    
    public static SaveGameContainer readSaveGame(){
        return (SaveGameContainer)SaveGame.loadGame(SAVEGAME_PATH, SAVEGAME_FILENAME,  JmeSystem.StorageFolderType.Internal);
    }

    public void registerMissionLocator(String mission, AssetManager assetManager){
        assetManager.registerLocator(MISSIONS_PATH + mission + MISSIONS_EXTENSION, ZipLocator.class);
    }
    
}
