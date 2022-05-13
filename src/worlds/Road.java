package worlds;

import MDP.State;

public class Road extends State{
    
    private String name;
    private String fromLocation;
    private String toLocation;
    private int length;
    private String type;

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
    
}
