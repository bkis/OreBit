package de.kritzelbit.orebit.entities;

import com.jme3.bullet.control.GhostControl;
import com.jme3.scene.Spatial;


public class Checkpoint {
    
    private GhostControl ghostControl;
    private Spatial spatial;

    
    public Checkpoint(Spatial spatial, GhostControl ghostControl) {
        this.ghostControl = ghostControl;
        this.spatial = spatial;
    }

    public GhostControl getGhostControl() {
        return ghostControl;
    }

    public void setGhostControl(GhostControl ghostControl) {
        this.ghostControl = ghostControl;
    }

    public Spatial getSpatial() {
        return spatial;
    }

    public void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }
    
}
