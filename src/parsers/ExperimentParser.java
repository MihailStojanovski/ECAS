package parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExperimentParser {
    
    public Map<String, List<String>> getExperimentPolicy(String filePath){
        Map<String, List<String>> policy = new HashMap<>();
        try {
            File myObj = new File(filePath);
            Scanner scanner = new Scanner(myObj);

            while (scanner.hasNextLine()) {
              String data = scanner.nextLine();
              data = data.replaceAll("\\{", "");
              data = data.replaceAll("\\}", "");
              String[] splitData = data.split(",");
              for(String s : splitData){
                String state = "";
                String action = "";
                String[] stateAction = s.split(":");
                if(stateAction.length >= 2){
                    state = stateAction[0];
                    action = stateAction[1];
                    List<String> actions = new ArrayList<>();
                    actions.add(action);
                    policy.put(state,actions);

                }

              }
            }
            scanner.close();
          } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        return policy;
    }
}
