package valueIterationAlgorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import agents.SelfDrivingCarAgent;
import rewards.Reward;

public class ValueIteration {

    private SelfDrivingCarAgent agent;
    private Reward ethicalRewardCalculator;
    private Double alpha;
    private Double convergenceAchieved;
    private Double gamma;
    
    private Map<String, Map<String, Double>> qHarm = new HashMap<>(); 
    private Map<String, Map<String, Double>> qGood = new HashMap<>(); 
    
    
    public ValueIteration(SelfDrivingCarAgent agent, Reward ethicalRewardCalculator, Double alpha, Double convergenceAchieved,  Double gamma){
        this.agent = agent;
        this.ethicalRewardCalculator = ethicalRewardCalculator;
        this.alpha = alpha;
        this.convergenceAchieved = convergenceAchieved;
        this.gamma = gamma;
    }


    public Map<String, List<String>> getPolicy(){

        // Initialisation to 0 for all state-action pairs
        for(String state : agent.getAllStateKeys()){
            Map<String, Double> tempActionHarm = new HashMap<>();
            Map<String, Double> tempActionGood = new HashMap<>();
            for(String action : agent.getPossibleActionsForState(state)){
                tempActionHarm.put(action, 0.);
                tempActionGood.put(action, 0.);
            }
            qHarm.put(state, tempActionHarm);
            qGood.put(state, tempActionGood);
        }

        // Setting the goal state and action STAY to -Card(S) and Card(S) respectively for harm and good
        qHarm.get(agent.getGoalState()).replace("STAY", -Double.valueOf(agent.getAllStateKeys().size()));
        qGood.get(agent.getGoalState()).replace("STAY", Double.valueOf(agent.getAllStateKeys().size()));

        Double convHarm = Double.MAX_VALUE;
        Double convGood = Double.MAX_VALUE;

        int counter = 0;

        // Start of while
        while( (alpha * convHarm + (1 - alpha) * convGood) > convergenceAchieved){
            counter++;
            convHarm = 0.;
            convGood = 0.;
            // Looping all states and possible actions
            for(String state : agent.getAllStateKeys()){
                for(String action : agent.getPossibleActionsForState(state)){

                    Double tempHarm = qHarm.get(state).get(action); 
                    Double tempGood = qGood.get(state).get(action); 

                    Double qSumHarm = 0.;
                    Double qSumGood = 0.;
                    Double transitionProbability;

                    // Loop for all the possible resulting states for calculating the sum 
                    for(String stateP : agent.getPossibleResultingStates(state, action)){
                        transitionProbability = agent.transitionFunction(state, action, stateP);

                        Double minHarm = Double.MAX_VALUE;
                        Double maxGood = -Double.MAX_VALUE;

                        // Find the min and max value for the possible actions from the resulting state
                        for(String actionP : agent.getPossibleActionsForState(stateP)){
                            if(minHarm >= qHarm.get(stateP).get(actionP)){
                                minHarm = qHarm.get(stateP).get(actionP);
                            }
                            if(maxGood <= qGood.get(stateP).get(actionP)){
                                maxGood = qGood.get(stateP).get(actionP);
                            }
                        }// End of loop for possible actions 

                        qSumHarm += transitionProbability * (agent.rewardFunction(state, action, stateP, ethicalRewardCalculator, false) + gamma * minHarm);
                        qSumGood += transitionProbability * (agent.rewardFunction(state, action, stateP, ethicalRewardCalculator, true) + gamma * maxGood);
                    }// End of loop for possible resulting states

                    // Set the new values for the state and action
                    qHarm.get(state).replace(action, qSumHarm);
                    qGood.get(state).replace(action, qSumGood);

                    convHarm = Math.max(convHarm, Math.abs(tempHarm - qHarm.get(state).get(action)));
                    convGood = Math.max(convGood, Math.abs(tempGood - qGood.get(state).get(action)));

                }
            }// End of loop of all states and possible actions
        }// End of while


        // OLD Policy extraction w.r.t. harm
    
        Map<String, List<String>> policyHarm = new HashMap<>();
        for(String state : agent.getAllStateKeys()){
            List<String> stateActionsHarm = new ArrayList<>();
            Double minAction = Double.MAX_VALUE;
            for(String action : agent.getPossibleActionsForState(state)){
                Double qHarmValue = qHarm.get(state).get(action);
                if(stateActionsHarm.isEmpty()){
                    stateActionsHarm.add(action);
                    minAction = qHarmValue;
                }else if(minAction.equals(qHarmValue)){
                    stateActionsHarm.add(action);
                }else if(minAction > qHarmValue){
                    stateActionsHarm.clear();
                    stateActionsHarm.add(action);
                    minAction = qHarmValue;
                }
            }
            policyHarm.put(state, stateActionsHarm);
        }


        // Policy extraction w.r.t. good
        // Map<String, List<String>> policyGood = new HashMap<>();
        // for(Entry<String, List<String>> e : policyHarm.entrySet()){
        //     List<String> stateActionsGood = new ArrayList<>();
        //     Double maxAction = -Double.MAX_VALUE;
        //     for(String action : e.getValue()){
        //         Double qGoodValue = qGood.get(e.getKey()).get(action);
        //         if(stateActionsGood.isEmpty()){
        //             stateActionsGood.add(action);
        //             maxAction = qGoodValue;
        //         }else if(maxAction.equals(qGoodValue)){
        //             stateActionsGood.add(action);
        //         }else if(maxAction < qGoodValue){
        //             stateActionsGood.clear();
        //             stateActionsGood.add(action);
        //             maxAction = qGoodValue;
        //         }
        //     }
        //     policyGood.put(e.getKey(), stateActionsGood);
        // }

        System.out.println(counter);
        return policyHarm;
    }


    
}
