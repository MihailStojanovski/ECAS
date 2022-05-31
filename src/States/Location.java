package states;

public class Location extends State{

    public Location(String name) {
        super(name);
    }
    
    
    @Override
    public String toString() {
        return super.getName();
    }
}