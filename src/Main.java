import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;


import valueIterationAlgorithms.PolicyExtractor;
import valueIterationAlgorithms.ValueIteration;
import agents.SelfDrivingCarAgent;
import factories.EvaluationFactory;
import factories.StateProfile;
import factories.VirtueEthicsData;
import parsers.ExperimentParser;
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

        // ExperimentParser expParser = new ExperimentParser();

        // Map<String, List<String>> moralityDCTAmoralT1policy = expParser.getExperimentPolicy("moralityExperiments/DCTAmoralT1.txt");
        // Map<String, List<String>> moralityDCTFewT1policy = expParser.getExperimentPolicy("moralityExperiments/DCTFewT1.txt");
        // Map<String, List<String>> moralityDCTManyT1policy = expParser.getExperimentPolicy("moralityExperiments/DCTManyT1.txt");


        
        // ---------------------------------- DCT ----------------------------------
        // System.out.println("---------------------------------- DCT ----------------------------------");

        List<Integer> contextHazardous = new ArrayList<>();
        contextHazardous.add(0);

        List<Integer> contextHazardousAndInconsiderate = new ArrayList<>();
        contextHazardousAndInconsiderate.add(0);
        contextHazardousAndInconsiderate.add(0);

        // Forbidden state profiles for DCT as explained in the original paper Hazardous(H) and Inconsiderate(I)
        StateProfile hazardous = new StateProfile("ALL", "ALL", "HIGH", "ALL", false);
        StateProfile inconsiderate = new StateProfile("ALL", "ALL", "NORMAL", "HEAVY", false);
        
        Map<Integer, StateProfile> profileMapHazardous = Collections.singletonMap(0, hazardous);

        Map<Integer, StateProfile> profileMapHazardousAndInconsiderate = Map.ofEntries( entry(0, hazardous),
                                                                                        entry(1, inconsiderate));
                                                                            


        // BufferedWriter writerDCT;
        // try {
        //     writerDCT = new BufferedWriter(new FileWriter("DCTresults2.txt", false));
            
        //     // Hazardous

        //     writerDCT.append("Hazardous \n");
        //     writerDCT.append("Hazardous DCT for task 1: " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("DINER");
        //     showLossDCT(contextHazardous, profileMapHazardous, parsedWorld, policyExtractor, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous DCT for task 2: " + "\n");
        //     parsedWorld.setStartLocation("HOME");
        //     parsedWorld.setGoalLocation("OFFICE");
        //     showLossDCT(contextHazardous, profileMapHazardous, parsedWorld, policyExtractor, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous DCT for task 3: " + "\n");
        //     parsedWorld.setStartLocation("TOWN_HALL");
        //     parsedWorld.setGoalLocation("PARK");
        //     showLossDCT(contextHazardous, profileMapHazardous, parsedWorld, policyExtractor, writerDCT);

        //     writerDCT.append("\nHazardous & Inconsiderate");

        //     // Hazardous and Inconsiderate

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 1: " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("DINER");
        //     showLossDCT(contextHazardousAndInconsiderate, profileMapHazardousAndInconsiderate, parsedWorld, policyExtractor, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 2: " + "\n");
        //     parsedWorld.setStartLocation("HOME");
        //     parsedWorld.setGoalLocation("OFFICE");
        //     showLossDCT(contextHazardousAndInconsiderate, profileMapHazardousAndInconsiderate, parsedWorld, policyExtractor, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 3: " + "\n");
        //     parsedWorld.setStartLocation("TOWN_HALL");
        //     parsedWorld.setGoalLocation("PARK");
        //     showLossDCT(contextHazardousAndInconsiderate, profileMapHazardousAndInconsiderate, parsedWorld, policyExtractor, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        
        /*
        System.out.println("Hazardous DCT for task 1: ");
        parsedWorld.setStartLocation("SCHOOL");
        parsedWorld.setGoalLocation("DINER");
        showLossDCT(context, profileMapHazardous, parsedWorld, policyExtractor);

        System.out.println("Hazardous DCT for task 2: ");
        parsedWorld.setStartLocation("HOME");
        parsedWorld.setGoalLocation("OFFICE");
        showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor);

        System.out.println("Hazardous DCT for task 3: ");
        parsedWorld.setStartLocation("TOWN_HALL");
        parsedWorld.setGoalLocation("PARK");
        showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor);


        System.out.println("Hazardous & Inconsiderate DCT for task 1: ");
        parsedWorld.setStartLocation("SCHOOL");
        parsedWorld.setGoalLocation("DINER");
        showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor);

        System.out.println("Hazardous & Inconsiderate DCT for task 2: ");
        parsedWorld.setStartLocation("HOME");
        parsedWorld.setGoalLocation("OFFICE");
        showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor);

        System.out.println("Hazardous & Inconsiderate DCT for task 3: ");
        parsedWorld.setStartLocation("TOWN_HALL");
        parsedWorld.setGoalLocation("PARK");
        showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor);
        */


        // ---------------------------------- PFD ----------------------------------
        // System.out.println("---------------------------------- PFD ----------------------------------");
        
        // PFD definitions of duties

        List<Integer> contextPFD = new ArrayList<>();
        contextPFD.add(0);
        contextPFD.add(0);
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

        // BufferedWriter writerPFD;
        // try {
        //     writerPFD = new BufferedWriter(new FileWriter("PFDresults2.txt", false));

        //     System.out.println("0 tolerance PFD for task 1: ");
        //     writerPFD.append("0 tolerance PFD for task 1: " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("DINER");
        //     showLossPFD(contextPFD, contextIndexToDuties, parsedWorld, policyExtractor, writerPFD);

        //     writerPFD.append("\n");

        //     System.out.println("0 tolerance PFD for task 2: ");
        //     writerPFD.append("0 tolerance PFD for task 2: " + "\n");
        //     parsedWorld.setStartLocation("HOME");
        //     parsedWorld.setGoalLocation("OFFICE");
        //     showLossPFD(contextPFD, contextIndexToDuties, parsedWorld, policyExtractor, writerPFD);

        //     writerPFD.append("\n");

        //     System.out.println("0 tolerance PFD for task 3: ");
        //     writerPFD.append("0 tolerance PFD for task 3: " + "\n");
        //     parsedWorld.setStartLocation("TOWN_HALL");
        //     parsedWorld.setGoalLocation("PARK");
        //     showLossPFD(contextPFD, contextIndexToDuties, parsedWorld, policyExtractor, writerPFD);


        //     writerPFD.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        


        // ---------------------------------- VE ----------------------------------
        
        System.out.println("---------------------------------- VE ----------------------------------");

        List<Integer> contextVElarge = new ArrayList<>(Arrays.asList(0,1,0,1,
                                                                    0,1,0,1,
                                                                    0,1,0,1,
                                                                    0,1,0,1));


        // Proactive: Avoid the Highway, School and College states

        StateProfile trajectoryStateP1 = new StateProfile("GAS_STATION", "ALL", "ALL", "ALL",true);
        String trajectoryActionP1 = "TURN_ONTO_ROUTE_116 || TURN_ONTO_SERVICE_ROAD_REVERSED || TURN_ONTO_COLLEGE_STREET_REVERSED";
        StateProfile trajectorySuccessorStateP1 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive1 = new VirtueEthicsData(0, 1, trajectoryStateP1, trajectoryActionP1, trajectorySuccessorStateP1);
        
        StateProfile trajectoryStateP5 = new StateProfile("OFFICE", "ALL", "ALL", "ALL",true);
        String trajectoryActionP5 = "TURN_ONTO_ROUTE_9 || TURN_ONTO_OAK_ROAD_REVERSED";
        StateProfile trajectorySuccessorStateP5 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive5 = new VirtueEthicsData(2, 3, trajectoryStateP5, trajectoryActionP5, trajectorySuccessorStateP5);
        
        
        StateProfile trajectoryStateP9 = new StateProfile("HOME", "ALL", "ALL", "ALL",true);
        String trajectoryActionP9 = "TURN_ONTO_GRAY_STREET";
        StateProfile trajectorySuccessorStateP9 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive9 = new VirtueEthicsData(4, 5, trajectoryStateP9, trajectoryActionP9, trajectorySuccessorStateP9);
        
        
        StateProfile trajectoryStateP11 = new StateProfile("TRAIN_STATION", "ALL", "ALL", "ALL",true);
        String trajectoryActionP11 = "TURN_ONTO_GRAY_STREET_REVERSED || TURN_ONTO_MERRICK_ROAD || TURN_ONTO_SERVICE_ROAD";
        StateProfile trajectorySuccessorStateP11 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive11 = new VirtueEthicsData(6, 7, trajectoryStateP11, trajectoryActionP11, trajectorySuccessorStateP11);
        
        StateProfile trajectoryStateP15 = new StateProfile("CAFE", "ALL", "ALL", "ALL",true);
        String trajectoryActionP15 = "TURN_ONTO_MAIN_STREET";
        StateProfile trajectorySuccessorStateP15 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive15 = new VirtueEthicsData(8, 9, trajectoryStateP15, trajectoryActionP15, trajectorySuccessorStateP15);

        StateProfile trajectoryStateP16 = new StateProfile("SCHOOL", "ALL", "ALL", "ALL",true);
        String trajectoryActionP16 = "TURN_ONTO_MATOON_STREET || TURN_ONTO_TRIANGLE_STREET || TURN_ONTO_ASTOR_DRIVE";
        StateProfile trajectorySuccessorStateP16 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive16 = new VirtueEthicsData(10, 11, trajectoryStateP16, trajectoryActionP16, trajectorySuccessorStateP16);


        // Cautious, light -> to normal, heavy -> to low

        StateProfile trajectoryStateC1Large = new StateProfile("ALL", "ALL", "NONE", "LIGHT",false);
        String trajectoryActionC1Large = "TO_NORMAL";
        StateProfile trajectorySuccessorStateC1Large = new StateProfile("ALL", "ALL", "NORMAL", "LIGHT",false);

        VirtueEthicsData cautious_1Large = new VirtueEthicsData(12, 13, trajectoryStateC1Large, trajectoryActionC1Large, trajectorySuccessorStateC1Large);

        StateProfile trajectoryStateC2Large = new StateProfile("ALL", "ALL", "NONE", "HEAVY",false);
        String trajectoryActionC2Large = "TO_LOW";
        StateProfile trajectorySuccessorStateC2Large = new StateProfile("ALL", "ALL", "LOW", "HEAVY",false);

        VirtueEthicsData cautious_2Large = new VirtueEthicsData(14, 15, trajectoryStateC2Large, trajectoryActionC2Large, trajectorySuccessorStateC2Large);

        

        // Check the context before executing
        List<VirtueEthicsData> dataListVElarge = new ArrayList<>(Arrays.asList(proactive1 ,proactive5, proactive9, proactive11, proactive15, proactive16, cautious_1Large, cautious_2Large));



        List<Integer> contextVEsmall = new ArrayList<>(Arrays.asList(0,1,0,1));

        StateProfile trajectoryStateC1Small = new StateProfile("ALL", "ALL", "NONE", "LIGHT",false);
        String trajectoryActionC1Small = "TO_NORMAL";
        StateProfile trajectorySuccessorStateC1Small = new StateProfile("ALL", "ALL", "NORMAL", "LIGHT",false);

        VirtueEthicsData cautious_1Small = new VirtueEthicsData(0, 1, trajectoryStateC1Small, trajectoryActionC1Small, trajectorySuccessorStateC1Small);

        StateProfile trajectoryStateC2Small = new StateProfile("ALL", "ALL", "NONE", "HEAVY",false);
        String trajectoryActionC2Small = "TO_LOW";
        StateProfile trajectorySuccessorStateC2Small = new StateProfile("ALL", "ALL", "LOW", "HEAVY",false);

        VirtueEthicsData cautious_2Small = new VirtueEthicsData(2, 3, trajectoryStateC2Small, trajectoryActionC2Small, trajectorySuccessorStateC2Small);
        List<VirtueEthicsData> dataListVEsmall = new ArrayList<>(Arrays.asList(cautious_1Small, cautious_2Small));
        


        BufferedWriter writerVE;
        try {
            writerVE = new BufferedWriter(new FileWriter("VEresults2.txt", true));
            
            // Small
            
            writerVE.append("Small VE trajectory list \n");
            writerVE.append("Small VE trajectory list for task 1: " + "\n");
            parsedWorld.setStartLocation("SCHOOL");
            parsedWorld.setGoalLocation("DINER");
            showLossVE(contextVEsmall, dataListVEsmall, parsedWorld, policyExtractor, writerVE);

            writerVE.append("\n");

            writerVE.append("Small VE trajectory list for task 2: " + "\n");
            parsedWorld.setStartLocation("HOME");
            parsedWorld.setGoalLocation("OFFICE");
            showLossVE(contextVEsmall, dataListVEsmall, parsedWorld, policyExtractor, writerVE);

            writerVE.append("\n");

            writerVE.append("Small VE trajectory list for task 3: " + "\n");
            parsedWorld.setStartLocation("TOWN_HALL");
            parsedWorld.setGoalLocation("PARK");
            showLossVE(contextVEsmall, dataListVEsmall, parsedWorld, policyExtractor, writerVE);

            writerVE.append("\nLarge VE trajectory list");

            // Large

            writerVE.append("Large VE trajectory list for task 1: " + "\n");
            parsedWorld.setStartLocation("SCHOOL");
            parsedWorld.setGoalLocation("DINER");
            showLossVE(contextVElarge, dataListVElarge, parsedWorld, policyExtractor, writerVE);

            writerVE.append("\n");

            writerVE.append("Large VE trajectory list for task 2: " + "\n");
            parsedWorld.setStartLocation("HOME");
            parsedWorld.setGoalLocation("OFFICE");
            showLossVE(contextVElarge, dataListVElarge, parsedWorld, policyExtractor, writerVE);

            writerVE.append("\n");

            writerVE.append("Large VE trajectory list for task 3: " + "\n");
            parsedWorld.setStartLocation("TOWN_HALL");
            parsedWorld.setGoalLocation("PARK");
            showLossVE(contextVElarge, dataListVElarge, parsedWorld, policyExtractor, writerVE);

            writerVE.append("\n");

            writerVE.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        
    }

    public static void showLossDCT(List<Integer> context, Map<Integer, StateProfile> profileList, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor, BufferedWriter writerDCT){
        
        // Evaluation factory setup
        EvaluationFactory evaluationFactoryDCT = new EvaluationFactory(context, parsedWorld);

        // Create evaluations for DCT with H and I
        evaluationFactoryDCT.createDCTevals(profileList);
        // Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalDCT = evaluationFactoryDCT.getStateActionEval();
        // Map<Integer,Map<String,Integer>> stateEvalDCT = evaluationFactoryDCT.getStateEval();

        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDCT = evaluationFactoryDCT.getTransitionEval();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDummy = evaluationFactoryDCT.getTransitionEvalsWithEvaluationValue(Integer.MAX_VALUE);


        RewardCalculator rewardCalculatorDCT = new RewardCalculator(context, transitionEvalsDCT, parsedWorld);
        RewardCalculator rewardCalculatorDummy = new RewardCalculator(context, transitionEvalsDummy, parsedWorld);

        
        SelfDrivingCarAgent agentDCT = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorDCT);
        SelfDrivingCarAgent agentDummy = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorDummy);

        // If gamma is 0.9, even with convergence choosing the maximum between the three criteria when at the state HOME the agent chooses to go to the wrong street (MATOON_STREET_REVERSED)
        // Decreasing the convergenceAchieved also gives us the correct entry (with a gamma of 0.9)
        // In the morality.js code the discount factor(gamma) is 0.99 and epsilon(convergenceAchieved) is 0.001
        ValueIteration valueIterationDCT = new ValueIteration(agentDCT, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);
        ValueIteration valueIterationDummy = new ValueIteration(agentDummy, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

        valueIterationDCT.calculateQValues();
        valueIterationDummy.calculateQValues();

        Map<String, Map<String, Double>> qHarmDCT = valueIterationDCT.getqHarm();
        Map<String, Map<String, Double>> qTaskDCT = valueIterationDCT.getqTask();

        Map<String, Map<String, Double>> qHarmDummy = valueIterationDummy.getqHarm();
        Map<String, Map<String, Double>> qTaskDummy = valueIterationDummy.getqTask();



        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

        Map<String, List<String>> policyHarmDCT = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmDCT);
        Map<String, List<String>> policyHarmTaskDCT = policyExtractor.minimizingExtractor(policyHarmDCT, qTaskDCT);

        // Map<String, List<String>> policyTaskDCT = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskDCT); 

        Map<String, List<String>> policyHarmDummy = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmDummy);
        Map<String, List<String>> policyHarmTaskDummy = policyExtractor.minimizingExtractor(policyHarmDummy, qTaskDummy);
        
        Map<String, Double> vHarmTaskDCT = valueIterationDCT.calculateAndReturnVforPolicy(policyHarmTaskDCT);
        Map<String, Double> vTaskDummy = valueIterationDummy.calculateAndReturnVforPolicy(policyHarmTaskDummy);


        // System.out.println("Moral");
        // for(String state : parsedWorld.getAllStateKeys()){
        //     System.out.println("State: " + state + ", policy[state]: " + policyHarmTaskDCT.get(state));
        // }

        // System.out.println("Dummy");
        // for(String state : parsedWorld.getAllStateKeys()){
        //     System.out.println("State: " + state + ", policy[state]: " + policyHarmTaskDummy.get(state));
        // }

        Double maxDiffDCT = -Double.MAX_VALUE;
        String diffStateDCT = "";
        for(String state : parsedWorld.getAllStateKeys()){
            if(maxDiffDCT < vHarmTaskDCT.get(state) - vTaskDummy.get(state)){
                maxDiffDCT = vHarmTaskDCT.get(state) - vTaskDummy.get(state);
                diffStateDCT = state;
            }
        }


        try {
            writerDCT.append("Most difference in state: TASK[" + diffStateDCT +"]= " + vTaskDummy.get(diffStateDCT) + "; HARM["+ diffStateDCT + "]= " + vHarmTaskDCT.get(diffStateDCT) + "\n");

            writerDCT.append("Highest difference value: " + maxDiffDCT + "\n");

            Double lossDCT = (maxDiffDCT / vTaskDummy.get(diffStateDCT)) * 100;
            writerDCT.append("Highest loss: " + lossDCT + "%\n");
    
            writerDCT.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vTaskDummy.get(parsedWorld.getStartLocation()) + "; HARM["+parsedWorld.getStartLocation()+"]="+vHarmTaskDCT.get(parsedWorld.getStartLocation()) + "\n");
            Double dif = vHarmTaskDCT.get(parsedWorld.getStartLocation()) - vTaskDummy.get(parsedWorld.getStartLocation());
            writerDCT.append("Difference at start location:" + dif + "\n");
            Double lossAtStart = (dif / vTaskDummy.get(parsedWorld.getStartLocation())) * 100;
            writerDCT.append("Loss at start location: " + lossAtStart + "%\n");

        } catch (IOException e) {
            e.printStackTrace();
        }


        // State and transition evaluations output
        /*
        // BufferedWriter writer2;
        // try {
        //     writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
        //     Map<Integer, Map<String, Integer>> hasContextMap = rewardCalculatorDCT.getStateEval();
        //     for(Map.Entry<Integer,Map<String,Integer>> hasContextEnrty : hasContextMap.entrySet()){
        //         writer2.append("CONTEXT: " + hasContextEnrty.getKey() + "{\n");
        //         Map<String,Integer> hasStateMap = hasContextEnrty.getValue();
        //         for(Map.Entry<String,Integer> hasStateEntry : hasStateMap.entrySet()){
        //             writer2.append("STATE: " + hasStateEntry.getKey() + " , WORTH: " + hasStateEntry.getValue() + "\n");
        //         }
        //         writer2.append("END CONTEXT: " + hasContextEnrty.getKey() + "}\n");
        //     }


        //     writer2.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // Transition evaluations output

        // BufferedWriter writer3;
        // try {
        //     writer3 = new BufferedWriter(new FileWriter("outputTransition.txt", false));
        
        //     for(Map.Entry<Integer, Map<String, Map<String, Map<String, Integer>>>> hasContextEnrty : evaluationFactoryDCT.getTransitionEval().entrySet()){
        //         Map<String, Map<String, Map<String, Integer>>> hasStateMap = hasContextEnrty.getValue();
        //         for(Map.Entry<String, Map<String, Map<String, Integer>>> hasStateEntry : hasStateMap.entrySet()){
        //             Map<String, Map<String, Integer>> hasActionMap = hasStateEntry.getValue();
        //             for(Map.Entry<String, Map<String, Integer>> hasActionEntry : hasActionMap.entrySet()){
        //                 Map<String, Integer> hasSuccessorMap = hasActionEntry.getValue();
        //                 for(Map.Entry<String, Integer> hasSuccessorEntry : hasSuccessorMap.entrySet()){

        //                     writer3.append("CONTEXT: " + hasContextEnrty.getKey() + "\n" +
        //                                     "\t STATE: " + hasStateEntry.getKey() + "\n" +
        //                                     "\t\t ACTION: " + hasActionEntry.getKey() + "\n" +
        //                                     "\t\t\t SUCCESSOR: " + hasSuccessorEntry.getKey() + "\n" +
        //                                     "\t\t\t\t EVALUATION: " + hasSuccessorEntry.getValue() + "\n");
        //                 }
        //             }
        //         }
        //     }


        //     writer3.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        */

    }

    public static void showLossPFD(List<Integer> context, Map<Integer, Map<StateProfile, String>> contextToDuties, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor, BufferedWriter writerPFD){
        
        EvaluationFactory evaluationFactoryPFD = new EvaluationFactory(context, parsedWorld);

        evaluationFactoryPFD.createPFDevals(contextToDuties);
        // Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalPFD = evaluationFactoryPFD.getStateActionEval();
        // Map<Integer,Map<String,Integer>> stateEvalPFD = evaluationFactoryPFD.getStateEval();

        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsPFD = evaluationFactoryPFD.getTransitionEval();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDummy = evaluationFactoryPFD.getTransitionEvalsWithEvaluationValue(Integer.MAX_VALUE);
        
        
        RewardCalculator rewardCalculatorPFD = new RewardCalculator(context, transitionEvalsPFD, parsedWorld);
        RewardCalculator rewardCalculatorDummy = new RewardCalculator(context, transitionEvalsDummy, parsedWorld);

        
        SelfDrivingCarAgent agentPFD = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorPFD);
        SelfDrivingCarAgent agentDummy = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorDummy);


        ValueIteration valueIterationPFD = new ValueIteration(agentPFD, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);
        ValueIteration valueIterationDummy = new ValueIteration(agentDummy, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

        valueIterationPFD.calculateQValues();
        valueIterationDummy.calculateQValues();

        Map<String, Map<String, Double>> qHarmPFD = valueIterationPFD.getqHarm();
        Map<String, Map<String, Double>> qTaskPFD = valueIterationPFD.getqTask();

        Map<String, Map<String, Double>> qHarmDummy = valueIterationDummy.getqHarm();
        Map<String, Map<String, Double>> qTaskDummy = valueIterationDummy.getqTask();
        
        
        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

        Map<String, List<String>> policyHarmPFD = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmPFD);

        Map<String, List<String>> policyHarmTaskPFD = policyExtractor.minimizingExtractor(policyHarmPFD, qTaskPFD);

        // Map<String, List<String>> policyTaskPFD = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskPFD); 
        
        Map<String, List<String>> policyHarmDummy = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmDummy);
        Map<String, List<String>> policyHarmTaskDummy = policyExtractor.minimizingExtractor(policyHarmDummy, qTaskDummy);

        // for(String state : parsedWorld.getAllStateKeys()){
        //     System.out.println("State: " + state + ", policy[state]: " + policyHarmTaskPFD.get(state));
        // }


        
        Map<String, Double> vHarmTaskPFD = valueIterationPFD.calculateAndReturnVforPolicy(policyHarmTaskPFD);
        Map<String, Double> vTaskDummy = valueIterationPFD.calculateAndReturnVforPolicy(policyHarmTaskDummy);

        Double maxDiffPFD = -Double.MAX_VALUE;
        String diffStatePFD = "";
        for(String state : parsedWorld.getAllStateKeys()){
            if(maxDiffPFD < vHarmTaskPFD.get(state) - vTaskDummy.get(state)){
                maxDiffPFD = vHarmTaskPFD.get(state) - vTaskDummy.get(state);
                diffStatePFD = state;
            }
        }


        try {
            writerPFD.append("Most difference in state: TASK[" + diffStatePFD +"]= " + vTaskDummy.get(diffStatePFD) + "; HARM["+ diffStatePFD + "]= " + vHarmTaskPFD.get(diffStatePFD) + "\n");

            writerPFD.append("Highest difference value: " + maxDiffPFD + "\n");

            Double lossPFD = (maxDiffPFD / vTaskDummy.get(diffStatePFD)) * 100;
            writerPFD.append("Highest loss: " + lossPFD + "%\n");
    
            writerPFD.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vTaskDummy.get(parsedWorld.getStartLocation()) + "; HARM["+parsedWorld.getStartLocation()+"]="+vHarmTaskPFD.get(parsedWorld.getStartLocation()) + "\n");
            Double dif = vHarmTaskPFD.get(parsedWorld.getStartLocation()) - vTaskDummy.get(parsedWorld.getStartLocation());
            writerPFD.append("Difference at start location: " + dif + "\n");
            Double lossAtStart = (dif / vTaskDummy.get(parsedWorld.getStartLocation())) * 100;
            writerPFD.append("Loss at start location: " + lossAtStart + "%\n");

        } catch (IOException e) {
            e.printStackTrace();
        }


        // State evaluations output
        /*
        BufferedWriter writer2;
        try {
            writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
            Map<Integer, Map<String, Integer>> hasContextMap = rewardCalculatorPFD.getStateEval();
            for(Map.Entry<Integer,Map<String,Integer>> hasContextEnrty : hasContextMap.entrySet()){
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

        // Transition evaluations output

        BufferedWriter writer3;
        try {
            writer3 = new BufferedWriter(new FileWriter("outputTransition.txt", false));
        
            for(Map.Entry<Integer, Map<String, Map<String, Map<String, Integer>>>> hasContextEnrty : evaluationFactoryPFD.getTransitionEval().entrySet()){
                Map<String, Map<String, Map<String, Integer>>> hasStateMap = hasContextEnrty.getValue();
                for(Map.Entry<String, Map<String, Map<String, Integer>>> hasStateEntry : hasStateMap.entrySet()){
                    Map<String, Map<String, Integer>> hasActionMap = hasStateEntry.getValue();
                    for(Map.Entry<String, Map<String, Integer>> hasActionEntry : hasActionMap.entrySet()){
                        Map<String, Integer> hasSuccessorMap = hasActionEntry.getValue();
                        for(Map.Entry<String, Integer> hasSuccessorEntry : hasSuccessorMap.entrySet()){

                            writer3.append("CONTEXT: " + hasContextEnrty.getKey() + "\n" +
                                            "\t STATE: " + hasStateEntry.getKey() + "\n" +
                                            "\t\t ACTION: " + hasActionEntry.getKey() + "\n" +
                                            "\t\t\t SUCCESSOR: " + hasSuccessorEntry.getKey() + "\n" +
                                            "\t\t\t\t EVALUATION: " + hasSuccessorEntry.getValue() + "\n");
                        }
                    }
                }
            }


            writer3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        */


    }

    public static void showLossVE(List<Integer> context, List<VirtueEthicsData> dataListVE, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor, BufferedWriter writerVE){
        
        EvaluationFactory evaluationFactoryVE = new EvaluationFactory(context, parsedWorld);

        evaluationFactoryVE.createVEevals(dataListVE);
        
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVE = evaluationFactoryVE.getTransitionEval();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDummy = evaluationFactoryVE.getTransitionEvalsWithEvaluationValue(Integer.MAX_VALUE);

        // State evaluations output
        /*
        BufferedWriter writer2;
        try {
            writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
            Map<Integer, Map<String, Integer>> hasContextMap = rewardCalculatorPFD.getStateEval();
            for(Map.Entry<Integer,Map<String,Integer>> hasContextEnrty : hasContextMap.entrySet()){
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

        // Transition evaluations output

        BufferedWriter writer3;
        try {
            writer3 = new BufferedWriter(new FileWriter("outputTransition.txt", false));
        
            for(Map.Entry<Integer, Map<String, Map<String, Map<String, Integer>>>> hasContextEnrty : evaluationFactoryPFD.getTransitionEval().entrySet()){
                Map<String, Map<String, Map<String, Integer>>> hasStateMap = hasContextEnrty.getValue();
                for(Map.Entry<String, Map<String, Map<String, Integer>>> hasStateEntry : hasStateMap.entrySet()){
                    Map<String, Map<String, Integer>> hasActionMap = hasStateEntry.getValue();
                    for(Map.Entry<String, Map<String, Integer>> hasActionEntry : hasActionMap.entrySet()){
                        Map<String, Integer> hasSuccessorMap = hasActionEntry.getValue();
                        for(Map.Entry<String, Integer> hasSuccessorEntry : hasSuccessorMap.entrySet()){

                            writer3.append("CONTEXT: " + hasContextEnrty.getKey() + "\n" +
                                            "\t STATE: " + hasStateEntry.getKey() + "\n" +
                                            "\t\t ACTION: " + hasActionEntry.getKey() + "\n" +
                                            "\t\t\t SUCCESSOR: " + hasSuccessorEntry.getKey() + "\n" +
                                            "\t\t\t\t EVALUATION: " + hasSuccessorEntry.getValue() + "\n");
                        }
                    }
                }
            }


            writer3.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        */
              
        RewardCalculator rewardCalculatorVE = new RewardCalculator(context, transitionEvalsVE, parsedWorld);
        RewardCalculator rewardCalculatorDummy = new RewardCalculator(context, transitionEvalsDummy, parsedWorld);

        SelfDrivingCarAgent agentVE = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorVE);
        SelfDrivingCarAgent agentDummy = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorDummy);

        ValueIteration valueIterationVE = new ValueIteration(agentVE, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);
        ValueIteration valueIterationDummy = new ValueIteration(agentDummy, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

        valueIterationVE.calculateQValues();
        valueIterationDummy.calculateQValues();

        Map<String, Map<String, Double>> qHarmVE = valueIterationVE.getqHarm();
        Map<String, Map<String, Double>> qGoodVE = valueIterationVE.getqGood();
        Map<String, Map<String, Double>> qTaskVE = valueIterationVE.getqTask();

        Map<String, Map<String, Double>> qHarmDummy = valueIterationDummy.getqHarm();
        Map<String, Map<String, Double>> qGoodDummy = valueIterationDummy.getqGood();
        Map<String, Map<String, Double>> qTaskDummy = valueIterationDummy.getqTask();

        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

        Map<String, List<String>> policyHarmVE = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmVE);

        // for(Map.Entry<String, List<String>> e2: policyHarmVE.entrySet()){
        //     if(e2.getKey().equals("GAS_STATION")){
        //         System.out.print("policyHarmVE: ");
        //         System.out.println(e2);
        //     }
        // }

        Map<String, List<String>> policyHarmGoodVE = policyExtractor.maximizingExtractor(policyHarmVE, qGoodVE);

        // for(Map.Entry<String, List<String>> e3: policyHarmGoodVE.entrySet()){
        //     if(e3.getKey().equals("GAS_STATION")){
        //         System.out.print("policyHarmGoodVE: ");
        //         System.out.println(e3);
        //     }
        // }
        
        Map<String, List<String>> policyHarmGoodTaskVE = policyExtractor.minimizingExtractor(policyHarmGoodVE, qTaskVE);
        

        Map<String, List<String>> policyHarmDummy = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmDummy); 
        Map<String, List<String>> policyHarmGoodDummy = policyExtractor.minimizingExtractor(policyHarmDummy, qGoodDummy); 
        Map<String, List<String>> policyHarmGoodTaskDummy = policyExtractor.minimizingExtractor(policyHarmGoodDummy, qTaskDummy); 

        // for(Map.Entry<String, List<String>> e4 : policyHarmGoodTaskVE.entrySet()){
        //         System.out.println(e4);
        // }
        
        Map<String, Double> vHarmGoodTaskVE = valueIterationVE.calculateAndReturnVforPolicy(policyHarmGoodTaskVE);
        Map<String, Double> vHarmGoodTaskDummy = valueIterationDummy.calculateAndReturnVforPolicy(policyHarmGoodTaskDummy);


        Double maxDiffVE = -Double.MAX_VALUE;
        String diffStateVE = "";

        for(String state : parsedWorld.getAllStateKeys()){
            if(maxDiffVE < vHarmGoodTaskVE.get(state) - vHarmGoodTaskDummy.get(state)){
                maxDiffVE = vHarmGoodTaskVE.get(state) - vHarmGoodTaskDummy.get(state);
                diffStateVE = state;
            }
        }


        try {
            writerVE.append("Most difference in state: TASK[" + diffStateVE +"]= " + vHarmGoodTaskDummy.get(diffStateVE) + "; ETHICS["+ diffStateVE + "]= " + vHarmGoodTaskVE.get(diffStateVE) + "\n");

            writerVE.append("Highest difference value: " + maxDiffVE + "\n");

            Double lossVE = (maxDiffVE / vHarmGoodTaskDummy.get(diffStateVE)) * 100;
            writerVE.append("Highest loss: " + lossVE + "%\n");
    
            writerVE.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vHarmGoodTaskDummy.get(parsedWorld.getStartLocation()) + "; HARM["+parsedWorld.getStartLocation()+"]="+vHarmGoodTaskVE.get(parsedWorld.getStartLocation()) + "\n");
            Double dif = vHarmGoodTaskVE.get(parsedWorld.getStartLocation()) - vHarmGoodTaskDummy.get(parsedWorld.getStartLocation());
            writerVE.append("Difference at start location: " + dif + "\n");
            Double lossAtStart = (dif / vHarmGoodTaskDummy.get(parsedWorld.getStartLocation())) * 100;
            writerVE.append("Loss at start location: " + lossAtStart + "%\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // System.out.println("Most difference in state: Task[" + diffStateVE +"] : " + vTaskVE.get(diffStateVE) + "; VE["+ diffStateVE + "] : " + vEthicsVE.get(diffStateVE));
        // System.out.println("Difference : " + maxDiffVE + " ; in the state : " + diffStateVE);

        // Double lossVE = (maxDiffVE / vTaskVE.get(diffStateVE)) * 100;
        // System.out.println("Loss % : " + lossVE);

        // System.out.println("vTask[HOME] : " + vTaskVE.get("HOME") + ", vHarm[HOME] : " + vEthicsVE.get("HOME"));


    }

    /*
    public static void showLossVE(List<Integer> context, List<VirtueEthicsData> dataListVE, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor, BufferedWriter writerVE){
        
        EvaluationFactory evaluationFactoryVE = new EvaluationFactory(context, parsedWorld);

        evaluationFactoryVE.createVEevals(dataListVE);
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalVE = evaluationFactoryVE.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEvalVE = evaluationFactoryVE.getStateEval();

        // BufferedWriter writer;
        // try {
        //     writer = new BufferedWriter(new FileWriter("outputStateAction.txt", false));
        
        //     for(Map.Entry<Integer,Map<String,Map<String,Integer>>> hasContextEnrty : stateActionEvalVE.entrySet()){
        //         writer.append("CONTEXT: " + hasContextEnrty.getKey() + "{\n");
        //         Map<String,Map<String,Integer>> hasStateMap = hasContextEnrty.getValue();
        //         for(Map.Entry<String,Map<String,Integer>> hasStateEntry : hasStateMap.entrySet()){
        //             writer.append("STATE: " + hasStateEntry.getKey() + " {\n ");
        //             Map<String,Integer> hasActionMap = hasStateEntry.getValue();
        //             for(Map.Entry<String,Integer> hasActionEntry : hasActionMap.entrySet()){
        //                 writer.append("ACTION: " + hasActionEntry.getKey() + ", WORTH: " + hasActionEntry.getValue() + "\n");
        //             }
        //             writer.append("END STATE: " + hasStateEntry.getKey() + " }\n ");
        //         }
        //         writer.append("END CONTEXT: " + hasContextEnrty.getKey() + "}\n");
        //     }


        //     writer.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // BufferedWriter writer2;
        // try {
        //     writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
        
        //     for(Map.Entry<Integer,Map<String,Integer>> hasContextEnrty : stateEvalVE.entrySet()){
        //         writer2.append("CONTEXT: " + hasContextEnrty.getKey() + "{\n");
        //         Map<String,Integer> hasStateMap = hasContextEnrty.getValue();
        //         for(Map.Entry<String,Integer> hasStateEntry : hasStateMap.entrySet()){
        //             writer2.append("STATE: " + hasStateEntry.getKey() + " , WORTH: " + hasStateEntry.getValue() + "\n");
        //         }
        //         writer2.append("END CONTEXT: " + hasContextEnrty.getKey() + "}\n");
        //     }


        //     writer2.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
            


        

        RewardCalculator rewardCalculatorVE = new RewardCalculator(context, transitionEvals, parsedWorld);
        
        SelfDrivingCarAgent agentVE = new SelfDrivingCarAgent(parsedWorld,rewardCalculatorVE);

        ValueIteration valueIterationVE = new ValueIteration(agentVE, parsedWorld, 0.001, 0.99, 1. , 1. , 1., 1.);

        valueIterationVE.calculateQValues();

        Map<String, Map<String, Double>> qHarmVE = valueIterationVE.getqHarm();
        Map<String, Map<String, Double>> qGoodVE = valueIterationVE.getqGood();
        Map<String, Map<String, Double>> qTaskVE = valueIterationVE.getqTask();

        // System.out.println("qHarmVE[GAS_STATION] = " + qHarmVE.get("GAS_STATION"));


        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

        Map<String, List<String>> policyHarmVE = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmVE);

        // for(Map.Entry<String, List<String>> e2: policyHarmVE.entrySet()){
        //     if(e2.getKey().equals("GAS_STATION")){
        //         System.out.print("policyHarmVE: ");
        //         System.out.println(e2);
        //     }
        // }

        Map<String, List<String>> policyHarmGoodVE = policyExtractor.maximizingExtractor(policyHarmVE, qGoodVE);

        // for(Map.Entry<String, List<String>> e3: policyHarmGoodVE.entrySet()){
        //     if(e3.getKey().equals("GAS_STATION")){
        //         System.out.print("policyHarmGoodVE: ");
        //         System.out.println(e3);
        //     }
        // }
        
        Map<String, List<String>> policyHarmGoodTaskVE = policyExtractor.minimizingExtractor(policyHarmGoodVE, qTaskVE);
        

        Map<String, List<String>> policyTaskVE = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskVE); 

        // for(Map.Entry<String, List<String>> e4 : policyHarmGoodTaskVE.entrySet()){

        //         System.out.println(e4);
            
        // }
        
        
        Map<String, Double> vTaskVE = valueIterationVE.calculateAndReturnVforPolicy(policyTaskVE);
        Map<String, Double> vEthicsVE = valueIterationVE.calculateAndReturnVforPolicy(policyHarmGoodTaskVE);


        Double maxDiffVE = -Double.MAX_VALUE;
        String diffStateVE = "";

        for(String state : parsedWorld.getAllStateKeys()){
            if(maxDiffVE < vEthicsVE.get(state) - vTaskVE.get(state)){
                maxDiffVE = vEthicsVE.get(state) - vTaskVE.get(state);
                diffStateVE = state;
            }
        }


        try {
            writerVE.append("Most difference in state: TASK[" + diffStateVE +"]= " + vTaskVE.get(diffStateVE) + "; ETHICS["+ diffStateVE + "]= " + vEthicsVE.get(diffStateVE) + "\n");

            writerVE.append("Highest difference value: " + maxDiffVE + "\n");

            Double lossVE = (maxDiffVE / vTaskVE.get(diffStateVE)) * 100;
            writerVE.append("Highest loss: " + lossVE + "%\n");
    
            writerVE.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vTaskVE.get(parsedWorld.getStartLocation()) + "; HARM["+parsedWorld.getStartLocation()+"]="+vEthicsVE.get(parsedWorld.getStartLocation()) + "\n");
            Double dif = vEthicsVE.get(parsedWorld.getStartLocation()) - vTaskVE.get(parsedWorld.getStartLocation());
            writerVE.append("Difference at start location: " + dif + "\n");
            Double lossAtStart = (dif / vTaskVE.get(parsedWorld.getStartLocation())) * 100;
            writerVE.append("Loss at start location: " + lossAtStart + "%\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // System.out.println("Most difference in state: Task[" + diffStateVE +"] : " + vTaskVE.get(diffStateVE) + "; VE["+ diffStateVE + "] : " + vEthicsVE.get(diffStateVE));
        // System.out.println("Difference : " + maxDiffVE + " ; in the state : " + diffStateVE);

        // Double lossVE = (maxDiffVE / vTaskVE.get(diffStateVE)) * 100;
        // System.out.println("Loss % : " + lossVE);

        // System.out.println("vTask[HOME] : " + vTaskVE.get("HOME") + ", vHarm[HOME] : " + vEthicsVE.get("HOME"));


    } 



     */
}