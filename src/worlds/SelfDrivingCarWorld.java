package worlds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelfDrivingCarWorld {
    
    private List<String> locations = new ArrayList<>();
    private List<Road> roads = new ArrayList<>();
    private String startLocation;
    private String goalLocation;
    
    //  An idea for later
    // private HashMap<String,Double> startLocationsWithProbabilities = new HashMap<>();
    

    public SelfDrivingCarWorld(List<String> locations, List<Road> roads, String startLocation, String goalLocation){
        this.locations = locations;
        this.roads = roads;
        this.startLocation = startLocation;
        this.goalLocation = goalLocation;
    }

    public List<String> getLocations() {
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
