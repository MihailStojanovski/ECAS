package worlds;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

import MDP.Location;
import MDP.Road;
import MDP.State;

public class SelfDrivingCarWorld {
    
    private List<Location> locations = new ArrayList<>();
    private List<Road> roads = new ArrayList<>();
    private String startLocation;
    private String goalLocation;
    

    public SelfDrivingCarWorld(List<Location> locations, List<Road> roads, String startLocation, String goalLocation){
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

    public String getStartLocation() {
        return startLocation;
    }

    public String getGoalLocation() {
        return goalLocation;
    }
}
