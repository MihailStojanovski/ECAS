package worlds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import states.Location;
import states.Road;
import states.State;

import static java.util.Map.entry;

public class SelfDrivingCarWorld {
    
    private List<Location> locations = new ArrayList<>();
    private List<Road> roads = new ArrayList<>();
    private State startLocation;
    private State goalLocation;
    

    public SelfDrivingCarWorld(List<Location> locations, List<Road> roads, State startLocation, State goalLocation){
        this.locations = locations;
        this.roads = roads;
        this.startLocation = startLocation;
        this.goalLocation = goalLocation;
    }


    public List<Location> getLocations() {
        return locations;
    }

    public List<Road> getRoads() {
        return roads;
    }

    public State getStartLocation() {
        return startLocation;
    }

    public State getGoalLocation() {
        return goalLocation;
    }
}
