package de.bkis.orebit.util;


public class ValueConverter {
    
    public static float shipPower(float value, boolean toInternal){
        if (toInternal){
            return (value * 1.5f) + 20;
        } else {
            return (value - 20) / 1.5f;
        }
    }
    
    public static float shipSpin(float value, boolean toInternal){
        if (toInternal){
            return value + 1;
        } else {
            return value - 1;
        }
    }
    
    public static float shipBeamLength(float value, boolean toInternal){
        if (toInternal){
            return (value * 3) + 10;
        } else {
            return (value - 10) / 3;
        }
    }
    
    public static float shipBoost(float value, boolean toInternal){
        if (toInternal){
            return value * 1.5f;
        } else {
            return value / 1.5f;
        }
    }
    
}
