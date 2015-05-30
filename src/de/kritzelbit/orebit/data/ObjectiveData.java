package de.kritzelbit.orebit.data;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder={"order", "type", "data1", "data2", "message"})
public class ObjectiveData implements Comparable<ObjectiveData>{
    
    //default values
    private int order = 0;
    private String type = "transport";
    private String data1 = "1";
    private String data2 = "base";
    private String message = "And now for something completely different...";
    
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public int compareTo(ObjectiveData data){
        return order - data.getOrder();
    }
    
   @Override
   public String toString(){
       return "Objective #" + order + ": " + message;
   }
    
}
