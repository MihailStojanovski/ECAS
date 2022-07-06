package parsers;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
  
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import states.Location;
import states.Road;
import states.State;
import worlds.SelfDrivingCarWorld;

public class WorldMapParser {

    public SelfDrivingCarWorld getWorldFromJsonMap(String filePath){
        Object obj = null;
        try {
            FileReader fileReader = new FileReader(filePath);
            obj = new JSONParser().parse(fileReader);
        } catch (IOException e1) {
            System.err.println(e1.getStackTrace());
        }catch (ParseException e2) {
            System.err.println(e2.getStackTrace());
        }
        
        JSONObject jsonObject = (JSONObject)obj;

        List<Road> roadsList = new ArrayList<>();

        Map roadsMap = ((Map)jsonObject.get("roads"));
        Iterator<Map.Entry> itr1 = roadsMap.entrySet().iterator();

        while(itr1.hasNext()){
            Map.Entry pair = itr1.next();
            String name = (String)pair.getKey();

            JSONObject roadDeets = (JSONObject)pair.getValue();

            String fromLocation = (String)roadDeets.get("fromLocation");
            Double length = Double.parseDouble((String)roadDeets.get("length"));
            String type = (String)roadDeets.get("type");
            String toLocation = (String)roadDeets.get("toLocation");

            Road r = new Road(name, fromLocation, toLocation, length, type);
            roadsList.add(r);

        }

        List<String> locations = ((List<String>)jsonObject.get("locations"));
    
        List<Location> locationsList = new ArrayList<>();
        for(String l : locations){
            locationsList.add(new Location(l));
        }

        return new SelfDrivingCarWorld(locationsList, roadsList);
    }
    
}
