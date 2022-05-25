import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.DoubleBinaryOperator;

import States.Location;
import States.Road;
import States.State;
import agents.SelfDrivingCarAgent;
import worlds.SelfDrivingCarWorld;

public class Main {
    public static void main(String[] args) {

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

        State startLocation = new Location("HOME");
        State goalLocation = new Location("GAS_STATION");

        SelfDrivingCarWorld world = new SelfDrivingCarWorld(locations, roads, startLocation, goalLocation);

        SelfDrivingCarAgent agent = new SelfDrivingCarAgent(world);

        List<String> locationStrings = new ArrayList<>(Arrays.asList("HOME","TRAIN_STATION","PIZZA_PLACE","COLLEGE","GAS_STATION"));


        //for(String str : agent.getAllStateKeys()){
        //    System.out.println(str);
        //}


        // Get Possible Actions for given state check
        Set<String> possibleActions = new HashSet<>();
        possibleActions.addAll(agent.getPossibleActionsForState("MERRICK_ROAD_NORTH_COUNTY_LOW_HEAVY"));
        System.out.println("MERRICK_ROAD_NORTH_COUNTY_LOW_HEAVY possible actions : " + possibleActions);
        possibleActions.clear();

        possibleActions.addAll(agent.getPossibleActionsForState("TRAIN_STATION"));
        System.out.println("TRAIN_STATION possible actions : " + possibleActions);
        possibleActions.clear();

        possibleActions.addAll(agent.getPossibleActionsForState("MERRICK_ROAD_NORTH_COUNTY_NONE_HEAVY"));
        System.out.println("MERRICK_ROAD_NORTH_COUNTY_NONE_HEAVY possible actions" + possibleActions);
        possibleActions.clear();
        


        // for(String str : agent.getAllActions()){
        //     System.out.println(str);
        // }


        // Transition Function
        // for(String currState : agent.getAllStateKeys()){
        //     for(String action : agent.getAllActions()){
        //         for(String nextState : agent.getAllStateKeys()){
        //             Double transitionProbability = agent.transitionFunction(currState, action, nextState);
        //             if(transitionProbability == 1){
        //                 System.out.println("Transition for : (" + currState + " , " + action + " , " + nextState + ") =  " + transitionProbability);
        //             }
        //         }
        //     }
        // }


        // Reward function
        // for(String stateR : agent.getAllStateKeys()){
        //     for(String actionR : agent.getAllActions()){
        //             Double reward = agent.rewardFunction(stateR, actionR);
        //             if(reward != -3600){
        //                 System.out.println("Reward for : (" + stateR + " , " + actionR +  ") =  " + reward);
        //             }
        //     }
        // }

        
        


    }

    // public Map<String,Double> updatingFunction(Map<String,Double> values,SelfDrivingCarAgent agent){
    //     Map<String,Double> vToReturn = new HashMap<>();
    //     Iterable<String> states = agent.getAllStateKeys();

    //     for(String state : states){
    //         Iterable<String> actions = agent.getPossibleActionsForState(state){

    //         }
    //     }
    // }

}
