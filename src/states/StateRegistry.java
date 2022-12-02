package states;

public class StateRegistry {
    
    private State state;
    private String speedAdjustment;
    private String pedestrianTraffic;

    public StateRegistry(State state){
        this.state = state;
    }

    public StateRegistry(State state, String speedAdjustments, String pedestrianTraffic){
        this.state = state;
        this.speedAdjustment   = speedAdjustments;
        this.pedestrianTraffic  = pedestrianTraffic;
    }

    public State getState() {
        return state;
    }

    public String getPedestrianTraffic() {
        return pedestrianTraffic;
    }

    public String getSpeedAdjustment() {
        return speedAdjustment;
    }

    public void setPedestrianTraffic(String pedestrianTraffic) {
        this.pedestrianTraffic = pedestrianTraffic;
    }

    public void setSpeedAdjustment(String speedAdjustment) {
        this.speedAdjustment = speedAdjustment;
    }

    public void setState(State state) {
        this.state = state;
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
            builder.append(speedAdjustment);
            builder.append("_");
            builder.append(pedestrianTraffic);

            return builder.toString();
        }
    }
}