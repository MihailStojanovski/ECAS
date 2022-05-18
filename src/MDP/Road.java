package MDP;

import java.util.Map;

public class Road extends State{
    
    private String name;
    private String fromLocation;
    private String toLocation;
    private int length;
    private String type;
    private Map.Entry<String,Integer> speedAdjustment;
    private Map.Entry<String,Double> pedestrianTraffic;

    public Road(String name, String fromLocation, String toLocation, int length, String type){
        super(name);
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.length = length;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public int getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public Map.Entry<String, Integer> getSpeedAdjustment() {
        return speedAdjustment;
    }

    public Map.Entry<String, Double> getPedestrianTraffic() {
        return pedestrianTraffic;
    }
    
    public void setSpeedAdjustment(Map.Entry<String, Integer> speedAdjustment) {
        this.speedAdjustment = speedAdjustment;
    }
    public void setPedestrianTraffic(Map.Entry<String, Double> pedestrianTraffic) {
        this.pedestrianTraffic = pedestrianTraffic;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.getName());
        builder.append("_");
        builder.append(this.type);
        builder.append("_");
        builder.append(this.speedAdjustment.getKey());
        builder.append("_");
        builder.append(this.pedestrianTraffic.getKey());

        return builder.toString();
    }
    
}
