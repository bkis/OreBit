package de.kritzelbit.orebit.util;


public class SoundPlayer {
    
    private SoundPlayer sp;
    
    private SoundPlayer(){}
    
    public void init(){
        if (sp != null) return;
        sp = new SoundPlayer();
    }
    
    public SoundPlayer getInstance(){
        if (sp != null){
            return sp;
        } else {
            System.out.println("[SOUND]\terror: sound player not initialized!");
            return null;
        }
    }
    
}
