package de.kritzelbit.orebit.data;

import de.kritzelbit.orebit.util.RandomName;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="mission")
public class Mission {
    
    //default values
    private String title = "Codename '" + RandomName.getRndName() + " " + RandomName.getRndCodeName() + "'";
    private String description = "Just do it.";
    private int timeLimit = 60;
    private int maxFuel = 2000;
    private int reward = 1000;
    private float gameSpeed = 0.5f;
    private String backgroundImage = "random";
    private String backgroundMusic = "random";
    private Set<ObjectiveData> objectives = new HashSet<ObjectiveData>();
    private Set<BaseData> bases = new HashSet<BaseData>();
    private Set<PlanetData> planets = new HashSet<PlanetData>();
    private Set<AsteroidData> asteroids = new HashSet<AsteroidData>();
    private Set<SatelliteData> satellites = new HashSet<SatelliteData>();
    private Set<OreData> ores = new HashSet<OreData>();
    
    
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

    public float getGameSpeed() {
        return gameSpeed;
    }

    public void setGameSpeed(float gameSpeed) {
        this.gameSpeed = gameSpeed;
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

    @XmlElement(name="objective")
    public Set<ObjectiveData> getObjectives() {
        return objectives;
    }

    public void setObjectives(Set<ObjectiveData> objectives) {
        this.objectives = objectives;
    }

    @XmlElement(name="base")
    public Set<BaseData> getBases() {
        return bases;
    }

    public void setBases(Set<BaseData> bases) {
        this.bases = bases;
    }

    @XmlElement(name="planet")
    public Set<PlanetData> getPlanets() {
        return planets;
    }

    public void setPlanets(Set<PlanetData> planets) {
        this.planets = planets;
    }

    @XmlElement(name="asteroid")
    public Set<AsteroidData> getAsteroids() {
        return asteroids;
    }

    public void setAsteroids(Set<AsteroidData> asteroids) {
        this.asteroids = asteroids;
    }

    @XmlElement(name="satellite")
    public Set<SatelliteData> getSatellites() {
        return satellites;
    }

    public void setSatellites(Set<SatelliteData> satellites) {
        this.satellites = satellites;
    }

    @XmlElement(name="ore")
    public Set<OreData> getOres() {
        return ores;
    }

    public void setOres(Set<OreData> ores) {
        this.ores = ores;
    }
    
}
