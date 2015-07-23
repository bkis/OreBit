package de.kritzelbit.orebit.util;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
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
import com.jme3.texture.Texture;
import de.kritzelbit.orebit.controls.AsteroidMassIndicatorControl;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.ForcesControl;
import de.kritzelbit.orebit.controls.MagnetControl;
import de.kritzelbit.orebit.controls.MoonControl;
import de.kritzelbit.orebit.controls.ShipGravityIndicatorControl;
import de.kritzelbit.orebit.controls.ThrusterVisualsControl;
import de.kritzelbit.orebit.data.AsteroidData;
import de.kritzelbit.orebit.data.BaseData;
import de.kritzelbit.orebit.data.CheckpointData;
import de.kritzelbit.orebit.data.MagnetData;
import de.kritzelbit.orebit.data.OreData;
import de.kritzelbit.orebit.data.PlanetData;
import de.kritzelbit.orebit.data.MoonData;
import de.kritzelbit.orebit.entities.AbstractGameObject;
import de.kritzelbit.orebit.entities.Asteroid;
import de.kritzelbit.orebit.entities.Base;
import de.kritzelbit.orebit.entities.Ore;
import de.kritzelbit.orebit.entities.Planet;
import de.kritzelbit.orebit.entities.Moon;
import de.kritzelbit.orebit.entities.Ship;
import java.util.Random;
import java.util.Set;


public class GameObjectBuilder {
    
    private static final float PLANET_SHININESS = 0f;
    private static final float PLANET_BOUNCYNESS = 0.3f;
    private static final float PLANET_FRICTION = 8f;
    
    private static final float ASTEROID_SHININESS = 1.0f;
    private static final float ASTEROID_BOUNCINESS = 0.3f;
    private static final float ASTEROID_FRICTION = 2f;
    
    
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;
    private Node rootNode;
    private Set<AbstractGameObject> gSources;
    private boolean hqGraphics;
    
    public GameObjectBuilder(SimpleApplication app,
            PhysicsSpace physicsSpace,
            Node rootNode,
            Set<AbstractGameObject> gSources,
            boolean hqGraphics){
        this.assetManager = app.getAssetManager();
        this.physicsSpace = physicsSpace;
        this.rootNode = rootNode;
        this.gSources = gSources;
        this.hqGraphics = hqGraphics;
    }
    
    
    public void buildPlanet(PlanetData data){
        //geometry
        Geometry planetGeom = buildSphereGeom(data.getId(), data.getRadius());
        planetGeom.setMaterial(buildMaterial(new ColorRGBA(
                data.getColorR(), data.getColorG(), data.getColorB(), 1)
                .mult(1.2f), PLANET_SHININESS));
        int i = new Random().nextInt(7) + 1;
        planetGeom.getMaterial().setTexture("DiffuseMap", assetManager
                .loadTexture("Textures/Planets/" 
                + i + ".jpg"));
        
        //node
        Node planetNode = new Node();
        planetNode.attachChild(planetGeom);
        if (hqGraphics)
            planetNode.attachChild(
                    buildMassIndicator(data.getRadius(), data.getMass()));
        
        //physics
        RigidBodyControl planetPhysics = new RigidBodyControl();
        planetGeom.addControl(planetPhysics);
        physicsSpace.add(planetPhysics);
        planetPhysics.setRestitution(PLANET_BOUNCYNESS); //bouncyness
        planetPhysics.setFriction(PLANET_FRICTION);
        planetPhysics.setMass(0); //static object
        
        //planet object
        Planet planet = new Planet(data.getId(), data.getRadius(), data.getMass(), planetNode, planetPhysics);
        
        planet.setLocation(data.getX(), data.getY());
        rootNode.attachChild(planet.getSpatial());
        gSources.add(planet);
    }
    
    public Ship buildShip(int thrust, int spin, int grabberLength, int fuel, int booster){
        Spatial shipModel = assetManager.loadModel("Models/Ship/ship.j3o");
        Geometry shipGeom = (Geometry)((Node)shipModel).getChild("ship");
        shipGeom.scale(0.8f);
        shipGeom.getMaterial().setColor("Ambient", ColorRGBA.Gray);
        shipGeom.getMaterial().setBoolean("UseMaterialColors",true);
        
        //physics
        RigidBodyControl shipPhysics = new RigidBodyControl(new BoxCollisionShape(new Vector3f(1.1f,1.1f,1.1f)));
        shipGeom.addControl(shipPhysics);
        physicsSpace.add(shipPhysics);
        shipPhysics.setRestitution(0); //bouncyness
        shipPhysics.setFriction(20);
        shipPhysics.setMass(1);
        
        //forces
        shipGeom.addControl(new ForcesControl(gSources));
        physicsSpace.addTickListener(shipGeom.getControl(ForcesControl.class));
        
        //grabber
        Geometry grabber = buildGrabberRayGeom(Vector3f.ZERO.clone(), Vector3f.ZERO.clone());
        grabber.setMaterial(buildUnshadedMaterial(new ColorRGBA(0,0.5f,1,1)));
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
        thrusterVisuals.setEndColor(new ColorRGBA(1f, 0f, 0f, 0.5f)); // red
        thrusterVisuals.setStartColor(new ColorRGBA(1f, 1f, 0f, 1f)); // yellow
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
        explosionVisuals.setStartColor(ColorRGBA.White);
        explosionVisuals.setEndColor(ColorRGBA.Gray);
        explosionVisuals.setGravity(0f,0f,0f);
        explosionVisuals.getParticleInfluencer().setVelocityVariation(0.5f);
        explosionVisuals.setRandomAngle(true);
        explosionVisuals.setStartSize(0.4f);
        explosionVisuals.setEndSize(0.6f);
        explosionVisuals.setHighLife(1.6f);
        
        //ship game object
        Ship ship = new Ship("ship", shipGeom, grabber, gravityIndicator, thrusterVisuals, explosionVisuals, thrust, spin, grabberLength);
        
        //flight control
        shipGeom.addControl(new FlightControl(ship, shipPhysics, thrust, spin, fuel, booster));
        physicsSpace.addTickListener(shipGeom.getControl(FlightControl.class));
        
        return ship;
    }
    
    public void buildMagnet(MagnetData data, RigidBodyControl target){
        Geometry magnetGeom = buildSphereGeom("magnet", 0.5f, 8);
        Material magnetMat = buildMaterial(ColorRGBA.LightGray, 5);
        magnetMat.setColor("GlowColor", new ColorRGBA(0.8f,0.8f,1,1));
        magnetGeom.setMaterial(magnetMat);
        magnetGeom.setUserData("type", "magnet");
        
        //physics
        RigidBodyControl magnetPhysics = new RigidBodyControl();
        magnetGeom.addControl(magnetPhysics);
        magnetPhysics.setMass(0.04f);
        magnetPhysics.setRestitution(0.9f);
        physicsSpace.add(magnetPhysics);
        
        //position
        magnetPhysics.setPhysicsLocation(
                new Vector3f(
                data.getX(),
                data.getY(),
                0));
        
        //magnet control
        magnetGeom.addControl(new MagnetControl(target, data.getSpeed()));
        physicsSpace.addTickListener(magnetGeom.getControl(MagnetControl.class));
        
        rootNode.attachChild(magnetGeom);
    }
    
    public void buildMoon(MoonData data){
        Planet target = getTargetPlanet(data.getPlanetID());
        if (target == null) return;
        //node, geometry, control
        Node moonNode = new Node("moonNode");
        Geometry moonGeom = buildSphereGeom("moon", data.getRadius());
        moonGeom.setMaterial(buildMaterial(new ColorRGBA(
                data.getColorR(), data.getColorG(), data.getColorB(), 1)
                .mult(2), PLANET_SHININESS));
        moonGeom.getMaterial().setTexture("DiffuseMap", assetManager
                .loadTexture("Textures/Moons/" 
                + (new Random().nextInt(3) + 1) + ".jpg"));
        moonNode.attachChild(moonGeom);
        moonGeom.setLocalTranslation(0, data.getDistance() + target.getRadius(), 0);
        moonNode.setLocalTranslation(target.getPhysicsControl().getPhysicsLocation());
        moonNode.addControl(new MoonControl(data.getSpeed()));
        
        //physics
        RigidBodyControl moonPhysics = new RigidBodyControl();
        moonGeom.addControl(moonPhysics);
        physicsSpace.add(moonPhysics);
        moonPhysics.setMass(0);
        moonPhysics.setKinematic(true);
        
        //node
        if (hqGraphics){
            Geometry massIndicator = buildMassIndicator(data.getRadius(), data.getMass());
            moonNode.attachChild(massIndicator);
            massIndicator.setLocalTranslation(0, data.getDistance() + target.getRadius(), 0);
        }
        
        //moon object
        Moon moon = new Moon("moon", moonNode,
                moonPhysics, data.getRadius(), data.getMass());
        
        rootNode.attachChild(moon.getSpatial());
        gSources.add(moon);
    }
    
    public void buildAsteroid(AsteroidData data){
        //geometry
        Spatial asteroidModel = assetManager.loadModel("Models/Asteroid/asteroid.j3o");
        Geometry asteroidGeom = (Geometry)((Node)asteroidModel).getChild("asteroidGeom");
        Material asteroidMat = buildMaterial(ColorRGBA.White, ASTEROID_SHININESS);
        asteroidMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Asteroids/asteroidDiffuse.jpg"));
        asteroidGeom.setMaterial(asteroidMat);

        //node
        if (hqGraphics){
            Geometry massIndicator = buildMassIndicator(data.getRadius()+0.1f, data.getMass());
            massIndicator.addControl(new AsteroidMassIndicatorControl(asteroidGeom));
            rootNode.attachChild(massIndicator);
        }
        
        //physics
        RigidBodyControl asteroidPhysics = new RigidBodyControl(new BoxCollisionShape(new Vector3f(1,1,1)));
        asteroidGeom.addControl(asteroidPhysics);
        physicsSpace.add(asteroidPhysics);
        asteroidPhysics.setRestitution(ASTEROID_BOUNCINESS);
        asteroidPhysics.setFriction(ASTEROID_FRICTION);
        asteroidPhysics.setMass(data.getMass()); //dynamic object
//        //sphere collision shape
//        SphereCollisionShape cShape = new SphereCollisionShape(data.getRadius());
//        asteroidPhysics.setCollisionShape(cShape);
        Vector3f initVel = new Vector3f(data.getInitVelX(), data.getInitVelY(), 0);
        asteroidPhysics.applyImpulse(
                initVel,
                asteroidPhysics.getPhysicsLocation().add(initVel.negate()));
        //apply random torque impulse
        asteroidPhysics.applyTorqueImpulse(new Vector3f(
                FastMath.rand.nextInt(5),
                FastMath.rand.nextInt(5),
                FastMath.rand.nextInt(5)));
        asteroidGeom.addControl(new ForcesControl(gSources));
        physicsSpace.addTickListener(asteroidGeom.getControl(ForcesControl.class));
        
        //asteroid object
        Asteroid asteroid = new Asteroid("asteroid", asteroidGeom, null,
                asteroidPhysics, data.getRadius(), data.getMass());
        
        asteroid.setLocation(data.getX(), data.getY());
        rootNode.attachChild(asteroid.getSpatial());
        gSources.add(asteroid);
    }
    
    public void buildBase(BaseData b){
        //geometry
        Spatial baseModel = assetManager.loadModel("Models/Base/base.j3o");
        Geometry baseGeom = (Geometry)((Node)baseModel).getChild("baseGeom");
        baseGeom.setName(b.getId());
        baseGeom.getMaterial().setBoolean("UseMaterialColors", true);
        baseGeom.getMaterial().setColor("Diffuse", ColorRGBA.White);
        baseGeom.getMaterial().setColor("Ambient", ColorRGBA.Gray);
        //baseGeom.getMaterial().setColor("GlowColor",ColorRGBA.White);
        baseGeom.getMaterial().setFloat("Shininess", 1);
        baseGeom.getMaterial().setTexture("GlowMap", assetManager.loadTexture("Models/Base/baseGlowMap.png"));
//        baseGeom.getMaterial().setTexture("NormalMap", assetManager.loadTexture("Models/Base/baseNormal.png"));
        
        baseGeom.scale(Base.BASE_SIZE);
        
        //physics
        BoxCollisionShape cShape = new BoxCollisionShape(baseGeom.getLocalScale());
        RigidBodyControl basePhysics = new RigidBodyControl(cShape);
        baseGeom.addControl(basePhysics);
        physicsSpace.add(basePhysics);
        basePhysics.setRestitution(0); //bouncyness
        basePhysics.setFriction(10);
        basePhysics.setMass(0); //static object
        
        //base object
        Base base = new Base(b.getId(), baseGeom, basePhysics);
        
        base.setLocation(b.getX(), b.getY());
        rootNode.attachChild(base.getSpatial());
        gSources.add(base);
    }
    
    public void buildOre(OreData data){
        Planet target = getTargetPlanet(data.getPlanetID());
        //geometry stone
        Node oreModel = (Node)assetManager.loadModel("Models/Ore/ore-stone.j3o");
        oreModel.attachChild(((Node)assetManager
                .loadModel("Models/Ore/ore-crystal.j3o")).getChild("oreCrystal"));
        Geometry oreStoneGeom = (Geometry)((Node)oreModel).getChild("oreStone");
        oreStoneGeom.setMaterial(buildMaterial(ColorRGBA.White, 1));
        oreStoneGeom.getMaterial().setBoolean("UseMaterialColors", false);
        oreStoneGeom.getMaterial().setTexture("DiffuseMap",
                assetManager.loadTexture("Textures/Ore/stone.jpg"));
        //geometry crystal
        Geometry oreCrystalGeom = (Geometry)((Node)oreModel).getChild("oreCrystal");
        int rnd = RandomValues.getRndInt(0, 2);
        ColorRGBA color = RandomValues.getRndColor(rnd);
        oreCrystalGeom.setMaterial(buildMaterial(color, 20));
        oreCrystalGeom.getMaterial().setColor("GlowColor", color);
        oreModel.scale(0.8f);
        
        //set position
        if (target != null){
            float dist = target.getRadius() + data.getRadius() + 0.2f;
            float pos = (data.getPosition()*360)*FastMath.DEG_TO_RAD;
            Vector3f angleVec = new Vector3f(FastMath.sin(pos),FastMath.cos(pos),0);
            oreModel.setLocalTranslation(
                    target.getSpatial().getLocalTranslation().add(angleVec.normalize().mult(dist)));
        } else {
            oreModel.setLocalTranslation(data.getX(), data.getY(), 0);
        }
     
        //physics
        RigidBodyControl orePhysics = new RigidBodyControl(new BoxCollisionShape(new Vector3f(0.9f,0.9f,0.9f)));
        oreModel.addControl(orePhysics);
        physicsSpace.add(orePhysics);
        orePhysics.setRestitution(0.5f); //bouncyness
        orePhysics.setFriction(8);
        orePhysics.setMass(data.getMass());
        oreModel.addControl(new ForcesControl(gSources));
        physicsSpace.addTickListener(oreModel.getControl(ForcesControl.class));
        
        //ore object
        Ore ore = new Ore("ore", oreModel, orePhysics, data.getRadius(), data.getMass());
        
        rootNode.attachChild(ore.getSpatial());
        gSources.add(ore);
    }
    
    public void buildCheckpoint(CheckpointData data){
        //geometry
        Geometry checkpointGeom = buildCheckpointGeom(data.getRadius(),
                new ColorRGBA(data.getColorR(), data.getColorG(), data.getColorB(), 1));
        
        //set rotation
        checkpointGeom.rotate(
                new Quaternion().fromAngles(
                FastMath.DEG_TO_RAD*data.getAngle(),
                FastMath.DEG_TO_RAD*90,
                0));
        
        //set position
        checkpointGeom.setLocalTranslation(data.getX(), data.getY(), 0);
        
        rootNode.attachChild(checkpointGeom);
        
        //physics (ghost control)
        GhostControl checkpointPhysics = new GhostControl(
                new CylinderCollisionShape(new Vector3f(data.getRadius()/2.6f, 0.1f, 0)));
        checkpointGeom.addControl(checkpointPhysics);
        physicsSpace.add(checkpointPhysics);
    }
    
    public Geometry buildBackgroundQuad(Camera cam, Texture bgTex){
        float dist = 300;
        float height = cam.getFrustumTop()*dist*2.5f;
        float width = cam.getFrustumRight()*dist*2.5f;
        
        Quad q = new Quad(width, height);
        Geometry background = new Geometry("background", q);
        background.rotate(0, 0, 180*FastMath.DEG_TO_RAD);
        background.setMaterial(buildUnshadedMaterial(ColorRGBA.White));
        background.getMaterial().setTexture("ColorMap", bgTex);
        background.rotate(FastMath.DEG_TO_RAD*180, 0, 0);
        background.move(width/2, -height/2, dist);
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
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", ColorRGBA.White.mult(color));
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", shininess);
        return mat;
    }
    
    public Material buildUnshadedMaterial(ColorRGBA color){
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        return mat;
    }
    
    private Geometry buildGrabberRayGeom(Vector3f from, Vector3f to){
        Line line = new Line(from, to);
        line.setLineWidth(2);
        Geometry lineGeom = new Geometry("grabber", line);
        return lineGeom;
    }
    
    private Geometry buildCheckpointGeom(float radius, ColorRGBA color){
        Torus t = new Torus(32, 4, 0.1f, radius+0.1f);
        Geometry checkPoint = new Geometry("checkPoint", t);
        //material
        checkPoint.setMaterial(buildUnshadedMaterial(color));
        checkPoint.getMaterial().setColor("GlowColor", color);
        return checkPoint;
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
        return massIndicator;
    }
    
    private Planet getTargetPlanet(String planetID){
        Planet target = null;
        for (AbstractGameObject obj : gSources){
            if (obj.getName().equals(planetID)
                    && obj instanceof Planet){
                target = (Planet)obj;
            }
        }
        if (target == null){
            System.out.println("ERROR: PLANET \"" + planetID + "\" NOT FOUND!");
        }
        return target;
    }
    
}
