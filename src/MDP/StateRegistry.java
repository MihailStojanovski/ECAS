package MDP;

import java.util.Map;
import java.util.Map.Entry;

public class StateRegistry {
    
    private Map.Entry<String,Integer> speedAdjustment;
    private Map.Entry<String,Double> pedestrianTraffic;


    public StateRegistry(Map.Entry<String,Integer> speedAdjustments, Map.Entry<String,Double> pedestrianTraffic){
        this.speedAdjustment   = speedAdjustments;
        this.pedestrianTraffic  = pedestrianTraffic;
    }


    public Entry<String, Double> getPedestrianTraffic() {
        return pedestrianTraffic;
    }

    public Entry<String, Integer> getSpeedAdjustment() {
        return speedAdjustment;
    }
    
}
