package de.bkis.orebit.data;



public class ObjectiveData implements Comparable<ObjectiveData>{
    
    //default values
    private int order = 0;
    private String type = "transport";
    private String target = "1";
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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
       return message;
   }
    
}
