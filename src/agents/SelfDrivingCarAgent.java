package agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.RowSetMetaData;

import MDP.State;
import MDP.StateRegistry;
import worlds.Location;
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
    private List<Location> locationStates = new ArrayList<>();
    private List<Road> roadStates = new ArrayList<>();
    private Map<State, StateRegistry> stateRegistry = new HashMap<State,StateRegistry>();
    private List<String> locationActions = new ArrayList<>();
    private List<String> roadActions = new ArrayList<>();

    public SelfDrivingCarAgent(SelfDrivingCarWorld world){
        this.world = world;
        setUpVariables();
    }

    private void setUpVariables(){
        StringBuilder builder = new StringBuilder();
        for (State state : locationStates) {
            stateRegistry.put(state,null);
        }

        // Adding cartesian product of speedAdjustments, pedestrianTraffic and road states
        for (State state : roadStates) {
            for(Map.Entry<String,Integer> speedEntry : speedAdjustments.entrySet()){
                for(Map.Entry<String,Double> pTrafficEntry : pedestrianTraffic.entrySet()){
                    StateRegistry tempSR = new StateRegistry(speedEntry, pTrafficEntry);    
                    stateRegistry.put(state,tempSR);
                }
            }
        }

        // Making location actions
        locationActions.add("STAY");
        for(State state : roadStates){
            builder.append("TURN_ONTO_");
            builder.append(state.getName());
            locationActions.add(builder.toString());
            builder.setLength(0);
        }

        // Making road actions
        roadActions.add("CRUISE");
        for(Map.Entry<String,String> a : accelerateActions.entrySet()){
            roadActions.add(a.getKey());
        }

    }

    public Double transitionFunction(State state, String action, State successorState){
        final StateRegistry currentStateRegistry = stateRegistry.get(state);
        final StateRegistry successorStateRegistry = stateRegistry.get(successorState);


        if(locationStates.contains(state)){
            if(roadActions.contains(action) || action.equals("STAY")){
                if(state.equals(successorState)){
                    return 1.0;
                }
                return 0.0;
            }

            if(state instanceof Location && successorState instanceof Road){
                StringBuilder builder = new StringBuilder("TURN_ONTO_");
                builder.append(successorState.getName());
                if(action.equals(builder.toString())){
                    if(state.getName().equals(((Road)successorState).getFromLocation()) && successorStateRegistry.getSpeedAdjustment().getKey().equals("NONE")){
                        return successorStateRegistry.getPedestrianTraffic().getValue();
                    }
                }

            }

        }
        


        return 0.0;
    }

    
    
}