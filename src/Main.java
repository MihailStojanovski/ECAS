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

import valueIterationAlgorithms.MaximizingPolicyExtractor;
import valueIterationAlgorithms.MinimizingPolicyExtractor;
import valueIterationAlgorithms.SuccessiveValueIteration;
import valueIterationAlgorithms.ValueIteration;
import agents.SelfDrivingCarAgent;
import factories.DCTFactory;
import factories.EvaluationFactory;
import factories.PFDFactory;
import factories.StateProfile;
import factories.VEFactory;
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

        MinimizingPolicyExtractor policyExtractor = new MinimizingPolicyExtractor();

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
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTH, transitionEvalsDCTBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous DCT for task 2: " + "\n");
        //     parsedWorld.setStartLocation("HOME");
        //     parsedWorld.setGoalLocation("OFFICE");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTH, transitionEvalsDCTBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous DCT for task 3: " + "\n");
        //     parsedWorld.setStartLocation("TOWN_HALL");
        //     parsedWorld.setGoalLocation("PARK");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTH, transitionEvalsDCTBlank, writerDCT);

        //     writerDCT.append("\nHazardous & Inconsiderate\n");

        //     // Hazardous and Inconsiderate

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 1: " + "\n");
        //     parsedWorld.setStartLocation("SCHOOL");
        //     parsedWorld.setGoalLocation("DINER");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTHI, transitionEvalsDCTBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 2: " + "\n");
        //     parsedWorld.setStartLocation("HOME");
        //     parsedWorld.setGoalLocation("OFFICE");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTHI, transitionEvalsDCTBlank, writerDCT);

        //     writerDCT.append("\n");

        //     writerDCT.append("Hazardous & Inconsiderate DCT for task 3: " + "\n");
        //     parsedWorld.setStartLocation("TOWN_HALL");
        //     parsedWorld.setGoalLocation("PARK");
        //     showLoss(contextHazardous, parsedWorld, transitionEvalsDCTHI, transitionEvalsDCTBlank, writerDCT);

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
        



        // Proactive: 
        
        // Avoid the Highway state

        StateProfile avoidHighwayState = new StateProfile("ALL", "HIGHWAY", "NONE", "ALL", false);
        String avoidHighwayAction = "TO_LOW || TO_NORMAL || TO_HIGH";
        StateProfile avoidHighwaySuccessorState = new StateProfile("ALL", "HIGHWAY", "ALL", "ALL",false);

        VirtueEthicsData avoidHighwayData = new VirtueEthicsData(avoidHighwayState, avoidHighwayAction, avoidHighwaySuccessorState);

        // Avoid the School state
        StateProfile avoidSchoolState1 = new StateProfile("MATOON_STREET_REVERSED", "ALL", "ALL", "ALL", false);
        String avoidSchoolAction1 = "CRUISE";
        StateProfile avoidSchoolSuccessorState1 = new StateProfile("SCHOOL", "ALL", "ALL", "ALL",true);

        VirtueEthicsData avoidShoolData1 = new VirtueEthicsData(avoidSchoolState1, avoidSchoolAction1, avoidSchoolSuccessorState1);

        StateProfile avoidSchoolState2 = new StateProfile("ASTOR_DRIVE_REVERSED", "ALL", "ALL", "ALL", false);
        String avoidSchoolAction2 = "CRUISE";
        StateProfile avoidSchoolSuccessorState2 = new StateProfile("SCHOOL", "ALL", "ALL", "ALL",true);

        VirtueEthicsData avoidShoolData2 = new VirtueEthicsData(avoidSchoolState2, avoidSchoolAction2, avoidSchoolSuccessorState2);

        StateProfile avoidShoolState3 = new StateProfile("TRIANGLE_STREET_REVERSED", "ALL", "ALL", "ALL", false);
        String avoidShoolAction3 = "CRUISE";
        StateProfile avoidShoolSuccessorState3 = new StateProfile("SCHOOL", "ALL", "ALL", "ALL",true);

        VirtueEthicsData avoidShoolData3 = new VirtueEthicsData(avoidShoolState3, avoidShoolAction3, avoidShoolSuccessorState3);


        // Cautious, light -> to normal, heavy -> to low

        StateProfile trajectoryStateC1Large = new StateProfile("ALL", "ALL", "NONE", "LIGHT",false);
        String trajectoryActionC1Large = "TO_NORMAL";
        StateProfile trajectorySuccessorStateC1Large = new StateProfile("ALL", "ALL", "NORMAL", "LIGHT",false);

        VirtueEthicsData cautious_1Large = new VirtueEthicsData(trajectoryStateC1Large, trajectoryActionC1Large, trajectorySuccessorStateC1Large);

        StateProfile trajectoryStateC2Large = new StateProfile("ALL", "ALL", "NONE", "HEAVY",false);
        String trajectoryActionC2Large = "TO_LOW";
        StateProfile trajectorySuccessorStateC2Large = new StateProfile("ALL", "ALL", "LOW", "HEAVY",false);

        
        VirtueEthicsData cautious_2Large = new VirtueEthicsData(trajectoryStateC2Large, trajectoryActionC2Large, trajectorySuccessorStateC2Large);
        
        StateProfile goalState = new StateProfile("PARK", "ALL", "ALL", "ALL",true);
        String goalAction = "STAY";
        StateProfile goalState2 = new StateProfile("PARK", "ALL", "ALL", "ALL",true);
        VirtueEthicsData goalData = new VirtueEthicsData(goalState, goalAction, goalState2);
        // Change the goal data as each goal changes, make a method that takes a string and does the changes we need for the world as well


        List<VirtueEthicsData> dataListVEproactive = new ArrayList<>(Arrays.asList(avoidHighwayData, avoidShoolData1, avoidShoolData2, avoidShoolData3));
        List<VirtueEthicsData> dataListVEcautious = new ArrayList<>(Arrays.asList(cautious_1Large, cautious_2Large, goalData));
        
        List<Integer> contextVEsmall = new ArrayList<>(Arrays.asList(0,1));
        Map<Integer, List<VirtueEthicsData>> dataMapVESmall = Collections.singletonMap(0, dataListVEcautious);

        VEFactory veFactory = new VEFactory(parsedWorld);
        veFactory.createPositiveEvaluations(dataMapVESmall);
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVEsmall = veFactory.getTransitionEval();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVEsmallBlank = veFactory.getBlankTransitionEvals(2);

        veFactory.clearTransitionEvals();

        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVElarge = new HashMap<>();
        Map<Integer, Map<String, Map<String, Map<String, Integer>>>> transitionEvalsVElargeBlank = veFactory.getBlankTransitionEvals(4);

        List<Integer> contextVElarge = new ArrayList<>(Arrays.asList(0,1, 0,1));
        Map<Integer, List<VirtueEthicsData>> dataMapCautious =   new HashMap<>(Map.ofEntries((entry(0, dataListVEcautious))));
        veFactory.createPositiveEvaluations(dataMapCautious);
        transitionEvalsVElarge.put(0,veFactory.getTransitionEvalForContextIndex(0));
        transitionEvalsVElarge.put(1,veFactory.getTransitionEvalForContextIndex(1));

        Map<Integer, List<VirtueEthicsData>> dataMapProactive =   new HashMap<>(Map.ofEntries((entry(2, dataListVEproactive))));
        veFactory.createNegativeTrajectoryEvals(dataMapProactive);

        transitionEvalsVElarge.put(2,veFactory.getTransitionEvalForContextIndex(2));
        transitionEvalsVElarge.put(3,veFactory.getTransitionEvalForContextIndex(3));



        // Avoid



        List<VirtueEthicsData> avoidListSchool = new ArrayList<>(Arrays.asList(avoidHighwayData));

        List<Integer> avoidContext = new ArrayList<>(Arrays.asList(0,1));

        Map<Integer,List<VirtueEthicsData>> avoidMap = new HashMap<>(Collections.singletonMap(0, avoidListSchool));

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
            // parsedWorld.setStartLocation("SCHOOL");
            // parsedWorld.setGoalLocation("DINER");
            // showLoss(avoidContext, parsedWorld, transitionEvalsAvoid, transitionEvalsAvoidBlank, writerVE);

            // writerVE.append("\n");
            
            // Small
            
            // writerVE.append("Small VE trajectory list \n");
            // writerVE.append("Small VE trajectory list for task 1: " + "\n");
            // parsedWorld.setStartLocation("SCHOOL");
            // parsedWorld.setGoalLocation("DINER");
            // showLoss(contextVEsmall, parsedWorld, transitionEvalsVEsmall, transitionEvalsVEsmallBlank, writerVE);

            // writerVE.append("\n");

            // writerVE.append("Small VE trajectory list for task 2: " + "\n");
            // parsedWorld.setStartLocation("HOME");
            // parsedWorld.setGoalLocation("OFFICE");
            // showLoss(contextVEsmall, parsedWorld, transitionEvalsVEsmall, transitionEvalsVEsmallBlank, writerVE);

            // writerVE.append("\n");

            // writerVE.append("Small VE trajectory list for task 3: " + "\n");
            // parsedWorld.setStartLocation("TOWN_HALL");
            // parsedWorld.setGoalLocation("PARK");
            // showLoss(contextVEsmall, parsedWorld, transitionEvalsVEsmall, transitionEvalsVEsmallBlank, writerVE);

            // Large
            
            // writerVE.append("\nLarge VE trajectory list\n");

            // writerVE.append("Large VE trajectory list for task 1: " + "\n");
            // parsedWorld.setStartLocation("SCHOOL");
            // parsedWorld.setGoalLocation("DINER");
            // showLoss(contextVElarge, parsedWorld, transitionEvalsVElarge, transitionEvalsVElargeBlank, writerVE);

            // writerVE.append("\n");

            // writerVE.append("Large VE trajectory list for task 2: " + "\n");
            // parsedWorld.setStartLocation("HOME");
            // parsedWorld.setGoalLocation("OFFICE");
            // showLoss(contextVElarge, parsedWorld, transitionEvalsVElarge, transitionEvalsVElargeBlank, writerVE);

            // writerVE.append("\n");

            // writerVE.append("Large VE trajectory list for task 3: " + "\n");
            // parsedWorld.setStartLocation("TOWN_HALL");
            // parsedWorld.setGoalLocation("PARK");
            // showLoss(contextVElarge, parsedWorld, transitionEvalsVElarge, transitionEvalsVElargeBlank, writerVE);

            // writerVE.append("\n");

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

    public static void startVEwithParams(String startLocation, String goalLocation){
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

        SuccessiveValueIteration successiveValueIteration = new SuccessiveValueIteration(agentEthics, parsedWorld, 0.001, 0.99, 1. , 1. , 1., 1.);
        Map<String, List<String>> successivePolicyEthics = successiveValueIteration.calculatePolicyForSuccessiveValueIteration();

        SuccessiveValueIteration successiveValueIterationBlank = new SuccessiveValueIteration(agentBlank, parsedWorld, 0.001, 0.99, 1. , 1. , 1., 1.);
        Map<String, List<String>> successivePolicyBlank = successiveValueIterationBlank.calculatePolicyForSuccessiveValueIteration();


        // valueIterationEthics.calculateQValues();
        // valueIterationBlank.calculateQValues();

        // Map<String, Map<String, Double>> qHarmEthics = valueIterationEthics.getqHarm();
        // Map<String, Map<String, Double>> qGoodEthics = valueIterationEthics.getqGood();
        // Map<String, Map<String, Double>> qTaskEthics = valueIterationEthics.getqTask();

        // Map<String, Map<String, Double>> qHarmBlank = valueIterationBlank.getqHarm();
        // Map<String, Map<String, Double>> qGoodBlank = valueIterationBlank.getqGood();
        // Map<String, Map<String, Double>> qTaskBlank = valueIterationBlank.getqTask();

        // MinimizingPolicyExtractor minimizingPolicyExtractor = new MinimizingPolicyExtractor();
        // MaximizingPolicyExtractor maximizingPolicyExtractor = new MaximizingPolicyExtractor();
        // Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();

        // Map<String, List<String>> policyHarmEthics = minimizingPolicyExtractor.extract(genericExtractionTarget, qHarmEthics);
        // Map<String, List<String>> policyHarmGoodEthics = maximizingPolicyExtractor.extract(policyHarmEthics, qGoodEthics);
        // Map<String, List<String>> policyHarmGoodTaskEthics = minimizingPolicyExtractor.extract(policyHarmGoodEthics, qTaskEthics);

        // Map<String, List<String>> policyHarmBlank = minimizingPolicyExtractor.extract(genericExtractionTarget, qHarmBlank); 
        // Map<String, List<String>> policyHarmGoodBlank = maximizingPolicyExtractor.extract(policyHarmBlank, qGoodBlank); 
        // Map<String, List<String>> policyHarmGoodTaskBlank = minimizingPolicyExtractor.extract(policyHarmGoodBlank, qTaskBlank); 

        // for(Map.Entry<String, List<String>> policyEntry : successivePolicyEthics.entrySet()){
        //     System.out.println(policyEntry);
        // }


        // BufferedWriter writer2;
        // try {
        //     writer2 = new BufferedWriter(new FileWriter("outputState.txt", false));
        //     Map<Integer, Map<String, Integer>> hasContextMap = rewardCalculatorEthics.getStateEval();
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

        // BufferedWriter writer3;
        // try {
        //     writer3 = new BufferedWriter(new FileWriter("outputTransition.txt", false));
        
        //     for(Map.Entry<Integer, Map<String, Map<String, Map<String, Integer>>>> hasContextEnrty : transitionEvalsEthics.entrySet()){
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


        // Map<String, Double> vHarmGoodTaskEthics = valueIterationEthics.calculateAndReturnVforPolicy(policyHarmGoodTaskEthics);
        // Map<String, Double> vTaskBlank = valueIterationBlank.calculateAndReturnVforPolicy(policyHarmGoodTaskBlank);


        Map<String, Double> vHarmGoodTaskEthics = successiveValueIteration.calculateAndReturnVforPolicy(successivePolicyEthics);
        Map<String, Double> vTaskBlank = successiveValueIterationBlank.calculateAndReturnVforPolicy(successivePolicyBlank);

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

}