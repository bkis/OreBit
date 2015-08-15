package de.kritzelbit.orebit.util;

import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        if(soundId.contains("radio")) stopAllSoundsWithKeySubstring("radio");
        sounds.get(soundId).play();
        //System.out.println("[SND]\tplaying: " + soundId);
    }
    
    public void playRandomMusic(){
        List<String> keys = new ArrayList<String>();
        for (String s : sounds.keySet())
            if (s.contains("music_")) keys.add(s);
        System.out.println(keys);
        String key = keys.get(RandomValues.getRndInt(0, keys.size()-1));
        if (!readyToPlay(key)) return;
        currentMusicKey = key;
        sounds.get(currentMusicKey).play();
    }
    
    public void playMusic(String path){
        if (!isSoundRegistered(path))
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
            if (e.getValue().isLooping())
                e.getValue().stop();
        }
    }
    
    public void stopAllSoundsWithKeySubstring(String contains){
        for (Entry<String, AudioNode> e : sounds.entrySet()){
            if (e.getKey().toLowerCase()
                    .contains(contains.toLowerCase())) e.getValue().stop();
        }
    }
    
    public void registerSound(String id, String path, boolean loop){
        sounds.put(id, newExternalSound(path, loop));
    }
    
    public boolean isSoundRegistered(String key){
        return sounds.containsKey(key);
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
        sounds.put("radioGameComplete", newSound("radioGameComplete", false));
        sounds.put("radioGameOver", newSound("radioGameOver", false));
        sounds.put("radioMissionComplete", newSound("radioMissionComplete", false));
        sounds.put("radioMissionFailedCrash", newSound("radioMissionFailedCrash", false));
        sounds.put("radioMissionFailedFuel", newSound("radioMissionFailedFuel", false));
        sounds.put("radioMissionFailedLost", newSound("radioMissionFailedLost", false));
        sounds.put("radioMissionFailedTime", newSound("radioMissionFailedTime", false));
        sounds.put("radioObjectiveCheckpoint", newSound("radioObjectiveCheckpoint", false));
        sounds.put("radioObjectiveCollect", newSound("radioObjectiveCollect", false));
        sounds.put("radioObjectiveLand", newSound("radioObjectiveLand", false));
        sounds.put("radioObjectiveSurvive", newSound("radioObjectiveSurvive", false));
        
        //music
        sounds.put("music_startrack", newSound("music_startrack", false));
        sounds.put("music_nt", newSound("music_nt", false));
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
