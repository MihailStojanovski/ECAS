package rewards;

import java.util.List;
import java.util.Map;

public class EthicalReward implements Reward {


    // The context with the moral values
    private List<Integer> context;
    // The first map represents the context value that the evaluation is associated with
    // The second map has a state as a key,the map in the value has an action as a key and the evaluation of the state-action pair as a value
    private Map<Integer,Map<String,Map<String,Integer>>> stateActionEval; 

    // The moral evaluation of a state with the integer representing the associated moral value
    private Map<Integer,Map<String,Integer>> stateEval;

    public EthicalReward(List<Integer> context, Map<Integer,Map<String,Map<String,Integer>>> stateActionEval, Map<Integer,Map<String,Integer>> stateEval) {
        this.context = context;
        this.stateActionEval = stateActionEval;
        this.stateEval = stateEval;
    }

    private Integer getTransitionEval(String state, String action, String successorState, int ctxValueIndex){
        Integer evalSA = stateActionEval.get(ctxValueIndex).get(state).get(action);
        Integer evalSPrime = stateEval.get(ctxValueIndex).get(successorState);
        return Math.min(evalSA,evalSPrime);
    }

    /*
        PROBLEM:
        The ethical evaluations need to have a value for each state, action, successor state for it to work this way.
        POSSIBLE SOLUTION:
        Make an ethical evaluation factory class that will take parameters and fill stuff up accordingly
    
    */
    @Override
    public EthicalRewardQuad getEthicalReward(String state, String action, String successorState) {

        EthicalRewardQuad quad = new EthicalRewardQuad();
        for(int i = 0; i < context.size(); i++){
            // Context value is morally bad
            if(context.get(i) == 0){
                if(getTransitionEval(state, action, successorState, i) == 1 && stateEval.get(i).get(state) != 1){
                    quad.incrementNabla();
                }
                else if(getTransitionEval(state, action, successorState, i) == 0 && stateEval.get(i).get(state) == 1){
                    quad.incrementNablaBarre();
                }
            }
            // Context value is morally good
            else if(context.get(i) == 1){
                if(getTransitionEval(state, action, successorState, i) == 1 && stateEval.get(i).get(state) != 1){
                    quad.incrementTriangle();
                }
                else if(getTransitionEval(state, action, successorState, i) == 0 && stateEval.get(i).get(state) == 1){
                    quad.incrementTriangleBarre();
                }
            } 
        }
        return quad;  
    }

    @Override
    public Integer getReward(String state, String action, String successorState) {
        // TODO Auto-generated method stub
        return null;
    }


}
