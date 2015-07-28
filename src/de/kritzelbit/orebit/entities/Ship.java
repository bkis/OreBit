package de.kritzelbit.orebit.entities;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import de.kritzelbit.orebit.controls.FlightControl;
import de.kritzelbit.orebit.controls.GrabberControl;
import de.kritzelbit.orebit.util.RandomValues;
import de.kritzelbit.orebit.util.SoundPlayer;


public class Ship extends AbstractGameObject {
    
    private int thrust;
    private int spin;
    private int grabberLength;
    private Geometry grabber;
    private Node shipVisualsNode;
    private PhysicsSpace physicsSpace;
    private boolean grabbing;
    private ParticleEmitter thrusterVisuals;
    private ParticleEmitter explosionVisuals;
    private PhysicsRigidBody grabbedObject;

    public Ship(String name,
            Spatial spatial,
            Geometry grabber,
            Geometry gravityIndicator,
            ParticleEmitter thrusterVisuals,
            ParticleEmitter explosionVisuals,
            int thrust,
            int spin,
            int grabberLength) {
        
        super(name, spatial, spatial.getControl(RigidBodyControl.class), 0.8f, 5);
        this.thrust = thrust;
        this.spin = spin;
        this.grabber = grabber;
        this.grabberLength = grabberLength;
        this.thrusterVisuals = thrusterVisuals;
        setThrusterVisuals(false);
        this.explosionVisuals = explosionVisuals;
        this.explosionVisuals.setEnabled(false);
        this.physicsSpace = physics.getPhysicsSpace();
        this.shipVisualsNode = new Node();
        this.shipVisualsNode.attachChild(spatial);
        this.shipVisualsNode.attachChild(gravityIndicator);
        this.shipVisualsNode.attachChild(thrusterVisuals);
        this.shipVisualsNode.attachChild(explosionVisuals);
        //setup grabber
        this.grabber.addControl(new GrabberControl((Line)grabber.getMesh()));
        this.grabber.getControl(GrabberControl.class).setEnabled(false);
        this.grabber.setCullHint(Spatial.CullHint.Never);
        
        spatial.setUserData("type", "ship");
    }
    
    public Node getNode(){
        return shipVisualsNode;
    }
    
    public void toggleGrabber(boolean enabled){
        if (!grabbing && enabled
                && grabber.getControl(GrabberControl.class) != null){
            //set up dummy rigid body
            PhysicsRigidBody objPhys = null;
            
            //detect closest physics object
            for (PhysicsRigidBody body : physicsSpace.getRigidBodyList()){
                if (((Spatial)body.getUserObject()).getUserData("grabbable") == null) continue;
                if (objPhys == null
                        || body.getPhysicsLocation().distance(physics.getPhysicsLocation())
                        < objPhys.getPhysicsLocation().distance(physics.getPhysicsLocation())){
                    objPhys = body;
                }
            }
            
            //exit if no grabbable object was found or distance too long
            if (objPhys == null
                    || objPhys.getPhysicsLocation().distance(
                    physics.getPhysicsLocation()) > grabberLength) return;
            
            grabbedObject = objPhys;
            
            //set up visual joint representation
            this.grabber.getControl(GrabberControl.class)
                    .setTargets(spatial, (Spatial)objPhys.getUserObject());
            
            //show grabber ray
            shipVisualsNode.attachChild(grabber);
            grabbing = true;
            SoundPlayer.getInstance().play("beamInit");
            SoundPlayer.getInstance().play("beamLoop");
        } else if (grabbing && !enabled) {
            //hide grabber ray
            shipVisualsNode.detachChild(grabber);
            grabbing = false;
            SoundPlayer.getInstance().stop("beamLoop");
            grabbedObject.applyTorqueImpulse(
                    new Vector3f(0, 0, RandomValues.getRndFloat(-10, 10)));
        }
        
        // enable/disable grabber control
        if (grabber.getControl(GrabberControl.class) != null)
            this.grabber.getControl(GrabberControl.class).setEnabled(grabbing);
    }

    public float getFuel(){
        return spatial.getControl(FlightControl.class).getFuel();
    }

    public int getThrust() {
        return thrust;
    }

    public int getSpin() {
        return spin;
    }
    
    public void setBoost(float booster){
        thrusterVisuals.setStartSize(0.8f*booster);
        thrusterVisuals.setEndSize(0.1f*booster);
    }
    
    public void destroy(Vector3f direction){
        deactivateControls();

        //remove spatial
        shipVisualsNode.detachChild(spatial);
        if (shipVisualsNode.getChild("gravityIndicator") != null)
            shipVisualsNode.getChild("gravityIndicator").removeFromParent();
        if (shipVisualsNode.getChild("grabber") != null)
            shipVisualsNode.getChild("grabber").removeFromParent();
        grabber.removeControl(GrabberControl.class);
        
        //remove physics
        physics.setEnabled(false);
        
        //explosion
        thrusterVisuals.setParticlesPerSec(50);
        thrusterVisuals.getParticleInfluencer().setInitialVelocity(direction.mult(20));
        thrusterVisuals.getParticleInfluencer().setVelocityVariation(0.45f);
        thrusterVisuals.setEnabled(true);
        thrusterVisuals.emitAllParticles();
        thrusterVisuals.setParticlesPerSec(0);
        
        //debris
        explosionVisuals.getParticleInfluencer().setInitialVelocity(direction.mult(40));
        explosionVisuals.setLocalTranslation(spatial.getWorldTranslation());
        explosionVisuals.setEnabled(true);
        explosionVisuals.emitAllParticles();
        explosionVisuals.setParticlesPerSec(0);
    }
    
    public final void setThrusterVisuals(boolean enabled){
        thrusterVisuals.setParticlesPerSec(enabled ? 30 : 0);
    }
    
    @Override
    public float getRadius(){
        return physics.getCollisionShape().getScale().y;
    }
    
    private void deactivateControls(){
        while (spatial.getNumControls() > 0){
            spatial.removeControl(spatial.getControl(0));
        }
    }
    
}
