package agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import States.Location;
import States.Road;
import States.State;
import States.StateRegistry;

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

    public SelfDrivingCarAgent(SelfDrivingCarWorld world){
        this.world = world;
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
                    StateRegistry tempSR = new StateRegistry(state, speedEntry, pTrafficEntry);
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

    }

    public List<String> getAllStateKeys(){
        return allStateKeys;
    }

    public List<String> getAllActions() {
        return allActions;
    }

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
        }
        if(roadStrings.contains(state)){
            possibleActions.addAll(roadActions); 
        }
        return possibleActions;
    }

    

    public Double transitionFunction(String state, String action, String successorState){
        StateRegistry currentStateRegistry = stateRegistryMap.get(state);
        StateRegistry successorStateRegistry = stateRegistryMap.get(successorState);


        // If the state is a location state
        if(locationStrings.contains(state)){

            // If the action is one of the road actions (changing speed) or action is to stay
            if(roadActions.contains(action) || action.equals("STAY")){

                // If the next state is the same as the current one, meaning the agent will stay in place
                if(state.equals(successorState)){
                    return 1.0;
                }
                return 0.0;
            }

            // Creating an action from the name of the successor road state
            StringBuilder builder = new StringBuilder("TURN_ONTO_");
            builder.append(successorStateRegistry.getState().getName());
            // If the action is turning onto the successor road state
            if(action.equals(builder.toString())){
                // If the current state is the road's starting location, and the speed adjustment is none, return the pedestrian traffic probability of the successor state
                if(currentStateRegistry.getState().getName().equals(((Road)successorStateRegistry.getState()).getFromLocation()) && successorStateRegistry.getSpeedAdjustment().getKey().equals("NONE")){
                    //System.out.println(state + " , " + " , " + action + " , " + successorState + " pTraffic : " + successorStateRegistry.getPedestrianTraffic().getValue());
                    return successorStateRegistry.getPedestrianTraffic().getValue();
                }
            }



            for(int i = 0; i < world.getRoads().size(); i++){
                String name = world.getRoads().get(i).getName();

                builder.setLength(0);
                builder.append("TURN_ONTO_");
                builder.append(name);
                if(action.equals(builder.toString()) && !name.equals(successorStateRegistry.getState().getName())){
                    if(state.equals(successorState) && currentStateRegistry.getState().getName().equals(world.getRoads().get(i).getFromLocation())){
                        return 1.0;
                    }
                }
            }
        }// End of location states
        
        // If the state is a road state
        if(roadStrings.contains(state)){
            if(locationActions.contains(action)){
                if(state.equals(successorState)){
                    return 1.0;
                }
                return 0.0;
            }

            if(accelerateActions.containsKey(action)){
                if( currentStateRegistry.getSpeedAdjustment().getKey().equals("NONE") && 
                    currentStateRegistry.getState().getName().equals(successorStateRegistry.getState().getName()) && 
                    currentStateRegistry.getPedestrianTraffic().getKey().equals(successorStateRegistry.getPedestrianTraffic().getKey()) &&
                    successorStateRegistry.getSpeedAdjustment().getKey().equals(accelerateActions.get(action))){
                        return 1.0;
                }
                if(!currentStateRegistry.getSpeedAdjustment().getKey().equals("NONE") && state.equals(successorState)){
                        return 1.0;
                    }
            }

            if(action.equals("CRUISE")){
                if(currentStateRegistry.getSpeedAdjustment().getKey().equals("NONE") &&
                    state.equals(successorState)){
                        return 1.0;
                    }
                if(!currentStateRegistry.getSpeedAdjustment().getKey().equals("NONE") && 
                successorState.equals(((Road)currentStateRegistry.getState()).getToLocation())){
                    return 1.0;
                }
            }
        }// End of road states
        
        return 0.0;
    }

    public Double rewardFunction(String state, String action){

        StateRegistry stateRegistry = stateRegistryMap.get(state);
        StringBuilder builder = new StringBuilder();
        
        if(world.getGoalLocation().getName().equals(stateRegistry.getState().getName()) && action.equals("STAY")){
            return 0.;
        }

        if(locationStrings.contains(state) && action.equals("STAY")){
            return -stayingTime;
        }

        if(locationStrings.contains(state) && locationActions.contains(action)){
            for(Road r : world.getRoads()){
                builder.setLength(0);
                builder.append("TURN_ONTO_");
                builder.append(r.getName());
                if(action.equals(builder.toString()) && stateRegistry.getState().getName().equals(r.getFromLocation())){
                    return -turningTime;
                }
            }
        }

        if(roadStrings.contains(state) && action.equals("CRUISE") && !stateRegistry.getSpeedAdjustment().getKey().equals("NONE")){
            Integer sLimit = this.speedLimits.get(((Road)stateRegistry.getState()).getType());
            Double distance = ((Road)stateRegistry.getState()).getLength();
            return -3600 * distance/sLimit;
        }

        if(roadStrings.contains(state) && accelerateActions.containsKey(action) && stateRegistry.getSpeedAdjustment().getKey().equals("NONE")){
            Integer speed = speedLimits.get(((Road)stateRegistry.getState()).getType()) + speedAdjustments.get(accelerateActions.get(action));
            return -accelerationRate * speed/10;
        }

        return -driverErrorPenalty;
    }

    public State startState(){
        return world.getStartLocation();
    }

    public StateRegistry getStateRegistry(String s){
        return stateRegistryMap.get(s);
    }


}