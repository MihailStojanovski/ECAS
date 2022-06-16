package agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import rewards.EthicalReward;
import rewards.EthicalRewardQuad;
import rewards.Reward;
import states.Location;
import states.Road;
import states.State;
import states.StateRegistry;

import java.util.ArrayList;
import java.util.Arrays;

import worlds.SelfDrivingCarWorld;

import static java.util.Map.entry;

/**
 * SelfDrivingCarAgent
 */
public class SelfDrivingCarAgent {


    private final Double stayingTime = 120.;
    private final Double turningTime = 5.;
    private final Double accelerationRate = 2.;
    private final Double driverErrorPenalty = 3600.;
    
    private SelfDrivingCarWorld world;

    private Map<String, Map<String, Map<String, Double>>> transitionsMap;
    private Map<String, Map<String, Map<String, EthicalRewardQuad>>> rewardsMap;

    public SelfDrivingCarAgent(SelfDrivingCarWorld world){
        this.world = world;

    }



/**
 * The getPossibleActionsForState function returns a set of possible actions for the given state.
 *
 * 
 * @param state Used to Determine the possible actions for a given state.
 * @return A set of possible actions for a given state.
 * 
 */
    public Set<String> getPossibleActionsForState(String state){
        Set<String> possibleActions = new HashSet<>();
        if(world.getLocationStrings().contains(state)){
            possibleActions.add("STAY");
            StringBuilder builder = new StringBuilder();
            for(String rS : world.getRoadStrings()){
                StateRegistry roadStateReg = world.getRegistryFromStateKey(rS);
                if(((Road)roadStateReg.getState()).getFromLocation().equals(state)){
                    builder.append("TURN_ONTO_");
                    builder.append(roadStateReg.getState().getName());
                    possibleActions.add(builder.toString());
                    builder.setLength(0);
                }
            }
        }
        if(world.getRoadStrings().contains(state)){
            if(world.getRegistryFromStateKey(state).getSpeedAdjustment().equals("NONE")){
                for(String a : world.getAccelerateActions().keySet())
                possibleActions.add(a); 
            }
            else{
                possibleActions.add("CRUISE");
            }
        }
        return possibleActions;
    }

/**
 * The getPossibleResultingStates function returns a set of possible resulting states from the given state and action.
 *
 * 
 * @param state The state in which the action is chosen.
 * @param action The action.
 * @return A set of possible resulting states given a state and an action.
 * 
 */
    public Set<String> getPossibleResultingStates(String state, String action){
        Set<String> possibleResultStates = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        // If the state is a location state check if the action is a turn onto action
        if(world.getLocationStrings().contains(state)){
            if(action.equals("STAY")){
                possibleResultStates.add(state);
            }else{
                for(String roadKey : world.getRoadStrings()){
                    StateRegistry roadReg = world.getRegistryFromStateKey(roadKey);
                    if(((Road)roadReg.getState()).getFromLocation().equals(state)){
                        if(roadReg.getSpeedAdjustment().equals("NONE")){
                            builder.append("TURN_ONTO_");
                            builder.append(roadReg.getState().getName());
                            if(action.equals(builder.toString())){
                                possibleResultStates.add(roadKey);
                            }
                            builder.setLength(0);
                        }
                    }  
                }
            }
            
        }else if(world.getRoadStrings().contains(state)){
            StateRegistry roadReg = world.getRegistryFromStateKey(state);
            if(roadReg.getSpeedAdjustment().equals("NONE")){
                if(world.getAccelerateActions().keySet().contains(action)){
                    StateRegistry tempReg = new StateRegistry(roadReg.getState(), world.getAccelerateActions().get(action), roadReg.getPedestrianTraffic());
                    possibleResultStates.add(tempReg.toString());
                }
            }else if(roadReg.getSpeedAdjustment().equals("LOW") || roadReg.getSpeedAdjustment().equals("NORMAL") || roadReg.getSpeedAdjustment().equals("HIGH")){
                if(action.equals("CRUISE")){
                    possibleResultStates.add(((Road)roadReg.getState()).getToLocation());
                }
            }
        }
        return possibleResultStates;
    }

    public Double transitionFunction(String currentState, String action, String successorState){

        StateRegistry currentStateRegistry = world.getRegistryFromStateKey(currentState);
        StateRegistry successorStateRegistry = world.getRegistryFromStateKey(successorState);

        if(world.getLocationStrings().contains(currentState)){
            if(action.equals("STAY")){
                if(successorState.equals(currentState)){
                    return 1.0;
                }
            }
            else if(world.getLocationActions().contains(action) && !action.equals("STAY") ){
                if(world.getRoadStrings().contains(successorState)){
                    if(successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                        return world.getPedestrianTraffic().get(successorStateRegistry.getPedestrianTraffic());
                    }
                }
            }
        }

        if(world.getRoadStrings().contains(currentState)){
            if(world.getRoadStrings().contains(successorState) && world.getAccelerateActions().containsKey(action)){
                if(currentStateRegistry.getSpeedAdjustment().equals("NONE") && !successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                    return 1.0;
                }
            }
            if(world.getLocationStrings().contains(successorState) && action.equals("CRUISE")){
                if(!currentStateRegistry.getSpeedAdjustment().equals("NONE")){
                    return 1.0;
                }
            }
        }
        return 0.0;
    }

    private void calculateAndSaveTransitions(String state, String action, String successorState){
        Double t = transitionFunction(state, action, successorState);
    }


    public Double rewardFunction(String state, String action, String successorState, Reward r){
        
        return 0.0;
    }

    public Double rewardFunction(String state, String action, String successorState, Reward r, boolean isGood){
            Double epsilonPrime = 0.7;
            Double epsilon = 0.7;
            EthicalRewardQuad quad = r.getEthicalReward(state, action, successorState);
            if(isGood){
                return quad.getTriangle() - quad.getBarredTriangle() - epsilonPrime * quad.getBarredTriangle();
            } 
            else{
                return quad.getNabla() - quad.getBarredNabla() + epsilon * quad.getBarredNabla();
            }
    }

    public SelfDrivingCarWorld getWorld() {
        return world;
    }

}