package valueIterationAlgorithms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agents.SelfDrivingCarAgent;
import worlds.SelfDrivingCarWorld;

public class SuccessiveValueIteration {
    
    private SelfDrivingCarAgent agent;
    private SelfDrivingCarWorld world;
    private Double convergenceAchieved;
    private Double gamma;
    private Double epsilonBN;
    private Double epsilonN;
    private Double epsilonBT;
    private Double epsilonT;

    private Map<String, Map<String, Double>> qHarm = new HashMap<>(); 
    private Map<String, Map<String, Double>> qGood = new HashMap<>();
    private Map<String, Map<String, Double>> qTask = new HashMap<>(); 

    private MinimizingPolicyExtractor minPolicyExtractor = new MinimizingPolicyExtractor();
    private MaximizingPolicyExtractor maxPolicyExtractor = new MaximizingPolicyExtractor();

    public SuccessiveValueIteration(SelfDrivingCarAgent agent, SelfDrivingCarWorld world, 
                                    Double convergenceAchieved, Double gamma, 
                                    Double epsilonBN, Double epsilonN, 
                                    Double epsilonBT, Double epsilonT){
        this.agent = agent;
        this.world = world;
        this.convergenceAchieved = convergenceAchieved;
        this.gamma = gamma;
        this.epsilonBN = epsilonBN;
        this.epsilonN = epsilonN;
        this.epsilonBT = epsilonBT;
        this.epsilonT = epsilonT;
    }

    public Map<String, List<String>> calculatePolicyForSuccessiveValueIteration(){
        Map<String, List<String>> finalPolicy = new HashMap<>();
        Map<String, List<String>> genericPolicyTarget = world.getMapOfStatesAndActions();
        
        calculateQHarmValues(genericPolicyTarget);
        Map<String, List<String>> harmPolicy = minPolicyExtractor.extract(genericPolicyTarget, qHarm);
        
        // System.out.println("GAS_STATION harm qValues: " + qHarm.get("GAS_STATION"));
        // System.out.println("GAS_STATION harm policy: " + harmPolicy.get("GAS_STATION"));

        // System.out.println("POST_OFFICE harm qValues: " + qHarm.get("POST_OFFICE"));
        // System.out.println("POST_OFFICE harm policy: " + harmPolicy.get("POST_OFFICE"));

        // BufferedWriter writer;
        // try {
        //     writer = new BufferedWriter(new FileWriter("policy.txt", true));

        // writer.append("\nHARM\n");
        // for(Map.Entry<String, List<String>> entry : harmPolicy.entrySet()){
        //     writer.append(entry.toString() + "\n");
        // }
        
        
        calculateQGoodValues(harmPolicy);
        Map<String, List<String>> goodPolicy = maxPolicyExtractor.extract(harmPolicy, qGood);
        
        // System.out.println("GAS_STATION good qValues: " + qGood.get("GAS_STATION"));
        // System.out.println("GAS_STATION harmGood policy: " + goodPolicy.get("GAS_STATION"));

        // System.out.println("POST_OFFICE good qValues: " + qGood.get("POST_OFFICE"));
        // System.out.println("POST_OFFICE harmGood policy: " + goodPolicy.get("POST_OFFICE"));

        // writer.append("\nGOOD\n");
        // for(Map.Entry<String, List<String>> entry : goodPolicy.entrySet()){
        //     writer.append(entry.toString() + "\n");
        // }
        

        calculateQTaskValues(goodPolicy);
        finalPolicy = minPolicyExtractor.extract(goodPolicy, qTask);

        // System.out.println("GAS_STATION task qValues: " + qTask.get("GAS_STATION"));
        // System.out.println("GAS_STATION harmGoodTask policy: " + finalPolicy.get("GAS_STATION"));

        // System.out.println("POST_OFFICE task qValues: " + qTask.get("POST_OFFICE"));
        // System.out.println("POST_OFFICE harmGoodTask policy: " + finalPolicy.get("POST_OFFICE"));
        
        // writer.append("\nTASK\n");
        // for(Map.Entry<String, List<String>> entry : finalPolicy.entrySet()){
        //     writer.append(entry.toString() + "\n");
        // }


    //     writer.close();
    // } catch (IOException e) {
    //     e.printStackTrace();
    // }
        return finalPolicy;
    }


    public void calculateQHarmValues(Map<String, List<String>> policy){



        // Initialisation to 0 for all state-action pairs
        for(String state : policy.keySet()){

            Map<String, Double> tempActionHarm = new HashMap<>();

            for(String action : policy.get(state)){
                tempActionHarm.put(action, 0D);
            }
            qHarm.put(state, tempActionHarm);
        }

        // qHarm.get(world.getGoalLocation()).replace("STAY", -Double.valueOf(world.getAllStateKeys().size()));


        Double convHarm = Double.MAX_VALUE;

        while( convHarm > convergenceAchieved){
            
            convHarm = 0.;
            // Looping all states and possible actions
            for(String state : policy.keySet()){
                for(String action : policy.get(state)){

                    Double tempHarm = qHarm.get(state).get(action); 

                    Double qSumHarm = 0.;
                    Double transitionProbability;

                    // Loop for all the possible resulting states for calculating the sum 
                    for(String stateP : world.getPossibleResultingStates(state, action)){
                        transitionProbability = agent.transitionFunction(state, action, stateP);

                        Double minHarm = Double.MAX_VALUE;

                        // Find the min value for the possible actions from the resulting state
                        for(String actionP : policy.get(stateP)){
                            if(minHarm >= qHarm.get(stateP).get(actionP)){
                                minHarm = qHarm.get(stateP).get(actionP);
                            }
                        }// End of loop for possible actions
                        Map<String, Double> quint = agent.getReward(state, action, stateP);
                        Double nabla = quint.get("NABLA");
                        Double barredNabla = quint.get("barredNABLA");

                        qSumHarm += transitionProbability * ((1 + epsilonN) * nabla - epsilonBN * barredNabla + gamma * minHarm);
                        
                    }// End of loop for possible resulting states

                    // Set the new values for the state and action
                    qHarm.get(state).replace(action, qSumHarm);

                    convHarm = Math.max(convHarm, Math.abs(tempHarm - qHarm.get(state).get(action)));

                }
            }// End of loop of all states and possible actions
        }// End of while
    }// End of calculateQHarmValues


    public void calculateQGoodValues(Map<String, List<String>> policy){

         

        // Initialisation to 0 for all state-action pairs
        for(String state : policy.keySet()){
            Map<String, Double> tempActionGood = new HashMap<>();
            for(String action : policy.get(state)){
                tempActionGood.put(action, 0D);
            }
            qGood.put(state, tempActionGood);
        }

        // qGood.get(world.getGoalLocation()).replace("STAY", Double.valueOf(world.getAllStateKeys().size()));

        Double convGood = Double.MAX_VALUE;

        while( convGood > convergenceAchieved){

            convGood = 0.;
            // Looping all states and possible actions
            for(String state : policy.keySet()){
                for(String action : policy.get(state)){

                    Double tempGood = qGood.get(state).get(action); 

                    Double qSumGood = 0.;
                    Double transitionProbability;

                    // Loop for all the possible resulting states for calculating the sum 
                    for(String stateP : world.getPossibleResultingStates(state, action)){
                        transitionProbability = agent.transitionFunction(state, action, stateP);

                        Double maxGood = -Double.MAX_VALUE;

                        // Find the min and max value for the possible actions from the resulting state
                        for(String actionP : policy.get(stateP)){
                            if(maxGood <= qGood.get(stateP).get(actionP)){
                                maxGood = qGood.get(stateP).get(actionP);
                            }
                        }// End of loop for possible actions
                        Map<String, Double> quint = agent.getReward(state, action, stateP);
                        Double triangle = quint.get("TRIANGLE");
                        Double barredTriangle = quint.get("barredTRIANGLE");

                        qSumGood += transitionProbability * (epsilonT * triangle - (1 + epsilonBT) * barredTriangle + gamma * maxGood);
                        
                    }// End of loop for possible resulting states

                    // Set the new values for the state and action
                    qGood.get(state).replace(action, qSumGood);

                    convGood = Math.max(convGood, Math.abs(tempGood - qGood.get(state).get(action)));

                }
            }// End of loop of all states and possible actions
        }// End of while
    }// End of calculateQGoodValues

    public void calculateQTaskValues(Map<String, List<String>> policy){

        
        // Initialisation to 0 for all state-action pairs
        for(String state : policy.keySet()){
            Map<String, Double> tempActionTask = new HashMap<>();
            for(String action : policy.get(state)){
                tempActionTask.put(action, 0D);
            }
            qTask.put(state, tempActionTask);
        }

        Double convTask = Double.MAX_VALUE;

        while( convTask > convergenceAchieved){

            convTask = 0.;
            // Looping all states and possible actions
            for(String state : policy.keySet()){
                for(String action : policy.get(state)){

                    Double tempTask = qTask.get(state).get(action);

                    Double qSumTask = 0.;
                    Double transitionProbability;

                    // Loop for all the possible resulting states for calculating the sum 
                    for(String stateP : world.getPossibleResultingStates(state, action)){
                        transitionProbability = agent.transitionFunction(state, action, stateP);

                        Double minTask = Double.MAX_VALUE;

                        // Find the min and max value for the possible actions from the resulting state
                        for(String actionP : policy.get(stateP)){
                            if(minTask >= qTask.get(stateP).get(actionP)){
                                minTask = qTask.get(stateP).get(actionP);
                            }
                        }// End of loop for possible actions
                        Map<String, Double> quint = agent.getReward(state, action, stateP);
                        Double taskReward = quint.get("TASK");
                    
                        qSumTask += transitionProbability * (taskReward + gamma * minTask);
                        
                    }// End of loop for possible resulting states

                    // Set the new values for the state and action

                    qTask.get(state).replace(action,qSumTask);


                    convTask = Math.max(convTask, Math.abs(tempTask - qTask.get(state).get(action)));


                }
            }// End of loop of all states and possible actions
        }// End of while
    }// End of calculateQTaskValues

    public Map<String, Double> calculateAndReturnVforPolicy(Map<String, List<String>> policy){
       
        Map<String, Double> v = new HashMap<>();
        for(String state : policy.keySet()){
            v.put(state, 0.);
        }

        Double conv = Double.MAX_VALUE;
        while(conv > convergenceAchieved){

           conv = 0.;

            for(String state : policy.keySet()){
                Double temp = v.get(state);
                Double minSum = Double.MAX_VALUE;
                for(String action : policy.get(state)){
                    Double sum = 0.;
                    for(String statePrime : world.getPossibleResultingStates(state, action)){
                        
                        Double transitionP = agent.getTransition(state, action, statePrime);
                        Double reward = agent.getReward(state, action, statePrime).get("TASK");

                        sum += transitionP * (reward + gamma * v.get(statePrime));
                    }
                    if(minSum > sum){
                        minSum = sum;
                    }
                    
                    v.replace(state, minSum);
                    conv = Math.max(conv, Math.abs(temp - v.get(state)));
                }
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
