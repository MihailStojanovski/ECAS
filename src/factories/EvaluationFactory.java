package factories;

import java.util.List;
import java.util.Map;

import states.Road;
import states.StateRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import worlds.SelfDrivingCarWorld;

public class EvaluationFactory {
    protected List<Integer> context;
    // The first map represents the context value that the evaluation is associated with
    // The second map has a state as a key,the map in the value has an action as a key and the evaluation of the state-action pair as a value
    private Map<Integer,Map<String,Map<String,Integer>>> stateActionEval; 

    // The moral evaluation of a state with the integer representing the associated moral value
    private Map<Integer,Map<String,Integer>> stateEval;

    private SelfDrivingCarWorld world;


    public EvaluationFactory(List<Integer> context, SelfDrivingCarWorld world){
        this.context = context;
        this.world = world;
        this.stateActionEval = new HashMap<>();
        this.stateEval = new HashMap<>();
    }

    public void fillUpStateEvalWith(Integer evalValue){
        for(int contextValueIndex = 0; contextValueIndex < context.size(); contextValueIndex++){
            Map<String, Integer> stateEvalTemp = new HashMap<>();
            for(String state : world.getAllStateKeys()){
                stateEvalTemp.put(state,evalValue);
            }
            stateEval.put(contextValueIndex,stateEvalTemp);
        }
    }

    public void fillUpStateEvalWith(Integer evalValue, int contextValueIndex){
        Map<String, Integer> stateEvalTemp = new HashMap<>();
        for(String state : world.getAllStateKeys()){
            stateEvalTemp.put(state,evalValue);
        }
        stateEval.put(contextValueIndex,stateEvalTemp);
        
    }

    public void fillUpStateActionEvalWith(Integer evalValue){
        for(int contextValueIndex = 0; contextValueIndex < context.size(); contextValueIndex++){
            Map<String, Map<String, Integer>> stateActionEvalTemp = new HashMap<>();
            for(String state : world.getAllStateKeys()){
                Map<String, Integer> actionTemp = new HashMap<>();
                for(String action : world.getPossibleActionsForState(state)){
                    actionTemp.put(action, evalValue);
                }
                stateActionEvalTemp.put(state,actionTemp);
            }
            stateActionEval.put(contextValueIndex, stateActionEvalTemp);
        }
    }

    public void fillUpStateActionEvalWith(Integer evalValue, int contextValueIndex){
        Map<String, Map<String, Integer>> stateActionEvalTemp = new HashMap<>();
        for(String state : world.getAllStateKeys()){
            Map<String, Integer> actionTemp = new HashMap<>();
            for(String action : world.getPossibleActionsForState(state)){
                actionTemp.put(action, evalValue);
            }
            stateActionEvalTemp.put(state,actionTemp);
        }
        stateActionEval.put(contextValueIndex, stateActionEvalTemp);
    }

    // ------------------------------ Start DCT ------------------------------
    // Idea about having the possibility to render any state as forbidden, needs a lot of ifs 
    // to sort out the ALL and a specific value for the ForbiddenProfile attributes.


    public void createDCTevals(List<StateProfile> profileList){

        fillUpStateActionEvalWith(Integer.MAX_VALUE);
        fillUpStateEvalWith(0);

        for(StateProfile p : profileList){
            List<String> allStates = world.getAllStateKeys();
            for(String state : allStates){

                int forbidCounter = 0;
                StateRegistry reg = world.getRegistryFromStateKey(state);
                
                if(p.getName().equals("ALL")){
                    forbidCounter++;
                }else if(p.getName().equals(reg.getState().getName())){
                    forbidCounter++;
                }
                
                if(reg.getState() instanceof Road){

                    if(p.getType().equals("ALL")){
                        forbidCounter++;
                    }else if(p.getType().equals(((Road)reg.getState()).getType())){
                        forbidCounter++;
                    }

                    if(p.getSpeedAdjustment().equals("ALL")){
                        forbidCounter++;
                    }else if(p.getSpeedAdjustment().equals(reg.getSpeedAdjustment())){
                        forbidCounter++;
                    }

                    if(p.getPedestrianTraffic().equals("ALL")){
                        forbidCounter++;
                    }else if(p.getPedestrianTraffic().equals(reg.getPedestrianTraffic())){
                        forbidCounter++;
                    }
                }

                if(forbidCounter == 4){
                    setStateEval(state, 1, profileList.indexOf(p));
                }
            }
        }
    }

    // ------------------------------- End DCT -------------------------------

    // ------------------------------ Start PFD ------------------------------

    // Context Index -> State Profile in which the duty is applied -> action which fulfills the duty

    public void createPFDevals(Map<Integer,Map<StateProfile,String>> contextToDuties){
        fillUpStateActionEvalWith(Integer.MAX_VALUE);
        fillUpStateEvalWith(Integer.MAX_VALUE);

        for(Map.Entry<Integer,Map<StateProfile,String>> entry : contextToDuties.entrySet()){

            int contextValueIndex = entry.getKey();
            Map<StateProfile,String> duties = entry.getValue();
            for(Map.Entry<StateProfile, String> d : duties.entrySet()){
                
                StateProfile stateProfile = d.getKey();
                String dutyAction = d.getValue();
                List<String> allStates = world.getAllStateKeys();
                for(String state : allStates){

                    int neglectCounter = 0;
                    StateRegistry reg = world.getRegistryFromStateKey(state);
                    
                    if(stateProfile.getName().equals("ALL")){
                        neglectCounter++;
                    }else if(stateProfile.getName().equals(reg.getState().getName())){
                        neglectCounter++;
                    }
                    
                    if(reg.getState() instanceof Road){

                        if(stateProfile.getType().equals("ALL")){
                            neglectCounter++;
                        }else if(stateProfile.getType().equals(((Road)reg.getState()).getType())){
                            neglectCounter++;
                        }

                        if(stateProfile.getSpeedAdjustment().equals("ALL")){
                            neglectCounter++;
                        }else if(stateProfile.getSpeedAdjustment().equals(reg.getSpeedAdjustment())){
                            neglectCounter++;
                        }

                        if(stateProfile.getPedestrianTraffic().equals("ALL")){
                            neglectCounter++;
                        }else if(stateProfile.getPedestrianTraffic().equals(reg.getPedestrianTraffic())){
                            neglectCounter++;
                        }
                    }
                    
                    List<String> dutyActionsSeparated = new LinkedList<>();
                    if(dutyAction.contains("||")){
                        dutyActionsSeparated = new LinkedList<String>(Arrays.asList(dutyAction.split(" ")));
                        dutyActionsSeparated.removeAll(Collections.singleton("||"));
                    }else{
                        dutyActionsSeparated.add(dutyAction);
                    }
                    
                    List<String> possibleActions = new LinkedList<>(world.getPossibleActionsForState(state));
                    if(possibleActions.containsAll(dutyActionsSeparated)){
                        // If the action is different from the actions given by the duties, they will be promoting the negative associated value of the context
                        for(String action : possibleActions){
                            int actionNeglectCounter = neglectCounter;
                            if(!dutyActionsSeparated.contains(action)){
                                actionNeglectCounter++;
                            }
                            if(actionNeglectCounter >= 5){
                                    setStateActionEval(state, action, 1, contextValueIndex);
                            }
                        }
                    }

                    
                }
            }
        }
    }

    // ------------------------------- End PFD -------------------------------

    // ------------------------------- Start VE ------------------------------

    public void createPositiveVEevals(List<VirtueEthicsData> dataListVE){

        for(VirtueEthicsData dataVE : dataListVE ){
            // All states and state-action pairs demote the positive value of the context
            fillUpStateActionEvalWith(0, dataVE.getPositiveContextIndex());
            fillUpStateEvalWith(0, dataVE.getPositiveContextIndex());

            // All states and state-action pairs are not applicable for the negative value of the context
            fillUpStateActionEvalWith(Integer.MAX_VALUE, dataVE.getNegativeContextIndex());
            fillUpStateEvalWith(Integer.MAX_VALUE, dataVE.getNegativeContextIndex());


            StateProfile trajectoryState = dataVE.getTrajectoryState();
            String trajectoryAction = dataVE.getTrajectoryAction();
            StateProfile trajectorySuccessorState = dataVE.getTrajectorySuccessorState();
    
            for(String state : world.getAllStateKeys()){

                int counter = 0;
                StateRegistry reg = world.getRegistryFromStateKey(state);
                
                if(trajectoryState.getName().equals("ALL")){
                    counter++;
                }else if(trajectoryState.getName().equals(reg.getState().getName())){
                    counter++;
                }
                
                if(reg.getState() instanceof Road){

                    if(trajectoryState.getType().equals("ALL")){
                        counter++;
                    }else if(trajectoryState.getType().equals(((Road)reg.getState()).getType())){
                        counter++;
                    }

                    if(trajectoryState.getSpeedAdjustment().equals("ALL")){
                        counter++;
                    }else if(trajectoryState.getSpeedAdjustment().equals(reg.getSpeedAdjustment())){
                        counter++;
                    }

                    if(trajectoryState.getPedestrianTraffic().equals("ALL")){
                        counter++;
                    }else if(trajectoryState.getPedestrianTraffic().equals(reg.getPedestrianTraffic())){
                        counter++;
                    }
                }

                if(counter == 4){
                    setStateEval(state, 1, dataVE.getPositiveContextIndex());
                }
                
                List<String> possibleActions = new LinkedList<>(world.getPossibleActionsForState(state));
                if(possibleActions.contains(trajectoryAction)){
                    // If the action is different from the action given by the trajectory, it will promote the negative associated value of the context
                    for(String action : possibleActions){
                        int actionNeglectCounter = counter;
                        if(!trajectoryAction.equals(action)){
                            actionNeglectCounter++;
                        }else{
                            // If the possible action is the same as the trajectory action, promote the positive value associated with that trajectory
                            setStateActionEval(state, action, 1, dataVE.getPositiveContextIndex());

                            for(String successorState : world.getPossibleResultingStates(state, action)){
                                // When the action is part of the trajectory, find the successor state in the trajectory and set the evaluation for the positive context value as not applicable (inf)
                                if(successorState.equals(trajectorySuccessorState.getName())){
                                    setStateEval(successorState, Integer.MAX_VALUE, dataVE.getPositiveContextIndex());
                                }
                            }

                        }
                        if(actionNeglectCounter >= 5){
                                setStateActionEval(state, action, 1, dataVE.getNegativeContextIndex());
                        }
                    }
                }

                
            }
        }

    }

    // ------------------------------- End VE --------------------------------

    public void setStateEval(String state, Integer evaluation, Integer contextValueIndex){
        stateEval.get(contextValueIndex).replace(state, evaluation);
    }

    public void setStateActionEval(String state, String action, Integer evaluation, Integer contextValueIndex){
        stateActionEval.get(contextValueIndex).get(state).replace(action, evaluation);
    }

    public Map<Integer,Map<String,Integer>> getStateEval(){
        return stateEval;
    }

    public Map<Integer, Map<String, Map<String, Integer>>> getStateActionEval() {
        return stateActionEval;
    }
}
