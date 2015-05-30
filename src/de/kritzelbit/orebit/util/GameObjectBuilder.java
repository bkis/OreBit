package de.kritzelbit.orebit.util;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
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
import de.kritzelbit.orebit.controls.AsteroidMassIndicatorControl;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.ForcesControl;
import de.kritzelbit.orebit.controls.MoonControl;
import de.kritzelbit.orebit.controls.ShipGravityIndicatorControl;
import de.kritzelbit.orebit.controls.ThrusterVisualsControl;
import de.kritzelbit.orebit.data.AsteroidData;
import de.kritzelbit.orebit.data.BaseData;
import de.kritzelbit.orebit.data.CheckpointData;
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
    
    public GameObjectBuilder(SimpleApplication app,
            PhysicsSpace physicsSpace,
            Node rootNode,
            Set<AbstractGameObject> gSources){
        this.assetManager = app.getAssetManager();
        this.physicsSpace = physicsSpace;
        this.rootNode = rootNode;
        this.gSources = gSources;
    }
    
    
    public void buildPlanet(PlanetData data){
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
        
        planet.setLocation(data.getX(), data.getY());
        rootNode.attachChild(planet.getSpatial());
        gSources.add(planet);
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
        shipPhysics.setFriction(50);
        shipPhysics.setMass(1);
        
        //controls
        shipGeom.addControl(new ForcesControl(gSources));
        shipGeom.addControl(new FlightControl(shipGeom, thrust, spin));
        physicsSpace.addTickListener(shipGeom.getControl(FlightControl.class));
        
        //grabber
        Geometry grabber = buildLineGeom(Vector3f.ZERO, Vector3f.ZERO);
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
    
    public void buildMoons(MoonData data){
        Planet target = getTargetPlanet(data.getPlanetID());
        if (target == null) return;
        //node, geometry, control
        Node moonNode = new Node();
        Geometry moonGeom = buildSphereGeom("moon", data.getRadius());
        moonGeom.setMaterial(buildMaterial(new ColorRGBA(
                data.getColorR(), data.getColorG(), data.getColorB(), 1)
                .mult(2), PLANET_SHININESS));
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
        Geometry massIndicator = buildMassIndicator(data.getRadius(), data.getMass());
        moonNode.attachChild(massIndicator);
        massIndicator.setLocalTranslation(0, data.getDistance() + target.getRadius(), 0);
        
        //moon object
        Moon moon = new Moon("moon", moonNode,
                moonPhysics, data.getRadius(), data.getMass());
        
        rootNode.attachChild(moon.getSpatial());
        gSources.add(moon);
    }
    
    public void buildAsteroid(AsteroidData data){
        //geometry
        //Geometry asteroidGeom = buildSphereGeom("asteroid", data.getRadius());
        Spatial asteroidModel = assetManager.loadModel("Models/Asteroid/asteroid.j3o");
        Geometry asteroidGeom = (Geometry)((Node)asteroidModel).getChild("asteroidGeom");
        Material asteroidMat = buildMaterial(ColorRGBA.White, ASTEROID_SHININESS);
        asteroidMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Asteroids/asteroid.jpg"));
        asteroidGeom.setMaterial(asteroidMat);
        //((Geometry)asteroidGeom).getMaterial().setColor("Diffuse", ColorRGBA.Blue);
        //TODO: Model, Texture
        //node
        Geometry massIndicator = buildMassIndicator(data.getRadius()+0.1f, data.getMass());
        massIndicator.addControl(new AsteroidMassIndicatorControl(asteroidGeom));
        rootNode.attachChild(massIndicator);
        
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
        
        //asteroid object
        Asteroid asteroid = new Asteroid("asteroid", asteroidGeom, massIndicator,
                asteroidPhysics, data.getRadius(), data.getMass());
        
        asteroid.setLocation(data.getX(), data.getY());
        rootNode.attachChild(asteroid.getSpatial());
        gSources.add(asteroid);
    }
    
    public void buildBase(BaseData b){
        //geometry
        Spatial baseModel = assetManager.loadModel("Models/Base/base.j3o");
        Geometry baseGeom = (Geometry)((Node)baseModel).getChild("baseGeom");
        baseGeom.getMaterial().setBoolean("UseMaterialColors", true);
        baseGeom.getMaterial().setColor("Diffuse", ColorRGBA.White);
        baseGeom.getMaterial().setColor("Ambient", ColorRGBA.LightGray);
        baseGeom.getMaterial().setColor("GlowColor",ColorRGBA.White);
        baseGeom.getMaterial().setTexture("GlowMap", assetManager.loadTexture("Models/Base/BaseGlow.png"));
//        baseGeom.setMaterial(buildMaterial(ColorRGBA.LightGray, 4));
//        baseGeom.getMaterial().setColor("GlowColor", new ColorRGBA(0.7f, 0.7f, 0.7f, 0.4f));
//        baseGeom.getMaterial().setTexture("DiffuseMap", assetManager
//                .loadTexture("Textures/Base/base.jpg"));
        
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
        oreStoneGeom.getMaterial().setTexture("DiffuseMap", assetManager.loadTexture("Textures/Ore/stone.jpg"));
        oreStoneGeom.move(0, 0, 0);
        Geometry oreCrystalGeom = (Geometry)((Node)oreModel).getChild("oreCrystal");
        oreCrystalGeom.setMaterial(buildMaterial(ColorRGBA.White, 20));
        oreCrystalGeom.getMaterial().setColor("GlowColor", new ColorRGBA(
                FastMath.rand.nextFloat(),
                FastMath.rand.nextFloat(),
                FastMath.rand.nextFloat(),
                1f).mult(3));
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
