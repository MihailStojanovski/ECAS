package factories;

public class ForbiddenStateProfile {
    String name;
    String type;
    
    // For later use
    /*
    String toLocation;
    String fromLocation;
    Double length;
    */ 
    
    String speedAdjustment;
    String pedestrianTraffic;



    public ForbiddenStateProfile(String name, String type, String speedAdjustment, String pedestrianTraffic){
        this.name = name;
        this.type = type;
        this.speedAdjustment = speedAdjustment;
        this.pedestrianTraffic = pedestrianTraffic;
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
