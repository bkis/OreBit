package de.kritzelbit.orebit.util;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class SoundPlayer {
    
    private static final String SOUNDS_PATH = "Sounds/";
    private static final String SOUNDS_EXTENSION = ".ogg";
    
    private AssetManager assetManager;
    private static SoundPlayer sp;
    private static Map<String, AudioNode> sounds;
    
    
    private SoundPlayer(AssetManager assetManager){
        this.assetManager = assetManager;
        initSoundsMap();
    }
    
    public static void init(AssetManager assetManager){
        if (sp == null){
            sp = new SoundPlayer(assetManager);
        }
    }
    
    public static SoundPlayer getInstance(){
        isInitialized();
        return sp;
    }
    
    public void play(String soundId){
        if (!isInitialized() || !sounds.containsKey(soundId)) return;
        
        if (sounds.get(soundId).isLooping()){
            sounds.get(soundId).play();
        } else {
            sounds.get(soundId).playInstance();
        }
    }
    
    public void stop(String soundId){
        if (!isInitialized() || !sounds.containsKey(soundId)) return;
        
        if (sounds.get(soundId).isLooping())
            sounds.get(soundId).stop();
        //TODO
    }
    
    public void stopLoops(){
        for (Entry<String, AudioNode> e : sounds.entrySet()){
            if (e.getValue().isLooping()) e.getValue().stop();
        }
    }
    
    public void registerSound(String id, String path, boolean loop){
        sounds.put(id, newExternalSound(path, loop));
    }
    
    private static boolean isInitialized(){
        if (sp != null){
            return true;
        } else {
            System.out.println("[SOUND]\terror: sound player not initialized.");
            return false;
        }
    }
    
    private void initSoundsMap(){
        sounds = new HashMap<String, AudioNode>();
        
        sounds.put("beamInit", newSound("beamInit", false));
        sounds.put("beamLoop", newSound("beamLoop", true));
        sounds.put("buy", newSound("buy", false));
        sounds.put("crash", newSound("crash", false));
        sounds.put("impact", newSound("impact", false));
        sounds.put("thrust", newSound("thrust", true));
        
        //TODO
    }
    
    private AudioNode newExternalSound(String path, boolean loop){
        AudioNode sound = new AudioNode(assetManager, path);
        
        sound.setPositional(false);
        sound.setLooping(loop);
        
        return sound;
    }
    
    private AudioNode newSound(String key, boolean loop){
        AudioNode sound = new AudioNode(assetManager, SOUNDS_PATH
                + key + SOUNDS_EXTENSION);
        
        sound.setPositional(false);
        sound.setLooping(loop);
        
        return sound;
    }
    
}
