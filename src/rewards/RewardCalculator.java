package rewards;

import java.util.List;
import java.util.Map;

import states.Road;
import states.StateRegistry;
import worlds.SelfDrivingCarWorld;

public class RewardCalculator{


    private Double stayingTime = 120.;
    private Double turningTime = 5.;
    private Double accelerationRate = 2.;
    private Double driverErrorPenalty = 3600.;

    // The context with the moral values
    private List<Integer> context;
    // The first map represents the context value that the evaluation is associated with
    // The second map has a state as a key,the map in the value has an action as a key and the evaluation of the state-action pair as a value
    private Map<Integer,Map<String,Map<String,Integer>>> stateActionEval; 

    // The moral evaluation of a state with the integer representing the associated moral value
    private Map<Integer,Map<String,Integer>> stateEval;

    private SelfDrivingCarWorld world;

    public RewardCalculator(List<Integer> context, Map<Integer,Map<String,Map<String,Integer>>> stateActionEval, Map<Integer,Map<String,Integer>> stateEval, SelfDrivingCarWorld world) {
        this.context = context;
        this.stateActionEval = stateActionEval;
        this.stateEval = stateEval;
        this.world = world;
    }

    private Integer getTransitionEval(String state, String action, String successorState, int ctxValueIndex){
        Integer evalSA = stateActionEval.get(ctxValueIndex).get(state).get(action);
        Integer evalSPrime = stateEval.get(ctxValueIndex).get(successorState);
        return Math.min(evalSA,evalSPrime);
    }

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
                    return 0.;
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
                    return 3600.0 * distance / speed;
                }
            }
            if(world.getRoadStrings().contains(successorState) && world.getAccelerateActions().containsKey(action)){
                if(currentStateRegistry.getSpeedAdjustment().equals("NONE") && !successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                    int speed = world.getSpeedLimits().get(((Road)world.getRegistryFromStateKey(currentState).getState()).getType());
                    return accelerationRate * speed / 10.;
                }
            }
        }
        return driverErrorPenalty;
    }

}
