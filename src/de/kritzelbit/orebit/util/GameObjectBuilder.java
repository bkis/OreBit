package de.kritzelbit.orebit.util;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import de.kritzelbit.orebit.controls.SatelliteControl;
import de.kritzelbit.orebit.entities.Planet;
import de.kritzelbit.orebit.entities.Satellite;


public class GameObjectBuilder {
    
    private static final float PLANET_SHININESS = 8f;
    private static final float PLANET_BOUNCYNESS = 0.4f;
    private static final float PLANET_FRICTION = 1f;
    
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;
    
    public GameObjectBuilder(SimpleApplication app, PhysicsSpace physicsSpace){
        this.assetManager = app.getAssetManager();
        this.physicsSpace = physicsSpace;
    }
    
    
    public Planet buildPlanet(String name,
            float radius,
            float mass,
            ColorRGBA color){
        //geometry
        Geometry planetGeom = buildSphereGeom(name, radius);
        planetGeom.setMaterial(buildMaterial(color, PLANET_SHININESS));
        //physics
        RigidBodyControl planetPhysics = new RigidBodyControl();
        planetGeom.addControl(planetPhysics);
        physicsSpace.add(planetPhysics);
        planetPhysics.setRestitution(PLANET_BOUNCYNESS); //bouncyness
        planetPhysics.setFriction(PLANET_FRICTION);
        planetPhysics.setMass(0); //static object
        //planet object
        Planet planet = new Planet(name, radius, mass, planetGeom, planetPhysics);
        return planet;
    }
    
    public Node buildShip(int fuel, int maxFuel, int thrust, int torque){
        //TODO
        return null;
    }
    
    public Satellite buildSatellite(String name,
            float radius,
            ColorRGBA color,
            Planet target,
            float distance,
            float speed){
        //node, geometry, control
        Node satNode = new Node();
        Geometry satGeom = buildSphereGeom(name, radius);
        satGeom.setMaterial(buildMaterial(color, PLANET_SHININESS)); //TODO: Model, Textur???
        satNode.attachChild(satGeom);
        satGeom.setLocalTranslation(0, distance + target.getRadius(), 0);
        satNode.setLocalTranslation(target.getPhysicsControl().getPhysicsLocation());
        satNode.addControl(new SatelliteControl(speed));
        //physics
        RigidBodyControl satPhysics = new RigidBodyControl();
        satGeom.addControl(satPhysics);
        physicsSpace.add(satPhysics);
        satPhysics.setMass(1);
        satPhysics.setKinematic(true);
        //satellite object
        Satellite sat = new Satellite(name, satNode, satPhysics);
        return sat;
    }
    
    private Geometry buildSphereGeom(String name, float radius){
        Sphere s = new Sphere(16, 16, radius);
        Geometry g = new Geometry(name, s);
        return g;
    }
    
    private Material buildMaterial(ColorRGBA color, float shininess){
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors",true);
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", shininess);
        //mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/planet_diffuse.png"));
        //mat.setTexture("NormalMap", assetManager.loadTexture("Textures/planet_normal.png"));
        return mat;
    }
}
