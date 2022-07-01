package valueIterationAlgorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import agents.SelfDrivingCarAgent;
import worlds.SelfDrivingCarWorld;

public class ValueIteration {

    private SelfDrivingCarAgent agent;
    private SelfDrivingCarWorld world;
    private Double alpha;
    private Double convergenceAchieved;
    private Double gamma;
    private Double epsilonBN;
    private Double epsilonN;
    private Double epsilonBT;
    private Double epsilonT;
    
    private Map<String, Map<String, Double>> qHarm = new HashMap<>(); 
    private Map<String, Map<String, Double>> qGood = new HashMap<>(); 
    private Map<String, Map<String, Double>> qTask = new HashMap<>(); 

    
    
    public ValueIteration(  SelfDrivingCarAgent agent, SelfDrivingCarWorld world, 
                            Double alpha, Double convergenceAchieved,  Double gamma, 
                            Double epsilonBN, Double epsilonN, 
                            Double epsilonBT, Double epsilonT){
        this.agent = agent;
        this.world = world;
        this.alpha = alpha;
        this.convergenceAchieved = convergenceAchieved;
        this.gamma = gamma;
        this.epsilonBN = epsilonBN;
        this.epsilonN = epsilonN;
        this.epsilonBT = epsilonBT;
        this.epsilonT = epsilonT;

    }


    public void calculateQValues(){

        // Initialisation to 0 for all state-action pairs
        for(String state : world.getAllStateKeys()){
            Map<String, Double> tempActionHarm = new HashMap<>();
            Map<String, Double> tempActionGood = new HashMap<>();
            Map<String, Double> tempActionTask = new HashMap<>();
            for(String action : world.getPossibleActionsForState(state)){
                tempActionHarm.put(action, 0.);
                tempActionGood.put(action, 0.);
                tempActionTask.put(action, 0.);
            }
            qHarm.put(state, tempActionHarm);
            qGood.put(state, tempActionGood);
            qTask.put(state, tempActionTask);
        }

        // Setting the goal state and action STAY to -Card(S) and Card(S) respectively for harm and good
        qHarm.get(world.getGoalLocation()).replace("STAY", -Double.valueOf(world.getAllStateKeys().size()));
        qGood.get(world.getGoalLocation()).replace("STAY", Double.valueOf(world.getAllStateKeys().size()));

        Double convHarm = Double.MAX_VALUE;
        Double convGood = Double.MAX_VALUE;
        Double convTask = Double.MAX_VALUE;

        Double convMax = Double.MAX_VALUE;


        int counter = 0;

        while( convMax > convergenceAchieved){
            counter++;
            convHarm = 0.;
            convGood = 0.;
            convTask = 0.;
            // Looping all states and possible actions
            for(String state : world.getAllStateKeys()){
                for(String action : world.getPossibleActionsForState(state)){

                    Double tempHarm = qHarm.get(state).get(action); 
                    Double tempGood = qGood.get(state).get(action); 
                    Double tempTask = qTask.get(state).get(action);

                    Double qSumHarm = 0.;
                    Double qSumGood = 0.;
                    Double qSumTask = 0.;
                    Double transitionProbability;

                    // Loop for all the possible resulting states for calculating the sum 
                    for(String stateP : world.getPossibleResultingStates(state, action)){
                        transitionProbability = agent.transitionFunction(state, action, stateP);

                        Double minHarm = Double.MAX_VALUE;
                        Double maxGood = -Double.MAX_VALUE;
                        Double minTask = Double.MAX_VALUE;

                        // Find the min and max value for the possible actions from the resulting state
                        for(String actionP : world.getPossibleActionsForState(stateP)){
                            if(minHarm >= qHarm.get(stateP).get(actionP)){
                                minHarm = qHarm.get(stateP).get(actionP);
                            }
                            if(minTask >= qTask.get(stateP).get(actionP)){
                                minTask = qTask.get(stateP).get(actionP);
                            }
                            if(maxGood <= qGood.get(stateP).get(actionP)){
                                maxGood = qGood.get(stateP).get(actionP);
                            }
                        }// End of loop for possible actions
                        Map<String, Double> quint = agent.getReward(state, action, stateP);
                        Double nabla = quint.get("NABLA");
                        Double barredNabla = quint.get("barredNABLA");
                        Double triangle = quint.get("TRIANGLE");
                        Double barredTriangle = quint.get("barredTRIANGLE");
                        Double taskReward = quint.get("TASK");

                        qSumHarm += transitionProbability * ((1 + epsilonN) * nabla - epsilonBN * barredNabla + gamma * minHarm);
                        qSumGood += transitionProbability * (epsilonT * triangle - (1 + epsilonBT) * barredTriangle + gamma * maxGood);
                        qSumTask += transitionProbability * (taskReward + gamma * minTask);
                        
                    }// End of loop for possible resulting states

                    // Set the new values for the state and action
                    qHarm.get(state).replace(action, qSumHarm);
                    qGood.get(state).replace(action, qSumGood);
                    qTask.get(state).replace(action,qSumTask);

                    convHarm = Math.max(convHarm, Math.abs(tempHarm - qHarm.get(state).get(action)));
                    convGood = Math.max(convGood, Math.abs(tempGood - qGood.get(state).get(action)));
                    convTask = Math.max(convTask, Math.abs(tempTask - qTask.get(state).get(action)));

                    convMax = Math.max(convGood, Math.max(convHarm, convTask));


                }
            }// End of loop of all states and possible actions
        }// End of while

        //System.out.println(qTask);

        // Policy extraction w.r.t. harm
        
        // Map<String, List<String>> policyHarm = new HashMap<>();
        // for(String state : world.getAllStateKeys()){
        //     List<String> stateActionsHarm = new ArrayList<>();
        //     Double minAction = Double.MAX_VALUE;
        //     for(String action : world.getPossibleActionsForState(state)){
        //         Double qHarmValue = qHarm.get(state).get(action);
        //         if(stateActionsHarm.isEmpty()){
        //             stateActionsHarm.add(action);
        //             minAction = qHarmValue;
        //         }else if(minAction.equals(qHarmValue)){
        //             stateActionsHarm.add(action);
        //         }else if(minAction > qHarmValue){
        //             stateActionsHarm.clear();
        //             stateActionsHarm.add(action);
        //             minAction = qHarmValue;
        //         }
        //     }
        //     policyHarm.put(state, stateActionsHarm);
        // }
        

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


        // Task Policy Extraction
        
    //     Map<String, List<String>> policyTask = new HashMap<>();
    //     for(String state : world.getAllStateKeys()){
    //         List<String> stateActionsTask = new ArrayList<>();
    //         Double minAction = Double.MAX_VALUE;
    //         for(String action : world.getPossibleActionsForState(state)){
    //             Double qTaskValue = qTask.get(state).get(action);
    //             if(stateActionsTask.isEmpty()){
    //                 stateActionsTask.add(action);
    //                 minAction = qTaskValue;
    //             }else if(minAction.equals(qTaskValue)){
    //                 stateActionsTask.add(action);
    //             }else if(minAction > qTaskValue){
    //                 stateActionsTask.clear();
    //                 stateActionsTask.add(action);
    //                 minAction = qTaskValue;
    //             }
    //         }
    //         policyTask.put(state, stateActionsTask);
    //     }
        
    //     System.out.println(counter);
    //     return policyTask;
    }// End of calculateQValues

    public Map<String, Double> getVforPolicy(Map<String, List<String>> policy){
       
        Map<String, Double> v = new HashMap<>();
        for(String state : world.getAllStateKeys()){
            v.put(state, 0.);
        }

        Double conv = Double.MAX_VALUE;
        Double sum = 0.;
        while(conv > convergenceAchieved){
           conv = 0.;
            for(String state : world.getAllStateKeys()){
                Double temp = v.get(state);
                Double maxSum = -Double.MAX_VALUE;
                for(String action : world.getPossibleActionsForState(state)){
                    sum = 0.;
                    for(String statePrime : world.getPossibleResultingStates(state, action)){
                        sum += agent.getTransition(state, action, statePrime) * (agent.getReward(state, action, statePrime).get("TASK") + gamma*v.get(statePrime));
                        if(state.equals("COLLEGE_STREET_REVERSED_CITY_HIGH_HEAVY") || statePrime.equals("COLLEGE_STREET_REVERSED_CITY_HIGH_HEAVY")){
                        }
                    }
                    if(maxSum < sum){
                        maxSum = sum;
                    }
                }
                conv = Math.max(conv, Math.abs(temp - v.get(state)));

                v.replace(state, maxSum);
            }
        }
        return v;
    }


    public Map<String, Map<String, Double>> getqGood() {
        return qGood;
    }

    public Map<String, Map<String, Double>> getqHarm() {
        return qHarm;
    }

    public Map<String, Map<String, Double>> getqTask() {
        return qTask;
    }


    
}
