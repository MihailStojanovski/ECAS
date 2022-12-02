package factories;

public class StateProfile {
    private String name;
    private String type;
    private boolean isLocation;    
    private String speedAdjustment;
    private String pedestrianTraffic;


    public StateProfile(String name, String type, String speedAdjustment, String pedestrianTraffic){
        this.name = name;
        this.type = type;
        this.speedAdjustment = speedAdjustment;
        this.pedestrianTraffic = pedestrianTraffic;
    }

    public StateProfile(String name, String type, String speedAdjustment, String pedestrianTraffic, boolean isLocation){
        this.name = name;
        this.type = type;
        this.speedAdjustment = speedAdjustment;
        this.pedestrianTraffic = pedestrianTraffic;
        this.isLocation = isLocation;
    }

    public boolean isLocation(){
        return isLocation;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSpeedAdjustment() {
        return speedAdjustment;
    }

    public String getPedestrianTraffic() {
        return pedestrianTraffic;
    }

}
