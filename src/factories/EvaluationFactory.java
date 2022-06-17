package factories;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import agents.SelfDrivingCarAgent;
import worlds.SelfDrivingCarWorld;

public class EvaluationFactory {
    private List<Integer> context;
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
