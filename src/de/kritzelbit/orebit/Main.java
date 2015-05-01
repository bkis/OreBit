package de.kritzelbit.orebit;

import de.kritzelbit.orebit.controls.ForcesControl;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Main extends SimpleApplication {
    
    private List<Geometry> geoms;

    public static void main(String[] args) {
        Main app = new Main();
        
        //configure settings
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024, 768);
        settings.setMinResolution(1024, 768);
        settings.setVSync(true);
        settings.setFullscreen(false);
        settings.setTitle("Ore Bit");
        
        app.showSettings = false;
        app.setSettings(settings);
        app.start();
    }
    
    
    @Override
    public void simpleInitApp() {
        
        //test cam settings
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(0,0,100));
        
        geoms = new ArrayList<Geometry>();

        //test sphere planet
        Sphere s = new Sphere(32, 32, 1);
        Geometry sphere = new Geometry("Sphere", s);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", ColorRGBA.Blue);
        mat.setColor("Ambient", ColorRGBA.Blue);
        mat.setColor("Specular", ColorRGBA.Blue);
        sphere.setMaterial(mat);
        rootNode.attachChild(sphere);
        geoms.add(sphere);
        
        //test sphere object
        Sphere o = new Sphere(32, 32, 2);
        Geometry obj = new Geometry("Sphere", o);
        obj.setMaterial(mat);
        obj.move(10, -20, 0);
        rootNode.attachChild(obj);
        geoms.add(obj);
        
        //test sphere object
        Sphere p = new Sphere(32, 32, 3);
        Geometry pl = new Geometry("Sphere", p);
        pl.setMaterial(mat);
        pl.move(20, -20, 0);
        rootNode.attachChild(pl);
        geoms.add(pl);
        
        //add controls
        sphere.addControl(new ForcesControl(new Vector3f(0,-40,0), new HashSet<Geometry>(geoms)));
        obj.addControl(new ForcesControl(new Vector3f(0,40,0), new HashSet<Geometry>(geoms)));
        pl.addControl(new ForcesControl(Vector3f.ZERO, new HashSet<Geometry>(geoms)));
        
        //ambient light
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient); 
        
        //sunlight
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun); 
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
