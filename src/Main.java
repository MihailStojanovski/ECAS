import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import states.Location;
import states.Road;
import states.State;
import valueIterationAlgorithms.PolicyExtractor;
import valueIterationAlgorithms.ValueIteration;
import agents.SelfDrivingCarAgent;
import factories.EvaluationFactory;
import factories.StateProfile;
import parsers.WorldMapParser;
import rewards.RewardCalculator;
import worlds.SelfDrivingCarWorld;

public class Main {
    public static void main(String[] args) {

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
        

        WorldMapParser parser = new WorldMapParser();
        SelfDrivingCarWorld parsedWorld = parser.getWorldFromJsonMap("maps/SelfDrivingCarMap.json");

        // VI test
        List<Integer> context = new ArrayList<>();
        context.add(0);
        context.add(0);
        
        // Forbidden state profiles for DCT as explained in the original paper Hazardous(H) and Inconsiderate(I)
        StateProfile hazardous = new StateProfile("ALL", "ALL", "HIGH", "ALL");
        StateProfile inconsiderate = new StateProfile("ALL", "ALL", "NORMAL", "HEAVY");
        
        List<StateProfile> profileList = new ArrayList<>();
        profileList.add(hazardous);
        profileList.add(inconsiderate);

        // PFD definitions of duties
        Map<Integer, Map<StateProfile, String>> contextToDuties = new HashMap<>();

        StateProfile smoothOperationState = new StateProfile("ALL", "ALL", "NONE", "LIGHT");
        String smoothOperationAction = "TO_HIGH ORRR TO_NORMAL";
        Map<StateProfile, String> sO = new HashMap<>();
        sO.put(smoothOperationState, smoothOperationAction);
        contextToDuties.put(0,sO);

        StateProfile carefulOperationState = new StateProfile("ALL", "ALL", "NONE", "HEAVY");
        String carefulOperationAction = "TO_LOW";
        Map<StateProfile, String> cO = new HashMap<>();
        cO.put(carefulOperationState, carefulOperationAction);
        contextToDuties.put(1,cO);


        // Evaluation factory setup
        EvaluationFactory eF = new EvaluationFactory(context, parsedWorld);

        // Create evaluations for DCT with H and I
        eF.createDCTevals(profileList);

        // eF.createPFDevals(contextToDuties,3);

               
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEval = eF.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEval = eF.getStateEval();
        
        RewardCalculator ethicalReward = new RewardCalculator(context, stateActionEval, stateEval, parsedWorld);
        
        SelfDrivingCarAgent agent = new SelfDrivingCarAgent(parsedWorld,ethicalReward);

        // If gamma is 0.9, even with convergence choosing the maximum between the three criteria when at the state home the agent chooses to go to the wrong street (MATOON_STREET_REVERSED)
        // Decreasing the convergenceAchieved also gives us the correct entry (with a gamma of 0.9)
        ValueIteration vi = new ValueIteration(agent, parsedWorld, 0.7, 0.01, 0.9, 1. , 1. , 0., 0.);
        
        vi.calculateQValues();

        Map<String, Map<String, Double>> qHarm = vi.getqHarm();
        Map<String, Map<String, Double>> qGood = vi.getqGood();
        Map<String, Map<String, Double>> qTask = vi.getqTask();

        Map<String, List<String>> genericExtractionTarget = parsedWorld.getMapOfStatesAndActions();
        PolicyExtractor pE = new PolicyExtractor();
        Map<String, List<String>> policyHarm = pE.minimizingExtractor(genericExtractionTarget, qHarm);

        Map<String, List<String>> policyHarmTask = pE.minimizingExtractor(policyHarm, qTask);

        Map<String, List<String>> policyGood = pE.maximizingExtractor(genericExtractionTarget, qGood);

        Map<String, List<String>> policyTask = pE.minimizingExtractor(genericExtractionTarget, qTask); 
        
        Map<String, Double> vTask = vi.calculateAndReturnVforPolicy(policyTask);
        Map<String, Double> vHarm = vi.calculateAndReturnVforPolicy(policyHarmTask);


        Double maxDiff = -Double.MAX_VALUE;
        String diffState = "";
        for(String state : parsedWorld.getAllStateKeys()){
            // System.out.println("State : " + state + " vHarm : " + vHarm.get(state) + " vTask : " + vTask.get(state));
            if(maxDiff < vHarm.get(state) - vTask.get(state)){
                maxDiff = vHarm.get(state) - vTask.get(state);
                diffState = state;
            }
        }

        System.out.println("Difference : " + maxDiff + " ; in the state : " + diffState);

        Double cost = maxDiff/vTask.get(diffState);
        System.out.println(cost * 100 );

        System.out.println("vTask[HOME] : " + vTask.get("HOME") + ", vHarm[HOME] : " + vHarm.get("HOME"));
        
        
    }

}
