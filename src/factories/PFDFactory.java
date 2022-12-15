package factories;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import worlds.SelfDrivingCarWorld;

public class PFDFactory extends EvaluationFactory{

    // Context Index -> State Profile in which the duty is applied -> action which fulfills the duty
    private Map<Integer,List<Map<StateProfile,String>>> contextToDuties;

    public PFDFactory(SelfDrivingCarWorld world, Map<Integer,List<Map<StateProfile,String>>> contextToDuties) {
        super(world);
        this.contextToDuties = contextToDuties;
    }
    

     public void createEvaluations(){
        
        for(Map.Entry<Integer, List<Map<StateProfile,String>>> entry : contextToDuties.entrySet()){
            
            int contextValueIndex = entry.getKey();
            List<Map<StateProfile,String>> dutiesList = entry.getValue();

            fillTransitionEvalForContextIndexWith(Integer.MAX_VALUE, contextValueIndex);
            for(Map<StateProfile,String> duties : dutiesList){
                for(Map.Entry<StateProfile, String> d : duties.entrySet()){
                
                    StateProfile stateProfile = d.getKey();
                    String dutyAction = d.getValue();
                    for(String state : world.getAllStateKeys()){
                        
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
                                int actionNeglectCounter = 0;
                                if(!dutyActionsSeparated.contains(action)){
                                    actionNeglectCounter++;
                                }
                                if(stateCorrespondsToProfile(stateProfile, state) && actionNeglectCounter > 0){
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
    }

    public void setContextToDuties(Map<Integer, List<Map<StateProfile, String>>> contextToDuties) {
        this.contextToDuties = contextToDuties;
    }
    
}
