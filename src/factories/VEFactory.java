package factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import worlds.SelfDrivingCarWorld;

public class VEFactory extends EvaluationFactory{


    public VEFactory(SelfDrivingCarWorld world) {
        super(world);
    }

    public void createPositiveEvaluations( Map<Integer, List<VirtueEthicsData>> dataMap){
    
        for(Map.Entry<Integer, List<VirtueEthicsData>> dataMapEntry : dataMap.entrySet()){
            int negativeContextIndex = dataMapEntry.getKey();
            int positiveContextIndex = dataMapEntry.getKey()+1;

            // All states and state-action pairs are not applicable for the first context index
            fillTransitionEvalForContextIndexWith(Integer.MAX_VALUE, negativeContextIndex);

            // All states and state-action pairs are not applicable for the second context index
            fillTransitionEvalForContextIndexWith(Integer.MAX_VALUE, positiveContextIndex);

            for(VirtueEthicsData dataVE : dataMapEntry.getValue()){



                StateProfile trajectoryStateProfile = dataVE.getTrajectoryStateProfile();
                String trajectoryAction = dataVE.getTrajectoryAction();
                StateProfile trajectorySuccessorStateProfile = dataVE.getTrajectorySuccessorStateProfile();

                for(String state : world.getAllStateKeys()){

                    // Check if the state corresponds to the starting state of the described trajectory
                    if(this.stateCorrespondsToProfile(trajectoryStateProfile, state)){
                        // If the state is a start of a positive trajectory all the transitions that lead to that state promote the associated positive context value
                        // i.e. entering a trajectory
                        for(String precedingAction : world.getPrecedingActionsForSuccessor(state)){
                            for(String precedingState : world.getPrecedingStatesForSuccessor(state, precedingAction)){
                                setTransitionEval(precedingState, precedingAction, state, 1, positiveContextIndex);
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
                                // i.e. intentional exit
                                if(!trajectoryActionsSeparated.contains(action) && !state.equals(world.getGoalLocation())){
                                    for(String successorState : world.getPossibleResultingStates(state, action)){
                                        setTransitionEval(state, action, successorState, 1, negativeContextIndex);
                                    }
                                }
                                else{
                                    // If the possible action is the same as the trajectory action (or the action is "ALL"), check the successor state 

                                    for(String successorState : world.getPossibleResultingStates(state, action)){

                                        // If the successor state matches the trajectory successor state, the transition promotes the positive context value
                                        // i.e. maintaining a trajectory
                                        if(stateCorrespondsToProfile(trajectorySuccessorStateProfile, successorState)){
                                            setTransitionEval(state, action, successorState, 1, positiveContextIndex);
                                        }
                                        else{
                                            // If the action was correct, but the successor is not, then we have an accidental exit which demotes the positive value
                                            // i.e. accidental exit of a trajectory
                                            setTransitionEval(state, action, successorState, 0, positiveContextIndex);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }
    } 

    public void createNegativeTrajectoryEvals(Map<Integer, List<VirtueEthicsData>> dataMap){
        
        for(Map.Entry<Integer, List<VirtueEthicsData>> dataMapEntry : dataMap.entrySet()){
            int negativeContextIndex = dataMapEntry.getKey();
            int positiveContextIndex = dataMapEntry.getKey()+1;

            // All states and state-action pairs are not applicable for the first context index
            fillTransitionEvalForContextIndexWith(Integer.MAX_VALUE, negativeContextIndex);

            // All states and state-action pairs are not applicable for the second context index
            fillTransitionEvalForContextIndexWith(Integer.MAX_VALUE, positiveContextIndex);

            for(VirtueEthicsData dataVE : dataMapEntry.getValue()){

                StateProfile trajectoryStateProfile = dataVE.getTrajectoryStateProfile();

                String trajectoryAction = dataVE.getTrajectoryAction();
                List<String> trajectoryActionsSeparated = new LinkedList<>();
                if(trajectoryAction.contains("||")){
                    trajectoryActionsSeparated = new LinkedList<String>(Arrays.asList(trajectoryAction.split(" ")));
                    trajectoryActionsSeparated.removeAll(Collections.singleton("||"));
                }else{
                    trajectoryActionsSeparated.add(trajectoryAction);
                }

                StateProfile trajectorySuccessorStateProfile = dataVE.getTrajectorySuccessorStateProfile();

                for(String state : world.getAllStateKeys()){
                    
                    // Check if the state corresponds to the starting state of the described trajectory
                    if(this.stateCorrespondsToProfile(trajectoryStateProfile, state)){
  

                        List<String> possibleActions = new ArrayList<>(world.getPossibleActionsForState(state));
                        if(possibleActions.containsAll(trajectoryActionsSeparated) || trajectoryAction.equals("ALL")){
                            for(String action : possibleActions){
                                
                                // If possible action is not contained in the trajectory actions
                                // Intentional exit
                                if(!trajectoryActionsSeparated.contains(action) && !state.equals(world.getGoalLocation())){
                                    for(String successorState : world.getPossibleResultingStates(state, action)){
                                        setTransitionEval(state, action, successorState, 1, positiveContextIndex);
                                        setTransitionEval(state, action, successorState, 0, negativeContextIndex);
                                    }
                                }
                                else{
                                    // If the possible action is the same as the trajectory action (or the action is "ALL"), check the successor state 

                                    for(String successorState : world.getPossibleResultingStates(state, action)){

                                        // If the successor state matches the trajectory successor state
                                        // Maintaining a trajectory
                                        if(stateCorrespondsToProfile(trajectorySuccessorStateProfile, successorState)){

                                            setTransitionEval(state, action, successorState, 1, negativeContextIndex);

                                            
                                        }
                                        else{
                                            // If the action was correct, but the successor is not
                                            // Accidental exit of a trajectory
                                            if(!state.equals(successorState)){
                                                setTransitionEval(state, action, successorState, 1, positiveContextIndex);
                                                setTransitionEval(state, action, successorState, 1, negativeContextIndex);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                       // If the state and successor state correspond to the profile, loops to itself promote the negative context and set the positive to non applicable
                        // Loop
                        for(String successorAction : world.getPossibleActionsForState(state)){
                            for(String successorState2 : world.getPossibleResultingStates(state, successorAction)){
                                if(successorState2.equals(state) && !state.equals(world.getGoalLocation()) && trajectoryStateProfile.isLocation()){
                                    setTransitionEval(state, successorAction, successorState2, Integer.MAX_VALUE, positiveContextIndex);
                                    setTransitionEval(state, successorAction, successorState2, 1, negativeContextIndex);
                                }
                            }
                        } 
                    }
                }
            }
        }
    }
    
}
