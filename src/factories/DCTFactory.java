package factories;

import java.util.List;
import java.util.Map;

import worlds.SelfDrivingCarWorld;

public class DCTFactory extends EvaluationFactory{

    // Context value index -> Forbidden state profile
    private Map<Integer, List<StateProfile>> forbidden;

    public DCTFactory(SelfDrivingCarWorld world, Map<Integer, List<StateProfile>> forbidden) {
        super(world);
        this.forbidden = forbidden;
    }
    
    public void createEvaluations(){
        
        for(Map.Entry<Integer, List<StateProfile>> forbiddenEntry : forbidden.entrySet()){
            fillTransitionEvalForContextIndexWith(Integer.MAX_VALUE, forbiddenEntry.getKey());

            Integer contextValueIndex = forbiddenEntry.getKey();
            List<StateProfile> profileList = forbiddenEntry.getValue();

            for(StateProfile profile : profileList){
                // Iterate over the states and check if a state is a forbidden one
                for(String state : world.getAllStateKeys()){

                    // If the state is forbidden, set the transitions towards that state as promoting harm
                    // and check if a successor of that state is not forbidden
                    if(stateCorrespondsToProfile(profile, state)){
                        for(String precedingAction : world.getPrecedingActionsForSuccessor(state)){
                            for(String precedingState : world.getPrecedingStatesForSuccessor(state, precedingAction)){

                                // System.out.println("state " + precedingState + " action " + precedingAction + " successor " + state);
                                this.setTransitionEval(precedingState, precedingAction, state, 1, contextValueIndex);

                            }
                        }
                        for(Map.Entry<Integer, List<StateProfile>> successiveForbiddenEntry : forbidden.entrySet()){

                            Integer successiveContextValueIndex = successiveForbiddenEntry.getKey();
                            List<StateProfile> successveProfileList = successiveForbiddenEntry.getValue();

                            for(StateProfile successiveProfile : successveProfileList)
                                for(String successiveAction : world.getPossibleActionsForState(state)){
                                    for(String successiveState : world.getPossibleResultingStates(state, successiveAction)){
                                        if(!stateCorrespondsToProfile(successiveProfile, successiveState)){
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
    }

    public void setForbidden(Map<Integer, List<StateProfile>> forbidden) {
        this.forbidden = forbidden;
    }

}
