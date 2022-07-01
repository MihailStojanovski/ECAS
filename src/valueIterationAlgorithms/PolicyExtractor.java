package valueIterationAlgorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolicyExtractor {


    public Map<String, List<String>> minimizingExtractor(Map<String, List<String>> extractionTarget, Map<String, Map<String, Double>> qValue ){
         
        Map<String, List<String>> policyMin = new HashMap<>();
        for(String state : extractionTarget.keySet()){
            List<String> stateActionsMin = new ArrayList<>();
            Double minAction = Double.MAX_VALUE;
            for(String action : extractionTarget.get(state)){
                Double qValueVariable = qValue.get(state).get(action);
                if(stateActionsMin.isEmpty()){
                    stateActionsMin.add(action);
                    minAction = qValueVariable;
                }else if(minAction.equals(qValueVariable)){
                    stateActionsMin.add(action);
                }else if(minAction > qValueVariable){
                    stateActionsMin.clear();
                    stateActionsMin.add(action);
                    minAction = qValueVariable;
                }
            }
            policyMin.put(state, stateActionsMin);
        }
        
        return policyMin;
    }


    public Map<String, List<String>> maximizingExtractor(Map<String, List<String>> extractionTarget, Map<String, Map<String, Double>> qValue){

        Map<String, List<String>> policyMax = new HashMap<>();
        for(String state : extractionTarget.keySet()){
            List<String> stateActionsMax = new ArrayList<>();
            Double maxAction = -Double.MAX_VALUE;
            for(String action : extractionTarget.get(state)){
                Double qValueVariable = qValue.get(state).get(action);
                if(stateActionsMax.isEmpty()){
                    stateActionsMax.add(action);
                    maxAction = qValueVariable;
                }else if(maxAction.equals(qValueVariable)){
                    stateActionsMax.add(action);
                }else if(maxAction < qValueVariable){
                    stateActionsMax.clear();
                    stateActionsMax.add(action);
                    maxAction = qValueVariable;
                }
            }
            policyMax.put(state,stateActionsMax);
        }

        return policyMax;
    }

    
}
