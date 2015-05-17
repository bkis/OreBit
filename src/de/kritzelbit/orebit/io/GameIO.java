package de.kritzelbit.orebit.io;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameIO {
    
    private static final String SAVEGAME_PATH  = System.getProperty("user.home") + "/.OreBit/savegame.sav";
    
    
    public static void writeSaveGame(SaveGame saveGame){
        try {
            BinaryExporter.getInstance().save(saveGame, new File(SAVEGAME_PATH));
        } catch (IOException ex) {
            Logger.getLogger(GameIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static SaveGame readSaveGame(){
        try {
            return (SaveGame) BinaryImporter.getInstance().load(new File(SAVEGAME_PATH));
        } catch (IOException ex) {
            Logger.getLogger(GameIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new SaveGame();
    }

}
