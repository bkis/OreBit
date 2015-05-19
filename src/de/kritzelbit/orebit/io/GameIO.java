package de.kritzelbit.orebit.io;

import com.jme3.system.JmeSystem;
import jme3tools.savegame.SaveGame;


public class GameIO {
    
    //TODO: CHANGE THIS TO SOMETHING THAT WORKS!!!
    private static final String SAVEGAME_PATH  = "OreBit";
    private static final String SAVEGAME_FILENAME  = "savegame.sav";
    
    public static void writeSaveGame(SaveGameContainer saveGame){
        SaveGame.saveGame(SAVEGAME_PATH, SAVEGAME_FILENAME, saveGame, JmeSystem.StorageFolderType.Internal);
//        try {
//            BinaryExporter.getInstance().save(saveGame, new File(SAVEGAME_PATH));
//        } catch (IOException ex) {
//            Logger.getLogger(GameIO.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    public static SaveGameContainer readSaveGame(){
        return (SaveGameContainer)SaveGame.loadGame(SAVEGAME_PATH, SAVEGAME_FILENAME,  JmeSystem.StorageFolderType.Internal);
//        File f = new File(SAVEGAME_PATH);
//        if (f.exists()){
//            try {
//                return (SaveGameContainer) BinaryImporter.getInstance().load(f);
//            } catch (IOException ex) {
//                Logger.getLogger(GameIO.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return new SaveGameContainer();
    }

}
