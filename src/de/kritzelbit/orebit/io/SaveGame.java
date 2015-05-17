package de.kritzelbit.orebit.io;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SaveGame implements Savable{
    
    //default values
    private static final float DEFAULT_SHIP_THRUST = 10f;
    private static final float DEFAULT_SHIP_ROTATE = 3f;
    private static final float DEFAULT_SHIP_GRABBER_LENGTH = 20f;
    private static final float DEFAULT_SHIP_MAX_FUEL = 1000f;
    private static final float DEFAULT_GAME_MISSION = 0f;
    private static final float DEFAULT_GAME_SPEED = 0.7f;
    private static final float DEFAULT_GAME_SHIPS_LEFT = 0f;
    private static final float DEFAULT_GAME_MONEY = 0f;
    
    //keys ship data
    public static final String SHIP_THRUST = "SHIP_THRUST";
    public static final String SHIP_ROTATE = "SHIP_ROT_SPEED";
    public static final String SHIP_GRABBER = "SHIP_GRABBER_LENGTH";
    public static final String SHIP_MAX_FUEL = "SHIP_MAX_FUEL";
    
    //keys game data
    public static final String GAME_MISSION = "GAME_MISSION";
    public static final String GAME_SPEED = "GAME_SPEED";
    public static final String GAME_SHIPS_LEFT = "GAME_SHIPS_LEFT";
    public static final String GAME_MONEY = "GAME_MONEY";
    
    //data
    private Map<String, Float> data;
    
    
    public SaveGame(){
        //create data map and put default values
        data = new HashMap<String, Float>();
        data.put(SHIP_THRUST,     DEFAULT_SHIP_THRUST);
        data.put(SHIP_ROTATE,     DEFAULT_SHIP_ROTATE);
        data.put(SHIP_GRABBER,    DEFAULT_SHIP_GRABBER_LENGTH);
        data.put(SHIP_MAX_FUEL,   DEFAULT_SHIP_MAX_FUEL);
        data.put(GAME_MISSION,    DEFAULT_GAME_MISSION);
        data.put(GAME_SPEED,      DEFAULT_GAME_SPEED);
        data.put(GAME_SHIPS_LEFT, DEFAULT_GAME_SHIPS_LEFT);
        data.put(GAME_MONEY,      DEFAULT_GAME_MONEY);
    }
    
    public float getData(String key){
        return data.get(key);
    }
    
    public void setData(String key, float value){
        data.put(key, value);
    }
    
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(data.get(SHIP_THRUST),     SHIP_THRUST,     DEFAULT_SHIP_THRUST);
        capsule.write(data.get(SHIP_ROTATE),     SHIP_ROTATE,     DEFAULT_SHIP_ROTATE);
        capsule.write(data.get(SHIP_GRABBER),    SHIP_GRABBER,    DEFAULT_SHIP_GRABBER_LENGTH);
        capsule.write(data.get(SHIP_MAX_FUEL),   SHIP_MAX_FUEL,   DEFAULT_SHIP_MAX_FUEL);
        capsule.write(data.get(GAME_MISSION),    GAME_MISSION,    DEFAULT_GAME_MISSION);
        capsule.write(data.get(GAME_SPEED),      GAME_SPEED,      DEFAULT_GAME_SPEED);
        capsule.write(data.get(GAME_SHIPS_LEFT), GAME_SHIPS_LEFT, DEFAULT_GAME_SHIPS_LEFT);
        capsule.write(data.get(GAME_MONEY),      GAME_MONEY,      DEFAULT_GAME_MONEY);
    }
 
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        data.put(SHIP_THRUST,     capsule.readFloat(SHIP_THRUST,     DEFAULT_SHIP_THRUST));
        data.put(SHIP_ROTATE,     capsule.readFloat(SHIP_ROTATE,     DEFAULT_SHIP_ROTATE));
        data.put(SHIP_GRABBER,    capsule.readFloat(SHIP_GRABBER,    DEFAULT_SHIP_GRABBER_LENGTH));
        data.put(SHIP_MAX_FUEL,   capsule.readFloat(SHIP_MAX_FUEL,   DEFAULT_SHIP_MAX_FUEL));
        data.put(GAME_MISSION,    capsule.readFloat(GAME_MISSION,    DEFAULT_GAME_MISSION));
        data.put(GAME_SPEED,      capsule.readFloat(GAME_SPEED,      DEFAULT_GAME_SPEED));
        data.put(GAME_SHIPS_LEFT, capsule.readFloat(GAME_SHIPS_LEFT, DEFAULT_GAME_SHIPS_LEFT));
        data.put(GAME_MONEY,      capsule.readFloat(GAME_MONEY,      DEFAULT_GAME_MONEY));
    }

}
