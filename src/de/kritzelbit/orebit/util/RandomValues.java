package de.kritzelbit.orebit.util;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.Random;



public class RandomValues {
    
    private static Random rnd = new Random();
    private static char[] v = {'a','e','i','o','u'};
    private static char[] c = {'q','w','r','t','z','p','s','d','f','g','h',
                               'j','k','l','y','x','c','v','b','n','m'};
    
    public static String getRndName(){
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < rnd.nextInt(5)+3; i++){
            if (i%2 == 0){
                sb.append(v[rnd.nextInt(v.length)]);
            } else {
                sb.append(c[rnd.nextInt(c.length)]);
            }
            if (i == 0) sb.setCharAt(0, sb.toString().toUpperCase().charAt(0));
        }    
        
        return sb.toString();
    }
    
    public static String getRndCodeName(){
        return (char)(int)(65+(Math.random()*10)) + "-"
                + (int)(Math.random()*100);
    }
    
    public static Vector3f getRndVector3f(float min, float max){
        return new Vector3f(
                getRndFloat(min, max),
                getRndFloat(min, max),
                getRndFloat(min, max));
    }
    
    public static ColorRGBA getRndColor(){
        return new ColorRGBA(
                getRndFloat(0, 1),
                getRndFloat(0, 1),
                getRndFloat(0, 1),
                1);
    }
    
    public static ColorRGBA getRndColor(int maxChannel){
        return new ColorRGBA(
                getRndFloat(maxChannel == 0 ? 1 : 0, 1),
                getRndFloat(maxChannel == 1 ? 1 : 0, 1),
                getRndFloat(maxChannel == 2 ? 1 : 0, 1),
                1);
    }
    
    public static ColorRGBA getRndColor(float alpha){
        return new ColorRGBA(
                getRndFloat(0, 1),
                getRndFloat(0, 1),
                getRndFloat(0, 1),
                alpha);
    }
    
    public static ColorRGBA getRndColor(float min, float alpha){
        return new ColorRGBA(
                getRndFloat(min, 1),
                getRndFloat(min, 1),
                getRndFloat(min, 1),
                alpha);
    }
    
    public static float getRndFloat(float min, float max){
        return (rnd.nextFloat() * (max-min)) + min;
    }
    
    public static int getRndInt(int min, int max){
        return rnd.nextInt(max-min+1)+min;
    }
    
}
