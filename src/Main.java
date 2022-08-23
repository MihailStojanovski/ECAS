import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.nimbus.State;

import valueIterationAlgorithms.PolicyExtractor;
import valueIterationAlgorithms.ValueIteration;
import agents.SelfDrivingCarAgent;
import factories.EvaluationFactory;
import factories.StateProfile;
import factories.VirtueEthicsData;
import parsers.WorldMapParser;
import rewards.RewardCalculator;
import worlds.SelfDrivingCarWorld;

public class Main {
    public static void main(String[] args) {


        // Parsing map to set up the world, and setting start and goal locations
        WorldMapParser parser = new WorldMapParser();
        SelfDrivingCarWorld parsedWorld = parser.getWorldFromJsonMap("maps/SelfDrivingCarMap.json");
        parsedWorld.setStartLocation("HOME");
        parsedWorld.setGoalLocation("OFFICE");

        PolicyExtractor policyExtractor = new PolicyExtractor();

        List<Integer> context = new ArrayList<>();
        context.add(0);
        context.add(0);

        // ---------------------------------- DCT ----------------------------------
        System.out.println("---------------------------------- DCT ----------------------------------");

        // Forbidden state profiles for DCT as explained in the original paper Hazardous(H) and Inconsiderate(I)
        StateProfile hazardous = new StateProfile("ALL", "ALL", "HIGH", "ALL",false);
        StateProfile inconsiderate = new StateProfile("ALL", "ALL", "NORMAL", "HEAVY",false);
        
        List<StateProfile> profileListHazardous = new ArrayList<>();
        profileListHazardous.add(hazardous);

        List<StateProfile> profileListHazardousAndInconsiderate = new ArrayList<>();
        profileListHazardousAndInconsiderate.add(hazardous);
        profileListHazardousAndInconsiderate.add(inconsiderate);

        // Hazardous
        // System.out.println("Hazardous DCT for task 1 : ");
        // parsedWorld.setGoalLocation("DINER");
        // showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor);

        // System.out.println("Hazardous DCT for task 2 : ");
        // parsedWorld.setGoalLocation("OFFICE");
        // showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor);

        // System.out.println("Hazardous DCT for task 3 : ");
        // parsedWorld.setGoalLocation("PARK");
        // showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor);


        // Hazardous and Inconsiderate

        // System.out.println("Hazardous & Inconsiderate DCT for task 1 : ");
        // parsedWorld.setGoalLocation("DINER");
        // showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor);

        // System.out.println("Hazardous & Inconsiderate DCT for task 2 : ");
        // parsedWorld.setGoalLocation("OFFICE");
        // showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor);

        // System.out.println("Hazardous & Inconsiderate DCT for task 3 : ");
        // parsedWorld.setGoalLocation("PARK");
        // showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor);

        // ---------------------------------- PFD ----------------------------------
        System.out.println("---------------------------------- PFD ----------------------------------");

        // PFD definitions of duties
        Map<Integer, Map<StateProfile, String>> contextIndexToDuties = new HashMap<>();

        StateProfile smoothOperationState = new StateProfile("ALL", "ALL", "NONE", "LIGHT",false);
        String smoothOperationAction = "TO_HIGH || TO_NORMAL";
        Map<StateProfile, String> smoothOperationDuty = new HashMap<>();
        smoothOperationDuty.put(smoothOperationState, smoothOperationAction);
        contextIndexToDuties.put(0,smoothOperationDuty);

        StateProfile carefulOperationState = new StateProfile("ALL", "ALL", "NONE", "HEAVY",false);
        String carefulOperationAction = "TO_LOW";
        Map<StateProfile, String> carefulOperationDuty = new HashMap<>();
        carefulOperationDuty.put(carefulOperationState, carefulOperationAction);
        contextIndexToDuties.put(1,carefulOperationDuty);


        // System.out.println("0 tolerance PFD for task 1 : ");
        // parsedWorld.setGoalLocation("DINER");
        // showLossPFD(context, contextIndexToDuties, parsedWorld, policyExtractor);

        // System.out.println("0 tolerance PFD for task 2 : ");
        // parsedWorld.setGoalLocation("OFFICE");
        // showLossPFD(context, contextIndexToDuties, parsedWorld, policyExtractor);

        // System.out.println("0 tolerance PFD for task 3 : ");
        // parsedWorld.setGoalLocation("PARK");
        // showLossPFD(context, contextIndexToDuties, parsedWorld, policyExtractor);

        // ---------------------------------- VE ----------------------------------
        System.out.println("---------------------------------- VE ----------------------------------");

        List<Integer> contextVE = new ArrayList<>(Arrays.asList(0,1));

        // MGT with location

        // StateProfile trajectoryState = new StateProfile("ALL", "ALL", "ALL", "ALL");
        // String trajectoryAction = "ALL";
        // StateProfile trajectorySuccessorState = new StateProfile("TOWN_HALL", "ALL", "ALL", "ALL");

        StateProfile trajectoryState = new StateProfile("ALL", "ALL", "NONE", "LIGHT",false);
        String trajectoryAction = "TO_NORMAL";
        StateProfile trajectorySuccessorState = new StateProfile("ALL", "ALL", "NORMAL", "LIGHT",false);

        VirtueEthicsData cautious_1 = new VirtueEthicsData(0, 1, trajectoryState, trajectoryAction, trajectorySuccessorState,true);

        StateProfile trajectoryStateC2 = new StateProfile("ALL", "ALL", "NONE", "HEAVY",false);
        String trajectoryActionC2 = "TO_LOW";
        StateProfile trajectorySuccessorStateC2 = new StateProfile("ALL", "ALL", "LOW", "HEAVY",false);

        VirtueEthicsData cautious_2 = new VirtueEthicsData(2, 3, trajectoryStateC2, trajectoryActionC2, trajectorySuccessorStateC2,true);

        StateProfile trajectoryStateP = new StateProfile("ALL", "ALL", "ALL", "ALL",true);
        String trajectoryActionP = "ALL";
        StateProfile trajectorySuccessorStateP = new StateProfile("ALL", "HIGHWAY", "ALL", "ALL",false);
        VirtueEthicsData proactive = new VirtueEthicsData(0, 1, trajectoryStateP, trajectoryActionP, trajectorySuccessorStateP,false);


        // Check the context before executing
        List<VirtueEthicsData> dataListVE = new ArrayList<>(Arrays.asList(proactive));

        System.out.println("VE for task 1 : ");
        parsedWorld.setGoalLocation("OFFICE");
        showLossVE(contextVE, dataListVE, parsedWorld, policyExtractor);
        
    }

    public static void showLossDCT(List<Integer> context, List<StateProfile> profileList, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor){
        
        // Evaluation factory setup
        EvaluationFactory evaluationFactoryDCT = new EvaluationFactory(context, parsedWorld);

        // Create evaluations for DCT with H and I
        evaluationFactoryDCT.createDCTevals(profileList);
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalDCT = evaluationFactoryDCT.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEvalDCT = evaluationFactoryDCT.getStateEval();

        RewardCalculator rewardCalculatorDCT = new RewardCalculator(context, stateActionEvalDCT, stateEvalDCT, parsedWorld);
        
        SelfDrivingCarAgent agentDCT = new SelfDrivingCarAgent(parsedWorld,rewardCalculatorDCT);

        // If gamma is 0.9, even with convergence choosing the maximum between the three criteria when at the state HOME the agent chooses to go to the wrong street (MATOON_STREET_REVERSED)
        // Decreasing the convergenceAchieved also gives us the correct entry (with a gamma of 0.9)
        // In the morality.js code the discount factor(gamma) is 0.99 and epsilon(convergenceAchieved) is 0.001
        ValueIteration valueIterationDCT = new ValueIteration(agentDCT, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

        valueIterationDCT.calculateQValues();
        Map<String, Map<String, Double>> qHarmDCT = valueIterationDCT.getqHarm();
        Map<String, Map<String, Double>> qTaskDCT = valueIterationDCT.getqTask();

        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();
        

        Map<String, List<String>> policyHarmDCT = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmDCT);

        Map<String, List<String>> policyHarmTaskDCT = policyExtractor.minimizingExtractor(policyHarmDCT, qTaskDCT);

        Map<String, List<String>> policyTaskDCT = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskDCT); 
        
        Map<String, Double> vTaskDCT = valueIterationDCT.calculateAndReturnVforPolicy(policyTaskDCT);
        Map<String, Double> vHarmDCT = valueIterationDCT.calculateAndReturnVforPolicy(policyHarmTaskDCT);

        // System.out.println("[TASK]");
        // for(Map.Entry<String, List<String>> e : policyTaskDCT.entrySet()){
        //     System.out.println(e);
        // }

        // System.out.println("[HARM+TASK]");
        // for(Map.Entry<String, List<String>> e : policyHarmTaskDCT.entrySet()){
        //     System.out.println(e);
        // }

        Double maxDiffDCT = -Double.MAX_VALUE;
        String diffStateDCT = "";
        for(String state : parsedWorld.getAllStateKeys()){
            // System.out.println("State : " + state + " vHarm : " + vHarm.get(state) + " vTask : " + vTask.get(state));
            if(maxDiffDCT < vHarmDCT.get(state) - vTaskDCT.get(state)){
                maxDiffDCT = vHarmDCT.get(state) - vTaskDCT.get(state);
                diffStateDCT = state;
            }
        }
        System.out.println("vTask[" + diffStateDCT +"] : " + vTaskDCT.get(diffStateDCT) + "; vHarm["+ diffStateDCT + "] : " + vHarmDCT.get(diffStateDCT));
        System.out.println("Difference : " + maxDiffDCT + " ; in the state : " + diffStateDCT);

        Double lossDCT = (maxDiffDCT / vTaskDCT.get(diffStateDCT)) * 100;
        System.out.println("Loss % : " + lossDCT);

        System.out.println("vTask[SCHOOL] : " + vTaskDCT.get("SCHOOL") + ", vHarm[SCHOOL] : " + vHarmDCT.get("SCHOOL"));
        Double dif = vHarmDCT.get("SCHOOL") - vTaskDCT.get("SCHOOL");
        System.out.println((dif / vTaskDCT.get("SCHOOL")) * 100);

        System.out.println("Iteration: " + valueIterationDCT.getCounter());

    }

    public static void showLossPFD(List<Integer> context, Map<Integer, Map<StateProfile, String>> contextToDuties, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor){
        
        EvaluationFactory evaluationFactoryPFD = new EvaluationFactory(context, parsedWorld);

        evaluationFactoryPFD.createPFDevals(contextToDuties);
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalPFD = evaluationFactoryPFD.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEvalPFD = evaluationFactoryPFD.getStateEval();

        RewardCalculator rewardCalculatorPFD = new RewardCalculator(context, stateActionEvalPFD, stateEvalPFD, parsedWorld);
        
        SelfDrivingCarAgent agentPFD = new SelfDrivingCarAgent(parsedWorld,rewardCalculatorPFD);

        ValueIteration valueIterationPFD = new ValueIteration(agentPFD, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

        valueIterationPFD.calculateQValues();

        Map<String, Map<String, Double>> qHarmPFD = valueIterationPFD.getqHarm();
        Map<String, Map<String, Double>> qTaskPFD = valueIterationPFD.getqTask();
        
        
        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

        Map<String, List<String>> policyHarmPFD = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmPFD);

        Map<String, List<String>> policyHarmTaskPFD = policyExtractor.minimizingExtractor(policyHarmPFD, qTaskPFD);

        Map<String, List<String>> policyTaskPFD = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskPFD); 
        
        Map<String, Double> vTaskPFD = valueIterationPFD.calculateAndReturnVforPolicy(policyTaskPFD);
        Map<String, Double> vHarmPFD = valueIterationPFD.calculateAndReturnVforPolicy(policyHarmTaskPFD);

        Double maxDiffPFD = -Double.MAX_VALUE;
        String diffStatePFD = "";
        for(String state : parsedWorld.getAllStateKeys()){
            // System.out.println("State : " + state + " vHarm : " + vHarm.get(state) + " vTask : " + vTask.get(state));
            if(maxDiffPFD < vHarmPFD.get(state) - vTaskPFD.get(state)){
                maxDiffPFD = vHarmPFD.get(state) - vTaskPFD.get(state);
                diffStatePFD = state;
            }
        }


        System.out.println("vTask[" + diffStatePFD +"] : " + vTaskPFD.get(diffStatePFD) + "; vHarm["+ diffStatePFD + "] : " + vHarmPFD.get(diffStatePFD));
        System.out.println("Difference : " + maxDiffPFD + " ; in the state : " + diffStatePFD);

        Double lossPFD = (maxDiffPFD / vTaskPFD.get(diffStatePFD)) * 100;
        System.out.println("Loss % : " + lossPFD);

        System.out.println("vTask[HOME] : " + vTaskPFD.get("HOME") + ", vHarm[HOME] : " + vHarmPFD.get("HOME"));

        System.out.println("Iteration: " + valueIterationPFD.getCounter());

    }

    public static void showLossVE(List<Integer> context, List<VirtueEthicsData> dataListVE, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor){
        
        EvaluationFactory evaluationFactoryVE = new EvaluationFactory(context, parsedWorld);

        evaluationFactoryVE.createVEevals(dataListVE);
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalVE = evaluationFactoryVE.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEvalVE = evaluationFactoryVE.getStateEval();

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter("outputStateAction.txt", false));
        
            for(Map.Entry<Integer,Map<String,Map<String,Integer>>> hasContextEnrty : stateActionEvalVE.entrySet()){
                writer.append("CONTEXT: " + hasContextEnrty.getKey() + "{\n");
                Map<String,Map<String,Integer>> hasStateMap = hasContextEnrty.getValue();
                for(Map.Entry<String,Map<String,Integer>> hasStateEntry : hasStateMap.entrySet()){
                    writer.append("STATE: " + hasStateEntry.getKey() + " {\n ");
                    Map<String,Integer> hasActionMap = hasStateEntry.getValue();
                    for(Map.Entry<String,Integer> hasActionEntry : hasActionMap.entrySet()){
                        writer.append("ACTION: " + hasActionEntry.getKey() + ", WORTH: " + hasActionEntry.getValue() + "\n");
                    }
                    writer.append("END STATE: " + hasStateEntry.getKey() + " }\n ");
                }
                writer.append("END CONTEXT: " + hasContextEnrty.getKey() + "}\n");
            }


            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedWriter writer2;
        try {
            writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
        
            for(Map.Entry<Integer,Map<String,Integer>> hasContextEnrty : stateEvalVE.entrySet()){
                writer2.append("CONTEXT: " + hasContextEnrty.getKey() + "{\n");
                Map<String,Integer> hasStateMap = hasContextEnrty.getValue();
                for(Map.Entry<String,Integer> hasStateEntry : hasStateMap.entrySet()){
                    writer2.append("STATE: " + hasStateEntry.getKey() + " , WORTH: " + hasStateEntry.getValue() + "\n");
                }
                writer2.append("END CONTEXT: " + hasContextEnrty.getKey() + "}\n");
            }


            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
            


        

        RewardCalculator rewardCalculatorVE = new RewardCalculator(context, stateActionEvalVE, stateEvalVE, parsedWorld);
        
        SelfDrivingCarAgent agentVE = new SelfDrivingCarAgent(parsedWorld,rewardCalculatorVE);

        ValueIteration valueIterationVE = new ValueIteration(agentVE, parsedWorld, 0.001, 0.99, 1. , 1. , 1., 1.);

        valueIterationVE.calculateQValues();

        Map<String, Map<String, Double>> qHarmVE = valueIterationVE.getqHarm();
        Map<String, Map<String, Double>> qGoodVE = valueIterationVE.getqGood();
        Map<String, Map<String, Double>> qTaskVE = valueIterationVE.getqTask();

        for(Map.Entry<String, Map<String,Double>> e : qHarmVE.entrySet()){
            if(e.getKey().equals("GAS_STATION")){
                System.out.println(e);

            }
            
        }

        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

        Map<String, List<String>> policyHarmVE = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmVE);
        // Policy print
        for(Map.Entry<String, List<String>> e : policyHarmVE.entrySet()){
            if(e.getKey().equals("GAS_STATION")){
                System.out.println(e);
            }
        }

        Map<String, List<String>> policyHarmGoodVE = policyExtractor.maximizingExtractor(policyHarmVE, qGoodVE);

        // Policy print
        // for(Map.Entry<String, List<String>> e : policyHarmGoodVE.entrySet()){
        //     System.out.println(e);
        // }
        
        Map<String, List<String>> policyHarmGoodTaskVE = policyExtractor.minimizingExtractor(policyHarmGoodVE, qTaskVE);
        

        Map<String, List<String>> policyTaskVE = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskVE); 
        
        Map<String, Double> vTaskVE = valueIterationVE.calculateAndReturnVforPolicy(policyTaskVE);
        Map<String, Double> vEthicsVE = valueIterationVE.calculateAndReturnVforPolicy(policyHarmGoodTaskVE);


        Double maxDiffVE = -Double.MAX_VALUE;
        String diffStateVE = "";
        for(String state : parsedWorld.getAllStateKeys()){
            // System.out.println("State : " + state + " vHarm : " + vHarm.get(state) + " vTask : " + vTask.get(state));
            if(maxDiffVE < vEthicsVE.get(state) - vTaskVE.get(state)){
                maxDiffVE = vEthicsVE.get(state) - vTaskVE.get(state);
                diffStateVE = state;
            }
        }


        System.out.println("Most difference in state: Task[" + diffStateVE +"] : " + vTaskVE.get(diffStateVE) + "; VE["+ diffStateVE + "] : " + vEthicsVE.get(diffStateVE));
        System.out.println("Difference : " + maxDiffVE + " ; in the state : " + diffStateVE);

        Double lossVE = (maxDiffVE / vTaskVE.get(diffStateVE)) * 100;
        System.out.println("Loss % : " + lossVE);

        System.out.println("vTask[HOME] : " + vTaskVE.get("HOME") + ", vHarm[HOME] : " + vEthicsVE.get("HOME"));


    }
}


        // Mini world example
        /*
        List<Location> locations = new ArrayList<>();
        locations.add(new Location("HOME"));
        locations.add(new Location("TRAIN_STATION"));
        locations.add(new Location("PIZZA_PLACE"));
        locations.add(new Location("COLLEGE"));
        locations.add(new Location("GAS_STATION"));

        List<Road> roads = new ArrayList<>();
        roads.add(new Road("GRAY_STREET_NORTH", "HOME", "TRAIN_STATION", 5., "CITY"));
        roads.add(new Road("GRAY_STREET_SOUTH", "TRAIN_STATION", "HOME", 5., "CITY"));

        roads.add(new Road("STATION_HIGHWAY_NORTH", "TRAIN_STATION", "GAS_STATION", 6., "HIGHWAY"));
        roads.add(new Road("STATION_HIGHWAY_SOUTH", "GAS_STATION", "TRAIN_STATION", 6., "HIGHWAY"));
        
        roads.add(new Road("MERRICK_ROAD_NORTH", "TRAIN_STATION", "PIZZA_PLACE", 4., "COUNTY"));
        roads.add(new Road("MERRICK_ROAD_SOUTH", "PIZZA_PLACE", "TRAIN_STATION", 4., "COUNTY"));

        roads.add(new Road("CUT_STREET_SOUTH", "PIZZA_PLACE", "COLLEGE", 3., "CITY"));
        roads.add(new Road("CUT_STREET_NORTH", "COLLEGE", "PIZZA_PLACE", 3., "CITY"));

        roads.add(new Road("COLLEGE_STREET_NORTH", "COLLEGE", "GAS_STATION", 2., "CITY"));
        roads.add(new Road("COLLEGE_STREET_SOUTH", "GAS_STATION", "COLLEGE", 2., "CITY"));

        // Road from home to pizza place
        roads.add(new Road("HP_NORTH", "HOME", "PIZZA_PLACE", 5., "CITY"));
        roads.add(new Road("HP_SOUTH", "PIZZA_PLACE", "HOME", 5., "CITY"));
        
        String startLocation = "HOME";
        String goalLocation = "GAS_STATION";
        
        SelfDrivingCarWorld world = new SelfDrivingCarWorld(locations, roads, startLocation, goalLocation);
        */


        /* 
        // ---------------------------------- DCT ----------------------------------
        System.out.println("---------------------------------- DCT ----------------------------------");


        List<Integer> context = new ArrayList<>();
        context.add(0);
        context.add(0);
        
        // Forbidden state profiles for DCT as explained in the original paper Hazardous(H) and Inconsiderate(I)
        StateProfile hazardous = new StateProfile("ALL", "ALL", "HIGH", "ALL");
        StateProfile inconsiderate = new StateProfile("ALL", "ALL", "NORMAL", "HEAVY");
        
        List<StateProfile> profileList = new ArrayList<>();
        profileList.add(hazardous);
        profileList.add(inconsiderate);

        // Evaluation factory setup
        EvaluationFactory evaluationFactoryDCT = new EvaluationFactory(context, parsedWorld);

        // Create evaluations for DCT with H and I
        evaluationFactoryDCT.createDCTevals(profileList);
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalDCT = evaluationFactoryDCT.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEvalDCT = evaluationFactoryDCT.getStateEval();

        RewardCalculator rewardCalculatorDCT = new RewardCalculator(context, stateActionEvalDCT, stateEvalDCT, parsedWorld);
        
        SelfDrivingCarAgent agentDCT = new SelfDrivingCarAgent(parsedWorld,rewardCalculatorDCT);

        // If gamma is 0.9, even with convergence choosing the maximum between the three criteria when at the state home the agent chooses to go to the wrong street (MATOON_STREET_REVERSED)
        // Decreasing the convergenceAchieved also gives us the correct entry (with a gamma of 0.9)
        // In the original code the discount factor is 0.99 and epsilon(convergence) is 0.001
        ValueIteration valueIterationDCT = new ValueIteration(agentDCT, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

        valueIterationDCT.calculateQValues();

        Map<String, Map<String, Double>> qHarmDCT = valueIterationDCT.getqHarm();
        Map<String, Map<String, Double>> qTaskDCT = valueIterationDCT.getqTask();

        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();
        

        Map<String, List<String>> policyHarmDCT = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmDCT);

        Map<String, List<String>> policyHarmTaskDCT = policyExtractor.minimizingExtractor(policyHarmDCT, qTaskDCT);

        Map<String, List<String>> policyTaskDCT = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskDCT); 
        
        Map<String, Double> vTaskDCT = valueIterationDCT.calculateAndReturnVforPolicy(policyTaskDCT);
        Map<String, Double> vHarmDCT = valueIterationDCT.calculateAndReturnVforPolicy(policyHarmTaskDCT);

        Double maxDiffDCT = -Double.MAX_VALUE;
        String diffStateDCT = "";
        for(String state : parsedWorld.getAllStateKeys()){
            // System.out.println("State : " + state + " vHarm : " + vHarm.get(state) + " vTask : " + vTask.get(state));
            if(maxDiffDCT < vHarmDCT.get(state) - vTaskDCT.get(state)){
                maxDiffDCT = vHarmDCT.get(state) - vTaskDCT.get(state);
                diffStateDCT = state;
            }
        }

        System.out.println("Difference : " + maxDiffDCT + " ; in the state : " + diffStateDCT);

        Double lossDCT = (maxDiffDCT / vTaskDCT.get(diffStateDCT)) * 100;
        System.out.println(lossDCT);

        System.out.println("vTask[HOME] : " + vTaskDCT.get("HOME") + ", vHarm[HOME] : " + vHarmDCT.get("HOME"));


        // ---------------------------------- PFD ----------------------------------
        System.out.println("---------------------------------- PFD ----------------------------------");

        // PFD definitions of duties
        Map<Integer, Map<StateProfile, String>> contextToDuties = new HashMap<>();

        StateProfile smoothOperationState = new StateProfile("ALL", "ALL", "NONE", "LIGHT");
        String smoothOperationAction = "TO_HIGH ORRR TO_NORMAL";
        Map<StateProfile, String> smoothOperationDuty = new HashMap<>();
        smoothOperationDuty.put(smoothOperationState, smoothOperationAction);
        contextToDuties.put(0,smoothOperationDuty);

        StateProfile carefulOperationState = new StateProfile("ALL", "ALL", "NONE", "HEAVY");
        String carefulOperationAction = "TO_LOW";
        Map<StateProfile, String> carefulOperationDuty = new HashMap<>();
        carefulOperationDuty.put(carefulOperationState, carefulOperationAction);
        contextToDuties.put(1,carefulOperationDuty);


        EvaluationFactory evaluationFactoryPFD = new EvaluationFactory(context, parsedWorld);

        evaluationFactoryPFD.createPFDevals(contextToDuties);
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalPFD = evaluationFactoryPFD.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEvalPFD = evaluationFactoryPFD.getStateEval();

        RewardCalculator rewardCalculatorPFD = new RewardCalculator(context, stateActionEvalPFD, stateEvalPFD, parsedWorld);
        
        SelfDrivingCarAgent agentPFD = new SelfDrivingCarAgent(parsedWorld,rewardCalculatorPFD);

        ValueIteration valueIterationPFD = new ValueIteration(agentPFD, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

        valueIterationPFD.calculateQValues();

        Map<String, Map<String, Double>> qHarmPFD = valueIterationPFD.getqHarm();
        Map<String, Map<String, Double>> qTaskPFD = valueIterationPFD.getqTask();
        

        Map<String, List<String>> policyHarmPFD = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmPFD);

        Map<String, List<String>> policyHarmTaskPFD = policyExtractor.minimizingExtractor(policyHarmPFD, qTaskPFD);

        Map<String, List<String>> policyTaskPFD = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskPFD); 
        
        Map<String, Double> vTaskPFD = valueIterationPFD.calculateAndReturnVforPolicy(policyTaskPFD);
        Map<String, Double> vHarmPFD = valueIterationPFD.calculateAndReturnVforPolicy(policyHarmTaskPFD);

        Double maxDiffPFD = -Double.MAX_VALUE;
        String diffStatePFD = "";
        for(String state : parsedWorld.getAllStateKeys()){
            // System.out.println("State : " + state + " vHarm : " + vHarm.get(state) + " vTask : " + vTask.get(state));
            if(maxDiffPFD < vHarmPFD.get(state) - vTaskPFD.get(state)){
                maxDiffPFD = vHarmPFD.get(state) - vTaskPFD.get(state);
                diffStatePFD = state;
            }
        }

        System.out.println("Difference : " + maxDiffPFD + " ; in the state : " + diffStatePFD);

        Double lossPFD = (maxDiffPFD / vTaskPFD.get(diffStatePFD)) * 100;
        System.out.println(lossPFD);

        System.out.println("vTask[HOME] : " + vTaskPFD.get("HOME") + ", vHarm[HOME] : " + vHarmPFD.get("HOME"));
*/

        // -----------TEST------------

        // StateProfile h = new StateProfile("ALL", "ALL", "HIGH", "ALL");
        // StateProfile i = new StateProfile("ALL", "ALL", "NORMAL", "HEAVY");

        // List<StateProfile> profileList = new ArrayList<>();
        // profileList.add(h);
        // profileList.add(i);

        // EvaluationFactory evaluationFactoryDCT = new EvaluationFactory(context, parsedWorld);

        // // Create evaluations for DCT with H and I
        // evaluationFactoryDCT.createDCTevals(profileList);
        // Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalDCT = evaluationFactoryDCT.getStateActionEval();
        // Map<Integer,Map<String,Integer>> stateEvalDCT = evaluationFactoryDCT.getStateEval();

        // RewardCalculator rewardCalculatorDCT = new RewardCalculator(context, stateActionEvalDCT, stateEvalDCT, parsedWorld);
        
        // SelfDrivingCarAgent agentTEST = new SelfDrivingCarAgent(parsedWorld,rewardCalculatorDCT);


        // Map<String, List<String>> testPi = new HashMap<>();
        // testPi.put("SCHOOL", Arrays.asList("TURN_ONTO_TRIANGLE_STREET"));
        // testPi.put("TRAIN_STATION", Arrays.asList("TURN_ONTO_SERVICE_ROAD"));
        // testPi.put("GAS_STATION", Arrays.asList("TURN_ONTO_SUNRISE_HIGHWAY"));
        // testPi.put("OFFICE", Arrays.asList("TURN_ONTO_ROUTE_9"));
        // testPi.put("DINER", Arrays.asList("STAY"));

        // testPi.put("TRIANGLE_STREET_CITY_NONE_LIGHT", Arrays.asList("TO_NORMAL"));
        // testPi.put("TRIANGLE_STREET_CITY_NONE_HEAVY", Arrays.asList("TO_LOW"));
        // testPi.put("TRIANGLE_STREET_CITY_LOW_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("TRIANGLE_STREET_CITY_LOW_HEAVY", Arrays.asList("CRUISE"));
        // testPi.put("TRIANGLE_STREET_CITY_NORMAL_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("TRIANGLE_STREET_CITY_NORMAL_HEAVY", Arrays.asList("CRUISE"));
        // testPi.put("TRIANGLE_STREET_CITY_NORMAL_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("TRIANGLE_STREET_CITY_NORMAL_HEAVY", Arrays.asList("CRUISE"));

        // testPi.put("SERVICE_ROAD_COUNTY_NONE_LIGHT", Arrays.asList("TO_NORMAL"));
        // testPi.put("SERVICE_ROAD_COUNTY_NONE_HEAVY", Arrays.asList("TO_LOW"));
        // testPi.put("SERVICE_ROAD_COUNTY_LOW_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("SERVICE_ROAD_COUNTY_LOW_HEAVY", Arrays.asList("CRUISE"));
        // testPi.put("SERVICE_ROAD_COUNTY_NORMAL_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("SERVICE_ROAD_COUNTY_NORMAL_HEAVY", Arrays.asList("CRUISE"));
        // testPi.put("SERVICE_ROAD_COUNTY_NORMAL_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("SERVICE_ROAD_COUNTY_NORMAL_HEAVY", Arrays.asList("CRUISE"));

        // testPi.put("SUNRISE_HIGHWAY_HIGHWAY_NONE_LIGHT", Arrays.asList("TO_NORMAL"));
        // testPi.put("SUNRISE_HIGHWAY_HIGHWAY_NONE_HEAVY", Arrays.asList("TO_LOW"));
        // testPi.put("SUNRISE_HIGHWAY_HIGHWAY_LOW_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("SUNRISE_HIGHWAY_HIGHWAY_LOW_HEAVY", Arrays.asList("CRUISE"));
        // testPi.put("SUNRISE_HIGHWAY_HIGHWAY_NORMAL_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("SUNRISE_HIGHWAY_HIGHWAY_NORMAL_HEAVY", Arrays.asList("CRUISE"));
        // testPi.put("SUNRISE_HIGHWAY_HIGHWAY_NORMAL_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("SUNRISE_HIGHWAY_HIGHWAY_NORMAL_HEAVY", Arrays.asList("CRUISE"));

        // testPi.put("ROUTE_9_COUNTY_NONE_LIGHT", Arrays.asList("TO_NORMAL"));
        // testPi.put("ROUTE_9_COUNTY_NONE_HEAVY", Arrays.asList("TO_LOW"));
        // testPi.put("ROUTE_9_COUNTY_LOW_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("ROUTE_9_COUNTY_LOW_HEAVY", Arrays.asList("CRUISE"));
        // testPi.put("ROUTE_9_COUNTY_NORMAL_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("ROUTE_9_COUNTY_NORMAL_HEAVY", Arrays.asList("CRUISE"));
        // testPi.put("ROUTE_9_COUNTY_NORMAL_LIGHT", Arrays.asList("CRUISE"));
        // testPi.put("ROUTE_9_COUNTY_NORMAL_HEAVY", Arrays.asList("CRUISE"));

        

        // ValueIteration valueIterationTEST = new ValueIteration(agentTEST, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

        // Map<String, Double> vTest = valueIterationTEST.calculateAndReturnVforPolicy(testPi);
        
        // for(Map.Entry<String, Double> e : vTest.entrySet()){
        //     System.out.println(e);

        // }

        // -----------TEST------------