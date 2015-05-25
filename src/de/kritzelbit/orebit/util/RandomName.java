package de.kritzelbit.orebit.util;

import java.util.Random;



public class RandomName {
    
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
    
}
