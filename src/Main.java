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

import states.Location;
import states.Road;
import states.State;
import agents.SelfDrivingCarAgent;
import factories.EvaluationFactory;
import rewards.EthicalReward;
import rewards.EthicalRewardQuad;
import rewards.Reward;
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

        // Reward Calculator necessities
        List<Integer> context = new ArrayList<>();
        context.add(0);
        
        SelfDrivingCarAgent agent = new SelfDrivingCarAgent(world);
        
        EvaluationFactory eF = new EvaluationFactory(context, agent, Integer.MAX_VALUE);

        eF.setStateEval("COLLEGE", 1, 0);
        Map<Integer,Map<String,Map<String,Integer>>> stateActionEval = eF.getStateActionEval();
        Map<Integer,Map<String,Integer>> stateEval = eF.getStateEval();


        Reward ethicalReward = new EthicalReward(context, stateActionEval, stateEval);

        List<String> locationStrings = new ArrayList<>(Arrays.asList("HOME","TRAIN_STATION","PIZZA_PLACE","COLLEGE","GAS_STATION"));

        for(String currState : agent.getAllStateKeys()){
            for(String action : agent.getPossibleActionsForState(currState)){
                for(String nextState : agent.getPossibleResultingStates(currState, action)){
                    Double reward = agent.rewardFunction(currState, action, nextState, ethicalReward, 1., 1., false);
                        System.out.println("Reward for : (" + currState + " , " + action + " , " + nextState + ") =  " + reward);
                }
            }
        }
     

        // Transition Function

        // for(String currState : agent.getAllStateKeys()){
        //     for(String action : agent.getPossibleActionsForState(currState)){
        //         for(String nextState : agent.getPossibleResultingStates(currState, action)){
        //             Double transitionProbability = agent.transitionFunction(currState, action, nextState);
        //                 System.out.println("Transition for : (" + currState + " , " + action + " , " + nextState + ") =  " + transitionProbability);
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

}
