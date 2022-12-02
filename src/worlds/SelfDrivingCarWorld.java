package worlds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import states.Location;
import states.Road;
import states.State;
import states.StateRegistry;


public class SelfDrivingCarWorld {
    
    private List<Location> locations = new ArrayList<>();
    private List<Road> roads = new ArrayList<>();
    private String startLocation;
    private String goalLocation;

    private Map<String,String> accelerateActions = new HashMap<>(); 
    private Map<String,Integer> speedAdjustments = new HashMap<>();
    private Map<String,Integer> speedLimits = new HashMap<>();
    private Map<String,Double> pedestrianTraffic = new HashMap<>();

    private List<String> allStateKeys = new ArrayList<>();
    private Map<String, StateRegistry> stateRegistryMap = new HashMap<String,StateRegistry>();
    private List<String> locationStrings = new ArrayList<>();
    private List<String> roadStrings = new ArrayList<>();
    private List<String> locationActions = new ArrayList<>();
    private List<String> roadActions = new ArrayList<>();
    private List<String> allActions;
    private Map<String, Map<String, List<String>>> transitions;
    private Map<String, Map<String, String>> inversedTransitions;

    public SelfDrivingCarWorld(List<Location> locations, List<Road> roads){
        this.locations = locations;
        this.roads = roads;
        setUpConstants();
        setUpVariables();
    }

    private void setUpConstants(){

        accelerateActions.put("TO_LOW","LOW");
        accelerateActions.put("TO_NORMAL","NORMAL");
        accelerateActions.put("TO_HIGH","HIGH");

        speedAdjustments.put("NONE",-100);
        speedAdjustments.put("LOW",-10);
        speedAdjustments.put("NORMAL",0);
        speedAdjustments.put("HIGH",10);

        speedLimits.put("CITY",25);
        speedLimits.put("COUNTY",45);
        speedLimits.put("HIGHWAY",75);

        pedestrianTraffic.put("LIGHT",0.8);
        pedestrianTraffic.put("HEAVY",0.2);
    }

    private void setUpVariables(){
        
        StringBuilder builder = new StringBuilder();

        for (State state : locations) {
            StateRegistry tempSR = new StateRegistry(state);
            stateRegistryMap.put(tempSR.toString(),tempSR);
            locationStrings.add(tempSR.toString());
            allStateKeys.add(tempSR.toString());
        }

        List<String> speedList = new ArrayList<>(Arrays.asList("NONE", "LOW", "NORMAL","HIGH"));
        List<String> pedestrianList = new ArrayList<>(Arrays.asList("LIGHT","HEAVY"));
        // Adding cartesian product of road states, speedAdjustments, pedestrianTraffic to a unique string for each
        for (State state : roads) {
            for(String speedEntry : speedList){
                for(String pTrafficEntry : pedestrianList){
                    StateRegistry tempSR = new StateRegistry(state, speedEntry, pTrafficEntry);
                    stateRegistryMap.put(tempSR.toString(),tempSR);
                    roadStrings.add(tempSR.toString());
                    allStateKeys.add(tempSR.toString());
                }
            }
        }

        // Making location actions
        for(State state : roads){
            builder.append("TURN_ONTO_");
            builder.append(state.getName());
            locationActions.add(builder.toString());
            builder.setLength(0);
        }
        locationActions.add("STAY");

        // Making road actions
        for(Map.Entry<String,String> a : accelerateActions.entrySet()){
            roadActions.add(a.getKey());
        }
        roadActions.add("CRUISE");

        allActions = new ArrayList<>(locationActions);
        allActions.addAll(roadActions);


        // Creating transitions
        // createTransitions();

    }// End Set up variables



    public Set<String> getPossibleActionsForState(String state){
        Set<String> possibleActions = new HashSet<>();
        if(locationStrings.contains(state)){
            possibleActions.add("STAY");
            StringBuilder builder = new StringBuilder();
            for(String rS : roadStrings){
                StateRegistry roadStateReg = getRegistryFromStateKey(rS);
                if(((Road)roadStateReg.getState()).getFromLocation().equals(state)){
                    builder.append("TURN_ONTO_");
                    builder.append(roadStateReg.getState().getName());
                    possibleActions.add(builder.toString());
                    builder.setLength(0);
                }
            }
        }
        if(roadStrings.contains(state)){
            if(getRegistryFromStateKey(state).getSpeedAdjustment().equals("NONE")){
                for(String a : accelerateActions.keySet())
                possibleActions.add(a); 
            }
            else{
                possibleActions.add("CRUISE");
            }
        }
        return possibleActions;
    }

    public Set<String> getPrecedingActionsForSuccessor(String successor){
        Set<String> precedingActions = new HashSet<>();
        if(roadStrings.contains(successor)){
            StateRegistry roadStateReg = getRegistryFromStateKey(successor);
            if(roadStateReg.getSpeedAdjustment().equals("NONE")){
                StringBuilder builder = new StringBuilder();
                builder.append("TURN_ONTO_");
                builder.append(roadStateReg.getState().getName());
                precedingActions.add(builder.toString());
                builder.setLength(0);
            }else{
                for(Map.Entry<String, String> accelerateEntry : accelerateActions.entrySet()){
                    if(roadStateReg.getSpeedAdjustment().equals(accelerateEntry.getValue())){
                        precedingActions.add(accelerateEntry.getKey());
                    }
                }
            }
        }
        if(locationStrings.contains(successor)){
            precedingActions.add("CRUISE");
        }

        return precedingActions;
    }


    public Set<String> getPossibleResultingStates(String state, String action){
        Set<String> possibleResultStates = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        // If the state is a location state check if the action is a turn onto action
        if(locationStrings.contains(state)){
            if(action.equals("STAY")){
                possibleResultStates.add(state);
            }else{
                for(String roadKey : roadStrings){
                    StateRegistry roadReg = getRegistryFromStateKey(roadKey);
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
            StateRegistry roadReg = getRegistryFromStateKey(state);
            if(roadReg.getSpeedAdjustment().equals("NONE")){
                if(accelerateActions.keySet().contains(action)){
                    StateRegistry tempReg = new StateRegistry(roadReg.getState(), accelerateActions.get(action), roadReg.getPedestrianTraffic());
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

    public Set<String> getPrecedingStatesForSuccessor(String successor, String action){
        Set<String> precedingStates = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        if(locationStrings.contains(successor)){
            if(action.equals("CRUISE")){
                List<String> speedList = new ArrayList<>(Arrays.asList("LOW", "NORMAL","HIGH"));
                List<String> pedestrianList = new ArrayList<>(Arrays.asList("LIGHT","HEAVY"));
                for(Road r : roads){
                    if(r.getToLocation().equals(successor)){
                        for(String speedEntry : speedList){
                            for(String pTrafficEntry : pedestrianList){
                                StateRegistry tempSR = new StateRegistry(r, speedEntry, pTrafficEntry);
                                precedingStates.add(tempSR.toString());
                            }
                        }
                    }
                }
                    
                
            }
        }else if(roadStrings.contains(successor)){
            StateRegistry roadReg = getRegistryFromStateKey(successor);
            if(roadReg.getSpeedAdjustment().equals("NONE")){
                builder.append("TURN_ONTO_");
                builder.append(roadReg.getState().getName());
                if(action.equals(builder.toString())){
                    precedingStates.add(((Road)roadReg.getState()).getFromLocation());
                }
                builder.setLength(0);
            }else{
                if(accelerateActions.keySet().contains(action)){
                    roadReg = new StateRegistry(roadReg.getState(), "NONE", roadReg.getPedestrianTraffic());
                    precedingStates.add(roadReg.toString());
                }
            }
        }

        return precedingStates;
    }

    public Map<String, List<String>> getMapOfStatesAndActions(){
        Map<String, List<String>> map = new HashMap<>();
        for(String state : getAllStateKeys()){
            List<String> actions = new ArrayList<>();
            for(String action : getPossibleActionsForState(state)){
                actions.add(action);
            } 
            map.put(state, actions);
        }
        return map;
    }



    private void createTransitions(){

        for(String state : getAllStateKeys()){
            Map<String, List<String>> actionSuccessorsMap = new HashMap<>();
            for(String action : getPossibleActionsForState(state)){
                List<String> successorList = new ArrayList<>();
                for(String successorState : getPossibleResultingStates(state, action)){
                    successorList.add(successorState);
                }
                actionSuccessorsMap.put(action, successorList);
            }
            transitions.put(state, actionSuccessorsMap);
        }
    }

    public Map<String, Map<String, String>> getInversedTransitions(){
        return inversedTransitions;
    }

    public Map<String, Map<String, List<String>>> getTransitions(){
        return transitions;
    }

    public List<String> getAllStateKeys(){
        return allStateKeys;
    }

    public List<String> getAllActions() {
        return allActions;
    }

    public StateRegistry getRegistryFromStateKey(String state){
        return stateRegistryMap.get(state);
    }

    public Map<String, StateRegistry> getStateRegistryMap() {
        return stateRegistryMap;
    }

    public Map<String, Integer> getSpeedLimits() {
        return speedLimits;
    }

    public Map<String, String> getAccelerateActions() {
        return accelerateActions;
    }

    public Map<String, Integer> getSpeedAdjustments() {
        return speedAdjustments;
    }

    public Map<String, Double> getPedestrianTraffic() {
        return pedestrianTraffic;
    }

    public List<String> getLocationStrings() {
        return locationStrings;
    }

    public List<String> getRoadStrings() {
        return roadStrings;
    }

    public List<String> getLocationActions() {
        return locationActions;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getGoalLocation() {
        return goalLocation;
    }

    public void setStartLocation(String start){
        this.startLocation = start;
    }

    public void setGoalLocation(String goal){
        this.goalLocation = goal;
    }
}
