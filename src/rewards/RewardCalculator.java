package rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import states.Road;
import states.StateRegistry;
import worlds.SelfDrivingCarWorld;

public class RewardCalculator{


    private final Double stayingTime = 120D;
    private final Double turningTime = 5D;
    private final Double accelerationRate = 2D;
    private final Double driverErrorPenalty = 3600D;

    // The context with the moral values
    private List<Integer> context;
    
    
    // The first map represents the context value that the evaluation is associated with
    // The second map has a state as a key,the map in the value has an action as a key and the evaluation of the state-action pair as a value

    // private Map<Integer,Map<String,Map<String,Integer>>> stateActionEval; 

    // The moral evaluation of a state with the integer representing the associated moral value
    private Map<Integer,Map<String,Integer>> stateEval;

    //      Context Index -> State   -> Action    -> List of ( Successor -> Evaluation )       
    private Map<Integer, Map<String, Map<String, Map<String, Integer>>>>  transitionEval;


    private SelfDrivingCarWorld world;

    public RewardCalculator(List<Integer> context, Map<Integer, Map<String, Map<String, Map<String, Integer>>>>  transitionEval, SelfDrivingCarWorld world) {
        this.context = context;
        // this.stateActionEval = stateActionEval;
        this.transitionEval = transitionEval;
        this.world = world;
        stateEval = new HashMap<>();
        calculateAllStateEvals();


    }

    private void calculateAllStateEvals(){


        for(int contextValueIndex = 0; contextValueIndex < context.size(); contextValueIndex++){
            Map<String, Integer> tempEval = new HashMap<>();
            for(String successorBeingEvaluated : world.getAllStateKeys()){
                int transitionCtr = 0;
                int promotingCtr = 0;
                int demotingCtr = 0;
                for(String action : world.getPrecedingActionsForSuccessor(successorBeingEvaluated)){
                    for(String state : world.getPrecedingStatesForSuccessor(successorBeingEvaluated, action)){
                        transitionCtr++;
                        if(getTransitionEval(state, action, successorBeingEvaluated, contextValueIndex) == 1){
                            promotingCtr ++;
                        } else if(getTransitionEval(state, action, successorBeingEvaluated, contextValueIndex) == 0){
                            demotingCtr ++;
                        }
                    }
                }
                

                if(transitionCtr == promotingCtr){
                    tempEval.put(successorBeingEvaluated,1);

                }else if(transitionCtr == demotingCtr){
                    tempEval.put(successorBeingEvaluated,0);

                }else{
                    tempEval.put(successorBeingEvaluated,Integer.MAX_VALUE);

                }

            }
            stateEval.put(contextValueIndex, tempEval);
        }

    }

    private Integer getTransitionEval(String state, String action, String successorState, int contextValueIndex){
        // Integer evalSA = stateActionEval.get(ctxValueIndex).get(state).get(action);
        // Integer evalSPrime = stateEval.get(ctxValueIndex).get(successorState);
        // return Math.min(evalSA,evalSPrime);
        // List<Map<String, Integer>> tempList = new ArrayList<>();
        // tempList =  this.transitionEval.get(contextValueIndex).get(state).get(action);
        // for(Map<String, Integer> successorEvalMap : tempList){
        //     if(successorEvalMap.containsKey(successorState)){
        //         return successorEvalMap.get(successorState);
        //     }
        // }
        try {
            return transitionEval.get(contextValueIndex).get(state).get(action).get(successorState);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Successor state [" + successorState + "] missing");
        }

        
    }
    // End new

    public EthicalRewardQuad getEthicalReward(String state, String action, String successorState) {

        EthicalRewardQuad quad = new EthicalRewardQuad();
        for(int i = 0; i < context.size(); i++){
            // Context value is morally bad
            if(context.get(i) == 0){
                if(getTransitionEval(state, action, successorState, i) == 1 && stateEval.get(i).get(state) != 1){
                    quad.incrementNabla();
                }
                else if(getTransitionEval(state, action, successorState, i) == 0 && stateEval.get(i).get(state) == 1){
                    quad.incrementBarredNabla();
                }
            }
            // Context value is morally good
            else if(context.get(i) == 1){
                if(getTransitionEval(state, action, successorState, i) == 1 && stateEval.get(i).get(state) != 1){
                    quad.incrementTriangle();
                }
                else if(getTransitionEval(state, action, successorState, i) == 0 && stateEval.get(i).get(state) == 1){
                    quad.incrementBarredTriangle();
                }
            } 
        }
        return quad;  
    }

    public Double getTaskReward(String currentState, String action, String successorState){

        StateRegistry currentStateRegistry = world.getRegistryFromStateKey(currentState);
        StateRegistry successorStateRegistry = world.getRegistryFromStateKey(successorState);

        if(world.getGoalLocation().equals(currentState)){
            if(action.equals("STAY")){
                if(successorState.equals(currentState)){
                    return 0D;
                }
            }
        }
        if(world.getLocationStrings().contains(currentState) && action.equals("STAY")){
            return stayingTime;
        }

        if(world.getLocationActions().contains(action) && !action.equals("STAY") ){
                if(world.getRoadStrings().contains(successorState)){
                    if(successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                        return turningTime;
                    }
                }
        }

        if(world.getRoadStrings().contains(currentState)){
            if(world.getLocationStrings().contains(successorState) && action.equals("CRUISE")){
                if(!currentStateRegistry.getSpeedAdjustment().equals("NONE")){
                    Double speed = world.getSpeedLimits().get(((Road)world.getRegistryFromStateKey(currentState).getState()).getType()) + Double.valueOf(world.getSpeedAdjustments().get(world.getRegistryFromStateKey(currentState).getSpeedAdjustment()));
                    Double distance = ((Road)world.getRegistryFromStateKey(currentState).getState()).getLength();
                    return 3600D * distance / speed;
                }
            }
            if(world.getRoadStrings().contains(successorState) && world.getAccelerateActions().containsKey(action)){
                if(currentStateRegistry.getSpeedAdjustment().equals("NONE") && !successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                    int speed = world.getSpeedLimits().get(((Road)world.getRegistryFromStateKey(currentState).getState()).getType());
                    return accelerationRate * speed / 10D;
                }
            }
        }
        return driverErrorPenalty;
    }

    public Map<Integer, Map<String, Integer>> getStateEval() {
        return stateEval;
    }

}
