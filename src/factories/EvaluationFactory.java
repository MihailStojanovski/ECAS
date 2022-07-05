package factories;

import java.util.List;
import java.util.Map;

import states.Road;
import states.StateRegistry;

import java.util.ArrayList;
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

    public void createPFDevals(Map<Integer,Map<StateProfile,String>> contextToDuties, int tolerance){
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
                    if(dutyAction.contains("ORRR")){
                        dutyActionsSeparated = new LinkedList<String>(Arrays.asList(dutyAction.split(" ")));
                        dutyActionsSeparated.removeAll(Collections.singleton("ORRR"));
                    }else{
                        dutyActionsSeparated.add(dutyAction);
                    }
                    
                    int toleranceCounter = 0;
                    List<String> possibleActions = new ArrayList<>(world.getPossibleActionsForState(state));
                    if(possibleActions.containsAll(dutyActionsSeparated)){
                        // If the action is different from the actions given by the duties, they will be promoting the negative associated value of the context
                        for(String action : possibleActions){
                            int actionNeglectCounter = neglectCounter;
                            if(!dutyActionsSeparated.contains(action)){
                                actionNeglectCounter++;
                            }
                            if(actionNeglectCounter == 5){
                                if(toleranceCounter > tolerance){
                                    setStateActionEval(state, action, 1, contextValueIndex);
                                }else{
                                    toleranceCounter++;
                                }
                            }
                        }
                    }

                    
                }
            }
        }
    }

    // ------------------------------- End PFD -------------------------------

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
