package States;

import java.util.Map;
import java.util.Map.Entry;

public class StateRegistry {
    
    private State state;
    private Map.Entry<String,Integer> speedAdjustment;
    private Map.Entry<String,Double> pedestrianTraffic;

    public StateRegistry(State state){
        this.state = state;
    }

    public StateRegistry(State state, Map.Entry<String,Integer> speedAdjustments, Map.Entry<String,Double> pedestrianTraffic){
        this.state = state;
        this.speedAdjustment   = speedAdjustments;
        this.pedestrianTraffic  = pedestrianTraffic;
    }

    public State getState() {
        return state;
    }

    public Entry<String, Double> getPedestrianTraffic() {
        return pedestrianTraffic;
    }

    public Entry<String, Integer> getSpeedAdjustment() {
        return speedAdjustment;
    }

    @Override
    public String toString() {
        if(state instanceof Location){
            return state.getName();
        }
        else{
            StringBuilder builder = new StringBuilder(state.getName());
            builder.append("_");
            builder.append(((Road)state).getType());
            builder.append("_");
            builder.append(speedAdjustment.getKey());
            builder.append("_");
            builder.append(pedestrianTraffic.getKey());

            return builder.toString();
        }
    }
}
