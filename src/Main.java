import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import states.Location;
import states.Road;
import states.State;
import valueIterationAlgorithms.ValueIteration;
import agents.SelfDrivingCarAgent;
import factories.EvaluationFactory;
import factories.ForbiddenStateProfile;
import parsers.WorldMapParser;
import rewards.EthicalReward;
import rewards.Reward;
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
        
        // Forbidden state profiles for DCT as explained in the original paper
        ForbiddenStateProfile hazardous = new ForbiddenStateProfile("ALL", "ALL", "HIGH", "ALL");
        ForbiddenStateProfile inconsiderate = new ForbiddenStateProfile("ALL", "ALL", "NORMAL", "HEAVY");
        
        List<ForbiddenStateProfile> profileList = new ArrayList<>();
        profileList.add(hazardous);
        profileList.add(inconsiderate);

        EvaluationFactory eF = new EvaluationFactory(context, parsedWorld);
        eF.createDCTevals(profileList);
        
        
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEval = eF.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEval = eF.getStateEval();
        
        Reward ethicalReward = new EthicalReward(context, stateActionEval, stateEval, parsedWorld);
        
        SelfDrivingCarAgent agent = new SelfDrivingCarAgent(parsedWorld,ethicalReward);

        ValueIteration vi = new ValueIteration(agent, parsedWorld, 0.7, 0.1, 0.9, 1.0 , 1. , 1., 1.);
        for(Entry<String, List<String>> e : vi.getPolicy().entrySet()){
            System.out.println(e);
        }

    }

}
