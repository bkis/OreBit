package de.kritzelbit.orebit.data;

import de.kritzelbit.orebit.util.RandomName;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"id", "x", "y", "shipPosition"})
public class BaseData {
    
    //default values
    private String id = "Base " + RandomName.getRndCodeName();
    private float x = 0;
    private float y = 0;
    private int shipPosition = 0;
    
    
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

    public int getShipPosition() {
        return shipPosition;
    }

    public void setShipPosition(int shipPosition) {
        this.shipPosition = shipPosition;
    }

}
