package de.kritzelbit.orebit.data;

import de.kritzelbit.orebit.util.RandomValues;

public class BaseData {
    
    //default values
    private String id = "Base " + RandomValues.getRndCodeName();
    private float x = 0;
    private float y = 0;
    
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}
