package de.kritzelbit.orebit.util;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import de.kritzelbit.orebit.controls.ForcesControl;
import de.kritzelbit.orebit.controls.SatelliteControl;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import de.kritzelbit.orebit.entities.Asteroid;
import de.kritzelbit.orebit.entities.Planet;
import de.kritzelbit.orebit.entities.Satellite;
import java.util.Set;


public class GameObjectBuilder {
    
    private static final float PLANET_SHININESS = 8f;
    private static final float PLANET_BOUNCYNESS = 0.3f;
    private static final float PLANET_FRICTION = 0.4f;
    
    private static final float ASTEROID_SHININESS = 10.0f;
    private static final float ASTEROID_BOUNCINESS = 0.3f;
    private static final float ASTEROID_FRICTION = 0.4f;
    
    
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;
    private Set<AbstractGameObject> gSources;
    
    public GameObjectBuilder(SimpleApplication app,
            PhysicsSpace physicsSpace,
            Set<AbstractGameObject> gSources){
        this.assetManager = app.getAssetManager();
        this.physicsSpace = physicsSpace;
        this.gSources = gSources;
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
            float mass,
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
        satPhysics.setMass(0);
        satPhysics.setKinematic(true);
        //satellite object
        Satellite sat = new Satellite(name, satNode, satPhysics, mass);
        return sat;
    }
    
    public Asteroid buildAsteroid(String name,
            float radius,
            float mass,
            ColorRGBA color,
            float initX,
            float initY,
            float initVelX,
            float initVelY){
        //geometry
        Geometry asteroidGeom = buildSphereGeom(name, radius);
        asteroidGeom.setMaterial(buildMaterial(color, ASTEROID_SHININESS));
        //physics
        RigidBodyControl asteroidPhysics = new RigidBodyControl();
        asteroidGeom.addControl(asteroidPhysics);
        physicsSpace.add(asteroidPhysics);
        asteroidPhysics.setRestitution(ASTEROID_BOUNCINESS);
        asteroidPhysics.setFriction(ASTEROID_FRICTION);
        asteroidPhysics.setMass(mass); //dynamic object
        asteroidPhysics.setPhysicsLocation(new Vector3f(initX, initY, 0));
        Vector3f initVel = new Vector3f(initVelX, initVelY, 0);
        asteroidPhysics.applyImpulse(
                initVel,
                asteroidPhysics.getPhysicsLocation().add(initVel.negate()));
        //game object
        asteroidGeom.addControl(new ForcesControl(gSources));
        Asteroid asteroid = new Asteroid(name, asteroidGeom, asteroidPhysics, mass);
        return asteroid;
    }
    
    private Geometry buildSphereGeom(String name, float radius){
        Sphere s = new Sphere(16, 16, radius);
        Geometry g = new Geometry(name, s);
        return g;
    }
    
    public Geometry buildRod(){
        Cylinder c = new Cylinder(2, 4, 0.2f, 4);
        Geometry rod = new Geometry("rod", c);
        rod.setMaterial(buildMaterial(ColorRGBA.White, 10));
        return rod;
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
