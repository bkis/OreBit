package de.kritzelbit.orebit.util;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class SoundPlayer extends AbstractAppState {
    
    private static final String SOUNDS_PATH = "Sounds/";
    private static final String SOUNDS_EXTENSION = ".ogg";
    
    private AssetManager assetManager;
    private boolean muted;
    private static SoundPlayer sp;
    private static Map<String, AudioNode> sounds;
    private String currentMusicKey;
    
    
    private SoundPlayer(AssetManager assetManager){
        this.assetManager = assetManager;
        this.muted = false;
        initSoundsMap();
    }
    
    public static void init(AssetManager assetManager){
        if (sp == null){
            sp = new SoundPlayer(assetManager);
        }
    }
    
    public static SoundPlayer getInstance(){
        checkIsInitialized();
        return sp;
    }
    
    @Override
    public void update(float tpf){
        if (currentMusicKey != null
                && sounds.get(currentMusicKey).getStatus() == AudioSource.Status.Stopped){
            sounds.put(currentMusicKey, newSound(currentMusicKey, false));
            sounds.get(currentMusicKey).play();
        }
    }
    
    public void play(String soundId){
        if (!readyToPlay(soundId)) return;
        
        if (sounds.get(soundId).isLooping()){
            sounds.get(soundId).play();
        } else {
            sounds.get(soundId).playInstance();
        }
    }
    
    public void playRandomMusic(){
        String key = "music" + RandomValues.getRndInt(0, getMusicCount()-1);
        currentMusicKey = key;
        sounds.get(currentMusicKey).play();
    }
    
    public void playMusic(String path){
        if (!sounds.containsKey(path))
            registerSound(path, path, false);
        currentMusicKey = path;
        sounds.get(currentMusicKey).play();
    }
    
    public void stop(String soundId){
        if (!readyToPlay(soundId)) return;
        sounds.get(soundId).stop();
    }
    
    public void stopAllSounds(){
        for (Entry<String, AudioNode> e : sounds.entrySet()){
            e.getValue().stop();
        }
        currentMusicKey = null;
    }
    
    public void stopAllLoops(){
        for (Entry<String, AudioNode> e : sounds.entrySet()){
            if (e.getValue().isLooping()) e.getValue().stop();
        }
    }
    
    public void registerSound(String id, String path, boolean loop){
        sounds.put(id, newExternalSound(path, loop));
    }
    
    public void setMuted(boolean muted){
        stopAllSounds();
        this.muted = muted;
    }
    
    public boolean isMuted(){
        return muted;
    }
    
    private boolean readyToPlay(String soundId){
        return checkIsInitialized()
                && sounds.containsKey(soundId)
                && !muted;
    }
    
    private static boolean checkIsInitialized(){
        if (sp != null){
            return true;
        } else {
            System.out.println("[SOUND]\terror: sound player not initialized.");
            return false;
        }
    }
    
    private void initSoundsMap(){
        sounds = new HashMap<String, AudioNode>();
        
        //sfx
        sounds.put("boostLoop", newSound("boostLoop", true));
        sounds.put("boostInit", newSound("boostInit", false));
        sounds.put("beamInit", newSound("beamInit", false));
        sounds.put("beamLoop", newSound("beamLoop", true));
        sounds.put("crash", newSound("crash", false));
        sounds.put("impact", newSound("impact", false));
        sounds.put("thrust", newSound("thrust", true));
        
        //music
        sounds.put("music0", newSound("music0", false));
    }
    
    private int getMusicCount(){
        if (!checkIsInitialized()) return 0;
        int count = 0;
        for (Entry<String, AudioNode> e : sounds.entrySet()){
            if (e.getKey().startsWith("music")) count++;
        }
        return count;
    }
    
    private AudioNode newExternalSound(String path, boolean loop){
        AudioNode sound = new AudioNode(assetManager, path);
        
        sound.setPositional(false);
        sound.setLooping(loop);
        
        return sound;
    }
    
    private AudioNode newSound(String key, boolean loop){
        AudioNode sound = new AudioNode(assetManager, SOUNDS_PATH
                + key + SOUNDS_EXTENSION, key.startsWith("music"));
        
        sound.setPositional(false);
        sound.setLooping(loop);
        
        return sound;
    }
    
}
