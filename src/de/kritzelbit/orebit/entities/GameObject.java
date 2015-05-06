
package de.kritzelbit.orebit.entities;

import com.jme3.scene.Spatial;


public abstract class GameObject {
    
    private Spatial spatial;
    private String name;
    
    public GameObject(String name, Spatial spatial){
        this.name = name;
        this.spatial = spatial;
    }
    
    public Spatial getSpatial(){
        return spatial;
    }
    
}
