package de.kritzelbit.orebit.data;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class MissionDataObject {
    
    private String title;
    private String description;
    private int timeLimit;
    private int maxFuel;
    private int reward;
    private Set<ObjectiveDataObject> objectives = new HashSet<ObjectiveDataObject>();
    private Set<BaseDataObject> bases = new HashSet<BaseDataObject>();
    private Set<PlanetDataObject> planets = new HashSet<PlanetDataObject>();
    private Set<AsteroidDataObject> asteroids = new HashSet<AsteroidDataObject>();
    private Set<SatelliteDataObject> satellites = new HashSet<SatelliteDataObject>();
    private Set<OreDataObject> ores = new HashSet<OreDataObject>();
    private String backgroundImage;
    private String backgroundMusic;
    private float gameSpeed;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getMaxFuel() {
        return maxFuel;
    }

    public void setMaxFuel(int maxFuel) {
        this.maxFuel = maxFuel;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
    
    @XmlElement(name="objective")
    public Set<ObjectiveDataObject> getObjectives() {
        return objectives;
    }

    public void setObjectives(Set<ObjectiveDataObject> objectives) {
        this.objectives = objectives;
    }

    @XmlElement(name="base")
    public Set<BaseDataObject> getBases() {
        return bases;
    }

    public void setBases(Set<BaseDataObject> bases) {
        this.bases = bases;
    }

    @XmlElement(name="planet")
    public Set<PlanetDataObject> getPlanets() {
        return planets;
    }

    public void setPlanets(Set<PlanetDataObject> planets) {
        this.planets = planets;
    }

    @XmlElement(name="asteroid")
    public Set<AsteroidDataObject> getAsteroids() {
        return asteroids;
    }

    public void setAsteroids(Set<AsteroidDataObject> asteroids) {
        this.asteroids = asteroids;
    }

    @XmlElement(name="satellite")
    public Set<SatelliteDataObject> getSatellites() {
        return satellites;
    }

    public void setSatellites(Set<SatelliteDataObject> satellites) {
        this.satellites = satellites;
    }

    @XmlElement(name="ore")
    public Set<OreDataObject> getOres() {
        return ores;
    }

    public void setOres(Set<OreDataObject> ores) {
        this.ores = ores;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBackgroundMusic() {
        return backgroundMusic;
    }

    public void setBackgroundMusic(String backgroundMusic) {
        this.backgroundMusic = backgroundMusic;
    }

    public float getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(float gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

}
