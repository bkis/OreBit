package de.kritzelbit.orebit.io;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameIO {
    
    private static final String SAVEGAME_PATH  = System.getProperty("user.home") + "/.OreBit/savegame.sav";
    
    /**
     * Saves the given savegame to a pre-defined location, which is
     * <code>&lt;user home&gt;/.OreBit/savegame.sav</code>
     * @param saveGame 
     */
    public static void writeSaveGame(SaveGame saveGame){
        try {
            BinaryExporter.getInstance().save(saveGame, new File(SAVEGAME_PATH));
        } catch (IOException ex) {
            Logger.getLogger(GameIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Tries to read an existing savegame from the users home directory
     * and returns the resulting SaveGame object. If no savegame can be found,
     * a new SaveGame instance, featuring default values for a fresh game,
     * will be returned.
     * @return SaveGame SaveGame object
     */
    public static SaveGame readSaveGame(){
        File f = new File(SAVEGAME_PATH);
        if (f.exists()){
            try {
                return (SaveGame) BinaryImporter.getInstance().load(f);
            } catch (IOException ex) {
                Logger.getLogger(GameIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new SaveGame();
    }

}
