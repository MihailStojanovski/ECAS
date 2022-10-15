import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


        BufferedWriter writerDCT;
        try {
            writerDCT = new BufferedWriter(new FileWriter("DCTresults.txt", true));
            
            // Hazardous
            writerDCT.append("Hazardous \n");
            writerDCT.append("Hazardous DCT for task 1: " + "\n");
            parsedWorld.setStartLocation("SCHOOL");
            parsedWorld.setGoalLocation("DINER");
            showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor, writerDCT);

            writerDCT.append("\n");

            writerDCT.append("Hazardous DCT for task 2: " + "\n");
            parsedWorld.setStartLocation("HOME");
            parsedWorld.setGoalLocation("OFFICE");
            showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor, writerDCT);

            writerDCT.append("\n");

            writerDCT.append("Hazardous DCT for task 3: " + "\n");
            parsedWorld.setStartLocation("TOWN_HALL");
            parsedWorld.setGoalLocation("PARK");
            showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor, writerDCT);

            writerDCT.append("\nHazardous & Inconsiderate");

            // Hazardous and Inconsiderate

            writerDCT.append("Hazardous & Inconsiderate DCT for task 1: " + "\n");
            parsedWorld.setStartLocation("SCHOOL");
            parsedWorld.setGoalLocation("DINER");
            showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor, writerDCT);

            writerDCT.append("\n");

            writerDCT.append("Hazardous & Inconsiderate DCT for task 2: " + "\n");
            parsedWorld.setStartLocation("HOME");
            parsedWorld.setGoalLocation("OFFICE");
            showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor, writerDCT);

            writerDCT.append("\n");

            writerDCT.append("Hazardous & Inconsiderate DCT for task 3: " + "\n");
            parsedWorld.setStartLocation("TOWN_HALL");
            parsedWorld.setGoalLocation("PARK");
            showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor, writerDCT);

            writerDCT.append("\n");

            writerDCT.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        // System.out.println("Hazardous DCT for task 1: ");
        // parsedWorld.setStartLocation("SCHOOL");
        // parsedWorld.setGoalLocation("DINER");
        // showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor);

        // System.out.println("Hazardous DCT for task 2: ");
        // parsedWorld.setStartLocation("HOME");
        // parsedWorld.setGoalLocation("OFFICE");
        // showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor);

        // System.out.println("Hazardous DCT for task 3: ");
        // parsedWorld.setStartLocation("TOWN_HALL");
        // parsedWorld.setGoalLocation("PARK");
        // showLossDCT(context, profileListHazardous, parsedWorld, policyExtractor);


        // System.out.println("Hazardous & Inconsiderate DCT for task 1: ");
        // parsedWorld.setStartLocation("SCHOOL");
        // parsedWorld.setGoalLocation("DINER");
        // showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor);

        // System.out.println("Hazardous & Inconsiderate DCT for task 2: ");
        // parsedWorld.setStartLocation("HOME");
        // parsedWorld.setGoalLocation("OFFICE");
        // showLossDCT(context, profileListHazardousAndInconsiderate, parsedWorld, policyExtractor);

        // System.out.println("Hazardous & Inconsiderate DCT for task 3: ");
        // parsedWorld.setStartLocation("TOWN_HALL");
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

        BufferedWriter writerPFD;
        try {
            writerPFD = new BufferedWriter(new FileWriter("PFDresults.txt", true));
            
            writerPFD.append("0 tolerance PFD for task 1: " + "\n");
            parsedWorld.setStartLocation("SCHOOL");
            parsedWorld.setGoalLocation("DINER");
            showLossPFD(context, contextIndexToDuties, parsedWorld, policyExtractor, writerPFD);

            writerPFD.append("\n");

            writerPFD.append("0 tolerance PFD for task 2: " + "\n");
            parsedWorld.setStartLocation("HOME");
            parsedWorld.setGoalLocation("OFFICE");
            showLossPFD(context, contextIndexToDuties, parsedWorld, policyExtractor, writerPFD);

            writerPFD.append("\n");

            writerPFD.append("0 tolerance PFD for task 3: " + "\n");
            parsedWorld.setStartLocation("TOWN_HALL");
            parsedWorld.setGoalLocation("PARK");
            showLossPFD(context, contextIndexToDuties, parsedWorld, policyExtractor, writerPFD);


            writerPFD.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        // ---------------------------------- VE ----------------------------------
        System.out.println("---------------------------------- VE ----------------------------------");

        List<Integer> contextVElarge = new ArrayList<>(Arrays.asList(0,1,0,1,
                                                                    0,1,0,1,
                                                                    0,1,0,1,
                                                                    0,1,0,1));


        // AVOID HIGHWAY

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
            writerVE = new BufferedWriter(new FileWriter("VEresults.txt", true));
            
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

    public static void showLossDCT(List<Integer> context, List<StateProfile> profileList, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor, BufferedWriter writerDCT){
        
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

        Double maxDiffDCT = -Double.MAX_VALUE;
        String diffStateDCT = "";
        for(String state : parsedWorld.getAllStateKeys()){
            if(maxDiffDCT < vHarmDCT.get(state) - vTaskDCT.get(state)){
                maxDiffDCT = vHarmDCT.get(state) - vTaskDCT.get(state);
                diffStateDCT = state;
            }
        }


        try {
            writerDCT.append("Most difference in state: TASK[" + diffStateDCT +"]= " + vTaskDCT.get(diffStateDCT) + "; HARM["+ diffStateDCT + "]= " + vHarmDCT.get(diffStateDCT) + "\n");

            writerDCT.append("Highest difference value: " + maxDiffDCT + "\n");

            Double lossDCT = (maxDiffDCT / vTaskDCT.get(diffStateDCT)) * 100;
            writerDCT.append("Highest loss: " + lossDCT + "%\n");
    
            writerDCT.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vTaskDCT.get(parsedWorld.getStartLocation()) + "; HARM["+parsedWorld.getStartLocation()+"]="+vHarmDCT.get(parsedWorld.getStartLocation()) + "\n");
            Double dif = vHarmDCT.get(parsedWorld.getStartLocation()) - vTaskDCT.get(parsedWorld.getStartLocation());
            writerDCT.append("Difference at start location:" + dif + "\n");
            Double lossAtStart = (dif / vTaskDCT.get(parsedWorld.getStartLocation())) * 100;
            writerDCT.append("Loss at start location: " + lossAtStart + "%\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // System.out.println("Difference : " + maxDiffDCT + " ; in the state : " + diffStateDCT);

        // Double lossDCT = (maxDiffDCT / vTaskDCT.get(diffStateDCT)) * 100;
        // System.out.println("Loss % : " + lossDCT);

        // System.out.println("vTask[SCHOOL] : " + vTaskDCT.get("SCHOOL") + ", vHarm[SCHOOL] : " + vHarmDCT.get("SCHOOL"));
        // Double dif = vHarmDCT.get("SCHOOL") - vTaskDCT.get("SCHOOL");
        // System.out.println((dif / vTaskDCT.get("SCHOOL")) * 100);

        // System.out.println("Iteration: " + valueIterationDCT.getCounter());

    }

    public static void showLossPFD(List<Integer> context, Map<Integer, Map<StateProfile, String>> contextToDuties, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor, BufferedWriter writerPFD){
        
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
            if(maxDiffPFD < vHarmPFD.get(state) - vTaskPFD.get(state)){
                maxDiffPFD = vHarmPFD.get(state) - vTaskPFD.get(state);
                diffStatePFD = state;
            }
        }


        try {
            writerPFD.append("Most difference in state: TASK[" + diffStatePFD +"]= " + vTaskPFD.get(diffStatePFD) + "; HARM["+ diffStatePFD + "]= " + vHarmPFD.get(diffStatePFD) + "\n");

            writerPFD.append("Highest difference value: " + maxDiffPFD + "\n");

            Double lossPFD = (maxDiffPFD / vTaskPFD.get(diffStatePFD)) * 100;
            writerPFD.append("Highest loss: " + lossPFD + "%\n");
    
            writerPFD.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vTaskPFD.get(parsedWorld.getStartLocation()) + "; HARM["+parsedWorld.getStartLocation()+"]="+vHarmPFD.get(parsedWorld.getStartLocation()) + "\n");
            Double dif = vHarmPFD.get(parsedWorld.getStartLocation()) - vTaskPFD.get(parsedWorld.getStartLocation());
            writerPFD.append("Difference at start location: " + dif + "\n");
            Double lossAtStart = (dif / vTaskPFD.get(parsedWorld.getStartLocation())) * 100;
            writerPFD.append("Loss at start location: " + lossAtStart + "%\n");

        } catch (IOException e) {
            e.printStackTrace();
        }


        // System.out.println("vTask[" + diffStatePFD +"] : " + vTaskPFD.get(diffStatePFD) + "; vHarm["+ diffStatePFD + "] : " + vHarmPFD.get(diffStatePFD));
        // System.out.println("Difference : " + maxDiffPFD + " ; in the state : " + diffStatePFD);

        // Double lossPFD = (maxDiffPFD / vTaskPFD.get(diffStatePFD)) * 100;
        // System.out.println("Loss % : " + lossPFD);

        // System.out.println("vTask[HOME] : " + vTaskPFD.get("HOME") + ", vHarm[HOME] : " + vHarmPFD.get("HOME"));


    }

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
            


        

        RewardCalculator rewardCalculatorVE = new RewardCalculator(context, stateActionEvalVE, stateEvalVE, parsedWorld);
        
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