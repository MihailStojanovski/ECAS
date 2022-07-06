package agents;

import java.util.HashMap;
import java.util.Map;

import rewards.RewardCalculator;
import rewards.EthicalRewardQuad;
import states.StateRegistry;


import worlds.SelfDrivingCarWorld;


/**
 * SelfDrivingCarAgent
 */
public class SelfDrivingCarAgent {
    
    private SelfDrivingCarWorld world;
    private RewardCalculator rewardCalculator;

    private Map<String, Map<String, Map<String, Double>>> transitionsMap;
    private Map<String, Map<String, Map<String, Map<String, Double>>>> rewardsMap;

    public SelfDrivingCarAgent(SelfDrivingCarWorld world, RewardCalculator rewardCalculator){
        this.world = world;
        this.rewardCalculator = rewardCalculator;
        this.transitionsMap = new HashMap<>();
        this.rewardsMap = new HashMap<>();

        this.calculateAndSaveTransitions();
        this.calculateAndSaveRewards();

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

    private void calculateAndSaveTransitions(){
        
        for( String state : world.getAllStateKeys()){
            Map<String, Map<String,Double>> actionToSPrime = new HashMap<>();
            for(String action : world.getPossibleActionsForState(state)){
                Map<String, Double> sPrimeToTransition= new HashMap<>();
                for(String successorState : world.getPossibleResultingStates(state, action)){
                    Double t = transitionFunction(state, action, successorState);
                    sPrimeToTransition.put(successorState,t);

                }
                actionToSPrime.put(action,sPrimeToTransition);
            }
            transitionsMap.put(state,actionToSPrime);
        }

    }


    private void calculateAndSaveRewards(){
        

        for( String state : world.getAllStateKeys()){
            Map<String, Map<String, Map<String,Double>>> actionToSPrime = new HashMap<>();
            for(String action : world.getPossibleActionsForState(state)){
                Map<String, Map<String,Double>> sPrimeToQuint = new HashMap<>();
                for(String successorState : world.getPossibleResultingStates(state, action)){
                    Map<String, Double> quint = new HashMap<>();

                    EthicalRewardQuad quad = rewardCalculator.getEthicalReward(state, action, successorState);
                    Double task = rewardCalculator.getTaskReward(state, action, successorState);

                    quint.put("NABLA", Double.valueOf(quad.getNabla()));
                    quint.put("TRIANGLE", Double.valueOf(quad.getTriangle()));
                    quint.put("barredNABLA", Double.valueOf(quad.getBarredNabla()));
                    quint.put("barredTRIANGLE", Double.valueOf(quad.getBarredTriangle()));
                    quint.put("TASK",task);

                    sPrimeToQuint.put(successorState,quint);

                }
                actionToSPrime.put(action,sPrimeToQuint);
            }
            rewardsMap.put(state,actionToSPrime);
        }

    }   

    public Double getTransition(String state, String action, String successorState){
        return transitionsMap.get(state).get(action).get(successorState);
    }

    public Map<String,Double> getReward(String state, String action, String successorState){
        return rewardsMap.get(state).get(action).get(successorState);  
    }

}