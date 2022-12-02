package factories;

import java.util.List;
import java.util.Map;

import states.Location;
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


    //      Context Index -> State   -> Action    -> List of ( Successor -> Evaluation )       
    // private Map<Integer, Map<String, Map<String, List<Map<String, Integer>>>>>  transitionEval;
    private Map<Integer, Map<String, Map<String, Map<String, Integer>>>>  transitionEval;


    private SelfDrivingCarWorld world;


    public EvaluationFactory(List<Integer> context, SelfDrivingCarWorld world){
        this.context = context;
        this.world = world;
        // this.stateActionEval = new HashMap<>();
        this.stateEval = new HashMap<>();
        this.transitionEval = new HashMap<>();
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

    private void fillTransitionEvalWith(Integer evalValue){

        for(int contextValueIndex = 0; contextValueIndex < context.size(); contextValueIndex++){

            Map<String, Map<String, Map<String, Integer>>> transitionEvalState = new HashMap<>();
            for(String state : world.getAllStateKeys()){
                Map<String, Map<String, Integer>> transitionEvalAction = new HashMap<>();
                for(String action : world.getPossibleActionsForState(state)){
                    Map<String, Integer> successorToEvalMap = new HashMap<>();
                    for(String successor : world.getPossibleResultingStates(state, action)){
                        successorToEvalMap.put(successor, evalValue);
                    }
                    transitionEvalAction.put(action, successorToEvalMap);
                }
                transitionEvalState.put(state, transitionEvalAction);
            }
            transitionEval.put(contextValueIndex, transitionEvalState);
        }
    }

    private void fillTransitionEvalForContextIndexWith(Integer evalValue, Integer contextIndex){
            Map<String, Map<String, Map<String, Integer>>> transitionEvalState = new HashMap<>();
            for(String state : world.getAllStateKeys()){
                Map<String, Map<String, Integer>> transitionEvalAction = new HashMap<>();
                for(String action : world.getPossibleActionsForState(state)){
                    Map<String, Integer> successorToEvalMap = new HashMap<>();
                    for(String successor : world.getPossibleResultingStates(state, action)){
                        successorToEvalMap.put(successor, evalValue);
                    }
                    transitionEvalAction.put(action, successorToEvalMap);
                }
                transitionEvalState.put(state, transitionEvalAction);
            }
            transitionEval.put(contextIndex, transitionEvalState);
    }

    public Map<Integer, Map<String, Map<String, Map<String, Integer>>>> getTransitionEvalsWithEvaluationValue(Integer evalValue){

        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> tEval = new HashMap<>();
        for(int contextValueIndex = 0; contextValueIndex < context.size(); contextValueIndex++){

            Map<String, Map<String, Map<String, Integer>>> transitionEvalState = new HashMap<>();
            for(String state : world.getAllStateKeys()){
                Map<String, Map<String, Integer>> transitionEvalAction = new HashMap<>();
                for(String action : world.getPossibleActionsForState(state)){
                    Map<String, Integer> successorToEvalMap = new HashMap<>();
                    for(String successor : world.getPossibleResultingStates(state, action)){
                        successorToEvalMap.put(successor, evalValue);
                    }
                    transitionEvalAction.put(action, successorToEvalMap);
                }
                transitionEvalState.put(state, transitionEvalAction);
            }
            tEval.put(contextValueIndex, transitionEvalState);
        }

        return tEval;
    }

    // ------------------------------ Start DCT ------------------------------


    public void createDCTevals(Map<Integer, StateProfile> forbidden){

        fillTransitionEvalWith(Integer.MAX_VALUE);


        for(Map.Entry<Integer, StateProfile> forbiddenEntry : forbidden.entrySet()){

            Integer contextValueIndex = forbiddenEntry.getKey();
            StateProfile profile = forbiddenEntry.getValue();

            // Iterate over the states and check if a state is a forbidden one
            for(String state : world.getAllStateKeys()){

                // If the state is forbidden, set the transitions towards that state as promoting harm
                // and check if a successor of that state is not forbidden
                if(isForbidden(profile, state)){
                    for(String precedingAction : world.getPrecedingActionsForSuccessor(state)){
                        for(String precedingState : world.getPrecedingStatesForSuccessor(state, precedingAction)){

                            // System.out.println("state " + precedingState + " action " + precedingAction + " successor " + state);
                            this.setTransitionEval(precedingState, precedingAction, state, 1, contextValueIndex);

                        }
                    }
                    for(Map.Entry<Integer, StateProfile> successiveForbiddenEntry : forbidden.entrySet()){
                        Integer successiveContextValueIndex = successiveForbiddenEntry.getKey();
                        StateProfile successiveProfile = successiveForbiddenEntry.getValue();



                        for(String successiveAction : world.getPossibleActionsForState(state)){
                            for(String successiveState : world.getPossibleResultingStates(state, successiveAction)){
                                if(!isForbidden(successiveProfile, successiveState)){
                                    // System.out.println("state " + state + " action " + successiveAction + " successor " + successiveState);
                                    this.setTransitionEval(state, successiveAction, successiveState, 0, successiveContextValueIndex);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isForbidden(StateProfile profile, String state){
        int forbidCounter = 0;
                StateRegistry reg = world.getRegistryFromStateKey(state);
                
                if(profile.getName().equals("ALL")){
                    forbidCounter++;
                }else if(profile.getName().equals(reg.getState().getName())){
                    forbidCounter++;
                }
                
                if(reg.getState() instanceof Road){

                    if(profile.getType().equals("ALL")){
                        forbidCounter++;
                    }else if(profile.getType().equals(((Road)reg.getState()).getType())){
                        forbidCounter++;
                    }

                    if(profile.getSpeedAdjustment().equals("ALL")){
                        forbidCounter++;
                    }else if(profile.getSpeedAdjustment().equals(reg.getSpeedAdjustment())){
                        forbidCounter++;
                    }

                    if(profile.getPedestrianTraffic().equals("ALL")){
                        forbidCounter++;
                    }else if(profile.getPedestrianTraffic().equals(reg.getPedestrianTraffic())){
                        forbidCounter++;
                    }
                }

                if((!profile.isLocation() && forbidCounter == 4) || (profile.isLocation() && forbidCounter == 1)){
                    return true;
                }
                return false;
    }

    // ------------------------------- End DCT -------------------------------

    // ------------------------------ Start PFD ------------------------------

    // Context Index -> State Profile in which the duty is applied -> action which fulfills the duty

    public void createPFDevals(Map<Integer,Map<StateProfile,String>> contextToDuties){
        fillTransitionEvalWith(Integer.MAX_VALUE);

        for(Map.Entry<Integer,Map<StateProfile,String>> entry : contextToDuties.entrySet()){

            int contextValueIndex = entry.getKey();
            Map<StateProfile,String> duties = entry.getValue();
            for(Map.Entry<StateProfile, String> d : duties.entrySet()){
                
                StateProfile stateProfile = d.getKey();
                String dutyAction = d.getValue();
                for(String state : world.getAllStateKeys()){

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
                        // If the action is different from the actions given by the duties, promote the associated negative value of the context
                        for(String action : possibleActions){
                            int actionNeglectCounter = neglectCounter;
                            if(!dutyActionsSeparated.contains(action)){
                                actionNeglectCounter++;
                            }
                            if((actionNeglectCounter > 4 && !stateProfile.isLocation()) || (actionNeglectCounter > 1 && stateProfile.isLocation())){
                                    for(String successor : world.getPossibleResultingStates(state, action)){
                                        setTransitionEval(state, action, successor, 1, contextValueIndex);
                                    }
                            }
                        }
                    }

                    
                }
            }
        }
    }

    // ------------------------------- End PFD -------------------------------

    // ------------------------------- Start VE ------------------------------

    public void createVEevals(List<VirtueEthicsData> dataListVE){
    
        for(VirtueEthicsData dataVE : dataListVE){

            // All states and state-action pairs are not applicable for the first context index
            fillTransitionEvalForContextIndexWith(Integer.MAX_VALUE, dataVE.getNegativeContextIndex());

            // All states and state-action pairs demote for the second context index
            fillTransitionEvalForContextIndexWith(0, dataVE.getPositiveContextIndex());

            StateProfile trajectoryStateProfile = dataVE.getTrajectoryStateProfile();
            String trajectoryAction = dataVE.getTrajectoryAction();
            StateProfile trajectorySuccessorStateProfile = dataVE.getTrajectorySuccessorStateProfile();

            for(String state : world.getAllStateKeys()){
                
                int counter = 0;
                StateRegistry stateRegistry = world.getRegistryFromStateKey(state);

                if(trajectoryStateProfile.getName().equals("ALL")){
                    counter++;
                }else if(trajectoryStateProfile.getName().equals(stateRegistry.getState().getName())){
                    counter++;
                }

                if(stateRegistry.getState() instanceof Road){

                    if(trajectoryStateProfile.getType().equals("ALL")){
                        counter++;
                    }else if(trajectoryStateProfile.getType().equals(((Road)stateRegistry.getState()).getType())){
                        counter++;
                    }

                    if(trajectoryStateProfile.getSpeedAdjustment().equals("ALL")){
                        counter++;
                    }else if(trajectoryStateProfile.getSpeedAdjustment().equals(stateRegistry.getSpeedAdjustment())){
                        counter++;
                    }

                    if(trajectoryStateProfile.getPedestrianTraffic().equals("ALL")){
                        counter++;
                    }else if(trajectoryStateProfile.getPedestrianTraffic().equals(stateRegistry.getPedestrianTraffic())){
                        counter++;
                    }
                }
                // Check if the state corresponds to the starting state of the described trajectory
                if((counter == 4 && !trajectoryStateProfile.isLocation()) || (counter == 1 && trajectoryStateProfile.isLocation())){
                    // If the state is a start of a positive trajectory all the transitions that lead to that state promote the associated positive context value
                    for(String precedingAction : world.getPrecedingActionsForSuccessor(state)){
                        for(String precedingState : world.getPrecedingStatesForSuccessor(state, precedingAction)){
                            setTransitionEval(precedingState, precedingAction, state, 1, dataVE.getPositiveContextIndex());
                        }
                    }

                    List<String> trajectoryActionsSeparated = new LinkedList<>();
                    if(trajectoryAction.contains("||")){
                        trajectoryActionsSeparated = new LinkedList<String>(Arrays.asList(trajectoryAction.split(" ")));
                        trajectoryActionsSeparated.removeAll(Collections.singleton("||"));
                    }else{
                        trajectoryActionsSeparated.add(trajectoryAction);
                    }

                    List<String> possibleActions = new ArrayList<>(world.getPossibleActionsForState(state));
                    if(possibleActions.containsAll(trajectoryActionsSeparated) || trajectoryAction.equals("ALL")){
                        for(String action : possibleActions){
                            
                            // If possible action is not contained in the trajectory actions, the transition promotes the negative associated value
                            if(!trajectoryActionsSeparated.contains(action) && !state.equals(world.getGoalLocation())){
                                for(String successorState : world.getPossibleResultingStates(state, action)){
                                    setTransitionEval(state, action, successorState, 1, dataVE.getNegativeContextIndex());
                                }
                            }
                            else{
                                // If the possible action is the same as the trajectory action (or the action is "ALL"), check the successor state 

                                for(String successorState : world.getPossibleResultingStates(state, action)){

                                    int successorCounter = 0;

                                    StateRegistry successorStateRegistry = world.getRegistryFromStateKey(successorState);

                                    if(trajectorySuccessorStateProfile.getName().equals("ALL")){
                                        successorCounter++;
                                    }else if(trajectorySuccessorStateProfile.getName().equals(successorStateRegistry.getState().getName())){
                                        successorCounter++;
                                    }
                    
                                    if(successorStateRegistry.getState() instanceof Road){
                    
                                        if(trajectorySuccessorStateProfile.getType().equals("ALL")){
                                            successorCounter++;
                                        }else if(trajectorySuccessorStateProfile.getType().equals(((Road)successorStateRegistry.getState()).getType())){
                                            successorCounter++;
                                        }
                    
                                        if(trajectorySuccessorStateProfile.getSpeedAdjustment().equals("ALL")){
                                            successorCounter++;
                                        }else if(trajectorySuccessorStateProfile.getSpeedAdjustment().equals(successorStateRegistry.getSpeedAdjustment())){
                                            successorCounter++;
                                        }
                    
                                        if(trajectorySuccessorStateProfile.getPedestrianTraffic().equals("ALL")){
                                            successorCounter++;
                                        }else if(trajectorySuccessorStateProfile.getPedestrianTraffic().equals(successorStateRegistry.getPedestrianTraffic())){
                                            successorCounter++;
                                        }
                                    }

                                    // If the successor state matches the trajectory successor state, the transition promotes the positive context value
                                    if((successorCounter == 4 && !trajectorySuccessorStateProfile.isLocation()) || (successorCounter == 1 && trajectorySuccessorStateProfile.isLocation())){
                                        setTransitionEval(state, action, successorState, 1, dataVE.getPositiveContextIndex());
                                    }
                                }
                            }
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

    public void setTransitionEval(String state, String action, String successor, Integer evaluation, Integer contextValueIndex){

        transitionEval.get(contextValueIndex).get(state).get(action).put(successor, evaluation);
    }

    public Map<Integer,Map<String,Integer>> getStateEval(){
        return stateEval;
    }

    public Map<Integer, Map<String, Map<String, Integer>>> getStateActionEval() {
        return stateActionEval;
    }

    public Map<Integer, Map<String, Map<String, Map<String, Integer>>>> getTransitionEval() {
        return transitionEval;
    }
}
