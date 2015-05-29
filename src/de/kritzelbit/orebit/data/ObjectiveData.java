package de.kritzelbit.orebit.data;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={"type", "data1", "data2"})
public class ObjectiveData {
    
    //default values
    private String type = "transport";
    private String data1 = "1";
    private String data2 = "base";
    private boolean achieved = false;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    @XmlTransient
    public boolean isAchieved() {
        return achieved;
    }

    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }
    
}
