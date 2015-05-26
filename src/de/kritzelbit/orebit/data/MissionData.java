package de.kritzelbit.orebit.data;

import de.kritzelbit.orebit.util.RandomName;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="mission")
@XmlType(propOrder={"title", "description", "timeLimit",
                    "maxFuel", "reward", "gameSpeed",
                    "backgroundImage", "backgroundMusic",
                    "objectives", "bases", "ores",
                    "planets", "asteroids", "satellites"})
public class MissionData {
    
    //default values
    private String title = "Codename '" + RandomName.getRndName() + " " + RandomName.getRndCodeName() + "'";
    private String description = "Just do it.";
    private int timeLimit = 0;
    private int maxFuel = 0;
    private int reward = 1000;
    private float gameSpeed = 0.5f;
    private String backgroundImage = "random";
    private String backgroundMusic = "random";
    private Set<ObjectiveData> objectives = new HashSet<ObjectiveData>();
    private Set<BaseData> bases = new HashSet<BaseData>();
    private Set<OreData> ores = new HashSet<OreData>();
    private Set<PlanetData> planets = new HashSet<PlanetData>();
    private Set<AsteroidData> asteroids = new HashSet<AsteroidData>();
    private Set<SatelliteData> satellites = new HashSet<SatelliteData>();
    
    
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

    @XmlElementWrapper(name="objectives")
    @XmlElement(name="objective")
    public Set<ObjectiveData> getObjectives() {
        return objectives;
    }

    public void setObjectives(Set<ObjectiveData> objectives) {
        this.objectives = objectives;
    }

    @XmlElementWrapper(name="bases")
    @XmlElement(name="base")
    public Set<BaseData> getBases() {
        return bases;
    }

    public void setBases(Set<BaseData> bases) {
        this.bases = bases;
    }

    @XmlElementWrapper(name="planets")
    @XmlElement(name="planet")
    public Set<PlanetData> getPlanets() {
        return planets;
    }

    public void setPlanets(Set<PlanetData> planets) {
        this.planets = planets;
    }

    @XmlElementWrapper(name="asteroids")
    @XmlElement(name="asteroid")
    public Set<AsteroidData> getAsteroids() {
        return asteroids;
    }

    public void setAsteroids(Set<AsteroidData> asteroids) {
        this.asteroids = asteroids;
    }

    @XmlElementWrapper(name="satellites")
    @XmlElement(name="satellite")
    public Set<SatelliteData> getSatellites() {
        return satellites;
    }

    public void setSatellites(Set<SatelliteData> satellites) {
        this.satellites = satellites;
    }

    @XmlElementWrapper(name="ores")
    @XmlElement(name="ore")
    public Set<OreData> getOres() {
        return ores;
    }

    public void setOres(Set<OreData> ores) {
        this.ores = ores;
    }
    
}
