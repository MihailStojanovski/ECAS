package rewards;

import states.Road;
import states.StateRegistry;
import worlds.SelfDrivingCarWorld;

public class TaskReward implements Reward{

    private SelfDrivingCarWorld world;

    private Double stayingTime = 120.;
    private Double turningTime = 5.;
    private Double accelerationRate = 2.;
    private Double driverErrorPenalty = 3600.;

    public TaskReward(SelfDrivingCarWorld world){
        this.world = world;
    }
    @Override
    public Double getTaskReward(String currentState, String action, String successorState){

        StateRegistry currentStateRegistry = world.getRegistryFromStateKey(currentState);
        StateRegistry successorStateRegistry = world.getRegistryFromStateKey(successorState);

        if(world.getGoalLocation().equals(currentState)){
            if(action.equals("STAY")){
                if(successorState.equals(currentState)){
                    return 0.;
                }
            }
        }
        if(world.getLocationStrings().contains(currentState) && action.equals("STAY")){
            return stayingTime;
        }

        if(world.getLocationActions().contains(action) && !action.equals("STAY") ){
                if(world.getRoadStrings().contains(successorState)){
                    if(successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                        return turningTime;
                    }
                }
        }

        if(world.getRoadStrings().contains(currentState)){
            if(world.getRoadStrings().contains(successorState) && world.getAccelerateActions().containsKey(action)){
                if(currentStateRegistry.getSpeedAdjustment().equals("NONE") && !successorStateRegistry.getSpeedAdjustment().equals("NONE")){
                    int speed = world.getSpeedLimits().get(((Road)world.getRegistryFromStateKey(currentState).getState()).getType());
                    return accelerationRate * speed / 10;
                }
            }
            if(world.getLocationStrings().contains(successorState) && action.equals("CRUISE")){
                if(!currentStateRegistry.getSpeedAdjustment().equals("NONE")){
                    int speed = world.getSpeedLimits().get(((Road)world.getRegistryFromStateKey(currentState).getState()).getType());
                    int distance = world.getSpeedAdjustments().get(action);
                    return Double.valueOf(3600 * distance / speed);
                }
            }
        }
        return driverErrorPenalty;
    }

    @Override
    public EthicalRewardQuad getEthicalReward(String state, String action, String successorState) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
