package agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import worlds.Road;
import worlds.SelfDrivingCarWorld;

import static java.util.Map.entry;

import java.util.ArrayList;

/*
enum AccelerateActions {
    ACCELERATE_TO_LOW_SPEED("LOW"),
    ACCELERATE_TO_NORMAL_SPEED("NORMAL"),
    ACCELERATE_TO_HIGH_SPEED("HIGH");

    private final String speed;
    
    AccelerateActions(String speed){
        this.speed = speed;
    }
}

enum SpeedAdjustments {
    NONE(null),
    LOW(-10),
    NORMAL(0),
    HIGH(10);

    private final Integer adjustment;
    
    SpeedAdjustments(Integer adjustment){
        this.adjustment = adjustment;
    }
}

enum SpeedLimits {
    CITY(25),
    COUNTY(45),
    HIGHWAY(75);

    private final Integer limit;

    SpeedLimits(Integer limit){
        this.limit = limit;
    }
}

enum PedestrianTraffic {
    LIGHT(0.8),
    HEAVY(0.2);

    private final Double probability;

    PedestrianTraffic(Double probability){
        this.probability = probability;
    }
}

*/
/**
 * SelfDrivingCarAgent
 */
public class SelfDrivingCarAgent {


    private final Integer stayingTime = 120;
    private final Integer turningTime = 5;
    private final Integer accelerationRate = 2;
    private final Integer driverErrorPenalty = 3600;

    private final Map<String,String> accelerateActions = Map.ofEntries(
      entry("TO_LOW","LOW"),
      entry("TO_NORMAL","NORMAL"),
      entry("TO_HIGH","HIGH")  
    );

    private final Map<String,Integer> speedAdjustments = Map.ofEntries(
      entry("NONE",null),
      entry("LOW",-10),
      entry("NORMAL",0),
      entry("HIGH",10)  
    );

    private final Map<String,Integer> speedLimits = Map.ofEntries(
      entry("CITY",25),
      entry("COUNTY",45),
      entry("HIGHWAY",75) 
    );

    private final Map<String,Double> pedestrianTraffic = Map.ofEntries(
      entry("LIGHT",0.8),
      entry("HEAVY",0.2)  
    );
    
    private SelfDrivingCarWorld world;
    private List<String> locationStates = new ArrayList<>();
    private List<Road> roadStates = new ArrayList<>();

    private void setUpVariables(SelfDrivingCarWorld world){
        this.locationStates = world.getLocations();
        this.roadStates = world.getRoads();
    }

    public SelfDrivingCarAgent(SelfDrivingCarWorld world){
        this.world = world;
        setUpVariables(world);
    }

    
    
}