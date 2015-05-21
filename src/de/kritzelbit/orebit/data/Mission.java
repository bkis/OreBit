package de.kritzelbit.orebit.data;

import java.util.ArrayList;
import java.util.List;


public class Mission {
    
    private String title;
    private String description;
    private String type;
    private int timeLimit;
    private int maxFuel;
    private List<Objective> objectives = new ArrayList<Objective>();
    private List<Planet> planets = new ArrayList<Planet>();
    private List<Asteroid> asteroids = new ArrayList<Asteroid>();
    private List<Satellite> satellites = new ArrayList<Satellite>();
    private List<Ore> ores = new ArrayList<Ore>();
    private List<Base> bases = new ArrayList<Base>();
    
}
