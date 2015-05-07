package de.kritzelbit.orebit.util;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.ForcesControl;
import de.kritzelbit.orebit.controls.SatelliteControl;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import de.kritzelbit.orebit.entities.Asteroid;
import de.kritzelbit.orebit.entities.Planet;
import de.kritzelbit.orebit.entities.Satellite;
import de.kritzelbit.orebit.entities.Ship;
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
    
    public Ship buildShip(int fuel, int maxFuel, int thrust, int spin){
        Box s = new Box(1,1,1);
        Geometry shipGeom = new Geometry("ship", s);
        Material shipMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        shipMat.setBoolean("UseMaterialColors",true);
        shipMat.setColor("Diffuse", ColorRGBA.Cyan);
        shipMat.setColor("Ambient", ColorRGBA.Cyan);
        shipMat.setColor("Specular", ColorRGBA.White);
        shipMat.setFloat("Shininess", 9);
        //shipMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/ship.png"));
        //shipMat.setTexture("NormalMap", assetManager.loadTexture("Textures/normal.png"));
        shipGeom.setMaterial(shipMat);
        
        //physics
        RigidBodyControl shipPhysics = new RigidBodyControl();
        shipGeom.addControl(shipPhysics);
        physicsSpace.add(shipPhysics);
        shipPhysics.setRestitution(0); //bouncyness
        shipPhysics.setFriction(1);
        shipPhysics.setMass(1);
        shipGeom.getControl(RigidBodyControl.class)
                .setPhysicsLocation(new Vector3f(10,10,0));
        shipGeom.addControl(new ForcesControl(gSources));
        shipGeom.addControl(new FlightControl(shipGeom, thrust, spin));
        
        Geometry grabber = buildBoxGeom("grabber", .2f);
        grabber.setMaterial(buildMaterial(ColorRGBA.White, 8));
        Node rodNode = new Node("rodNode");
        rodNode.attachChild(grabber);
        grabber.rotate(FastMath.DEG_TO_RAD*90, 0, 0);
        RigidBodyControl rodPhysics = new RigidBodyControl();
        rodNode.addControl(rodPhysics);
        physicsSpace.add(rodPhysics);
        rodPhysics.setRestitution(0); //bouncyness
        rodPhysics.setFriction(1);
        rodPhysics.setMass(0.1f);
        rodPhysics.setPhysicsRotation(new Quaternion()
                .fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0)));
        
        rodNode.getControl(RigidBodyControl.class)
                .setPhysicsLocation(new Vector3f(10,6.5f,0));
        rodNode.addControl(new ForcesControl(gSources));
        
        HingeJoint joint = new HingeJoint(shipGeom.getControl(RigidBodyControl.class), // A
                     rodNode.getControl(RigidBodyControl.class), // B
                     new Vector3f(0, 0, 0),  // pivot point local to A
                     new Vector3f(0, 4, 0),    // pivot point local to B 
                     Vector3f.UNIT_Z,           // DoF Axis of A (Z axis)
                     Vector3f.UNIT_Z);          // DoF Axis of B (Z axis)
        physicsSpace.add(joint);
        Ship ship = new Ship("ship", shipGeom, shipPhysics, rodNode, 1, 1000, 1000, 20, 2);
        return ship;
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
    
    public Geometry buildBoxGeom(String name, float size){
        Box b = new Box(size, size, size);
        Geometry box = new Geometry(name, b);
        return box;
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
