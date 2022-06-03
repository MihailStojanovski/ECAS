package agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import rewards.EthicalReward;
import rewards.EthicalRewardQuad;
import rewards.Reward;
import states.Location;
import states.Road;
import states.State;
import states.StateRegistry;

import java.util.ArrayList;

import worlds.SelfDrivingCarWorld;

import static java.util.Map.entry;

/**
 * SelfDrivingCarAgent
 */
public class SelfDrivingCarAgent {


    private final Double stayingTime = 120.;
    private final Double turningTime = 5.;
    private final Double accelerationRate = 2.;
    private final Double driverErrorPenalty = 3600.;

    private final Map<String,String> accelerateActions = Map.ofEntries(
      entry("TO_LOW","LOW"),
      entry("TO_NORMAL","NORMAL"),
      entry("TO_HIGH","HIGH")  
    );

    private final Map<String,Integer> speedAdjustments = Map.ofEntries(
      entry("NONE",-100),
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
    private List<String> allStateKeys = new ArrayList<>();
    private Map<String, StateRegistry> stateRegistryMap = new HashMap<String,StateRegistry>();
    private List<String> locationStrings = new ArrayList<>();
    private List<String> roadStrings = new ArrayList<>();
    private List<String> locationActions = new ArrayList<>();
    private List<String> roadActions = new ArrayList<>();
    private List<String> allActions;
    private Double epsilon = 0.5;
    private Double epsilonPrime = 0.5;

    public SelfDrivingCarAgent(SelfDrivingCarWorld world){
        this.world = world;
        setUpVariables();
    }

    public SelfDrivingCarAgent(SelfDrivingCarWorld world, Double epsilon, Double epsilonPrime){
        this.world = world;
        this.epsilon = epsilon;
        this.epsilonPrime = epsilonPrime;
        setUpVariables();
    }


    private void setUpVariables(){
        StringBuilder builder = new StringBuilder();

        for (State state : world.getLocations()) {
            StateRegistry tempSR = new StateRegistry(state);
            stateRegistryMap.put(tempSR.toString(),tempSR);
            locationStrings.add(tempSR.toString());
            allStateKeys.add(tempSR.toString());
        }

        // Adding cartesian product of road states, speedAdjustments, pedestrianTraffic to a unique string for each
        for (State state : world.getRoads()) {
            for(Map.Entry<String,Integer> speedEntry : speedAdjustments.entrySet()){
                for(Map.Entry<String,Double> pTrafficEntry : pedestrianTraffic.entrySet()){
                    StateRegistry tempSR = new StateRegistry(state, speedEntry.getKey(), pTrafficEntry.getKey());
                    stateRegistryMap.put(tempSR.toString(),tempSR);
                    roadStrings.add(tempSR.toString());
                    allStateKeys.add(tempSR.toString());
                }
            }
        }

        // Making location actions
        locationActions.add("STAY");
        for(State state : world.getRoads()){
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

        allActions = new ArrayList<>(locationActions);
        allActions.addAll(roadActions);

    }// End Set up variables

    public List<String> getAllStateKeys(){
        return allStateKeys;
    }

    public List<String> getAllActions() {
        return allActions;
    }


/**
 * The getPossibleActionsForState function returns a set of possible actions for the given state.
 *
 * 
 * @param state Used to Determine the possible actions for a given state.
 * @return A set of possible actions for a given state.
 * 
 */
    public Set<String> getPossibleActionsForState(String state){
        Set<String> possibleActions = new HashSet<>();
        if(locationStrings.contains(state)){
            StringBuilder builder = new StringBuilder();
            for(String rS : roadStrings){
                StateRegistry roadStateReg = stateRegistryMap.get(rS);
                if(((Road)roadStateReg.getState()).getFromLocation().equals(state)){
                    builder.append("TURN_ONTO_");
                    builder.append(roadStateReg.getState().getName());
                    possibleActions.add(builder.toString());
                    builder.setLength(0);
                }
            }
            possibleActions.add("STAY");
        }
        if(roadStrings.contains(state)){
            if(stateRegistryMap.get(state).getSpeedAdjustment().equals("NONE")){
                for(Map.Entry<String, String> a : accelerateActions.entrySet())
                possibleActions.add(a.getKey()); 
            }
            else{
                possibleActions.add("CRUISE");
            }
        }
        return possibleActions;
    }

/**
 * The getPossibleResultingStates function returns a set of possible resulting states from the given state and action.
 *
 * 
 * @param state The state in which the action is chosen.
 * @param action The action.
 * @return A set of possible resulting states given a state and an action.
 * 
 */
    public Set<String> getPossibleResultingStates(String state, String action){
        Set<String> possibleResultStates = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        // If the state is a location state check if the action is a turn onto action
        if(locationStrings.contains(state)){
            if(action.equals("STAY")){
                possibleResultStates.add(state);
            }else{
                for(String roadKey : roadStrings){
                    StateRegistry roadReg = stateRegistryMap.get(roadKey);
                    if(((Road)roadReg.getState()).getFromLocation().equals(state)){
                        if(roadReg.getSpeedAdjustment().equals("NONE")){
                            builder.append("TURN_ONTO_");
                            builder.append(roadReg.getState().getName());
                            if(action.equals(builder.toString())){
                                possibleResultStates.add(roadKey);
                            }
                            builder.setLength(0);
                        }
                    }  
                }
            }
            
        }else if(roadStrings.contains(state)){
            StateRegistry roadReg = stateRegistryMap.get(state);
            if(roadReg.getSpeedAdjustment().equals("NONE")){
                if(accelerateActions.keySet().contains(action)){
                    StateRegistry tempReg = new StateRegistry(roadReg.getState(),accelerateActions.get(action),roadReg.getPedestrianTraffic());
                    possibleResultStates.add(tempReg.toString());
                }
            }else if(roadReg.getSpeedAdjustment().equals("LOW") || roadReg.getSpeedAdjustment().equals("NORMAL") || roadReg.getSpeedAdjustment().equals("HIGH")){
                if(action.equals("CRUISE")){
                    possibleResultStates.add(((Road)roadReg.getState()).getToLocation());
                }
            }
        }
        return possibleResultStates;
    }


    public Double transitionFunction(String currentState, String action, String successorState){

        /*

        4 types of transitions:

        ++++    t(loc,stay,loc) = 1.0   
        ++++    t(loc,turn_onto,road_NONE) = 0.8(Light) || 0.2(Heavy)

        ++++    t(road_NONE,to_speed,road_Speed) = 1.0
        ++++    t(road_speed,cruise,loc) = 1.0

        */
        StateRegistry currentStateRegistry = stateRegistryMap.get(currentState);
        StateRegistry successorStateRegistry = stateRegistryMap.get(successorState);

        if(locationStrings.contains(currentState)){
            if(action.equals("STAY")){
                if(successorState.equals(currentState)){
                    return 1.0;
                }
            }
            else if(locationActions.contains(action) && !action.equals("STAY") ){
                if(roadStrings.contains(successorState)){
                    if(successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                        return pedestrianTraffic.get(successorStateRegistry.getPedestrianTraffic());
                    }
                }
            }
        }

        if(roadStrings.contains(currentState)){
            if(roadStrings.contains(successorState) && accelerateActions.containsKey(action)){
                if(currentStateRegistry.getSpeedAdjustment().equals("NONE") && !successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                    return 1.0;
                }
            }
            if(locationStrings.contains(successorState) && action.equals("CRUISE")){
                if(!currentStateRegistry.getSpeedAdjustment().equals("NONE")){
                    return 1.0;
                }
            }
        }
        return 0.0;
    }


    public Double rewardFunction(String state, String action, String successorState, Reward r){
        
        return 0.0;
    }

    public Double rewardFunction(String state, String action, String successorState, Reward r, boolean isGood){
            EthicalRewardQuad quad = r.getEthicalReward(state, action, successorState);
            if(isGood){
                return quad.getTriangle() - quad.getBarredTriangle() - epsilonPrime * quad.getBarredTriangle();
            } 
            else{
                return quad.getNabla() - quad.getBarredNabla() + epsilon * quad.getBarredNabla();
            }
    }

    public String getStartState(){
        return stateRegistryMap.get(world.getStartLocation().getName()).toString();
    }

    public String getGoalState(){
        return stateRegistryMap.get(world.getGoalLocation().getName()).toString();
    }

    public StateRegistry getStateRegistry(String s){
        return stateRegistryMap.get(s);
    }

}