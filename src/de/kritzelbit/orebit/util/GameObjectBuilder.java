package de.kritzelbit.orebit.util;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import de.kritzelbit.orebit.controls.AsteroidMassIndicatorControl;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.ForcesControl;
import de.kritzelbit.orebit.controls.SatelliteControl;
import de.kritzelbit.orebit.controls.ShipGravityIndicatorControl;
import de.kritzelbit.orebit.controls.ThrusterVisualsControl;
import de.kritzelbit.orebit.data.AsteroidData;
import de.kritzelbit.orebit.data.BaseData;
import de.kritzelbit.orebit.data.OreData;
import de.kritzelbit.orebit.data.PlanetData;
import de.kritzelbit.orebit.data.SatelliteData;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import de.kritzelbit.orebit.entities.Asteroid;
import de.kritzelbit.orebit.entities.Base;
import de.kritzelbit.orebit.entities.Ore;
import de.kritzelbit.orebit.entities.Planet;
import de.kritzelbit.orebit.entities.Satellite;
import de.kritzelbit.orebit.entities.Ship;
import java.util.Random;
import java.util.Set;


public class GameObjectBuilder {
    
    private static final float PLANET_SHININESS = 0f;
    private static final float PLANET_BOUNCYNESS = 0.3f;
    private static final float PLANET_FRICTION = 10f;
    
    private static final float ASTEROID_SHININESS = 10.0f;
    private static final float ASTEROID_BOUNCINESS = 0.3f;
    private static final float ASTEROID_FRICTION = 0f;
    
    
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
    
    
    public Planet buildPlanet(PlanetData data){
        
        //geometry
        Geometry planetGeom = buildSphereGeom(data.getId(), data.getRadius());
        planetGeom.setMaterial(buildMaterial(new ColorRGBA(
                data.getColorR(), data.getColorG(), data.getColorB(), 1)
                .mult(2), PLANET_SHININESS));
        planetGeom.getMaterial().setTexture("DiffuseMap", assetManager
                .loadTexture("Textures/Planets/" 
                + (new Random().nextInt(7) + 1) + ".jpg"));
        
        //node
        Node planetNode = new Node();
        planetNode.attachChild(planetGeom);
        planetNode.attachChild(buildMassIndicator(data.getRadius(), data.getMass()));
        
        //physics
        RigidBodyControl planetPhysics = new RigidBodyControl();
        planetGeom.addControl(planetPhysics);
        physicsSpace.add(planetPhysics);
        planetPhysics.setRestitution(PLANET_BOUNCYNESS); //bouncyness
        planetPhysics.setFriction(PLANET_FRICTION);
        planetPhysics.setMass(0); //static object
        
        //planet object
        Planet planet = new Planet(data.getId(), data.getRadius(), data.getMass(), planetNode, planetPhysics);
        return planet;
    }
    
    public Ship buildShip(int fuel, int maxFuel, int thrust, int spin, int grabberLength){
        Geometry shipGeom = buildBoxGeom("shipGeom", 1);
        //shipMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/ship.png"));
        //shipMat.setTexture("NormalMap", assetManager.loadTexture("Textures/normal.png"));
        shipGeom.setMaterial(buildMaterial(ColorRGBA.Cyan, 0));
        
        //physics
        RigidBodyControl shipPhysics = new RigidBodyControl();
        shipGeom.addControl(shipPhysics);
        physicsSpace.add(shipPhysics);
        shipPhysics.setRestitution(0); //bouncyness
        shipPhysics.setFriction(0);
        shipPhysics.setMass(1);
        
        //controls
        shipGeom.addControl(new ForcesControl(gSources));
        shipGeom.addControl(new FlightControl(shipGeom, thrust, spin));
        
        //grabber
        Geometry grabber = buildLineGeom(Vector3f.ZERO, Vector3f.ZERO);
        grabber.setMaterial(buildUnshadedMaterial(ColorRGBA.Blue));
        grabber.getMaterial().setColor("GlowColor", ColorRGBA.Blue);
        
        //gravity indicator
        Geometry gravityIndicator = buildSphereGeom("gravityIndicator", 0.3f, 4);
        gravityIndicator.setMaterial(buildUnshadedMaterial(ColorRGBA.White));
        gravityIndicator.addControl(new ShipGravityIndicatorControl(shipGeom));
        
        //thruster visuals
        ParticleEmitter thrusterVisuals = new ParticleEmitter("thrusterVisuals", ParticleMesh.Type.Triangle, 30);
        Material thrusterMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        thrusterMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/flame.png"));
        thrusterVisuals.setMaterial(thrusterMat);
        thrusterVisuals.setImagesX(2); thrusterVisuals.setImagesY(2);
        thrusterVisuals.setEndColor(new ColorRGBA(1f, 0f, 0f, 0.5f) ); // red
        thrusterVisuals.setStartColor(new ColorRGBA(1f, 1f, 0f, 1f) ); // yellow
        thrusterVisuals.setInWorldSpace(false);
        thrusterVisuals.setStartSize(0.8f);
        thrusterVisuals.setEndSize(0.1f);
        thrusterVisuals.setGravity(0f,0f,0f);
        thrusterVisuals.setLowLife(0.2f);
        thrusterVisuals.setHighLife(0.29f);
        thrusterVisuals.getParticleInfluencer().setVelocityVariation(0.1f);
        thrusterVisuals.addControl(new ThrusterVisualsControl(shipGeom));
        
        //explosion visuals
        ParticleEmitter explosionVisuals = new ParticleEmitter("explosionVisuals", ParticleMesh.Type.Triangle, 20);
        Material explosionMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        explosionMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/Debris.png"));
        explosionVisuals.setImagesX(3); explosionVisuals.setImagesY(3);
        explosionVisuals.setMaterial(explosionMat);
        explosionVisuals.setRotateSpeed(20);
        explosionVisuals.setSelectRandomImage(true);
        explosionVisuals.setStartColor(ColorRGBA.Cyan);
        explosionVisuals.setEndColor(ColorRGBA.Cyan);
        explosionVisuals.setGravity(0f,0f,0f);
        explosionVisuals.getParticleInfluencer().setVelocityVariation(0.5f);
        explosionVisuals.setRandomAngle(true);
        explosionVisuals.setStartSize(0.4f);
        explosionVisuals.setEndSize(0.6f);
        explosionVisuals.setHighLife(1.6f);
        
        Ship ship = new Ship("ship", shipGeom, grabber, gravityIndicator, thrusterVisuals, explosionVisuals, fuel, maxFuel, thrust, spin, grabberLength);
        return ship;
    }
    
    public Satellite buildSatellite(SatelliteData data, Planet target){
        //node, geometry, control
        Node satNode = new Node();
        Geometry satGeom = buildSphereGeom("satellite", data.getRadius());
        satGeom.setMaterial(buildMaterial(new ColorRGBA(
                data.getColorR(), data.getColorG(), data.getColorB(), 1)
                .mult(2), PLANET_SHININESS));
        satNode.attachChild(satGeom);
        satGeom.setLocalTranslation(0, data.getDistance() + target.getRadius(), 0);
        satNode.setLocalTranslation(target.getPhysicsControl().getPhysicsLocation());
        satNode.addControl(new SatelliteControl(data.getSpeed()));
        //physics
        RigidBodyControl satPhysics = new RigidBodyControl();
        satGeom.addControl(satPhysics);
        physicsSpace.add(satPhysics);
        satPhysics.setMass(0);
        satPhysics.setKinematic(true);
        
        //node
        Geometry massIndicator = buildMassIndicator(data.getRadius(), data.getMass());
        satNode.attachChild(massIndicator);
        massIndicator.setLocalTranslation(0, data.getDistance() + target.getRadius(), 0);
        
        //satellite object
        Satellite sat = new Satellite("satellite", satNode,
                satPhysics, data.getRadius(), data.getMass());
        return sat;
    }
    
    public Asteroid buildAsteroid(AsteroidData data){
        //geometry
        //Geometry asteroidGeom = buildSphereGeom("asteroid", data.getRadius());
        Spatial asteroidGeom = assetManager.loadModel("Models/Asteroid/asteroid.j3o");
        asteroidGeom.setMaterial(buildMaterial(ColorRGBA.White, ASTEROID_SHININESS));
        //TODO: Model, Texture
        //node
        Geometry massIndicator = buildMassIndicator(data.getRadius(), data.getMass());
        massIndicator.addControl(new AsteroidMassIndicatorControl(asteroidGeom));
        
        //physics
        RigidBodyControl asteroidPhysics = new RigidBodyControl();
        asteroidGeom.addControl(asteroidPhysics);
        physicsSpace.add(asteroidPhysics);
        asteroidPhysics.setRestitution(ASTEROID_BOUNCINESS);
        asteroidPhysics.setFriction(ASTEROID_FRICTION);
        asteroidPhysics.setMass(data.getMass()); //dynamic object
        Vector3f initVel = new Vector3f(data.getInitVelX(), data.getInitVelY(), 0);
        asteroidPhysics.applyImpulse(
                initVel,
                asteroidPhysics.getPhysicsLocation().add(initVel.negate()));
        //game object
        asteroidGeom.addControl(new ForcesControl(gSources));
        
        Asteroid asteroid = new Asteroid("asteroid", asteroidGeom, massIndicator,
                asteroidPhysics, data.getRadius(), data.getMass());
        return asteroid;
    }
    
    public Base buildBase(BaseData b){
        //geometry
        Geometry baseGeom = buildBoxGeom(b.getId(), Base.BASE_SIZE);
        baseGeom.setMaterial(buildMaterial(ColorRGBA.LightGray, 4));
        baseGeom.getMaterial().setColor("GlowColor", new ColorRGBA(0.7f, 0.7f, 0.7f, 0.4f));
        baseGeom.getMaterial().setTexture("DiffuseMap", assetManager
                .loadTexture("Textures/Base/base.jpg"));
     
        //physics
        RigidBodyControl basePhysics = new RigidBodyControl();
        baseGeom.addControl(basePhysics);
        physicsSpace.add(basePhysics);
        basePhysics.setRestitution(0); //bouncyness
        basePhysics.setFriction(10);
        basePhysics.setMass(0); //static object
        
        //planet object
        Base base = new Base(b.getId(), baseGeom, basePhysics);
        return base;
    }
    
    public Ore buildOre(OreData data){
        //geometry
        Geometry oreGeom = buildBoxGeom("ore", data.getRadius());
        oreGeom.setMaterial(buildMaterial(ColorRGBA.LightGray, 4));
        oreGeom.getMaterial().setColor("GlowColor", new ColorRGBA(1f, 0f, 1f, 1f));
     
        //physics
        RigidBodyControl orePhysics = new RigidBodyControl();
        oreGeom.addControl(orePhysics);
        physicsSpace.add(orePhysics);
        orePhysics.setRestitution(0.5f); //bouncyness
        orePhysics.setFriction(8);
        orePhysics.setMass(data.getMass());
        oreGeom.addControl(new ForcesControl(gSources));
        
        //planet object
        Ore ore = new Ore("ore", oreGeom, orePhysics, data.getRadius(), data.getMass());
        return ore;
    }
    
    public Geometry buildBackgroundQuad(Camera cam){
        float dist = 300;
        float height = cam.getFrustumTop()*dist*2.5f;
        float width = cam.getFrustumRight()*dist*2.5f;
        
        //System.out.println("Viewport: " + width + ", " + height);
        Quad q = new Quad(width, height);
        Geometry background = new Geometry("background", q);
        background.setMaterial(buildUnshadedMaterial(ColorRGBA.White));
        background.getMaterial().setTexture("ColorMap", assetManager.loadTexture("Textures/Backgrounds/space.jpg"));
        background.rotate(FastMath.DEG_TO_RAD*180, 0, 0);
        background.move(-width/2, height/2, dist);
        //background.setQueueBucket(Bucket.Sky);
        return background;
    }
    
    private Geometry buildSphereGeom(String name, float radius){
        return buildSphereGeom(name, radius, 32);
    }
    
    private Geometry buildSphereGeom(String name, float radius, int samples){
        Sphere s = new Sphere(samples, samples, radius);
        Geometry g = new Geometry(name, s);
        g.rotate(FastMath.DEG_TO_RAD*90, 0, 0);
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
        mat.setBoolean("UseAlpha",true);
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", shininess);
        return mat;
    }
    
    public Material buildUnshadedMaterial(ColorRGBA color){
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        return mat;
    }
    
    private Geometry buildLineGeom(Vector3f from, Vector3f to){
        Line line = new Line(from, to);
        line.setLineWidth(2);
        Geometry lineGeom = new Geometry("line", line);
        return lineGeom;
    }
    
    private Geometry buildMassIndicator(float radius, float mass){
        Torus t = new Torus(32, 4, 0.1f, radius+0.02f);
        Geometry massIndicator = new Geometry("massIndicator", t);
        //material
        massIndicator.setMaterial(buildUnshadedMaterial(ColorRGBA.BlackNoAlpha));
//        massIndicator.getMaterial().setColor("GlowColor",
//                new ColorRGBA((3+(mass/2))/15, 0, ((6-(mass/2)))/8, 1).mult(3));
        massIndicator.getMaterial().setColor("GlowColor",
                new ColorRGBA((3+(mass/2))/15, 0, ((5-(mass/2)))/8, 1).mult(3));
        massIndicator.getMaterial().getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        massIndicator.setQueueBucket(Bucket.Transparent);
        massIndicator.setMaterial(massIndicator.getMaterial());
        return massIndicator;
    }
    
}
