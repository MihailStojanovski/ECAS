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
import factories.DCTFactory;
import factories.EvaluationFactory;
import factories.PFDFactory;
import factories.StateProfile;
import factories.VEFactory;
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
        
        List<Integer> contextHazardous = new ArrayList<>();
        contextHazardous.add(0);
        
        List<Integer> contextHazardousAndInconsiderate = new ArrayList<>();
        contextHazardousAndInconsiderate.add(0);
        // contextHazardousAndInconsiderate.add(0);
        
        // Forbidden state profiles for DCT as explained in the original paper Hazardous(H) and Inconsiderate(I)
        StateProfile hazardous = new StateProfile("ALL", "ALL", "HIGH", "ALL", false);
        List<StateProfile> hazardousList = new ArrayList<>(Arrays.asList(hazardous));
        Map<Integer, List<StateProfile>> profileMapHazardous = Collections.singletonMap(0, hazardousList);
        
        
        StateProfile inconsiderate = new StateProfile("ALL", "ALL", "NORMAL", "HEAVY", false);
        List<StateProfile> hazardousInconsiderateProfileList = new ArrayList<>(Arrays.asList(hazardous,inconsiderate));
        Map<Integer, List<StateProfile>> profileMapHazardousAndInconsiderate = Collections.singletonMap(0, hazardousInconsiderateProfileList);
        
        
        DCTFactory dctFactory = new DCTFactory(parsedWorld, profileMapHazardousAndInconsiderate);
        dctFactory.createEvaluations();
        
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDCTHI = new HashMap<>();
        transitionEvalsDCTHI.put(0,dctFactory.getTransitionEvalForContextIndex(0));
        
        dctFactory.clearTransitionEvals();
        dctFactory.setForbidden(profileMapHazardous);
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDCTH = new HashMap<>();
        dctFactory.createEvaluations();
        transitionEvalsDCTH.put(0,dctFactory.getTransitionEvalForContextIndex(0));
        
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDCTBlank = new HashMap<>(dctFactory.getBlankTransitionEvals(1));
        
        
        
        // System.out.println("---------------------------------- DCT ----------------------------------");
        // BufferedWriter writerDCT;
        // try {
        //     writerDCT = new BufferedWriter(new FileWriter("DCTresults2.txt", true));
            
        //     // Hazardous

        //     writerDCT.append("Hazardous \n");
        //     writerDCT.append("Hazardous DCT for task 1: " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("DINER");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTH, transitionEvalsBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous DCT for task 2: " + "\n");
        //     parsedWorld.setStartLocation("HOME");
        //     parsedWorld.setGoalLocation("OFFICE");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTH, transitionEvalsBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous DCT for task 3: " + "\n");
        //     parsedWorld.setStartLocation("TOWN_HALL");
        //     parsedWorld.setGoalLocation("PARK");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTH, transitionEvalsBlank, writerDCT);

        //     writerDCT.append("\nHazardous & Inconsiderate\n");

        //     // Hazardous and Inconsiderate

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 1: " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("DINER");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTHI, transitionEvalsBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 2: " + "\n");
        //     parsedWorld.setStartLocation("HOME");
        //     parsedWorld.setGoalLocation("OFFICE");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTHI, transitionEvalsBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 3: " + "\n");
        //     parsedWorld.setStartLocation("TOWN_HALL");
        //     parsedWorld.setGoalLocation("PARK");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTHI, transitionEvalsBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        


        // ---------------------------------- PFD ----------------------------------
        
        // PFD definitions of duties
        
        List<Integer> contextPFD = new ArrayList<>();
        contextPFD.add(0);
        
        
        StateProfile smoothOperationState = new StateProfile("ALL", "ALL", "NONE", "LIGHT",false);
        String smoothOperationAction = "TO_HIGH || TO_NORMAL";
        Map<StateProfile, String> smoothOperationDuty = new HashMap<>();
        smoothOperationDuty.put(smoothOperationState, smoothOperationAction);
        
        StateProfile carefulOperationState = new StateProfile("ALL", "ALL", "NONE", "HEAVY",false);
        String carefulOperationAction = "TO_LOW";
        Map<StateProfile, String> carefulOperationDuty = new HashMap<>();
        carefulOperationDuty.put(carefulOperationState, carefulOperationAction);
        
        List<Map<StateProfile, String>> dutiesList = new ArrayList<>(Arrays.asList(smoothOperationDuty, carefulOperationDuty));
        
        Map<Integer, List<Map<StateProfile, String>>> contextIndexToDutiesList = new HashMap<>();
        contextIndexToDutiesList.put(0,dutiesList);
        
        PFDFactory pfdFactory = new PFDFactory(parsedWorld, contextIndexToDutiesList);
        pfdFactory.createEvaluations();
        
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsPFD = pfdFactory.getTransitionEval();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsPFDBlank = pfdFactory.getBlankTransitionEvals(1);
        
        
        
        // System.out.println("---------------------------------- PFD ----------------------------------");
        // BufferedWriter writerPFD;
        // try {
        //     writerPFD = new BufferedWriter(new FileWriter("PFDresults2.txt", true));

        //     writerPFD.append("0 tolerance PFD for task 1: " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("DINER");
        //     showLoss(contextPFD, parsedWorld, transitionEvalsPFD, transitionEvalsPFDBlank, writerPFD);

        //     writerPFD.append("\n");

        //     writerPFD.append("0 tolerance PFD for task 2: " + "\n");
        //     parsedWorld.setStartLocation("HOME");
        //     parsedWorld.setGoalLocation("OFFICE");
        //     showLoss(contextPFD, parsedWorld, transitionEvalsPFD, transitionEvalsPFDBlank, writerPFD);

        //     writerPFD.append("\n");

        //     writerPFD.append("0 tolerance PFD for task 3: " + "\n");
        //     parsedWorld.setStartLocation("TOWN_HALL");
        //     parsedWorld.setGoalLocation("PARK");
        //     showLoss(contextPFD, parsedWorld, transitionEvalsPFD, transitionEvalsPFDBlank, writerPFD);

        //     writerPFD.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        


        // ---------------------------------- VE ----------------------------------
        



        // Proactive: Avoid the Highway and School  states

        StateProfile trajectoryStateP1 = new StateProfile("GAS_STATION", "ALL", "ALL", "ALL",true);
        String trajectoryActionP1 = "TURN_ONTO_ROUTE_116 || TURN_ONTO_SERVICE_ROAD_REVERSED || TURN_ONTO_COLLEGE_STREET_REVERSED";
        StateProfile trajectorySuccessorStateP1 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive1 = new VirtueEthicsData(trajectoryStateP1, trajectoryActionP1, trajectorySuccessorStateP1);
        
        StateProfile trajectoryStateP5 = new StateProfile("OFFICE", "ALL", "ALL", "ALL",true);
        String trajectoryActionP5 = "TURN_ONTO_ROUTE_9 || TURN_ONTO_OAK_ROAD_REVERSED";
        StateProfile trajectorySuccessorStateP5 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive5 = new VirtueEthicsData(trajectoryStateP5, trajectoryActionP5, trajectorySuccessorStateP5);
        
        
        StateProfile trajectoryStateP9 = new StateProfile("HOME", "ALL", "ALL", "ALL",true);
        String trajectoryActionP9 = "TURN_ONTO_GRAY_STREET";
        StateProfile trajectorySuccessorStateP9 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive9 = new VirtueEthicsData(trajectoryStateP9, trajectoryActionP9, trajectorySuccessorStateP9);
        
        
        StateProfile trajectoryStateP11 = new StateProfile("TRAIN_STATION", "ALL", "ALL", "ALL",true);
        String trajectoryActionP11 = "TURN_ONTO_GRAY_STREET_REVERSED || TURN_ONTO_MERRICK_ROAD || TURN_ONTO_SERVICE_ROAD";
        StateProfile trajectorySuccessorStateP11 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive11 = new VirtueEthicsData( trajectoryStateP11, trajectoryActionP11, trajectorySuccessorStateP11);
        
        StateProfile trajectoryStateP15 = new StateProfile("CAFE", "ALL", "ALL", "ALL",true);
        String trajectoryActionP15 = "TURN_ONTO_MAIN_STREET";
        StateProfile trajectorySuccessorStateP15 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive15 = new VirtueEthicsData(trajectoryStateP15, trajectoryActionP15, trajectorySuccessorStateP15);

        StateProfile trajectoryStateP16 = new StateProfile("SCHOOL", "ALL", "ALL", "ALL",true);
        String trajectoryActionP16 = "TURN_ONTO_MATOON_STREET || TURN_ONTO_TRIANGLE_STREET || TURN_ONTO_ASTOR_DRIVE";
        StateProfile trajectorySuccessorStateP16 = new StateProfile("ALL", "ALL", "ALL", "ALL",false);
        VirtueEthicsData proactive16 = new VirtueEthicsData(trajectoryStateP16, trajectoryActionP16, trajectorySuccessorStateP16);


        // Cautious, light -> to normal, heavy -> to low

        StateProfile trajectoryStateC1Large = new StateProfile("ALL", "ALL", "NONE", "LIGHT",false);
        String trajectoryActionC1Large = "TO_NORMAL";
        StateProfile trajectorySuccessorStateC1Large = new StateProfile("ALL", "ALL", "NORMAL", "LIGHT",false);

        VirtueEthicsData cautious_1Large = new VirtueEthicsData(trajectoryStateC1Large, trajectoryActionC1Large, trajectorySuccessorStateC1Large);

        StateProfile trajectoryStateC2Large = new StateProfile("ALL", "ALL", "NONE", "HEAVY",false);
        String trajectoryActionC2Large = "TO_LOW";
        StateProfile trajectorySuccessorStateC2Large = new StateProfile("ALL", "ALL", "LOW", "HEAVY",false);

        VirtueEthicsData cautious_2Large = new VirtueEthicsData(trajectoryStateC2Large, trajectoryActionC2Large, trajectorySuccessorStateC2Large);

        List<VirtueEthicsData> dataListVEproactive = new ArrayList<>(Arrays.asList(proactive1 ,proactive5, proactive9, proactive11, proactive15, proactive16));
        List<VirtueEthicsData> dataListVEcautious = new ArrayList<>(Arrays.asList(cautious_1Large, cautious_2Large));
        

        List<Integer> contextVEsmall = new ArrayList<>(Arrays.asList(0,1));
        Map<Integer, List<VirtueEthicsData>> dataMapVESmall = Collections.singletonMap(0, dataListVEcautious);

        VEFactory veFactory = new VEFactory(parsedWorld);
        veFactory.createPositiveEvaluations(dataMapVESmall);
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVEsmall = veFactory.getTransitionEval();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVEsmallBlank = veFactory.getBlankTransitionEvals(2);

        veFactory.clearTransitionEvals();

        List<Integer> contextVElarge = new ArrayList<>(Arrays.asList(0,1, 0,1));
        Map<Integer, List<VirtueEthicsData>> dataMapVELarge =   new HashMap<>(Map.ofEntries(entry(2, dataListVEproactive),
                                                                                            entry(0, dataListVEcautious)));
        veFactory.createPositiveEvaluations(dataMapVELarge);

        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVElarge = veFactory.getTransitionEval();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVElargeBlank = veFactory.getBlankTransitionEvals(4);

        // Avoid
        StateProfile avoidState = new StateProfile("ALL", "HIGHWAY", "NONE", "ALL", false);
        String avoidAction = "TO_LOW || TO_NORMAL || TO_HIGH";
        StateProfile avoidSuccessorState = new StateProfile("ALL", "HIGHWAY", "ALL", "ALL",false);

        // StateProfile avoidState = new StateProfile("GAS_STATION", "ALL", "ALL", "ALL", true);
        // String avoidAction = "TURN_ONTO_SUNRISE_HIGHWAY";
        // StateProfile avoidSuccessorState = new StateProfile("ALL", "HIGHWAY", "NONE", "ALL",false);

        // StateProfile avoidState2 = new StateProfile("OFFICE", "ALL", "ALL", "ALL", true);
        // String avoidAction2 = "TURN_ONTO_SUNRISE_HIGHWAY_REVERSED";
        // StateProfile avoidSuccessorState2 = new StateProfile("ALL", "HIGHWAY", "NONE", "ALL",false);

        VirtueEthicsData avoidData = new VirtueEthicsData(avoidState, avoidAction, avoidSuccessorState);
        // VirtueEthicsData avoidData2 = new VirtueEthicsData(avoidState2, avoidAction2, avoidSuccessorState2);
        List<VirtueEthicsData> avoidList = new ArrayList<>(Arrays.asList(avoidData));

        List<Integer> avoidContext = new ArrayList<>(Arrays.asList(0,1));

        Map<Integer,List<VirtueEthicsData>> avoidMap = new HashMap<>(Collections.singletonMap(0, avoidList));

        VEFactory veAvoid = new VEFactory(parsedWorld);
        veAvoid.createNegativeTrajectoryEvals(avoidMap);
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsAvoid = veAvoid.getTransitionEval();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsAvoidBlank = veAvoid.getBlankTransitionEvals(2);
        

        System.out.println("---------------------------------- VE ----------------------------------");
        BufferedWriter writerVE;
        try {
            writerVE = new BufferedWriter(new FileWriter("VEresults2.txt", true));

            // Avoid
            
            // writerVE.append("Avoid Highway from home to office " + "\n");
            // parsedWorld.setStartLocation("HOME");
            // parsedWorld.setGoalLocation("OFFICE");
            // showLoss(avoidContext, parsedWorld, transitionEvalsAvoid, transitionEvalsAvoidBlank, writerVE);

            // writerVE.append("\n");
            
            // Small
            
            writerVE.append("Small VE trajectory list \n");
            writerVE.append("Small VE trajectory list for task 1: " + "\n");
            parsedWorld.setStartLocation("SCHOOL");
            parsedWorld.setGoalLocation("DINER");
            showLoss(contextVEsmall, parsedWorld, transitionEvalsVEsmall, transitionEvalsVEsmallBlank, writerVE);

            writerVE.append("\n");

            writerVE.append("Small VE trajectory list for task 2: " + "\n");
            parsedWorld.setStartLocation("HOME");
            parsedWorld.setGoalLocation("OFFICE");
            showLoss(contextVEsmall, parsedWorld, transitionEvalsVEsmall, transitionEvalsVEsmallBlank, writerVE);

            writerVE.append("\n");

            writerVE.append("Small VE trajectory list for task 3: " + "\n");
            parsedWorld.setStartLocation("TOWN_HALL");
            parsedWorld.setGoalLocation("PARK");
            showLoss(contextVEsmall, parsedWorld, transitionEvalsVEsmall, transitionEvalsVEsmallBlank, writerVE);

            writerVE.append("\nLarge VE trajectory list\n");

            // Large

            writerVE.append("Large VE trajectory list for task 1: " + "\n");
            parsedWorld.setStartLocation("SCHOOL");
            parsedWorld.setGoalLocation("DINER");
            showLoss(contextVElarge, parsedWorld, transitionEvalsVElarge, transitionEvalsVElargeBlank, writerVE);

            writerVE.append("\n");

            writerVE.append("Large VE trajectory list for task 2: " + "\n");
            parsedWorld.setStartLocation("HOME");
            parsedWorld.setGoalLocation("OFFICE");
            showLoss(contextVElarge, parsedWorld, transitionEvalsVElarge, transitionEvalsVElargeBlank, writerVE);

            writerVE.append("\n");

            writerVE.append("Large VE trajectory list for task 3: " + "\n");
            parsedWorld.setStartLocation("TOWN_HALL");
            parsedWorld.setGoalLocation("PARK");
            showLoss(contextVElarge, parsedWorld, transitionEvalsVElarge, transitionEvalsVElargeBlank, writerVE);

            writerVE.append("\n");

            writerVE.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
/* 
        // ------------------------------------------ Combined ------------------------------------------

        // List<Integer> combinedContext = new ArrayList<>(Arrays.asList(0, 0, 0,1));
        // Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsCombined = new HashMap<>();
        // Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsCombinedBlank = new HashMap<>();

        // // DCT
        // StateProfile noHighSpeed = new StateProfile("ALL", "ALL", "HIGH", "ALL", false);
        // List<StateProfile> noHighSpeedList = new ArrayList<>(Arrays.asList(noHighSpeed));
        // Map<Integer, List<StateProfile>> profileMapNoHighSpeed = Collections.singletonMap(0, noHighSpeedList);
        // DCTFactory dctCombined = new DCTFactory(parsedWorld, profileMapNoHighSpeed);
        // dctCombined.createEvaluations();
        // transitionEvalsCombined.put(0, dctCombined.getTransitionEvalForContextIndex(0));
        // transitionEvalsCombinedBlank.put(0, dctCombined.getBlankTransitionEvals(1).get(0));

        // // PFD
        // StateProfile normalOnLightState = new StateProfile("ALL", "ALL", "NONE", "LIGHT",false);
        // String normalOnLightAction = "TO_NORMAL";
        // Map<StateProfile, String> normalOnLightDuty = new HashMap<>();
        // normalOnLightDuty.put(normalOnLightState, normalOnLightAction);

        // List<Map<StateProfile, String>> dutyList = new ArrayList<>(Arrays.asList(normalOnLightDuty));

        // Map<Integer, List<Map<StateProfile, String>>> contextIndexToDutyMap = new HashMap<>();
        // contextIndexToDutyMap.put(1, dutyList);

        // PFDFactory pfdCombined = new PFDFactory(parsedWorld, contextIndexToDutyMap);
        // pfdCombined.createEvaluations();

        // transitionEvalsCombined.put(1, pfdCombined.getTransitionEvalForContextIndex(1));
        // transitionEvalsCombinedBlank.put(1, pfdCombined.getBlankTransitionEvals(1).get(0));

        // // VE
        // StateProfile lowOnHeavyTrajectoryState = new StateProfile("ALL", "ALL", "NONE", "HEAVY",false);
        // String lowOnHeavyTrajectoryTrajectoryAction = "TO_LOW";
        // StateProfile lowOnHeavyTrajectorytrajectorySuccessorState = new StateProfile("ALL", "ALL", "LOW", "HEAVY",false);

        // VirtueEthicsData lowOnHeavyData = new VirtueEthicsData(lowOnHeavyTrajectoryState, lowOnHeavyTrajectoryTrajectoryAction, lowOnHeavyTrajectorytrajectorySuccessorState);

        // List<VirtueEthicsData> dataListLowOnHeavy = new ArrayList<>(Arrays.asList(lowOnHeavyData));
        // Map<Integer, List<VirtueEthicsData>> dataMapLowOnHeavy = Collections.singletonMap(2, dataListLowOnHeavy);

        // VEFactory veCombined = new VEFactory(parsedWorld, dataMapLowOnHeavy);
        // veCombined.createEvaluations();
        // transitionEvalsCombined.put(2, veCombined.getTransitionEvalForContextIndex(2));
        // transitionEvalsCombined.put(3, veCombined.getTransitionEvalForContextIndex(3));

        // transitionEvalsCombinedBlank.put(2, veCombined.getBlankTransitionEvals(1).get(0));
        // transitionEvalsCombinedBlank.put(3, veCombined.getBlankTransitionEvals(1).get(0));

        
        // System.out.println("---------------------------------- Combined ----------------------------------");
        // BufferedWriter writerCombined;
        // try {
        //     writerCombined = new BufferedWriter(new FileWriter("CombinedResults.txt", true));
                  
        //     writerCombined.append("Combined: \n");
        //     writerCombined.append("DCT : No high speed in any state " + "\n");
        //     writerCombined.append("PFD : Normal speed in light pedestrian traffic states " + "\n");
        //     writerCombined.append("VE : Low speed in heavy pedestrian traffic states " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("DINER");
        //     showLoss(combinedContext, parsedWorld, transitionEvalsCombined, transitionEvalsCombinedBlank, writerCombined);

        //     writerCombined.append("\n");

        //     writerCombined.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        // List<Integer> combinedContextTrain = new ArrayList<>(Arrays.asList(0, 0,1));
        // Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsTrain = new HashMap<>();
        // Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsTrainBlank = new HashMap<>();

        // pfdCombined.clearTransitionEvals();

        // StateProfile trainStation = new StateProfile("TRAIN_STATION", "ALL", "ALL", "ALL",true);
        // String turnToServiceRoad = "TURN_ONTO_SERVICE_ROAD";
        // Map<StateProfile, String> trainStationDuty = new HashMap<>();
        // trainStationDuty.put(trainStation, turnToServiceRoad);

        // List<Map<StateProfile, String>> trainStationDutyList = new ArrayList<>(Arrays.asList(trainStationDuty));

        // Map<Integer, List<Map<StateProfile, String>>> contextIndexToDutyTrainStation = new HashMap<>();
        // contextIndexToDutyTrainStation.put(0, trainStationDutyList);

        // pfdCombined.setContextToDuties(contextIndexToDutyTrainStation);
        // pfdCombined.createEvaluations();

        // transitionEvalsTrain.put(0, pfdCombined.getTransitionEvalForContextIndex(0));
        // transitionEvalsTrainBlank.put(0, pfdCombined.getBlankTransitionEvals(1).get(0));

        // veCombined.clearTransitionEvals();

        // StateProfile trainStationTrajecotryState = new StateProfile("TRAIN_STATION", "ALL", "ALL", "ALL",true);
        // String merrickRoadAction = "TURN_ONTO_MERRICK_ROAD";
        // StateProfile merrickRoadSuccessor = new StateProfile("ALL", "ALL", "NONE", "ALL",false);

        // VirtueEthicsData trainData = new VirtueEthicsData(trainStationTrajecotryState, merrickRoadAction, merrickRoadSuccessor);

        // List<VirtueEthicsData> trainListVE = new ArrayList<>(Arrays.asList(trainData));
        // Map<Integer, List<VirtueEthicsData>> contextToDataList = new HashMap<>(Collections.singletonMap(1, trainListVE));

        // veCombined.setDataMap(contextToDataList);
        // veCombined.createEvaluations();

        // transitionEvalsTrain.put(1, veCombined.getTransitionEvalForContextIndex(1));
        // transitionEvalsTrain.put(2, veCombined.getTransitionEvalForContextIndex(2));

        // transitionEvalsTrainBlank.put(1, veCombined.getBlankTransitionEvals(1).get(0));
        // transitionEvalsTrainBlank.put(2, veCombined.getBlankTransitionEvals(1).get(0));
        

        
        // System.out.println("---------------------------------- Combined ----------------------------------");
        // BufferedWriter writerCombinedTrain;
        // try {
        //     writerCombinedTrain = new BufferedWriter(new FileWriter("CombinedResultsTrain.txt", false));
                  
        //     writerCombinedTrain.append("Combined for train station: \n");
        //     writerCombinedTrain.append("PFD : Turn onto Service Road " + "\n");
        //     writerCombinedTrain.append("VE : Turn onto Merrick Road " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("COLLEGE");
        //     showLoss(combinedContextTrain, parsedWorld, transitionEvalsTrain, transitionEvalsTrainBlank, writerCombinedTrain);

        //     writerCombinedTrain.append("\n");

        //     writerCombinedTrain.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

*/


    }


    public static void showLoss(List<Integer> context, 
                                SelfDrivingCarWorld parsedWorld, 
                                Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsEthics, 
                                Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsBlank, 
                                BufferedWriter writer){

        RewardCalculator rewardCalculatorEthics = new RewardCalculator(context, transitionEvalsEthics, parsedWorld);
        RewardCalculator rewardCalculatorBlank = new RewardCalculator(context, transitionEvalsBlank, parsedWorld);
            
        SelfDrivingCarAgent agentEthics = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorEthics);
        SelfDrivingCarAgent agentBlank = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorBlank);
        

        ValueIteration valueIterationEthics = new ValueIteration(agentEthics, parsedWorld, 0.001, 0.99, 1. , 1. , 1., 1.);
        ValueIteration valueIterationBlank = new ValueIteration(agentBlank, parsedWorld, 0.001, 0.99, 1. , 1. , 1., 1.);

        valueIterationEthics.calculateQValues();
        valueIterationBlank.calculateQValues();

        Map<String, Map<String, Double>> qHarmEthics = valueIterationEthics.getqHarm();
        Map<String, Map<String, Double>> qGoodEthics = valueIterationEthics.getqGood();
        Map<String, Map<String, Double>> qTaskEthics = valueIterationEthics.getqTask();

        Map<String, Map<String, Double>> qHarmBlank = valueIterationBlank.getqHarm();
        Map<String, Map<String, Double>> qGoodBlank = valueIterationBlank.getqGood();
        Map<String, Map<String, Double>> qTaskBlank = valueIterationBlank.getqTask();

        PolicyExtractor policyExtractor = new PolicyExtractor();
        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

        Map<String, List<String>> policyHarmEthics = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmEthics);
        Map<String, List<String>> policyHarmGoodEthics = policyExtractor.maximizingExtractor(policyHarmEthics, qGoodEthics);
        Map<String, List<String>> policyHarmGoodTaskEthics = policyExtractor.minimizingExtractor(policyHarmGoodEthics, qTaskEthics);

        Map<String, List<String>> policyHarmBlank = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmBlank); 
        Map<String, List<String>> policyHarmGoodBlank = policyExtractor.maximizingExtractor(policyHarmBlank, qGoodBlank); 
        Map<String, List<String>> policyHarmGoodTaskBlank = policyExtractor.minimizingExtractor(policyHarmGoodBlank, qTaskBlank); 

        for(Map.Entry<String, List<String>> policyEntry : policyHarmGoodTaskEthics.entrySet()){
            System.out.println(policyEntry);
        }

        // System.out.println("TRAIN_STATION harm policy: " + policyHarmEthics.get("TRAIN_STATION"));
        // System.out.println("TRAIN_STATION harm qValues: " + qHarmEthics.get("TRAIN_STATION"));

        // System.out.println("TRAIN_STATION harmGood policy: " + policyHarmGoodEthics.get("TRAIN_STATION"));
        // System.out.println("TRAIN_STATION good qValues: " + qGoodEthics.get("TRAIN_STATION"));

        // System.out.println("TRAIN_STATION harmGoodTask policy: " + policyHarmGoodTaskEthics.get("TRAIN_STATION"));
        // System.out.println("TRAIN_STATION task qValues: " + qTaskEthics.get("TRAIN_STATION"));

        // System.out.println("GAS_STATION harm policy: " + policyHarmEthics.get("GAS_STATION"));
        // System.out.println("GAS_STATION harm qValues: " + qHarmEthics.get("GAS_STATION"));

        // System.out.println("GAS_STATION harmGood policy: " + policyHarmGoodEthics.get("GAS_STATION"));
        // System.out.println("GAS_STATION good qValues: " + qGoodEthics.get("GAS_STATION"));

        // System.out.println("GAS_STATION harmGoodTask policy: " + policyHarmGoodTaskEthics.get("GAS_STATION"));
        // System.out.println("GAS_STATION task qValues: " + qTaskEthics.get("GAS_STATION"));



        BufferedWriter writer2;
        try {
            writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
            Map<Integer, Map<String, Integer>> hasContextMap = rewardCalculatorEthics.getStateEval();
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

        BufferedWriter writer3;
        try {
            writer3 = new BufferedWriter(new FileWriter("outputTransition.txt", false));
        
            for(Map.Entry<Integer, Map<String, Map<String, Map<String, Integer>>>> hasContextEnrty : transitionEvalsEthics.entrySet()){
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



        Map<String, Double> vHarmGoodTaskEthics = valueIterationEthics.calculateAndReturnVforPolicy(policyHarmGoodTaskEthics);
        Map<String, Double> vTaskBlank = valueIterationBlank.calculateAndReturnVforPolicy(policyHarmGoodTaskBlank);

        Double maxDiff = -Double.MAX_VALUE;
        String diffState = "";
        for(String state : parsedWorld.getAllStateKeys()){
            if(maxDiff < vHarmGoodTaskEthics.get(state) - vTaskBlank.get(state)){
                maxDiff = vHarmGoodTaskEthics.get(state) - vTaskBlank.get(state);
                diffState = state;
            }
        }


        try {
            writer.append("Most difference in state: TASK[" + diffState +"]= " + vTaskBlank.get(diffState) + "; Ethics["+ diffState + "]= " + vHarmGoodTaskEthics.get(diffState) + "\n");

            writer.append("Highest difference value: " + maxDiff + "\n");

            Double lossDCT = (maxDiff / vTaskBlank.get(diffState)) * 100;
            writer.append("Highest loss: " + lossDCT + "%\n");
    
            writer.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vTaskBlank.get(parsedWorld.getStartLocation()) + "; Ethics["+parsedWorld.getStartLocation()+"]="+vHarmGoodTaskEthics.get(parsedWorld.getStartLocation()) + "\n");
            Double dif = vHarmGoodTaskEthics.get(parsedWorld.getStartLocation()) - vTaskBlank.get(parsedWorld.getStartLocation());
            writer.append("Difference at start location:" + dif + "\n");
            Double lossAtStart = (dif / vTaskBlank.get(parsedWorld.getStartLocation())) * 100;
            writer.append("Loss at start location: " + lossAtStart + "%\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
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
        

    }
*/
    // public static void showLossPFD(List<Integer> context, Map<Integer, Map<StateProfile, String>> contextToDuties, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor, BufferedWriter writerPFD){
        
    //     EvaluationFactory evaluationFactoryPFD = new EvaluationFactory(context, parsedWorld);

    //     evaluationFactoryPFD.createPFDevals(contextToDuties);
    //     // Map<Integer,Map<String,Map<String,Integer>>> stateActionEvalPFD = evaluationFactoryPFD.getStateActionEval();
    //     // Map<Integer,Map<String,Integer>> stateEvalPFD = evaluationFactoryPFD.getStateEval();

    //     Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsPFD = evaluationFactoryPFD.getTransitionEval();
    //     Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDummy = evaluationFactoryPFD.getTransitionEvalsWithEvaluationValue(Integer.MAX_VALUE);
        
        
    //     RewardCalculator rewardCalculatorPFD = new RewardCalculator(context, transitionEvalsPFD, parsedWorld);
    //     RewardCalculator rewardCalculatorDummy = new RewardCalculator(context, transitionEvalsDummy, parsedWorld);

        
    //     SelfDrivingCarAgent agentPFD = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorPFD);
    //     SelfDrivingCarAgent agentDummy = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorDummy);


    //     ValueIteration valueIterationPFD = new ValueIteration(agentPFD, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);
    //     ValueIteration valueIterationDummy = new ValueIteration(agentDummy, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

    //     valueIterationPFD.calculateQValues();
    //     valueIterationDummy.calculateQValues();

    //     Map<String, Map<String, Double>> qHarmPFD = valueIterationPFD.getqHarm();
    //     Map<String, Map<String, Double>> qTaskPFD = valueIterationPFD.getqTask();

    //     Map<String, Map<String, Double>> qHarmDummy = valueIterationDummy.getqHarm();
    //     Map<String, Map<String, Double>> qTaskDummy = valueIterationDummy.getqTask();
        
        
    //     Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

    //     Map<String, List<String>> policyHarmPFD = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmPFD);

    //     Map<String, List<String>> policyHarmTaskPFD = policyExtractor.minimizingExtractor(policyHarmPFD, qTaskPFD);

    //     // Map<String, List<String>> policyTaskPFD = policyExtractor.minimizingExtractor(genericExtractionTarget, qTaskPFD); 
        
    //     Map<String, List<String>> policyHarmDummy = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmDummy);
    //     Map<String, List<String>> policyHarmTaskDummy = policyExtractor.minimizingExtractor(policyHarmDummy, qTaskDummy);

    //     // for(String state : parsedWorld.getAllStateKeys()){
    //     //     System.out.println("State: " + state + ", policy[state]: " + policyHarmTaskPFD.get(state));
    //     // }


        
    //     Map<String, Double> vHarmTaskPFD = valueIterationPFD.calculateAndReturnVforPolicy(policyHarmTaskPFD);
    //     Map<String, Double> vTaskDummy = valueIterationPFD.calculateAndReturnVforPolicy(policyHarmTaskDummy);

    //     Double maxDiffPFD = -Double.MAX_VALUE;
    //     String diffStatePFD = "";
    //     for(String state : parsedWorld.getAllStateKeys()){
    //         if(maxDiffPFD < vHarmTaskPFD.get(state) - vTaskDummy.get(state)){
    //             maxDiffPFD = vHarmTaskPFD.get(state) - vTaskDummy.get(state);
    //             diffStatePFD = state;
    //         }
    //     }


    //     try {
    //         writerPFD.append("Most difference in state: TASK[" + diffStatePFD +"]= " + vTaskDummy.get(diffStatePFD) + "; HARM["+ diffStatePFD + "]= " + vHarmTaskPFD.get(diffStatePFD) + "\n");

    //         writerPFD.append("Highest difference value: " + maxDiffPFD + "\n");

    //         Double lossPFD = (maxDiffPFD / vTaskDummy.get(diffStatePFD)) * 100;
    //         writerPFD.append("Highest loss: " + lossPFD + "%\n");
    
    //         writerPFD.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vTaskDummy.get(parsedWorld.getStartLocation()) + "; HARM["+parsedWorld.getStartLocation()+"]="+vHarmTaskPFD.get(parsedWorld.getStartLocation()) + "\n");
    //         Double dif = vHarmTaskPFD.get(parsedWorld.getStartLocation()) - vTaskDummy.get(parsedWorld.getStartLocation());
    //         writerPFD.append("Difference at start location: " + dif + "\n");
    //         Double lossAtStart = (dif / vTaskDummy.get(parsedWorld.getStartLocation())) * 100;
    //         writerPFD.append("Loss at start location: " + lossAtStart + "%\n");

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }


    //     // State evaluations output
    //     /*
    //     BufferedWriter writer2;
    //     try {
    //         writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
    //         Map<Integer, Map<String, Integer>> hasContextMap = rewardCalculatorPFD.getStateEval();
    //         for(Map.Entry<Integer,Map<String,Integer>> hasContextEnrty : hasContextMap.entrySet()){
    //             writer2.append("CONTEXT: " + hasContextEnrty.getKey() + "{\n");
    //             Map<String,Integer> hasStateMap = hasContextEnrty.getValue();
    //             for(Map.Entry<String,Integer> hasStateEntry : hasStateMap.entrySet()){
    //                 writer2.append("STATE: " + hasStateEntry.getKey() + " , WORTH: " + hasStateEntry.getValue() + "\n");
    //             }
    //             writer2.append("END CONTEXT: " + hasContextEnrty.getKey() + "}\n");
    //         }


    //         writer2.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     // Transition evaluations output

    //     BufferedWriter writer3;
    //     try {
    //         writer3 = new BufferedWriter(new FileWriter("outputTransition.txt", false));
        
    //         for(Map.Entry<Integer, Map<String, Map<String, Map<String, Integer>>>> hasContextEnrty : evaluationFactoryPFD.getTransitionEval().entrySet()){
    //             Map<String, Map<String, Map<String, Integer>>> hasStateMap = hasContextEnrty.getValue();
    //             for(Map.Entry<String, Map<String, Map<String, Integer>>> hasStateEntry : hasStateMap.entrySet()){
    //                 Map<String, Map<String, Integer>> hasActionMap = hasStateEntry.getValue();
    //                 for(Map.Entry<String, Map<String, Integer>> hasActionEntry : hasActionMap.entrySet()){
    //                     Map<String, Integer> hasSuccessorMap = hasActionEntry.getValue();
    //                     for(Map.Entry<String, Integer> hasSuccessorEntry : hasSuccessorMap.entrySet()){

    //                         writer3.append("CONTEXT: " + hasContextEnrty.getKey() + "\n" +
    //                                         "\t STATE: " + hasStateEntry.getKey() + "\n" +
    //                                         "\t\t ACTION: " + hasActionEntry.getKey() + "\n" +
    //                                         "\t\t\t SUCCESSOR: " + hasSuccessorEntry.getKey() + "\n" +
    //                                         "\t\t\t\t EVALUATION: " + hasSuccessorEntry.getValue() + "\n");
    //                     }
    //                 }
    //             }
    //         }


    //         writer3.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     */


    // }

    // public static void showLossVE(List<Integer> context, List<VirtueEthicsData> dataListVE, SelfDrivingCarWorld parsedWorld, PolicyExtractor policyExtractor, BufferedWriter writerVE){
        
    //     EvaluationFactory evaluationFactoryVE = new EvaluationFactory(context, parsedWorld);

    //     evaluationFactoryVE.createVEevals(dataListVE);
        
    //     Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVE = evaluationFactoryVE.getTransitionEval();
    //     Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsDummy = evaluationFactoryVE.getTransitionEvalsWithEvaluationValue(Integer.MAX_VALUE);

    //     // State evaluations output
    //     /*
    //     BufferedWriter writer2;
    //     try {
    //         writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
    //         Map<Integer, Map<String, Integer>> hasContextMap = rewardCalculatorPFD.getStateEval();
    //         for(Map.Entry<Integer,Map<String,Integer>> hasContextEnrty : hasContextMap.entrySet()){
    //             writer2.append("CONTEXT: " + hasContextEnrty.getKey() + "{\n");
    //             Map<String,Integer> hasStateMap = hasContextEnrty.getValue();
    //             for(Map.Entry<String,Integer> hasStateEntry : hasStateMap.entrySet()){
    //                 writer2.append("STATE: " + hasStateEntry.getKey() + " , WORTH: " + hasStateEntry.getValue() + "\n");
    //             }
    //             writer2.append("END CONTEXT: " + hasContextEnrty.getKey() + "}\n");
    //         }


    //         writer2.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     // Transition evaluations output

    //     BufferedWriter writer3;
    //     try {
    //         writer3 = new BufferedWriter(new FileWriter("outputTransition.txt", false));
        
    //         for(Map.Entry<Integer, Map<String, Map<String, Map<String, Integer>>>> hasContextEnrty : evaluationFactoryPFD.getTransitionEval().entrySet()){
    //             Map<String, Map<String, Map<String, Integer>>> hasStateMap = hasContextEnrty.getValue();
    //             for(Map.Entry<String, Map<String, Map<String, Integer>>> hasStateEntry : hasStateMap.entrySet()){
    //                 Map<String, Map<String, Integer>> hasActionMap = hasStateEntry.getValue();
    //                 for(Map.Entry<String, Map<String, Integer>> hasActionEntry : hasActionMap.entrySet()){
    //                     Map<String, Integer> hasSuccessorMap = hasActionEntry.getValue();
    //                     for(Map.Entry<String, Integer> hasSuccessorEntry : hasSuccessorMap.entrySet()){

    //                         writer3.append("CONTEXT: " + hasContextEnrty.getKey() + "\n" +
    //                                         "\t STATE: " + hasStateEntry.getKey() + "\n" +
    //                                         "\t\t ACTION: " + hasActionEntry.getKey() + "\n" +
    //                                         "\t\t\t SUCCESSOR: " + hasSuccessorEntry.getKey() + "\n" +
    //                                         "\t\t\t\t EVALUATION: " + hasSuccessorEntry.getValue() + "\n");
    //                     }
    //                 }
    //             }
    //         }


    //         writer3.close();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     */
              
    //     RewardCalculator rewardCalculatorVE = new RewardCalculator(context, transitionEvalsVE, parsedWorld);
    //     RewardCalculator rewardCalculatorDummy = new RewardCalculator(context, transitionEvalsDummy, parsedWorld);

    //     SelfDrivingCarAgent agentVE = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorVE);
    //     SelfDrivingCarAgent agentDummy = new SelfDrivingCarAgent(parsedWorld, rewardCalculatorDummy);

    //     ValueIteration valueIterationVE = new ValueIteration(agentVE, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);
    //     ValueIteration valueIterationDummy = new ValueIteration(agentDummy, parsedWorld, 0.001, 0.99, 1. , 1. , 0., 0.);

    //     valueIterationVE.calculateQValues();
    //     valueIterationDummy.calculateQValues();

    //     Map<String, Map<String, Double>> qHarmVE = valueIterationVE.getqHarm();
    //     Map<String, Map<String, Double>> qGoodVE = valueIterationVE.getqGood();
    //     Map<String, Map<String, Double>> qTaskVE = valueIterationVE.getqTask();

    //     Map<String, Map<String, Double>> qHarmDummy = valueIterationDummy.getqHarm();
    //     Map<String, Map<String, Double>> qGoodDummy = valueIterationDummy.getqGood();
    //     Map<String, Map<String, Double>> qTaskDummy = valueIterationDummy.getqTask();

    //     Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

    //     Map<String, List<String>> policyHarmVE = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmVE);

    //     // for(Map.Entry<String, List<String>> e2: policyHarmVE.entrySet()){
    //     //     if(e2.getKey().equals("GAS_STATION")){
    //     //         System.out.print("policyHarmVE: ");
    //     //         System.out.println(e2);
    //     //     }
    //     // }

    //     Map<String, List<String>> policyHarmGoodVE = policyExtractor.maximizingExtractor(policyHarmVE, qGoodVE);

    //     // for(Map.Entry<String, List<String>> e3: policyHarmGoodVE.entrySet()){
    //     //     if(e3.getKey().equals("GAS_STATION")){
    //     //         System.out.print("policyHarmGoodVE: ");
    //     //         System.out.println(e3);
    //     //     }
    //     // }
        
    //     Map<String, List<String>> policyHarmGoodTaskVE = policyExtractor.minimizingExtractor(policyHarmGoodVE, qTaskVE);
        

    //     Map<String, List<String>> policyHarmDummy = policyExtractor.minimizingExtractor(genericExtractionTarget, qHarmDummy); 
    //     Map<String, List<String>> policyHarmGoodDummy = policyExtractor.minimizingExtractor(policyHarmDummy, qGoodDummy); 
    //     Map<String, List<String>> policyHarmGoodTaskDummy = policyExtractor.minimizingExtractor(policyHarmGoodDummy, qTaskDummy); 

    //     // for(Map.Entry<String, List<String>> e4 : policyHarmGoodTaskVE.entrySet()){
    //     //         System.out.println(e4);
    //     // }
        
    //     Map<String, Double> vHarmGoodTaskVE = valueIterationVE.calculateAndReturnVforPolicy(policyHarmGoodTaskVE);
    //     Map<String, Double> vHarmGoodTaskDummy = valueIterationDummy.calculateAndReturnVforPolicy(policyHarmGoodTaskDummy);


    //     Double maxDiffVE = -Double.MAX_VALUE;
    //     String diffStateVE = "";

    //     for(String state : parsedWorld.getAllStateKeys()){
    //         if(maxDiffVE < vHarmGoodTaskVE.get(state) - vHarmGoodTaskDummy.get(state)){
    //             maxDiffVE = vHarmGoodTaskVE.get(state) - vHarmGoodTaskDummy.get(state);
    //             diffStateVE = state;
    //         }
    //     }


    //     try {
    //         writerVE.append("Most difference in state: TASK[" + diffStateVE +"]= " + vHarmGoodTaskDummy.get(diffStateVE) + "; ETHICS["+ diffStateVE + "]= " + vHarmGoodTaskVE.get(diffStateVE) + "\n");

    //         writerVE.append("Highest difference value: " + maxDiffVE + "\n");

    //         Double lossVE = (maxDiffVE / vHarmGoodTaskDummy.get(diffStateVE)) * 100;
    //         writerVE.append("Highest loss: " + lossVE + "%\n");
    
    //         writerVE.append("Values at start location: TASK[" + parsedWorld.getStartLocation() + "]=" + vHarmGoodTaskDummy.get(parsedWorld.getStartLocation()) + "; HARM["+parsedWorld.getStartLocation()+"]="+vHarmGoodTaskVE.get(parsedWorld.getStartLocation()) + "\n");
    //         Double dif = vHarmGoodTaskVE.get(parsedWorld.getStartLocation()) - vHarmGoodTaskDummy.get(parsedWorld.getStartLocation());
    //         writerVE.append("Difference at start location: " + dif + "\n");
    //         Double lossAtStart = (dif / vHarmGoodTaskDummy.get(parsedWorld.getStartLocation())) * 100;
    //         writerVE.append("Loss at start location: " + lossAtStart + "%\n");

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     // System.out.println("Most difference in state: Task[" + diffStateVE +"] : " + vTaskVE.get(diffStateVE) + "; VE["+ diffStateVE + "] : " + vEthicsVE.get(diffStateVE));
    //     // System.out.println("Difference : " + maxDiffVE + " ; in the state : " + diffStateVE);

    //     // Double lossVE = (maxDiffVE / vTaskVE.get(diffStateVE)) * 100;
    //     // System.out.println("Loss % : " + lossVE);

    //     // System.out.println("vTask[HOME] : " + vTaskVE.get("HOME") + ", vHarm[HOME] : " + vEthicsVE.get("HOME"));


    // }

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