package worlds;

public class Road {
    
    private String name;
    private String fromLocation;
    private String toLocation;
    private int length;
    private String type;

    public Road(String name, String fromLocation, String toLocation, int length, String type){
        this.name = name;
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
