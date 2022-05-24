package States;

import java.util.Map;

public class Road extends State{
    
    private String name;
    private String fromLocation;
    private String toLocation;
    private Double length;
    private String type;

    public Road(String name, String fromLocation, String toLocation, Double length, String type){
        super(name);
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.length = length;
        this.type = type;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public Double getLength() {
        return length;
    }

    public String getType() {
        return type;
    }
    
}
