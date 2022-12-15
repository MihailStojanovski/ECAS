package factories;

import java.util.List;
import java.util.Map;

import states.Road;
import states.StateRegistry;

import java.util.HashMap;

import worlds.SelfDrivingCarWorld;

public abstract class EvaluationFactory {

    // The moral evaluation of a state with the integer representing the associated moral value
    private Map<Integer,Map<String,Integer>> stateEval;


    //      Context Index -> State   -> Action    -> List of ( Successor -> Evaluation )       
    private Map<Integer, Map<String, Map<String, Map<String, Integer>>>>  transitionEval;

    protected SelfDrivingCarWorld world;


    public EvaluationFactory(SelfDrivingCarWorld world){
        this.world = world;
        // this.stateActionEval = new HashMap<>();
        this.stateEval = new HashMap<>();
        this.transitionEval = new HashMap<>();
    }


    // protected void fillTransitionEvalWith(Integer evalValue){

    //     for(int contextValueIndex = 0; contextValueIndex < context.size(); contextValueIndex++){

    //         Map<String, Map<String, Map<String, Integer>>> transitionEvalState = new HashMap<>();
    //         for(String state : world.getAllStateKeys()){
    //             Map<String, Map<String, Integer>> transitionEvalAction = new HashMap<>();
    //             for(String action : world.getPossibleActionsForState(state)){
    //                 Map<String, Integer> successorToEvalMap = new HashMap<>();
    //                 for(String successor : world.getPossibleResultingStates(state, action)){
    //                     successorToEvalMap.put(successor, evalValue);
    //                 }
    //                 transitionEvalAction.put(action, successorToEvalMap);
    //             }
    //             transitionEvalState.put(state, transitionEvalAction);
    //         }
    //         transitionEval.put(contextValueIndex, transitionEvalState);
    //     }
    // }

    public void clearTransitionEvals(){
        transitionEval.clear();
    }

    protected void fillTransitionEvalForContextIndexWith(Integer evalValue, Integer contextIndex){
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

    public Map<Integer, Map<String, Map<String, Map<String, Integer>>>> getBlankTransitionEvals(Integer contextSize){

        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> tEval = new HashMap<>();
        for(int contextValueIndex = 0; contextValueIndex < contextSize; contextValueIndex++){

            Map<String, Map<String, Map<String, Integer>>> transitionEvalState = new HashMap<>();
            for(String state : world.getAllStateKeys()){
                Map<String, Map<String, Integer>> transitionEvalAction = new HashMap<>();
                for(String action : world.getPossibleActionsForState(state)){
                    Map<String, Integer> successorToEvalMap = new HashMap<>();
                    for(String successor : world.getPossibleResultingStates(state, action)){
                        successorToEvalMap.put(successor, Integer.MAX_VALUE);
                    }
                    transitionEvalAction.put(action, successorToEvalMap);
                }
                transitionEvalState.put(state, transitionEvalAction);
            }
            tEval.put(contextValueIndex, transitionEvalState);
        }

        return tEval;
    }

    protected boolean stateCorrespondsToProfile(StateProfile profile, String state){
        int correspondsCounter = 0;
                StateRegistry reg = world.getRegistryFromStateKey(state);
                
                if(profile.getName().equals("ALL")){
                    correspondsCounter++;
                }else if(profile.getName().equals(reg.getState().getName())){
                    correspondsCounter++;
                }
                
                if(reg.getState() instanceof Road){

                    if(profile.getType().equals("ALL")){
                        correspondsCounter++;
                    }else if(profile.getType().equals(((Road)reg.getState()).getType())){
                        correspondsCounter++;
                    }

                    if(profile.getSpeedAdjustment().equals("ALL")){
                        correspondsCounter++;
                    }else if(profile.getSpeedAdjustment().equals(reg.getSpeedAdjustment())){
                        correspondsCounter++;
                    }

                    if(profile.getPedestrianTraffic().equals("ALL")){
                        correspondsCounter++;
                    }else if(profile.getPedestrianTraffic().equals(reg.getPedestrianTraffic())){
                        correspondsCounter++;
                    }
                }

                if((!profile.isLocation() && correspondsCounter == 4) || (profile.isLocation() && correspondsCounter == 1)){
                    return true;
                }
                return false;
    }


    public void setStateEval(String state, Integer evaluation, Integer contextValueIndex){
        stateEval.get(contextValueIndex).replace(state, evaluation);
    }

    public void setTransitionEval(String state, String action, String successor, Integer evaluation, Integer contextValueIndex){

        transitionEval.get(contextValueIndex).get(state).get(action).put(successor, evaluation);
    }

    public Map<Integer,Map<String,Integer>> getStateEval(){
        return stateEval;
    }

    public Map<Integer, Map<String, Map<String, Map<String, Integer>>>> getTransitionEval() {
        return transitionEval;
    }

    public Map<String, Map<String, Map<String, Integer>>> getTransitionEvalForContextIndex(Integer contextIndex){
        return transitionEval.get(contextIndex);
    }
}
