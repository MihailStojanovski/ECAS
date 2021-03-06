package states;

public class Road extends State{
    
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Street Name : ");
        builder.append(getName());
        builder.append(", From : ");
        builder.append(fromLocation);
        builder.append(", To : ");
        builder.append(toLocation);
        builder.append(", Type : ");
        builder.append(type);
        builder.append(", Length  : ");
        builder.append(length);
        builder.append(";");
        return builder.toString();
    }
    
}