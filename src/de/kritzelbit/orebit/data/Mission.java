package de.kritzelbit.orebit.data;

import java.util.HashSet;
import java.util.Set;


public class Mission {
    
    private String title;
    private String description;
    private int timeLimit;
    private int maxFuel;
    private Set<Objective> objectives = new HashSet<Objective>();
    private Set<Base> bases = new HashSet<Base>();
    private Set<Planet> planets = new HashSet<Planet>();
    private Set<Asteroid> asteroids = new HashSet<Asteroid>();
    private Set<Satellite> satellites = new HashSet<Satellite>();
    private Set<Ore> ores = new HashSet<Ore>();

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

    public Set<Objective> getObjectives() {
        return objectives;
    }

    public void setObjectives(Set<Objective> objectives) {
        this.objectives = objectives;
    }

    public Set<Base> getBases() {
        return bases;
    }

    public void setBases(Set<Base> bases) {
        this.bases = bases;
    }

    public Set<Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(Set<Planet> planets) {
        this.planets = planets;
    }

    public Set<Asteroid> getAsteroids() {
        return asteroids;
    }

    public void setAsteroids(Set<Asteroid> asteroids) {
        this.asteroids = asteroids;
    }

    public Set<Satellite> getSatellites() {
        return satellites;
    }

    public void setSatellites(Set<Satellite> satellites) {
        this.satellites = satellites;
    }

    public Set<Ore> getOres() {
        return ores;
    }

    public void setOres(Set<Ore> ores) {
        this.ores = ores;
    }
    
}
